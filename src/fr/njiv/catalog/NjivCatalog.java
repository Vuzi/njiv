package fr.njiv.catalog;

import java.util.List;

import javax.swing.JPanel;

import fr.njiv.image.NjivImage;

public interface NjivCatalog {

	/**
	 * Return the name of the catalogue
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the description of the catalogue
	 * @return
	 */
	public String getDescription();
	
	/**
	 * If true, then the catalogue use a panel for catalogue options
	 * @return
	 */
	public boolean hasPanel();
	
	/**
	 * Return the catalogue option panel
	 * @return
	 */
	public JPanel catalogueOptionPanel();
	
	/**
	 * Generate the catalogue with the provided list of images
	 * @param images
	 */
	public void generateCatalogue(List<NjivImage> images);
	
}
