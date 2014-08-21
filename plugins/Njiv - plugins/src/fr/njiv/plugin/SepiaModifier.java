package fr.njiv.plugin;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import fr.njiv.Utils;
import fr.njiv.image.NjivImage;
import fr.njiv.image.NjivImageModificator;

public class SepiaModifier implements NjivImageModificator {

	@Override
	public String getDesc() {
		return "Transform the image to a sepia version";
	}

	@Override
	public String getName() {
		return "Sepia";
	}

	@Override
	public JPanel getPanel(NjivImage img) {
		return null;
	}

	@Override
	public boolean hasPanel() {
		return false;
	}

	@Override
	public BufferedImage modifyImage(BufferedImage img) {
		img = Utils.cloneBufferedImage(img);
		Sepia.apply(img, 20);
		return img;
	}

}
