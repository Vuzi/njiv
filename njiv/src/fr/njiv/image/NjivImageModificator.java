package fr.njiv.image;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * 
 * @author Vuzi
 *
 */
public interface NjivImageModificator {

	/**
	 * Image modifier. This method take the image and an hashmap containing the options of
	 * the modification, and return the new version of the image.
	 * @param image The BufferedImage to be modified
	 * @param options The options of the modifier
	 * @return The modified image
	 */
	public abstract BufferedImage modifyImage(BufferedImage image);
	
	/**
	 * Return the name of the modification, used for the right-click menu and toolbar menu
	 * @return The name of the modification
	 */
	public abstract String getName();
	
	/**
	 * Return the description of the modification.
	 * @return
	 */
	public abstract String getDesc();
	
	/**
	 * Return true if the modifier need a panel, false otherwise
	 * @return
	 */
	public abstract boolean hasPanel();
	
	/**
	 * Return the panel of the modifier.
	 * Note that the "Apply" and "Cancel" button will be add automaticaly.
	 * @return The panel of the modifier
	 */
	public abstract JPanel getPanel(NjivImage image);
	
}
