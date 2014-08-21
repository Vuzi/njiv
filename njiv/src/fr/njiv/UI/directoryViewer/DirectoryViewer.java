package fr.njiv.UI.directoryViewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.json.exceptions.JSONParsingException;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

import fr.njiv.NjivLoadingException;
import fr.njiv.OptionPanel;
import fr.njiv.PluginLoader;
import fr.njiv.UI.UIStyle;
import fr.njiv.UI.VerticalWrapFlowLayout;
import fr.njiv.UI.diaporama.Diaporama;
import fr.njiv.UI.diaporama.DiaporamaOptions;
import fr.njiv.UI.diaporama.NjivDiaporamaTransition;
import fr.njiv.UI.diaporama.plugin.NjivDiaporamaPlugin;
import fr.njiv.UI.diaporama.transition.CrossfadingEffect;
import fr.njiv.UI.diaporama.transition.ToBlackEffect;
import fr.njiv.catalog.CatalogPanel;
import fr.njiv.catalog.NjivCatalog;
import fr.njiv.catalog.PDFCatalog;
import fr.njiv.directory.NjivDirectory;
import fr.njiv.image.NjivImage;

/**
 * Directory viewer
 * @author Vuzi
 *
 */
public class DirectoryViewer extends JFrame {

	private static final long serialVersionUID = 4185992928939434505L;
	
	private TitledBorder jtreeBorder;
	private JSplitPane splitPane;
	
	private NjivDirectory treeSource;
	private DefaultMutableTreeNode treeRoot;
	private JTree tree;
	
	private int previewSize = 130;
	private JPanel scrollable;
	
	private ArrayList<ImageAdvancedThumbnail> thumbnails;

	// Catalogues
	private NjivCatalog catalogs[] = {
			new PDFCatalog()
	};
	
	private CatalogPanel catalogPanel;
	
	// Diaporama transitions
	private NjivDiaporamaTransition transitions[] = {
			new CrossfadingEffect(),
			new ToBlackEffect()
	};
	
	private DiaporamaOptions diaporamaOptions;

	/**
	 * Element of the tree
	 * @author vuzi
	 *
	 */
	private class treeElement {
		
		public String path;
		public String name;
		public boolean isImage;
		public NjivDirectory directory;
		
		public treeElement(String path, String name, boolean isImage) {
			this.path = path;
			this.name = name;
			this.isImage = isImage;
			this.directory = null;
		}

		public treeElement(String path, String name, boolean isImage, NjivDirectory directory) {
			this.path = path;
			this.name = name;
			this.isImage = isImage;
			this.directory = directory;
		}
		
		public String toString() {
			return this.name;
		}
	}

	/**
	 * Create the application.
	 */
	public DirectoryViewer(String path) {
		super();

		System.out.println("[i] Opening directory viewer for '"+path+"'");
		initializeTreeSource(path);
		initialize();
		initializePlugins();
		updateTree();
		
		thumbnails = new ArrayList<ImageAdvancedThumbnail>();
	}

	/**
	 * Show the viewer with no directory open
	 */
	public DirectoryViewer() {
		super();

		System.out.println("[i] Opening directory viewer with no path");
		initialize();
		initializePlugins();
		
		thumbnails = new ArrayList<ImageAdvancedThumbnail>();
	}
	
	/**
	 * Change the tree path
	 * @param path
	 */
	public void changePath(String path) {
		initializeTreeSource(path);
		updateTree();
	}
	
	public void closePath() {
		tree.setVisible(false);
		setTitle("Njiv - Directory viewer");
	}

	/**
	 * Initialize the diaporama plugins
	 */
	private void initializePlugins() {
		if(PluginLoader.diaporamaPlugins.size() > 0) {
			ArrayList<NjivDiaporamaTransition> list = new ArrayList<NjivDiaporamaTransition>();
			
			// old values
			for(NjivDiaporamaTransition transition : transitions ) {
				list.add(transition);
			}
			
			// plugins
			for(NjivDiaporamaPlugin plugin : PluginLoader.diaporamaPlugins) {
				for(NjivDiaporamaTransition transition : plugin.getTransitions() ) {
					list.add(transition);
				}
			}
			
			transitions = list.toArray(transitions);
		}
	}

