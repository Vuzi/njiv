package fr.njiv.UI.imageViewer.plugin;

import java.util.List;

import fr.njiv.image.NjivImageModificator;

public interface NjivImageViewerPlugin {
	
	/**
	 * Return the name of the plugin
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the description of the plugin
	 * @return
	 */
	public String getDescription();

	/**
	 * Return the modifiers from the plugin. If null or empty list is
	 * returned, no modifier will be added.
	 * @return
	 */
	public List<NjivImageModificator> getModifiers();
	
}
