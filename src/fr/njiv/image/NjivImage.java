package fr.njiv.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import fr.njiv.NjivCaller;
import fr.njiv.NjivLoadingException;
import fr.njiv.Utils;

public class NjivImage {

	// List of registered images
	private ArrayList<BufferedImage> previous = new ArrayList<BufferedImage>();
	private int bufferSize = 10;
	
	private ArrayList<BufferedImage> next = new ArrayList<BufferedImage>();
	
	// Current image
	private File fileImage;
	private String fileName;
	private BufferedImage image;
	private String format;
	private BasicFileAttributes attr;
	private ImageIcon icon;
	
	// Exif
	private Metadata metadata;
	
	public NjivImage(String fileName) throws NjivLoadingException {
		this.fileName = fileName;
		refresh();
	}
	
	public boolean needGenerateIcon() {
		return this.icon == null;
	}
	
	public ImageIcon getIcon(int size) {
		if(this.icon == null) {
			System.out.println("[i] Generating new icon...");
			float scale = Math.min((float)size / (float)image.getHeight(), (float)size / (float)image.getWidth());
			this.icon = new ImageIcon(((Image)image).getScaledInstance((int)(scale * image.getWidth()), (int)(scale * image.getHeight()), Image.SCALE_AREA_AVERAGING));
		}
		return this.icon;
	}
	
	// Listener
	private NjivCaller listener = null;
	
	public void setNjivCaller(NjivCaller listener) {
		this.listener = listener;
	}

	public NjivCaller getNjivCaller() {
		return this.listener;
	}
	
	/**
	 * Refresh the image in case of changes of the file
	 * @throws NjivLoadingException
	 */
	public void refresh() throws NjivLoadingException {
		System.out.println("[i] Updating image infos...");
		try {
			fileImage = new File(fileName);
			
			// Tests
			if(fileImage.exists() == false) {
				throw new NjivLoadingException("Image file doesn't exist");
			}
			
			if(fileImage.isDirectory() == true) {
				throw new NjivLoadingException("Can't open directory with the NjivImage class");
			}
			
			// Infos
			attr = Files.readAttributes(fileImage.toPath(), BasicFileAttributes.class);

			// Exif
			try {
				metadata = ImageMetadataReader.readMetadata(fileImage);
			} catch (ImageProcessingException ex) {
				System.out.println("[x] Error while loading metadata from image : "+ex);
				metadata = null;
			}
			
			// BufferedImage
			if(this.previous.size() > 0)
				this.saveImage();
			
			image = ImageIO.read(fileImage);
			
			if(image == null) {
				throw new NjivLoadingException("Error while loading image : format not supported");
			}

			// Format
			try {
				ImageInputStream iis = ImageIO.createImageInputStream(this.fileImage);
				format = ImageIO.getImageReaders(iis).next().getFormatName();
				iis.close();
			} catch (IOException e) {
				System.out.println("[x] Can't read file type : "+e.getLocalizedMessage());
				format = "Unknown";
			}
			
			// Need new icon
			this.icon = null;
			
		} catch (IOException ex) {
			System.out.println("[x] Error while loading image : "+ex);
			throw new NjivLoadingException("Error while loading image", ex);
		}
	}
	
	public void modify(NjivImageModificator modificator, HashMap<String, Object> options) {
		this.saveImage();
		this.icon = null;
		if(this.listener != null)
			this.listener.onChange();
		this.image = modificator.modifyImage(this.image, options);
	}
	
	/**
	 * Undo the changes applied to the image 
	 */
	public void undo() {
		this.revertImage();
	}
	
	/**
	 * Redo the changes applied to the image
	 */
	public void redo() {
		this.cancelRevertImage();
	}
	
	/**
	 * Save the image in the temporary buffer
	 */
	public void saveImage() {
		this.saveImageInPrevious();
	}

	/**
	 * Revert the image based on the temporary buffer
	 */
	public void revertImage() {
		if(this.previous.size() > 0) {
			System.out.println("[i] Reverting image");
			this.saveImageInNext();
			this.image = popImageFromPrevious();
		} else
			System.out.println("[x] No image to revert to");
	}

	/**
	 * Cancel the revert of the image based on the temporary buffer
	 */
	public void cancelRevertImage() {
		if(this.next.size() > 0) {
			System.out.println("[i] Canceling image reverting");
			this.saveImageInPrevious();
			this.image = popImageFromNext();
		} else
			System.out.println("[x] No image to cancel the revert to");
	}

	private BufferedImage popImageFromNext() {
		return this.next.remove(0);
	}
	
	private BufferedImage popImageFromPrevious() {
		return this.previous.remove(0);
	}
	
	private void saveImageInNext() {
		// Get the right size for the list
		while(this.next.size() >= this.bufferSize) {
			this.next.remove(this.next.size()-1);
		}
		
		// Add the element to save 
		this.next.add(0, this.image);
	}
	
	private void saveImageInPrevious() {
		// Get the right size for the list
		while(this.previous.size() >= this.bufferSize) {
			this.previous.remove(this.previous.size()-1);
		}
		
		// Add the element to save 
		this.previous.add(0, this.image);
	}
	
	public ArrayList<BufferedImage> getPrevious() {
		return previous;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public File getFileImage() {
		return fileImage;
	}

	public BufferedImage getImage() {
		return image;
	}

	public String getName() {
		return fileImage.getName();
	}

	public String getPath() {
		return fileImage.getAbsolutePath();
	}

	public String getRights() {
		StringBuilder rights = new StringBuilder("");
		rights.append((fileImage.canRead()) ? "r" : "-");
		rights.append((fileImage.canWrite()) ? "w" : "-");
		rights.append((fileImage.canExecute()) ? "x" : "-");
		return rights.toString();
	}

	public boolean isHidden() {
		return fileImage.isHidden();
	}

	public Date getCreationTime() {
		return new Date(attr.creationTime().toMillis());
	}

	public Date getLastAccessTime() {
		return new Date(attr.lastAccessTime().toMillis());
	}

	public Date getLastModifiedTime() {
		return new Date(attr.lastModifiedTime().toMillis());
	}

	public int getHeight() {
		return this.image.getHeight();
	}
	
	public int getWidth() {
		return this.image.getWidth();
	}
	
	/**
	 * Return the size of the image
	 * @return size of the image in octect
	 */
	public long getSize() {
		return fileImage.length();
	}
	
	public String getSizeFormated() {
		return Utils.formatedFileSize(fileImage.length());
	}

	public Metadata getMetada() {
		return metadata;
	}
	
	public String getFormat() {
		return this.format;
	}
	
	public String getImageFormat() {
		return "Image/"+getFormat();
	}
	
	public void updateFilename(String fileName) throws NjivLoadingException {
		this.fileName = fileName;
		refresh();
	}
	
}