	/**
	 * Initialize the source of the tree. If the source cannot be read, then the viewer is stopped
	 * @param path
	 */
	private void initializeTreeSource(String path) {
		NjivDirectory oldSource = treeSource;
		try {
			treeSource = new NjivDirectory(path);
		} catch (NjivLoadingException e) {
			JOptionPane.showMessageDialog(this,
				    "Error while reading directory : "+e.getLocalizedMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			if(oldSource != null) {
				treeSource = oldSource;
			} else {
				closePath();
			}
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		if(this.treeSource != null)
			this.setTitle("Njiv - "+this.treeSource.getPath());
		else
			this.setTitle("Njiv - Directory viewer");
			
		this.setIconImage(UIStyle.logo);
		this.setBounds(100, 100, 528, 433);
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.out.println("[i] Closing directory viewer");
				if(diaporamaOptions != null)
					diaporamaOptions.getOptionPanel().dispose();
				if(catalogPanel != null)
					catalogPanel.dispose();
				dispose();
			}
		});
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		initializeLeftPanel();
		initializeRightPanel();
		initializeMenuBar();
		
		this.pack();
	}

	/**
	 * Initialiaze the left panel (with the thumbnails)
	 */
	private void initializeLeftPanel() {

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 10, 5, 10));
		panel.setPreferredSize(new Dimension(400, 600));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		scrollable = new JPanel();
		scrollable.setBorder(new EmptyBorder(5, 5, 5, 5));
		JScrollPane scrollPane = new JScrollPane(scrollable);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(450, 150));
		scrollPane.setMinimumSize(new Dimension(200, 150));
		scrollable.setBackground(new Color(255, 255, 255));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		FlowLayout fl_scrollable = new FlowLayout(FlowLayout.LEFT, 5, 5);
		fl_scrollable.setAlignOnBaseline(true);
		scrollable.setLayout(new VerticalWrapFlowLayout(FlowLayout.LEFT, 5, 5));
		
		splitPane.setLeftComponent(scrollPane);
	}

	/**
	 * Initialize the right panel
	 */
	private void initializeRightPanel() {
		
		JPanel rightPanel = new JPanel();
		rightPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		splitPane.setRightComponent(rightPanel);
		rightPanel.setLayout(new BorderLayout(0, 0));

		
		JPanel treePanel = new JPanel();
		jtreeBorder = new TitledBorder(null, "Folder tree", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		treePanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane(treePanel);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new TitledBorder(new LineBorder(new Color(175, 175, 171)), "Folder tree", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(50, 65, 105)));
		rightPanel.add(scrollPane, BorderLayout.CENTER);
		
		if(treeSource != null)
			treeRoot = new DefaultMutableTreeNode(treeSource.getPath());
		else
			treeRoot = new DefaultMutableTreeNode("");
			
		DefaultTreeModel myModel = new DefaultTreeModel(treeRoot);
		tree = new JTree(myModel);
		tree.setShowsRootHandles(true);

		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if(selPath != null) {
					treeElement elem = (treeElement) ((DefaultMutableTreeNode)selPath.getLastPathComponent()).getUserObject();
					
					// Right click
					if(e.getButton() == 3) {
						
						JPopupMenu rightClickMenu = new JPopupMenu();

						if(elem.isImage) {
							JMenuItem loadImg = new JMenuItem("Load the image");
							loadImg.addMouseListener(new MouseAdapter() {
								public void mouseReleased(MouseEvent e) {
									loadTreeElemImage(elem);
								}
							});
							rightClickMenu.add(loadImg);
						} else {
							JMenuItem loadDirContent = new JMenuItem("Load the content of the directory");
							loadDirContent.addMouseListener(new MouseAdapter() {
								public void mouseReleased(MouseEvent e) {
									loadTreeElemDir(elem);
								}
							});
							rightClickMenu.add(loadDirContent);

							JMenuItem loadDir = new JMenuItem("Open in the viewer");
							loadDir.addMouseListener(new MouseAdapter() {
								public void mouseReleased(MouseEvent e) {
									changePath(elem.path);
								}
							});
							rightClickMenu.add(loadDir);
						}

						rightClickMenu.show(e.getComponent(), e.getX(), e.getY()); 
					}
					// Left double-click
					else if(e.getButton() == 1 && e.getClickCount() == 2) {
						if(elem.isImage)
							loadTreeElemImage(elem);
					}
				}
			}
		});
		
		tree.expandRow(5);
		
		if(treeSource == null)
			tree.setVisible(false);
		
		treePanel.add(tree);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel.getLayout();
		flowLayout_2.setHgap(0);
		rightPanel.add(panel, BorderLayout.NORTH);
		
		JPanel btnPanel = new JPanel();
		panel.add(btnPanel);
		btnPanel.setBorder(null);
		GridBagLayout gbl_btnPanel = new GridBagLayout();
		gbl_btnPanel.columnWidths = new int[]{285, 0};
		gbl_btnPanel.rowHeights = new int[] {0, 0, 0};
		gbl_btnPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_btnPanel.rowWeights = new double[]{0.0, 0.0, 1.0};
		btnPanel.setLayout(gbl_btnPanel);
		
		JPanel diaporamaPanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) diaporamaPanel.getLayout();
		flowLayout_1.setVgap(0);
		diaporamaPanel.setBorder(null);
		GridBagConstraints gbc_diaporamaPanel = new GridBagConstraints();
		gbc_diaporamaPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_diaporamaPanel.insets = new Insets(0, 0, 5, 0);
		gbc_diaporamaPanel.gridx = 0;
		gbc_diaporamaPanel.gridy = 0;
		btnPanel.add(diaporamaPanel, gbc_diaporamaPanel);
		
		JButton launchDiaporamaBtn = new JButton("Launch diaporama");
		launchDiaporamaBtn.setPreferredSize(new Dimension(135, 25));
		diaporamaPanel.add(launchDiaporamaBtn);
		
		JButton diaporamaOptionBtn = new JButton("Diaporama options");
		diaporamaOptionBtn.setPreferredSize(new Dimension(135, 25));
		diaporamaPanel.add(diaporamaOptionBtn);
		diaporamaOptionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDiaporamaOptions();
			}
		});
		launchDiaporamaBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				launchDiaporama();
			}
		});
		
		JPanel catalogueCreatorPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) catalogueCreatorPanel.getLayout();
		flowLayout.setVgap(0);
		catalogueCreatorPanel.setBorder(null);
		GridBagConstraints gbc_catalogueCreatorPanel = new GridBagConstraints();
		gbc_catalogueCreatorPanel.insets = new Insets(0, 0, 5, 0);
		gbc_catalogueCreatorPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_catalogueCreatorPanel.gridx = 0;
		gbc_catalogueCreatorPanel.gridy = 1;
		btnPanel.add(catalogueCreatorPanel, gbc_catalogueCreatorPanel);
		
		JButton btnCatalogueCreator = new JButton("Catalogue creator");
		btnCatalogueCreator.setPreferredSize(new Dimension(275, 25));
		btnCatalogueCreator.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				showCatalogPanel();
			}
		});
		catalogueCreatorPanel.add(btnCatalogueCreator);
	}

	/**
	 * Initialize the menu bar
	 */
	private void initializeMenuBar() {
		
		DirectoryViewer me = this;
		
		JMenuBar menuBar = new JMenuBar();
		this.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		// File
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem itemOpenNewDir = new JMenuItem("Open a new directory");
		itemOpenNewDir.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				JFileChooser chooser = new JFileChooser(new File("."));
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String choosen = chooser.getSelectedFile().getAbsolutePath();
	
					if(choosen != null) {
						changePath(choosen);
					}
				}
			}
		});
		mnFile.add(itemOpenNewDir);
		
		JMenuItem itemCloseDir = new JMenuItem("Close the directory");
		itemCloseDir.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				closePath();
			}
		});
		mnFile.add(itemCloseDir);
		
		// Diaporama
		JMenu mnDiaporama = new JMenu("Diaporama");
		menuBar.add(mnDiaporama);
		
		JMenuItem itemShowDiaporama = new JMenuItem("Show diaporama");
		itemShowDiaporama.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				launchDiaporama();
			}
		});
		mnDiaporama.add(itemShowDiaporama);
		
		JMenuItem itemShowDiaporamaOption = new JMenuItem("Diaporama options");
		itemShowDiaporamaOption.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				showDiaporamaOptions();
			}
		});
		mnDiaporama.add(itemShowDiaporamaOption);
		
		JSeparator separator = new JSeparator();
		mnDiaporama.add(separator);
		
		JMenuItem itemSaveDiaporama = new JMenuItem("Save diaporama");
		itemSaveDiaporama.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				JFileChooser chooser = new JFileChooser(new File("."));
			    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			    chooser.setFileFilter(new FileNameExtensionFilter("Njiv diaporama format (*.diapo)", ".diapo", ".diapo"));
	
				if (chooser.showSaveDialog(me) == JFileChooser.APPROVE_OPTION) {
					String choosen = chooser.getSelectedFile().getAbsolutePath();
	
					if(choosen != null) {
						saveState(choosen);
					}
				}
				
			}
		});
		mnDiaporama.add(itemSaveDiaporama);
		
		JMenuItem itemLoadDiaporama = new JMenuItem("Load diaporama");
		itemLoadDiaporama.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				JFileChooser chooser = new JFileChooser(new File("."));
			    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			    chooser.setFileFilter(new FileNameExtensionFilter("Njiv diaporama format (*.diapo)", ".diapo", "json"));
	
				if (chooser.showOpenDialog(me) == JFileChooser.APPROVE_OPTION) {
					String choosen = chooser.getSelectedFile().getAbsolutePath();
	
					if(choosen != null) {
						loadState(choosen);
					}
				}
				
			}
		});
		mnDiaporama.add(itemLoadDiaporama);
		
		// Catalogue
		JMenu catalogueMenu = new JMenu("Catalog");
		menuBar.add(catalogueMenu);
		
		JMenuItem itemShowCatalog = new JMenuItem("Catalog generator");
		itemShowCatalog.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				showCatalogPanel();
			}
		});
		catalogueMenu.add(itemShowCatalog);

		// Options
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenuItem itemShowOptions = new JMenuItem("Show the option panel");
		itemShowOptions.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				new OptionPanel().setVisible(true);
			}
		});
		mnOptions.add(itemShowOptions);

	}
	
	@SuppressWarnings("unchecked")
	private void loadState(String choosen) {		
		InputStream is;
		
	try {
        is = new FileInputStream(choosen);

		JsonParserFactory factory = JsonParserFactory.getInstance();
		JSONParser parser = factory.newJsonParser();
		Map<String, Object> options = (Map<String, Object>)parser.parseJson(is, "utf8");

		if(diaporamaOptions == null)
			diaporamaOptions = new DiaporamaOptions(Arrays.asList(transitions));

		// Options
		if(options.containsKey("use_infinite")) {
			if(options.get("use_infinite").equals("true"))
				diaporamaOptions.setInfinite(true);
			else
				diaporamaOptions.setInfinite(false);
		}
		
		if(options.containsKey("use_random")) {
			if(options.get("use_random").equals("true"))
				diaporamaOptions.setRandom(true);
			else
				diaporamaOptions.setRandom(false);
		}
		
		if(options.containsKey("time_frame")) {
			Integer timer = new Integer((String)options.get("time_frame"));
			diaporamaOptions.setTimer(timer);
		}
		
		// Transitions
		if(options.containsKey("transitions")) {
			
			// Everything to false
			for(Entry<NjivDiaporamaTransition, Boolean> entry : diaporamaOptions.getTransitionsUsage().entrySet()) {
				diaporamaOptions.getTransitionsUsage().put(entry.getKey(), false);
			}
			
			// Set the selected to true
			List<String> transitionsUsed = (List<String>) options.get("transitions");
			List<NjivDiaporamaTransition> transitions = diaporamaOptions.getTransitions();
			for(String transitionUsed : transitionsUsed) {
				for(NjivDiaporamaTransition transition : transitions) {
					if(transition.getName().equals(transitionUsed)) {
						diaporamaOptions.getTransitionsUsage().put(transition, true);
					}
				}
			}
		}
		
		diaporamaOptions.getOptionPanel(true);
		
		// Images
		if(options.containsKey("images")) {
			List<String> images = (List<String>) options.get("images");
			
			for(String image : images) {
				this.loadImage(image);
			}
		}
		
        is.close();
		
	    } catch (FileNotFoundException e) {
			System.out.println("[x] Configuration file not found : "+e.getLocalizedMessage());
			JOptionPane.showMessageDialog(this,
				    "Error while loading : "+e.getLocalizedMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
	    } catch (IOException e) {
			System.out.println("[x] Can't read configuration file : "+e.getLocalizedMessage());
			JOptionPane.showMessageDialog(this,
				    "Error while loading : "+e.getLocalizedMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
	    } catch (JSONParsingException e) {
			System.out.println("[x] Can't parse configuration file : "+e.getLocalizedMessage());
			JOptionPane.showMessageDialog(this,
				    "Error while loading : "+e.getLocalizedMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	protected void saveState(String choosen) {
		
		System.out.println("[i] State save start...");

		try {
			File file = new File(choosen);
			 
			// if file doesnt exists, then create it
			if (file.exists())
				file.delete();
			file.createNewFile();

			// open the buffered writer
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			bw.write("// Auto generated by Njiv the "+new Date().toString()+"\n{\n");
			int i = 1;
			
			if(thumbnails.size() > 0) {
				// Images
				bw.write("    \"images\": [\n");
				for(ImageAdvancedThumbnail image : thumbnails) {
					if(i == thumbnails.size())
						bw.write("        \""+image.getImage().getPath()+"\"\n");
					else
						bw.write("        \""+image.getImage().getPath()+"\",\n");
					
					i++;
				}
				
				bw.write("    ],\n");
			}
			
			if(diaporamaOptions == null)
				diaporamaOptions = new DiaporamaOptions(Arrays.asList(transitions));
			
			// Diapo options
			bw.write("    \"use_infinite\" : "+diaporamaOptions.useInfinite()+",\n");
			bw.write("    \"use_random\" : "+diaporamaOptions.useRandom()+",\n");
			bw.write("    \"time_frame\" : "+diaporamaOptions.getTimer());
			
			// Transitions to use
			
			if(diaporamaOptions.getTransitionsToUse().size() > 0) {
				bw.write(",\n    \"transitions\" : [\n");
				i = 1;
				for(NjivDiaporamaTransition transitions : diaporamaOptions.getTransitionsToUse()) {
					if(i == diaporamaOptions.getTransitionsToUse().size())
						bw.write("        \""+transitions.getName()+"\"\n");
					else
						bw.write("        \""+transitions.getName()+"\",\n");
					
					i++;
				}
				bw.write("    ]");
			}
			
			bw.write("\n}\n");
			bw.close();
			
			System.out.println("[i] State save done !");
			
		} catch (IOException e) {
			System.out.println("[i] Error while saving : "+e.getLocalizedMessage());
			JOptionPane.showMessageDialog(this,
				    "Error while saving : "+e.getLocalizedMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Load an image based on the tree element
	 * @param elem
	 */
	private void loadTreeElemImage(treeElement elem) {
		loadImage(elem.path);
	}

	/**
	 * Load a directory based on the tree element
	 * @param elem
	 */
	private void loadTreeElemDir(treeElement elem) {
		for(File file : elem.directory.getImages()) {
			loadImage(file.getPath());
		}
	}
	
	/**
	 * Load an image with its path
	 * @param path
	 */
	public void loadImage(String path) {
		System.out.println("[i] Selected '"+path+"', opening it..");
		try {
			ImageAdvancedThumbnail iat = new ImageAdvancedThumbnail(path, previewSize, this);
			thumbnails.add(iat);
			scrollable.add(iat);
			splitPane.revalidate();
		} catch (NjivLoadingException e) {
			System.out.println("[x] Error while loading image : "+e.getLocalizedMessage());
		}
	}
	
	/**
	 * Load an image already load in memory
	 * @param image The image to add
	 */
	public void loadImage(NjivImage image) {
		System.out.println("[i] Selected '"+image.getPath()+"', opening it..");
		ImageAdvancedThumbnail iat = new ImageAdvancedThumbnail(image, previewSize, this);
		thumbnails.add(iat);
		scrollable.add(iat);
		splitPane.revalidate();
	}
	
	/**
	 * Initialize the file tree
	 * @param path
	 */
	private void updateTree() {
		
		this.setTitle("Njiv - "+this.treeSource.getPath());
		
		// Empty
		treeRoot = new DefaultMutableTreeNode(treeSource.getPath());
		tree.setModel(new DefaultTreeModel(treeRoot));
		
		treeRoot.removeAllChildren();
		tree.setRootVisible(true);
		tree.setVisible(true);
		
		// Directories
		for(NjivDirectory nd : treeSource.getSubDirectories()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new treeElement(nd.getFile().getAbsolutePath(), nd.getFile().getName(), false, nd));
			treeNode(node, nd);
			treeRoot.add(node);
		}
		
		// Files
		for(File f : treeSource.getImages()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new treeElement(f.getAbsolutePath(), f.getName(), true));
			treeRoot.add(node);
		}
		
		tree.expandRow(0);
		tree.setRootVisible(false);

		jtreeBorder.setTitle("Folder tree  -  "+this.treeSource.getPath());
	}
	
	/**
	 * Internal initialization of the tree
	 * @param node
	 * @param source
	 */
	private void treeNode(DefaultMutableTreeNode node, NjivDirectory source) {
		// Directories
		for(NjivDirectory nd : source.getSubDirectories()) {
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new treeElement(nd.getFile().getAbsolutePath(), nd.getFile().getName(), false, nd));
			treeNode(newNode, nd);
			node.add(newNode);
		}
		
		// Files
		for(File f : source.getImages()) {
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new treeElement(f.getAbsolutePath(), f.getName(), true));
			node.add(newNode);
		}
	}
	
	/**
	 * Return the list of the thumbnails
	 * @return
	 */
	public ArrayList<ImageAdvancedThumbnail> getThumbnails() {
		return this.thumbnails;
	}
	
	/**
	 * Delete the thumbnails
	 * @param i
	 */
	public void deleteThumbnails(int i) {
		if(i >= 0 && i < thumbnails.size()) {
			scrollable.remove(thumbnails.remove(i));
			
			this.validate();
			repaint();
		}
	}
	
	/**
	 * Switch two thumbnails
	 * @param a First index
	 * @param b Second index
	 */
	public void switchThumbnails(int a, int b) {
		if(b >= 0 && b < thumbnails.size()) {
			scrollable.remove(a);
			scrollable.add(thumbnails.get(a), b);
			
			thumbnails.get(a).hideControlButtons();
		    Collections.swap(thumbnails, a, b);
		    
			this.validate();
			repaint();
		}
	}
	
	/**
	 * Return the images selected
	 * @return
	 */
	public List<NjivImage> getImages() {
		 ArrayList<NjivImage> list = new ArrayList<>();
		 for(ImageAdvancedThumbnail thumbnail : thumbnails) {
			 list.add(thumbnail.getImage());
		 }
		 return list;
	}
	
	private void launchDiaporama() {
		if(diaporamaOptions == null)
			diaporamaOptions = new DiaporamaOptions(Arrays.asList(transitions));
		new Diaporama(diaporamaOptions, getImages());
	}
	
	private void showDiaporamaOptions() {
		if(diaporamaOptions == null)
			diaporamaOptions = new DiaporamaOptions(Arrays.asList(transitions));
		diaporamaOptions.getOptionPanel().setVisible(true);
	}
	
	private void showCatalogPanel() {
		if(catalogPanel == null)
			catalogPanel = new CatalogPanel(Arrays.asList(catalogs), this);
		catalogPanel.setVisible(true);
	}
}
