package fr.njiv.UI.diaporama;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import fr.njiv.Options;
import fr.njiv.Utils;
import fr.njiv.UI.KeyAction;
import fr.njiv.UI.KeyActionListener;
import fr.njiv.UI.UIStyle;
import fr.njiv.image.NjivImage;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// TODO :
//  - Ajouter la sauvegarde / chargement des options de diaporama
//  - Ajouter la possiblité de mettre en pause

/**
 * Frame of the diaporama
 * @author vuzi
 *
 */
public class Diaporama extends JFrame {
	
	private static final long serialVersionUID = 653010759051650407L;

	DiaporamaPanel panel;

	// Array of originals actions
	// This action are binded with keys in the conf file, and used for defining which key refers to which action
	private Object [][] actions = { 
		{ "pause"    , new KeyAction(){ public void todo() { System.out.println("pause");                                        } } },
		{ "close"    , new KeyAction(){ public void todo() { dispose(); timer.stop();                                            } } },
		{ "next"     , new KeyAction(){ public void todo() { if(transition != null) { endTransition(); } else { nextImage(); }   } } }
	};

	private ArrayList<DiaporamaImage> images;
	private DiaporamaOptions options;
	
	private int imageIndex;
	private int previousImageIndex;
	
	private NjivDiaporamaTransition transition;
	
	private Timer timer;
	private Timer transitionTimer;
	
	/**
	 * Internal diaporama image, containing the image to display (A resized version, or the original version)
	 * and the bounds where to display it
	 * @author vuzi
	 *
	 */
	public class DiaporamaImage {
		private NjivImage image;
		private BufferedImage toDisplay;
		private Rectangle bounds;
		
		/**
		 * Constructor, compute the new bounds of the future diaporama
		 * @param image
		 * @param panel
		 */
		public DiaporamaImage(NjivImage image, JPanel panel) {
			
			this.image = image;
			float scale = getScale(panel);
			
	        int newW = (int)((float)(image.getImage().getWidth()) * scale);
	        int newH = (int)((float)(image.getImage().getHeight()) * scale);
	        
	        int x = (int) ((panel.getPreferredSize().getWidth()/2) - (newW/2));
	        int y = (int) ((panel.getPreferredSize().getHeight()/2) - (newH/2));
	        
			this.bounds = new Rectangle(x, y, newW, newH);
			
			 if(Options.diaporama.containsKey("image_resize_quality") && ((String)Options.diaporama.get("image_resize_quality")).equals("quality")) {
				 Image tmp = ((Image)(image.getImage())).getScaledInstance((int)(scale * image.getWidth()), (int)(scale * image.getHeight()), Image.SCALE_AREA_AVERAGING);
				 toDisplay = Utils.imageToBufferedImage(tmp);
			 } else {
				 System.out.println("ici");
				 toDisplay = image.getImage();
			 }
		}
		
		/**
		 * Compute the image scale
		 * @param panel
		 * @return
		 */
		private float getScale(JPanel panel) {
			float rap_x = (float)panel.getPreferredSize().getHeight() / (float)image.getImage().getHeight();
	    	float rap_y = (float)panel.getPreferredSize().getWidth() / (float)image.getImage().getWidth();
	    	
	    	if(rap_x <= rap_y) {
	    		return rap_x;
	    	} else {
	    		return rap_y;
	    	}
		}

		public NjivImage getImage() {
			return image;
		}

		public BufferedImage getToDisplay() {
			return toDisplay;
		}

		public Rectangle getBounds() {
			return bounds;
		}
	}
	
	/**
	 * Internal panel of the diaporama
	 * @author vuzi
	 *
	 */
	private class DiaporamaPanel extends JPanel {

		private static final long serialVersionUID = -6576271788284486308L;

