package fr.njiv.UI.imageViewer;

import java.util.HashMap;

import javax.swing.JPanel;

public abstract class ImageViewerModifierPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3242134073839088553L;

	/**
	 * This method should sum all the options in the panel, and return an HashMap containing
	 * everything. The result will be used as the options for the modification of the image.
	 * 
	 * @return The sum of all the panel's options 
	 */
	abstract HashMap<String, Object> getOptions();
	
}
