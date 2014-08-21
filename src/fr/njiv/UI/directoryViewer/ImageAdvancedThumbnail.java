package fr.njiv.UI.directoryViewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;

import fr.njiv.NjivCaller;
import fr.njiv.NjivLoadingException;
import fr.njiv.UI.AlphaIcon;
import fr.njiv.UI.TransparentButton;
import fr.njiv.UI.UIStyle;
import fr.njiv.UI.imageViewer.ImageViewer;
import fr.njiv.image.NjivImage;

/**
 * This class is more than a simple thumbnail, because it also contains the whole image
 * It is used to generate automatically new thumbnails when needed, and to handle everything
 * related to this thumbnail (buttons, image loading, etc..)
 * 
 * @author vuzi
 *
 */
public class ImageAdvancedThumbnail extends JLayeredPane {
	
	private static final long serialVersionUID = -7642653889268588151L;
	
	// Images
	private NjivImage image;
	private JLabel thumbnail;
	
	// Buttons
	private TransparentButton btnCross;
	private TransparentButton btnLeft;
	private TransparentButton btnRight;
	private TransparentButton btnGlass;
	
	// Preview infos
	private int previewSize;
	private ImageIcon loading;
	private JLabel loadingText;

	// Right click menu
	private JPopupMenu rightClickMenu;
	
	// Container
	DirectoryViewer container;
	
	/**
	 * Constructor
	 * @param filename Name of the file to load
	 * @param previewSize Size of the preview (Should not be modified)
	 * @param container Container
	 * @throws NjivLoadingException
	 */
	public ImageAdvancedThumbnail(final String filename, int previewSize, DirectoryViewer container) throws NjivLoadingException {

		super();
		initializePanel(previewSize, container);

		// Load the image
		reloadImage(filename);
	}
	
	public ImageAdvancedThumbnail(NjivImage image, int previewSize, DirectoryViewer container) {

		super();
		initializePanel(previewSize, container);

		// Load the image
		add(loadingText, 0, -1);
		add(thumbnail, 1, -1);

		loadingText.setText("Loading image...");
		thumbnail.setIcon(loading);
		
		repaint();
		initialize(image);
	}
	
	private void initializePanel(int previewSize, DirectoryViewer container) {
		
		//Set the panel
		this.container = container;
		this.previewSize = previewSize;
		this.setOpaque(true);
		this.setBackground(new Color(240, 240, 240));
		this.setPreferredSize(new Dimension(previewSize, previewSize));
		this.setLayout(null);
		
		//Load screen
		loading = UIStyle.loading;
		
		thumbnail = new JLabel(this.loading);
		thumbnail.setBounds(0, 0, previewSize, previewSize);
		
		loadingText = new JLabel("Loading image...");
		loadingText.setFont(UIStyle.fontSmallLight);
		loadingText.setBounds(25, 70, 130, 50);
		loadingText.setForeground(new Color(180, 180, 180));
	}

	/**
	 * Reload image using the old image name
	 */
	private void reloadImage() {
		reloadImage(image.getPath());
	}
	
	/**
	 * Reload the image using the provided filename
	 * @param filename
	 */
	private void reloadImage(final String filename) {
		ImageAdvancedThumbnail me = this;
		
		// loading screen
		removeAll();

		add(loadingText, 0, -1);
		add(thumbnail, 1, -1);

		loadingText.setText("Loading image...");
		thumbnail.setIcon(loading);
		
		repaint();
		
		// Create the background NjivImage
		SwingWorker<NjivImage, Integer> loading = new SwingWorker<NjivImage, Integer>() {
        	
        	NjivImage img;

			@Override
			protected NjivImage doInBackground() throws Exception {
      	    	// Load the image
				try {
					System.out.println("[T] ["+Thread.currentThread().getName()+"] Loading image "+filename+" in background...");
					img = new NjivImage(filename);
				} catch(NjivLoadingException e) {
					System.out.println("[T] ["+Thread.currentThread().getName()+"] Error while load "+filename+" : "+e.getLocalizedMessage());
					JOptionPane.showMessageDialog(me,
						    filename+" : "+e.getLocalizedMessage(),
						    "loading error",
						    JOptionPane.ERROR_MESSAGE);
					setVisible(false);
					container.getThumbnails().remove(me);
				}
				return img;
			}
			
			@Override
			protected void done() {
				if(img != null) {
					System.out.println("[T] Loading of "+filename+" done !");
					initialize(img);
				}
	        }
		};
		loading.execute();
	}
	