		/**
		 * Paint the image on the background
		 */
	    protected void paintComponent(Graphics g) {
			super.paintComponents(g);

	        Graphics2D g2 = (Graphics2D)g;
	        
	        // Fill the background
	        g.setColor(this.getBackground());
	        g.fillRect(0, 0, this.getWidth(), this.getHeight());

	        // Resize method
	        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
		                        RenderingHints.VALUE_RENDER_QUALITY);
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	        
			if(transition != null) {

				DiaporamaImage image1 = previousImageIndex >= 0 ? images.get(previousImageIndex) : null;
				DiaporamaImage image2 = imageIndex < images.size() ? images.get(imageIndex) : null;
				
		        if(!transition.updateTransition(image1, image2, g2)) {
		        	// End of the transition
		        	endTransition();
		        }
			} else if(imageIndex >= 0 && imageIndex < images.size()) {
				Rectangle bounds = images.get(imageIndex).bounds;

				// Draw just the image
				g2.drawImage(images.get(imageIndex).toDisplay, bounds.x, bounds.y, bounds.width, bounds.height, null);
			}
		}
	}
	
	/**
	 * Create the application.
	 */
	public Diaporama(DiaporamaOptions options, List<NjivImage> sourceImages) {
		super();
		
		System.out.println("[i] Launching diaporama...");
		
		if(sourceImages.size() <= 0) {
			JOptionPane.showMessageDialog(null,
				    "Error while launching diaporama : No image selected",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			setVisible(false);
			dispose();
			return;
		}

		if(options != null) {
			this.options = options;
			
			// Initialize
			initialize();
			
			// Binding
			setKeyBinding();
			
			// Images (background loading)
			System.out.println("[T] Computing image bounds");
			this.images = new ArrayList<DiaporamaImage>();
			SwingWorker<Integer, Integer> loading = new SwingWorker<Integer, Integer>() {

				@Override
				protected Integer doInBackground() throws Exception {
					System.out.println("[T] ["+Thread.currentThread().getName()+"] Loading diaporama images in background...");
	      	    	// Load the images
					for(NjivImage image : sourceImages) {
						images.add(new DiaporamaImage(image, panel));
					}

					return 0;
				}
				
				@Override
				protected void done() {
					System.out.println("[T] Loading of diaporama images done !");
					launchDiaporama(); // Start the timer
		        }
			};
			loading.execute();
			this.imageIndex = -1;
			this.previousImageIndex = -1;
			
			// Timer
			initializeTimer();
			
			// Loading screen
			displayLoadingScreen();
		} else {
			dispose();
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		panel = new DiaporamaPanel();
		
		setUndecorated(true);
	    setVisible(true);
	    setContentPane(panel);
		panel.setBackground(Color.BLACK);
		setBounds(0, 0, screenSize.width, screenSize.height);
		panel.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
		
		setBlankCursor();
	}
	
	/**
	 * Initialize the timer used to switch between images
	 */
	private void initializeTimer() {

		transitionTimer = new Timer(2, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.repaint();
			}
		});
		
		timer = new Timer(options.getTimer()*1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextImage();
			}
		});
	}
	
	/**
	 * Start a random transition
	 */
	private void startRandomTransition() {
		// If transition to use
		if(options.getTransitionsToUse().size() > 0) {
			timer.stop();
			transitionTimer.start();
			
			transition = options.getTransitionsToUse().get((new Random()).nextInt(options.getTransitionsToUse().size()));
			transition.startTransition();
		} else {
			if(!timer.isRunning())
				timer.start();
			repaint();
		}
	}
	
	/**
	 * Load the next image
	 */
	private void nextImage() {
		
		previousImageIndex = imageIndex;
		
		// get next image to print
		if(options.useRandom()) {
			imageIndex = (new Random()).nextInt(images.size());
		} else {
			imageIndex++;
			if(imageIndex >= images.size()) {
				if(options.useInfinite())
					imageIndex = 0;
				else {
					if(imageIndex > images.size())
						stop();
				}
			}
		}
		
		// transition
		startRandomTransition();
	}
	
	/**
	 * End the transition
	 */
	private void endTransition() {
		transitionTimer.stop();
		timer.start();
		transition = null;
	}
	
	/**
	 * Display the loading screen
	 */
	private void displayLoadingScreen() {
		JLabel label = new JLabel("Loading...");
		label.setFont(UIStyle.fontLargeLight);
		label.setForeground(Color.WHITE);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setBounds(new Rectangle(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height));
		panel.add(label);
	}
	
	/**
	 * Start the diaporama
	 */
	private void launchDiaporama() {

		System.out.println("[i] Launch of the diaporama");

		panel.removeAll();
		startRandomTransition();
		
		// get first image to print
		if(options.useRandom()) {
			imageIndex = (new Random()).nextInt(images.size());
		} else {
			imageIndex = 0;
		}
		this.repaint();
	}

	/**
	 * Set the key binding
	 */
	@SuppressWarnings("unchecked")
	private void setKeyBinding() {
		
		HashMap<String, KeyAction> toexec = new HashMap<String, KeyAction>();
		
		// Get the keys
		Map<String, String> keys;
		Object map = Options.diaporama.get("key_binding");
		if(map != null)
			keys = (Map<String, String>) map;
		else
			keys = new HashMap<String, String>(); 
			
		// Initalize the lists
		for(int i = 0; i < actions.length; i++) {
			toexec.put((String)(actions[i][0]), (KeyAction)(actions[i][1]));
		}

		panel.setFocusable(true);
		panel.requestFocus();
		panel.addKeyListener(new KeyActionListener(panel, keys, toexec));
	}

	/**
	 * Set the cursor to blank
	 */
	private void setBlankCursor() {
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		panel.setCursor(blankCursor);
	}
	
	/**
	 * Get the options
	 * @return The options
	 */
	public DiaporamaOptions getOptions() {
		return options;
	}
	
	/** 
	 * Stop the diaporama 
	 */
	public void stop() {
		this.dispose();
		this.timer.stop();
		System.out.println("[i] End of the diaporama");
	}

}
