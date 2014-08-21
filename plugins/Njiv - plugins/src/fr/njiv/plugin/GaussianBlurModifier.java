package fr.njiv.plugin;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.swing.JPanel;

import fr.njiv.image.NjivImage;
import fr.njiv.image.NjivImageModificator;

public class GaussianBlurModifier implements NjivImageModificator {

	@Override
	public String getDesc() {
		return "Perform a gaussian blur on the image";
	}

	@Override
	public String getName() {
		return "Gaussian blur";
	}

	@Override
	public JPanel getPanel(NjivImage arg0) {
		return null;
	}

	@Override
	public boolean hasPanel() {
		return false;
	}

	@Override
	public BufferedImage modifyImage(BufferedImage image) {
		
        BufferedImageOp op = Blur.getGaussianBlurFilter(5, true);
    	return op.filter(image, null);
		
	}

}
