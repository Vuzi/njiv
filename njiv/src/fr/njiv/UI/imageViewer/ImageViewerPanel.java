package fr.njiv.UI.imageViewer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import fr.njiv.NjivCaller;
import fr.njiv.Options;
import fr.njiv.PluginLoader;
import fr.njiv.Utils;
import fr.njiv.UI.KeyAction;
import fr.njiv.UI.KeyActionListener;
import fr.njiv.UI.imageViewer.plugin.NjivImageViewerPlugin;
import fr.njiv.image.NjivImage;
import fr.njiv.image.NjivImageModificator;
import fr.njiv.image.modifier.ModifierBlackAndWhite;
import fr.njiv.image.modifier.ModifierRotate;

public class ImageViewerPanel extends JPanel implements NjivCaller {
	
	private static final long serialVersionUID = 890861634785201126L;
	private static int TOOLBAR_HEIGHT = 21;
	
	// Container
	private ImageViewer container;
	
	// Image in memory
	private NjivImage image;
	private SwingWorker<BufferedImage, Integer> rendering;
	
	// Smoother image version, used for downscale
	private BufferedImage toDisplay;
	
	// Right click menu
	private JPopupMenu rightClickMenu;
	
	// Array of originals actions
	// This action are binded with keys in the conf file, and used for defining which key refers to which action
	private Object [][] actions = { 
		{ "open_file"    , new KeyAction(){ public void todo() { container.openNewFile(); } } },
		{ "open_file_new", new KeyAction(){ public void todo() { container.openNewFileNewWindow(); } } },
		{ "save_file"    , new KeyAction(){ public void todo() { container.saveFile();    } } },
		{ "save_file_as" , new KeyAction(){ public void todo() { container.saveFileAs();  } } },
		{ "original_size", new KeyAction(){ public void todo() { setZoom(1.0f);           } } },
		{ "original_pos" , new KeyAction(){ public void todo() { setX(0); setY(0);        } } },
		{ "refresh"      , new KeyAction(){ public void todo() { container.reload(); setZoom(1.0f);} } },
		{ "zoom_in"      , new KeyAction(){ public void todo() { zoomIn();                } } },
		{ "zoom_out"     , new KeyAction(){ public void todo() { zoomOut();               } } },
		{ "move_left"    , new KeyAction(){ public void todo() { moveLeft();              } } },
		{ "move_right"   , new KeyAction(){ public void todo() { moveRight();             } } },
		{ "move_up"      , new KeyAction(){ public void todo() { moveUp();                } } },
		{ "move_down"    , new KeyAction(){ public void todo() { moveDown();              } } },
		{ "undo"         , new KeyAction(){ public void todo() { image.undo(); resetSmoothedDisplay(); repaint(); } } },
		{ "redo"         , new KeyAction(){ public void todo() { image.redo(); resetSmoothedDisplay(); repaint(); } } },
		{ "close"        , new KeyAction(){ public void todo() { container.close();       } } }
	};

	// Array of right click items
	// Key are the right click option name, and the keyaction the action performed
	private Object [][] rightClickItems = {
		{ "Undo"          , new KeyAction(){ public void todo() { image.undo(); repaint(); } } },
		{ "Redo"          , new KeyAction(){ public void todo() { image.redo(); repaint(); } } },
		{ "Reset position", new KeyAction(){ public void todo() { setX(0); setY(0);        } } },
		{ "Reset zoom"    , new KeyAction(){ public void todo() { setZoom(1.0f);           } } }
	};
 	
	// Array of originals modifiers
	// (linked to actions)
	private NjivImageModificator[] modifiers = {
		new ModifierRotate(),
		new ModifierBlackAndWhite()
	};
	
	// Scale of the image
	private float scale = 1.0f;
	private boolean resized = false;
	
	// Position of the image
	// (Only used with a scale != 1.0f)
	private int posX = 0;
	private int posY = 0;
	
	// Previous position of the mouse
	private int prevMouseX = 0;
	private int prevMouseY = 0;
	
	/**
	 * Constructor for the image panel
	 * @param image
	 */
	public ImageViewerPanel(NjivImage image, ImageViewer container) {
		super();
		this.image = image;
		this.image.addChangeListener(this);
		this.container = container;

		setKeyBinding();
		setMouseBinding();
		setRightClickMenu();
		
		this.setBackground(Color.WHITE);
		this.setFocusable(true);
		this.requestFocus();
		this.setDefaultZoomLevel();
		this.repaint();
	}
	
