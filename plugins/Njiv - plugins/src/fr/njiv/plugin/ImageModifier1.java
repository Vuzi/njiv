package fr.njiv.plugin;

import java.util.ArrayList;
import java.util.List;

import fr.njiv.UI.imageViewer.plugin.NjivImageViewerPlugin;
import fr.njiv.image.NjivImageModificator;

public class ImageModifier1 implements NjivImageViewerPlugin {

	@Override
	public String getDescription() {
		return "Pack of image modifier";
	}

	@Override
	public List<NjivImageModificator> getModifiers() {
		ArrayList<NjivImageModificator> list = new ArrayList<>();
		list.add(new SepiaModifier());
		list.add(new SepiaModifierWithPanel());
		list.add(new BlurModifier());
		list.add(new GaussianBlurModifier());
		list.add(new GaussianBlurModifierWithPanel());
		return list;
	}

	@Override
	public String getName() {
		return "Image modifier pack 1";
	}

}
