package fr.njiv.UI.imageViewer;

import javax.imageio.ImageIO;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import fr.njiv.NjivCaller;
import fr.njiv.NjivLoadingException;
import fr.njiv.OptionPanel;
import fr.njiv.Options;
import fr.njiv.Utils;
import fr.njiv.UI.UIStyle;
import fr.njiv.UI.directoryViewer.DirectoryViewer;
import fr.njiv.image.NjivImage;

public class ImageViewer extends JFrame implements NjivCaller {

	// UID
	private static final long serialVersionUID = -1501927733453998891L;
	
	// Image displayed
	private NjivImage image;
	private boolean changed = false;
	private boolean diaporamaImage= false;
	
	// Panel where is displayed the image
	private ImageViewerPanel viewerPanel;
	private JPanel panel_1;
	
	// Color
	private final Color exitInfoColor = new Color(130, 130, 130, 130);
	private final Color enterInfoColor = new Color(100, 100, 100, 240);
	private final Color exitInfoColorText = new Color(255, 255, 255, 170);
	private final Color enterInfoColorText = new Color(255, 255, 255, 240);
	
	private JLabel lblResolutionx;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel;
	private JLabel lblFormatImagejpeg;
	
	private boolean isReady = false;
	private NjivCaller onCloseListener;
	
	/**
	 * Create the application.
	 * @param image 
	 */
	public ImageViewer(NjivImage image, boolean diaporamaImage) {
		this.diaporamaImage = diaporamaImage;
		initialize(image);
	}
	
	/**
	 * Create the application.
	 * @param image 
	 */
	public ImageViewer(NjivImage image) {
		initialize(image);
	}

