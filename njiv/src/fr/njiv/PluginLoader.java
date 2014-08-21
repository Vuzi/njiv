package fr.njiv;

import java.util.ArrayList;
import java.util.List;

import fr.njiv.UI.diaporama.plugin.NjivDiaporamaPlugin;
import fr.njiv.UI.imageViewer.plugin.NjivImageViewerPlugin;

public class PluginLoader {

	public static List<NjivImageViewerPlugin> imageViewerPlugins;
	public static List<NjivDiaporamaPlugin> diaporamaPlugins;
	
	public static void init() {
		// Image viewer
		System.out.println("[i] Loading image viewer plugins");
		try {
			imageViewerPlugins = new NjivPluginLoader<NjivImageViewerPlugin>(NjivImageViewerPlugin.class, "plugins/imageViewer").loadPlugins();
		} catch (Exception e) {
			System.out.println("[x] Error while loading plugins for image viewer : "+e.getLocalizedMessage());
			imageViewerPlugins = new ArrayList<NjivImageViewerPlugin>();
		}
		
		// Diaporama
		System.out.println("[i] Loading diaporama plugins");
		try {
			diaporamaPlugins = new NjivPluginLoader<NjivDiaporamaPlugin>(NjivDiaporamaPlugin.class, "plugins/diaporama").loadPlugins();
		} catch (Exception e) {
			System.out.println("[x] Error while loading plugins for diaporama : "+e.getLocalizedMessage());
			diaporamaPlugins = new ArrayList<NjivDiaporamaPlugin>();
		}
		
		// Catalogs types
		System.out.println("[i] Loading catalogs plugins");
	}
	
}
