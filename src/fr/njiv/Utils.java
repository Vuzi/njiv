package fr.njiv;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import fr.njiv.UI.KeyAction;
import fr.njiv.UI.KeyActionListener;
import fr.njiv.UI.directoryViewer.DirectoryViewer;
import fr.njiv.UI.imageViewer.ImageViewer;
import fr.njiv.image.NjivImage;

public class Utils {
	
	public static BufferedImage cloneBufferedImage(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static String formatedFileSize(long bytes) {
	    int u = 0;
	    for (;bytes > 1024*1024; bytes >>= 10) {
	        u++;
	    }
	    if (bytes > 1024)
	        u++;
	    return String.format("%.1f %cB", bytes/1024f, " KMGTPE".charAt(u));
	}
	
	public static String capitalizeFirstLetter(String original){
	    if(original == null || original.length() == 0)
	        return original;
	    return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public static void launchViewer(final NjivImage image, Frame parent) {
		if(image != null) {
			// The UI thread
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						System.out.println("[i] Starting the new viewer window...");
						ImageViewer window = new ImageViewer(image);
						window.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			System.out.println("[x] No image selected, not starting the viewer window");
		}
	}
	
	public static void launchImageViewer(String filename, Frame parent) {
		// Try to load the image
		NjivImage image = loadImage(filename, parent);
		launchViewer(image, parent);
	}
	
	public static void launchDirectoryViewer(String filename, Frame parent) {
		new DirectoryViewer(filename).setVisible(true);
	}
	
	public static NjivImage loadImage(String fileName, Frame parent) {
		// Image to open
		NjivImage image = null;
		
		// If no image, try to load
		if(fileName == null) {
			FileDialog chooser = new java.awt.FileDialog(parent, "Select an image to load", FileDialog.LOAD);
			chooser.setVisible(true);
			
			String choosen = chooser.getFile();

			if(choosen == null) {
    			JOptionPane.showMessageDialog(null,
    				    "No file selected",
    				    "Error",
    				    JOptionPane.ERROR_MESSAGE);
    			return null;
			} else {
				fileName = chooser.getDirectory() + "/" + choosen;
			}  
			  
		}
		
		// Try to open
		try {
			image = new NjivImage(fileName);
			return image;
		} catch (NjivLoadingException e1) {
			//e1.printStackTrace();
			JOptionPane.showMessageDialog(null,
				    "Error : "+e1.getMessage(),
				    "Error while loading '"+fileName+"'",
				    JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
	}
	
	public static String[] getSupportedFormat() {
		// For now, only suppots images supported by ImageIO
		return ImageIO.getWriterFormatNames();
	}
	
	public static BufferedImage imageToBufferedImage(Image img) {
	    if (img instanceof BufferedImage) {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	public static HashMap<String, KeyAction> setKeyBinder(Object[][] actions, Map<String, String> binding, Component componentBinding) {

		// Initalize the lists
		HashMap<String, KeyAction> toexec = new HashMap<String, KeyAction>();
		
		for(int i = 0; i < actions.length; i++) {
			toexec.put((String)(actions[i][0]), (KeyAction)(actions[i][1]));
		}
		
		// If no binding
		if(binding == null)
			binding = new HashMap<String, String>(); 

		// Set the listener
		componentBinding.setFocusable(true);
		componentBinding.requestFocus();
		componentBinding.addKeyListener(new KeyActionListener(componentBinding, binding, toexec));
		
		return toexec;
	}
}