	/**
	 * Right click menu
	 */
	private void setRightClickMenu() {
		
		final ImageViewerPanel me = this;
		rightClickMenu = new JPopupMenu();
		
		// Right click items
		for(int i = 0; i < rightClickItems.length; i++) {
			final int final_i = i;
			
			JMenuItem jmi = new JMenuItem((String)rightClickItems[i][0]);
			
			jmi.addMouseListener(new MouseListener() {
				
				public void mouseReleased(MouseEvent e) {
					((KeyAction)(rightClickItems[final_i][1])).todo();
				}
				
				public void mousePressed(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseClicked(MouseEvent arg0) {}
				
			});
			
			rightClickMenu.add(jmi);
			
		}
		
		// Sub menu
		JMenu subRightClickMenu = new JMenu("Modifications");
		
		// Modifiers
		for(NjivImageModificator modifier : modifiers) {
			JMenuItem jmi = new JMenuItem(Utils.capitalizeFirstLetter(modifier.getName()));
			jmi.setToolTipText(modifier.getDesc());
			jmi.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					me.image.modify(modifier);       // Apply the modifier
					resetSmoothedDisplay();          // Reset the smoothed display
					repaint();                       // Repaint everything
					System.out.println("[i] modifier : "+modifier.getName()+" called (right-click menu)");
				}
			});

