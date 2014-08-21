package fr.njiv.image;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import fr.njiv.UI.imageViewer.ImageViewerModifierPanel;

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
	public abstract BufferedImage modifyImage(BufferedImage image, HashMap<String, Object> options);
	
	/**
	 * Return the name of the modification, used for the right-click menu and toolbar menu
	 * @return The name of the modification
	 */
	public abstract String getName();
	
	public abstract String getDesc();
	
	/**
	 * Return the panel of the modifier. This panel should be a ImageViewerModifierPanel and thus have
	 * a method to get the options, wich will be used later in the modifyImage method.
	 * If this method return null, then the modifyImage will be called without any options.
	 * Note that the "Apply" and "Cancel" button will be add automaticaly.
	 * @return The panel of the modifier
	 */
	public abstract ImageViewerModifierPanel modifierPanel(NjivImage image);
	
}