	/**
	 * Initialize everything
	 */
	private void initialize(NjivImage image) {
		
		// init
		this.image = image;
		this.image.addChangeListener(this);

		initializePanel();
		
		setMenuBar();

		setKeyBinding();
		resize();
		this.isReady = true;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializePanel() {
		
		ImageViewer me = this;
		
		this.setIconImage(UIStyle.logo);
		this.setTitle("Njiv - " + image.getName());
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		
		this.viewerPanel = new ImageViewerPanel(this.image, this);
		this.setContentPane(viewerPanel);
		this.viewerPanel.setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel_1.setBackground(exitInfoColor);
		panel_1.setForeground(exitInfoColorText);
		panel_1.addMouseListener(new MouseAdapter() {
			
			public void mouseExited(MouseEvent e) {
				panel_1.setBackground(exitInfoColor);
				panel_1.setForeground(exitInfoColorText);
				
				me.repaint();
			}
			
			public void mouseEntered(MouseEvent e) {
				panel_1.setBackground(enterInfoColor);
				panel_1.setForeground(enterInfoColorText);
				me.repaint();
			}
			
		});
		viewerPanel.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		
		setInfoPanel();
	}
	
	/**
	 * Set the menu bar of the main window
	 */
	private void setMenuBar() {
		
		final ImageViewer me = this;
		JMenuBar menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu file = new JMenu("File");
		menuBar.add(file);

		JMenuItem refresf = new JMenuItem("Refresh");
		file.add(refresf);
		refresf.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				me.reload();
			}
		});
		
		file.add(new JSeparator());
		
		if(!this.diaporamaImage) {
			JMenuItem newfile = new JMenuItem("Open new file");
			file.add(newfile);
			newfile.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					me.openNewFile();
				}
				
			});
		}
		
		JMenuItem newfilew = new JMenuItem("Open new file in another window");
		file.add(newfilew);
		newfilew.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				me.openNewFileNewWindow();
			}
		});
		
		JMenuItem closefile = new JMenuItem("Close the file");
		file.add(closefile);
		closefile.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				me.dispose();
			}
		});

		JMenuItem save = new JMenuItem("Save the changes");
		file.add(save);
		save.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				me.saveFile();
			}
		});
		
		JMenuItem saveNewName = new JMenuItem("Save as...");
		file.add(saveNewName);
		saveNewName.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				me.saveFileAs();
			}
		});
		
		JMenu options = new JMenu("Options");
		menuBar.add(options);

		JMenuItem optionPanel = new JMenuItem("Show the option panel");
		options.add(optionPanel);
		optionPanel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				new OptionPanel().setVisible(true);
			}
		});
		
		if(!this.diaporamaImage) {
			JMenu diapo = new JMenu("Diaporama");
			menuBar.add(diapo);
	
			JMenuItem diapoPanel = new JMenuItem("Option in the diaporama panel");
			diapo.add(diapoPanel);
			diapoPanel.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					DirectoryViewer viewer = new DirectoryViewer();
					viewer.loadImage(image);
					viewer.setVisible(true);
					dispose();
				}
			});
		}
	}
	
	/**
	 * Create the info panel
	 */
	private void setInfoPanel() {
		
		// Resolution
		lblResolutionx = new JLabel();
		lblResolutionx.setForeground(Color.BLACK);
		panel_1.add(lblResolutionx);
		
		// File size
		lblNewLabel_1 = new JLabel();
		lblNewLabel_1.setForeground(Color.BLACK);
		panel_1.add(lblNewLabel_1);
		
		// Path
		lblNewLabel = new JLabel();
		lblNewLabel.setForeground(Color.BLACK);
		panel_1.add(lblNewLabel);
		
		// Format
		lblFormatImagejpeg = new JLabel();
		lblFormatImagejpeg.setForeground(Color.BLACK);
		panel_1.add(lblFormatImagejpeg);
		
		updateInfoPanel();
	}
	
	/**
	 * Update the informations of the info panel using the image informations
	 */
	void updateInfoPanel() {
		if(viewerPanel.getScale() == 0.0 || viewerPanel.getScale() == 1.0) {
			lblResolutionx.setText("Resolution: "+image.getWidth() + "x" + image.getHeight()+" px");
		} else {
			lblResolutionx.setText("Resolution: "+image.getWidth() + "x" + image.getHeight()+" px ("+String.format("%.0f", viewerPanel.getScale()*100.0)+"%)");
		}
		lblNewLabel_1.setText("File size: "+image.getSizeFormated() + " ( "+image.getSize()+" )");
		lblNewLabel.setText("Path : "+image.getPath());
		lblFormatImagejpeg.setText("Format: "+image.getImageFormat());
	}
	
	/**
	 * Resize the window using the options
	 */
	private void resize() {
		this.setLocationRelativeTo(null);
		if(Options.imageViewer.containsKey("default_size_to_img") && 
		   ((String)Options.imageViewer.get("default_size_to_img")).equals("true"))
			this.setBounds(0, 0, image.getImage().getWidth()+2, image.getImage().getHeight()+25);
		else
			this.setBounds(0, 0, 778, 560);
	}
	
	/**
	 * Set key binding
	 */
	private void setKeyBinding() {
		
		final ImageViewer me = this;
		
		// Key events
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
	            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	            	System.out.println("[i] escape pressed");
	            	me.close();
	            }
			}
		});
	}
	
	/**
	 * Open a new file in the current window
	 */
	public void openNewFile() {
		NjivImage image = Utils.loadImage(null, this);
		
		if(image != null) {
			this.image = image;

			this.viewerPanel.updateImage(image);
			this.updateTitle();
			this.updateInfoPanel();
			this.viewerPanel.setZoom(1.0f);
		}
	}
	
	/**
	 * Change the title to show any modification on the image
	 */
	public void updateTitle() {
		if(!this.changed) {
			this.setTitle("Njiv - "+image.getName() + "*");
			this.changed = true;
		}
	}
	
	/**
	 * Change the title to its default state
	 */
	public void updateTitleNoChange() {
		if(this.changed)
			this.changed = false;
		this.setTitle("Njiv - "+image.getName());
	}
	
	/**
	 * Open a new file in a new window
	 */
	public void openNewFileNewWindow() {
		final NjivImage image = Utils.loadImage(null, this);
		
		if(image != null) {
			System.out.println("[i] Starting the new viewer window...");
			ImageViewer window = new ImageViewer(image);
			window.setVisible(true);

		}
	}

	/**
	 * Open a new file in a new window
	 */
	public void openNewFileNewWindow(String filename) {
		final NjivImage image = Utils.loadImage(filename, this);
		
		if(image != null) {
			System.out.println("[i] Starting the new viewer window...");
			ImageViewer window = new ImageViewer(image);
			window.setVisible(true);

		}
	}
	
	/**
	 * Save the file using its own name and format
	 */
	public void saveFile() {
		
		String format = this.image.getFormat();
		String fileName = this.image.getPath();

		this.save(fileName, format);
	}
	
	/**
	 * Save the file asking the user to a new name
	 */
	public void saveFileAs() {
		// Choose the file to save to
		FileDialog chooser = new java.awt.FileDialog(this, "Select a name to save the image '"+this.image.getName()+"'", FileDialog.SAVE);
		chooser.setVisible(true);
		
		if(chooser.getFile() == null) {
			return;
		}
		
		String choosen = chooser.getDirectory() + chooser.getFile();
		String format;
		
		// Try to get the format
		try {
			String tmp[] = choosen.split("\\.");
			format = tmp[tmp.length - 1];
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
				    "Error while saving file : "+choosen+" is not a valid filename",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		    e.printStackTrace();
			return;
		}

		this.save(choosen, format);
	}
	
	/**
	 * Save the file using the given parameters
	 * @param fileName
	 * @param format
	 */
	public void save(String fileName, String format) {
		try {
			System.out.println("[i] Saving file '"+this.image.getName()+"' to '"+fileName+"'");
			
			File outputfile = new File(fileName);
			//ImageIO
			if (!ImageIO.write(this.image.getImage(), format, outputfile)) {
				System.out.println("Error while saving file : "+format+" is not a supported format");
				JOptionPane.showMessageDialog(null,
						"Error while saving file : "+format+" is not a supported format",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Update every info
			this.reload(fileName);
			
		} catch (IOException e) {
			System.out.println("[x] Error while saving file : "+e.getLocalizedMessage());
			JOptionPane.showMessageDialog(null,
				    "Error while saving file : "+e.getLocalizedMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void reload() {
		try {
			// Update every info
			this.image.refresh();
			this.updateTitleNoChange();
			this.updateInfoPanel();
			this.repaint();
			this.viewerPanel.setDefaultZoomLevel();
		} catch (NjivLoadingException e) {
			System.out.println("[x] Error while reloading file : "+e.getLocalizedMessage());
			JOptionPane.showMessageDialog(null,
					"Error while reloading file : "+e.getLocalizedMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			this.dispose();
		}
	}

	public void reload(String fileName) {
		try {
			// Update every info
			this.image.updateFilename(fileName);
			this.updateTitleNoChange();
			this.updateInfoPanel();
			this.repaint();
			this.viewerPanel.setDefaultZoomLevel();
		} catch (NjivLoadingException e) {
			System.out.println("[x] Error while reloading file : "+e.getLocalizedMessage());
			JOptionPane.showMessageDialog(null,
					"Error while reloading file : "+e.getLocalizedMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			this.dispose();
		}
	}
	/**
	 * Close the viewer
	 */
	public void close() {
		System.out.println("[i] Closing viewer...");
		this.setVisible(false);
		if(this.onCloseListener != null)
			this.onCloseListener.onChange();
		this.dispose();
	}

	@Override
	public void onChange() {
		this.updateTitle();
		this.viewerPanel.repaint();
	}
	
	public boolean isReady() {
		return this.isReady;
	}
	
	public void setCloseListener(NjivCaller caller) {
		this.onCloseListener = caller;
	}

}