			subRightClickMenu.add(jmi);
		}

		rightClickMenu.add(subRightClickMenu);
		
		// Plugin modifiers
		JMenu subMenuPlugins = new JMenu("Plugins");
		
		for(NjivImageViewerPlugin modifierPlugin : PluginLoader.imageViewerPlugins) {
			JMenu subMenuPlugin = new JMenu(modifierPlugin.getName());
			subMenuPlugin.setToolTipText(modifierPlugin.getDescription());
			
			if(modifierPlugin.getModifiers() != null) {
				for(NjivImageModificator modifier : modifierPlugin.getModifiers()) {
					JMenuItem jmi = new JMenuItem(Utils.capitalizeFirstLetter(modifier.getName()));
					jmi.setToolTipText(modifier.getDesc());
					jmi.addMouseListener(new MouseAdapter() {
						public void mouseReleased(MouseEvent e) {
							me.image.modify(modifier);       // Apply the modifier
							resetSmoothedDisplay();          // Reset the smoothed display
							repaint();                       // Repaint everything
							System.out.println("[i] modifier : "+modifier.getName()+" called (plugin/right-click menu)");
						}
					});
					
					subMenuPlugin.add(jmi);
				}
			}
			
			subMenuPlugins.add(subMenuPlugin);
		}
		
		rightClickMenu.add(subMenuPlugins);	
	}
	
	/**
	 * Set the biding for the image modifier
	 * @param toexec An hashmap containing keys and implemented interfaces containing the operation to do
	 */
	private void setImageModifiersBinding(HashMap<String, KeyAction> toexec) {
		for(int i = 0; i < modifiers.length; i++) {
			final int final_i = i;

			toexec.put("modifier:"+modifiers[i].getName(), new KeyAction() {
				
				public void todo() {
					image.modify(modifiers[final_i]);       // Apply the modifier
					resetSmoothedDisplay();                 // Reset the smoothed display
					repaint();                              // Repaint everything
					System.out.println("[i] modifier : "+modifiers[final_i].getName()+" called (hotkey)");
				}
				
			});
		}
	}
	
	/**
	 * Set the key binding
	 */
	@SuppressWarnings("unchecked")
	private void setKeyBinding() {
		
		HashMap<String, KeyAction> toexec = new HashMap<String, KeyAction>();
		
		// Get the keys
		Map<String, String> keys;
		Object map = Options.imageViewer.get("key_binding");
		if(map != null)
			keys = (Map<String, String>) map;
		else
			keys = new HashMap<String, String>(); 
					
		// Initalize the lists
		for(int i = 0; i < actions.length; i++) {
			toexec.put((String)(actions[i][0]), (KeyAction)(actions[i][1]));
		}
		
		// Here TODO plugins
		setImageModifiersBinding(toexec);
		this.addKeyListener(new KeyActionListener(this, keys, toexec));
	}
	
	/**
	 * Set mouse binding, this biding is not defined by the configuration file
	 */
	private void setMouseBinding() {

		final ImageViewerPanel me = this;
		
		// Zoom in and out with the wheel
		this.addMouseWheelListener(new MouseWheelListener() {
			
			public void mouseWheelMoved(MouseWheelEvent e) {
				int notches = e.getWheelRotation();
				
				if (notches < 0) {
					// Up
	            	me.zoomIn();
				} else {
					// Down
	            	me.zoomOut();
				}
			}
		});
		
		// Drag of the mouse
		this.addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseMoved(MouseEvent e) {}
			
			public void mouseDragged(MouseEvent e) {
				
				// Only with the left click
				if (SwingUtilities.isLeftMouseButton(e)) {
	            	me.moveX(e.getX() - prevMouseX);
	            	me.moveY(e.getY() - prevMouseY);
	
	            	me.prevMouseX = e.getX();
	            	me.prevMouseY = e.getY();
					
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}
			}
		});
		
		// Detect pressed mouse
		this.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				// Cursor back to default
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			
			public void mousePressed(MouseEvent e) {
            	me.prevMouseX = e.getX();
            	me.prevMouseY = e.getY();
            	
            	if(e.getButton() == MouseEvent.BUTTON3) {
            		me.rightClickMenu(e);
            	}
			}
			
			public void mouseExited(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseClicked(MouseEvent arg0) {}
		});
		
	}
	
	/**
	 * Set the right click menu
	 * @param e The event
	 */
	private void rightClickMenu(MouseEvent e) {
		rightClickMenu.show(e.getComponent(), e.getX(), e.getY()); 
	}
	
	/**
	 * Zoom in the image by + 0.05 on the scale
	 */
	public void zoomIn() {
		this.setZoom(scale*1.05f);
	}
	
	/**
	 * Zoom out of the image by - 0.05 on the scale
	 */
	public void zoomOut() {
		this.setZoom(scale*0.95f);
	}

	/*
	 * Zoom in the image
	 */
	public void zoom(float value) {
		setZoom(scale + value);
	}

	/*
	 * Set the zoom of the the image
	 */
	public void setZoom(float value) {
		if(value < 0.01f)
			value = 0.01f;
		
		if(value == 1.0f)
			resized = false;
		else {
			resized = true;
			float ratio = (float)value / (float)this.scale;
			this.posX = (int)(this.posX * ratio);
			this.posY = (int)(this.posY * ratio);
		}
		
		this.scale = value;          // New scale
		this.resetSmoothedDisplay(); // Need new smoothed image
    	this.updateContainerInfos(); // Update zoom info
		this.repaint();              // Repaint everything
	}
	
	/**
	 * Move the image up by 10px
	 */
	public void moveUp() {
		this.moveY(-10);
	}

	/**
	 * Move the image down by 10px
	 */
	public void moveDown() {
		this.moveY(10);
	}

	/**
	 * Move the image left by 10px
	 */
	public void moveLeft() {
		this.moveX(-10);
	}

	/**
	 * Move the image right by 10px
	 */
	public void moveRight() {
		this.moveX(10);
	}

	/**
	 * Move the image on the x axis
	 */
	public void moveX(int px) {
		this.setX(this.posX + px);
	}

	/**
	 * Move the image on the y axis
	 */
	public void moveY(int py) {
		this.setY(this.posY + py);
	}

	/**
	 * Set the x decalage for the image
	 */
	public void setX(int x) {
		this.posX = x;
		this.resized = true;
		this.repaint();
	}

	/**
	 * Set the y decalage for the image
	 */
	public void setY(int y) {
		this.posY = y;
		this.resized = true;
		this.repaint();
	}
	
	/**
	 * Compute the default zoom level based on the size of the window, the auto_zoom flag and the image_resize_method
	 */
	public void setDefaultZoomLevel() {

        // New size
        if(Options.imageViewer.containsKey("image_resize_method") && ((String)Options.imageViewer.get("image_resize_method")).equals("fit")) {
	        // Fit, scale unused
        	return;
        } else {
        	// Proportional
        	float rap_x = (float)this.getHeight() / (float)image.getImage().getHeight();
        	float rap_y = (float)this.getWidth() / (float)image.getImage().getWidth();
        	
        	// Auto zoom control
        	if(Options.imageViewer.containsKey("auto_zoom") && ((String)Options.imageViewer.get("auto_zoom")).equals("false")) {
            	if(Math.min(rap_x, rap_y) >= 1)
            		rap_x = rap_y = 1;
        	}
        	
        	if(rap_x <= rap_y) {
        		this.scale = rap_x;
        	} else {
        		this.scale = rap_y;
        	}
    	
        	System.out.println("[i] scale default : "+this.scale);
        }
	}
	
	/**
	 * Paint the component with the image in the background. This method takes in account
	 * the scale of the image, the position and with the right options can start
	 * a Swing worker thread to get smoother downscalled images.
	 */
    protected void paintComponent(Graphics g) {
      
        super.paintComponents(g);

        Graphics2D g2 = (Graphics2D)g;
        
        // If there is a rendering in process, tell it to stop
        if(rendering != null && !rendering.isDone()) {
        	rendering.cancel(true);
        }
        
        int newW;
	    int newH;
        
        // New size
        if(Options.imageViewer.containsKey("image_resize_method") && ((String)Options.imageViewer.get("image_resize_method")).equals("fit")) {
            newW = this.getWidth();
            newH = this.getHeight() - TOOLBAR_HEIGHT;
        } else {

            if(this.resized == false) {
            	setDefaultZoomLevel();
            }
            
            newW = (int)((float)(image.getImage().getWidth()) * scale);
            newH = (int)((float)(image.getImage().getHeight() - TOOLBAR_HEIGHT) * scale);
        }
        
        // Fill the background
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        // Resize method
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
	                        RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Compute the position
        int x = (this.getWidth()/2) - (newW/2);
        int y = ((this.getHeight() - TOOLBAR_HEIGHT)/2) - (newH/2) + TOOLBAR_HEIGHT;
        
        if(this.resized) {
        	x += posX;
        	y += posY;
        } else {
        	posX = 0;
        	posY = 0;
        }
        
        // If settings allow to have nice quality
        if(Options.imageViewer.containsKey("image_resize_quality") && ((String)Options.imageViewer.get("image_resize_quality")).equals("quality")) {
        	if(toDisplay != null) {
        		g2.drawImage(this.toDisplay, x, y, newW, newH, null);
        	} else {
        		g2.drawImage(this.image.getImage(), x, y, newW, newH, null);
	        
		        /*
		         * Try to make a better image in another thread to not slow everything
		         * Only for downscaled images
		         */
        		if(this.scale < 1.0f) {
			        rendering = new SwingWorker<BufferedImage, Integer>() {
			        	
			        	BufferedImage img;
		
						@Override
						protected BufferedImage doInBackground() throws Exception {
			      	    	 
							// Wait for some inactivity
							try {
			      	    		Thread.sleep(100);
							} catch(InterruptedException ex) {
				      	    	System.out.println("[T] ["+Thread.currentThread().getName()+"] Work cancelled ");
								return null;
							}
			      	    	 
			      	    	System.out.println("[T] ["+Thread.currentThread().getName()+"] Starting work in thread...");
			      	    	Image tmp = ((Image)(image.getImage())).getScaledInstance((int)(scale * image.getWidth()), (int)(scale * image.getHeight()), Image.SCALE_AREA_AVERAGING);
			      	    	img = Utils.imageToBufferedImage(tmp);
			      	    	
			      			if(this.isCancelled())
			      				return null;
			      			else
			      				return img;
						}
						
						@Override
						protected void done() {
							if(img != null) {
								System.out.println("[T] ["+Thread.currentThread().getName()+"] Work done !");
								setSmoothedDisplay(img);
							} else
								System.out.println("[T] ["+Thread.currentThread().getName()+"] Work cancelled !");
				        }
					};
					rendering.execute();
        		}
        	}
        
        } else
	        // Draw just the image
	        g2.drawImage(image.getImage(), x, y, newW, newH, null);
    }
    
    /**
     * Update the displayed image
     * @param image
     */
    public void updateImage(NjivImage image) {
    	this.image = image;
    	this.repaint();
    }

    /**
     * Return the active scale used
     * @return
     */
	public float getScale() {
		return this.scale;
	}
	
	/**
	 * Updates infos in the container, if the container is ready
	 */
	private void updateContainerInfos() {
		if(container.isReady())
			container.updateInfoPanel();
	}

	/**
	 * Call by the worker to set a smoothest version of the image
	 * @param img
	 */
	private void setSmoothedDisplay(BufferedImage img) {
		this.toDisplay = img;
		this.repaint();
	}
	
	private void resetSmoothedDisplay() {
		this.toDisplay = null;
	}
	
	public void onChange() {
		this.resetSmoothedDisplay(); // Need new smoothed image
    	this.updateContainerInfos(); // Update zoom info
		this.repaint();              // Repaint everything
	}
	
}
