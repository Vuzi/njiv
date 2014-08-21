package fr.njiv.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fr.njiv.Options;

public class UIStyle {

	// Font used by the program
	public static Font fontLargeLight = new Font("Open Sans Light", Font.PLAIN, 22);
	public static Font fontRegularLight = new Font("Open Sans Light", Font.PLAIN, 15);
	public static Font fontSmallLight = new Font("Open Sans", Font.PLAIN, 11);
	public static Font fontRegular = new Font("Open Sans", Font.PLAIN, 14);

	// Color used by the program
	public static Color lightGray = new Color(250, 250, 250);
	public static Color darkGray = new Color(220, 220, 220);
	public static Color lighterGray = new Color(240, 240, 240);
	
	// Image used by the program
	public static ImageIcon loading;
	public static ImageIcon cross;
	public static ImageIcon left;
	public static ImageIcon right;
	public static ImageIcon mglass;
	
	public static Image logo;
	
	public static void init()  {
		
		// Icons
		try {
			logo = ImageIO.read(fr.njiv.UI.UIStyle.class.getResource("res/glass.png"));
			loading = new ImageIcon(fr.njiv.UI.UIStyle.class.getResource("res/loading.gif"));
			cross = new ImageIcon(fr.njiv.UI.UIStyle.class.getResource("res/cross.png"));
			left = new ImageIcon(fr.njiv.UI.UIStyle.class.getResource("res/left.png"));
			right = new ImageIcon(fr.njiv.UI.UIStyle.class.getResource("res/right.png"));
			mglass = new ImageIcon(fr.njiv.UI.UIStyle.class.getResource("res/glass.png"));
		} catch (Exception e) {
			System.out.println("[x] Error while loading icons and images");
			e.printStackTrace();
		}
		
		// Use system
		if(Options.all.containsKey("UI_style") && Options.all.get("UI_style").equals("system")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException e) {
				System.out.println("[x] Error while loading system look and feel, using the regular...");
				e.printStackTrace();
			}
		} 
		// Use modified Java interface
		else {
			// Couleur focus
			UIManager.put("Button.focus", Color.ORANGE);
			
			// SplitPan
			UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder(0, 0, 0, 0));
			
			// Button
			UIManager.getDefaults().put("Button.font", fontSmallLight);
			UIManager.getDefaults().put("Button.background", Color.WHITE);
			UIManager.getDefaults().put("Button.selectionBackground", lighterGray);
			
			// Label
			UIManager.getDefaults().put("Label.font", fontRegular);
			
			// Tooltip
			UIManager.getDefaults().put("ToolTip.background", Color.WHITE);
			UIManager.getDefaults().put("ToolTip.border", BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			UIManager.getDefaults().put("ToolTip.font", fontRegular);
			
			// MenuBar
			UIManager.getDefaults().put("MenuBar.background", fontSmallLight);
			UIManager.getDefaults().put("MenuBar.border", BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
			
			// Menu
			UIManager.getDefaults().put("Menu.font", fontSmallLight);
			UIManager.getDefaults().put("Menu.selectionBackground", darkGray);
			UIManager.getDefaults().put("Menu.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));
			
			// MenuItem
			UIManager.getDefaults().put("MenuItem.font", fontSmallLight);
			UIManager.getDefaults().put("MenuItem.background", Color.WHITE);
			UIManager.getDefaults().put("MenuItem.acceleratorSelectionForeground ", Color.WHITE);
			UIManager.getDefaults().put("MenuItem.selectionBackground", lighterGray);
			UIManager.getDefaults().put("MenuItem.border", BorderFactory.createEmptyBorder());
			
			// Checkbox
			UIManager.getDefaults().put("CheckBox.font", fontSmallLight);
	
			// PopupMenu
			UIManager.getDefaults().put("PopupMenu.border", BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(230, 230, 230)));
			
			// Titled panel
			UIManager.getDefaults().put("TitledBorder.font", fontSmallLight);
		}
	}
}
