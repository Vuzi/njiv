package fr.njiv.plugin;

import java.util.ArrayList;
import java.util.List;

import fr.njiv.catalog.NjivCatalog;
import fr.njiv.catalog.NjivCatalogPlugin;

public class CatalogsPack1 implements NjivCatalogPlugin {

	@Override
	public List<NjivCatalog> getCatalogs() {
		ArrayList<NjivCatalog> list = new ArrayList<NjivCatalog>();
		list.add(new PDFCatalog());
		list.add(new ImgurCatalog());
		return list;
	}

	@Override
	public String getDescription() {
		return "PDF Catalog generator";
	}

	@Override
	public String getName() {
		return "PDF Catalog generator pack";
	}

}
