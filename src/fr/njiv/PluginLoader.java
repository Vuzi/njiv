package fr.njiv;

import java.util.ArrayList;
import java.util.List;

import fr.njiv.UI.imageViewer.plugin.NjivImageViewerPlugin;

public class PluginLoader {

	public static List<NjivImageViewerPlugin> imageViewerPlugins;
	
	public static void init() {
		// Image viewer
		try {
			imageViewerPlugins = new NjivPluginLoader<NjivImageViewerPlugin>(NjivImageViewerPlugin.class, "plugins/imageViewer").loadPlugins();
		} catch (Exception e) {
			System.out.println("[x] Error while loading plugins for image viewer : "+e.getLocalizedMessage());
			e.printStackTrace();
			imageViewerPlugins = new ArrayList<NjivImageViewerPlugin>();
		}
		
		// Diaporama transitions
		
		// Catalogue types
	}
	
}