	/**
	 * Iinitialize everything
	 * @param loadedImage
	 */
	private void initialize(NjivImage loadedImage) {
		final ImageAdvancedThumbnail me = this;
		
		this.image = loadedImage;
		this.updateIcon();
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
            	if(e.getButton() == MouseEvent.BUTTON3) {
            		rightClickMenu(e);
            	}
			}
		});
		
		// Add the buttons
		btnCross = new TransparentButton(new AlphaIcon(UIStyle.cross, 0.5f));
		btnCross.setContentAreaFilled(false);
		btnCross.setBorderPainted(false);
		btnCross.setBounds(103, 7, 20, 23);
		btnCross.addMouseListener(new  MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				hideControlButtons();
			}
			public void mouseClicked(MouseEvent e) {
				container.deleteThumbnails(container.getThumbnails().indexOf(me));
			}
			public void mouseEntered(MouseEvent e) {
				showControlButtons();
				btnCross.highlight();
			}
			
		});
		add(btnCross, 2, -1);
		
		btnLeft = new TransparentButton(new AlphaIcon(UIStyle.left, 0.5f));
		btnLeft.setContentAreaFilled(false);
		btnLeft.setBorderPainted(false);
		btnLeft.setBounds(103, 37, 20, 23);
		btnLeft.addMouseListener(new  MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				hideControlButtons();
			}
			public void mouseClicked(MouseEvent e) {
				int index = container.getThumbnails().indexOf(me);
				container.switchThumbnails(index, index-1);
			}
			public void mouseEntered(MouseEvent e) {
				showControlButtons();
				btnLeft.highlight();
			}
			
		});
		add(btnLeft, 2, -1);
		
		btnRight = new TransparentButton(new AlphaIcon(UIStyle.right, 0.5f));
		btnRight.setContentAreaFilled(false);
		btnRight.setBorderPainted(false);
		btnRight.setBounds(103, 67, 20, 23);
		btnRight.addMouseListener(new  MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				hideControlButtons();
			}
			public void mouseClicked(MouseEvent e) {
				int index = container.getThumbnails().indexOf(me);
				container.switchThumbnails(index, index+1);
			}
			public void mouseEntered(MouseEvent e) {
				showControlButtons();
				btnRight.highlight();
			}
			
		});
		add(btnRight, 2, -1);
		
		btnGlass = new TransparentButton(new AlphaIcon(UIStyle.mglass, 0.5f));
		btnGlass.setContentAreaFilled(false);
		btnGlass.setBorderPainted(false);
		btnGlass.setBounds(103, 97, 20, 23);
		btnGlass.addMouseListener(new  MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				hideControlButtons();
			}
			public void mouseClicked(MouseEvent e) {
				viewImage();
			}
			public void mouseEntered(MouseEvent e) {
				showControlButtons();
				btnGlass.highlight();
			}
			
		});
		add(btnGlass, 2, -1);
		
		// The opacity effect
		addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent e) {
				hideControlButtons();
			}
			public void mouseEntered(MouseEvent e) {
				showControlButtons();
			}
		});
		
		this.initializeRightClickMenu();
		this.repaint();
	}
	
	/**
	 * Initialize the right click menu
	 */
	private void initializeRightClickMenu() {
		final ImageAdvancedThumbnail me = this;
		
		rightClickMenu = new JPopupMenu();

		// Items
		JMenuItem delete = new JMenuItem("Delete");
		delete.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				container.deleteThumbnails(container.getThumbnails().indexOf(me));
			}
		});
		rightClickMenu.add(delete);
		
		JMenuItem reload = new JMenuItem("Reload image");
		reload.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				reloadImage();
			}
		});
		rightClickMenu.add(reload);
		rightClickMenu.add(new JSeparator());
		
		JMenuItem moveRight = new JMenuItem("Move to right");
		moveRight.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int index = container.getThumbnails().indexOf(me);
				container.switchThumbnails(index, index+1);
			}
		});
		rightClickMenu.add(moveRight);
		
		JMenuItem moveLeft = new JMenuItem("Move to left");
		moveLeft.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int index = container.getThumbnails().indexOf(me);
				container.switchThumbnails(index, index-1);
			}
		});
		rightClickMenu.add(moveLeft);
		rightClickMenu.add(new JSeparator());
		
		JMenuItem view = new JMenuItem("View image");
		view.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				viewImage();
			}
		});
		rightClickMenu.add(view);
	}

	/**
	 * Hide the buttons (with alpha)
	 */
	void hideControlButtons() {
		btnCross.hide();
		btnLeft.hide();
		btnRight.hide();
		btnGlass.hide();
		repaint();
	}
	
	/**
	 * Show the buttons (with alpha)
	 */
	void showControlButtons() {
		btnCross.show();
		btnLeft.show();
		btnRight.show();
		btnGlass.show();
		repaint();
	}
	
	/**
	 * Set the icon
	 * @param icon
	 */
	private void setIcon(ImageIcon icon) {
		thumbnail.setIcon(icon);
	}
	
	/**
	 * Update the icon if needed. If the icon needs to be updated, this update
	 * is performed in a swing worker thread
	 */
	public void updateIcon() {
		if(image.needGenerateIcon()) {
			loadingText.setText("Updating icon...");
			thumbnail.setIcon(this.loading);
			this.repaint();
			
			// Create the background NjivImage
			SwingWorker<ImageIcon, Integer> rescale = new SwingWorker<ImageIcon, Integer>() {
	        	
				ImageIcon img;
	
				@Override
				protected ImageIcon doInBackground() throws Exception {
	      	    	// Load the image
					System.out.println("[T] ["+Thread.currentThread().getName()+"] Updating icon image for "+image.getName()+" in background...");
					img = image.getIcon(previewSize);
					return img;
				}
				
				@Override
				protected void done() {
					if(img != null) {
						System.out.println("[T] Icon updated for "+image.getName()+" !");
						setIcon(img);
						loadingText.setText("");
					} else
						loadingText.setText("[ Error while loading icon ]");
		        }
			};
			rescale.execute();
		}
	}
	
	/**
	 * Get the image
	 * @return
	 */
	public NjivImage getImage() {
		return this.image;
	}
	
	/**
	 * Place the right click menu
	 * @param e
	 */
	private void rightClickMenu(MouseEvent e) {
		rightClickMenu.show(e.getComponent(), e.getX(), e.getY()); 
	}
	
	/**
	 * Launch the viewer with the NjivImage to show it
	 */
	private void viewImage() {
		System.out.println("diaporama not");
		ImageViewer viewer = new ImageViewer(image, true);
		viewer.setCloseListener(new NjivCaller() {
			
			public void onChange() {
				updateIcon();
			}
		});
		viewer.setVisible(true);
	}
}
