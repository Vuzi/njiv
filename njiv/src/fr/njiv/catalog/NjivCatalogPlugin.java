package fr.njiv.catalog;

import java.util.List;

public interface NjivCatalogPlugin {
	
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
	 * Return the catalogs from the plugin
	 * @return
	 */
	public List<NjivCatalog> getCatalogs();
	
}
