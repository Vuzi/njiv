package fr.njiv.plugin;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import javax.swing.JPanel;

import fr.njiv.image.NjivImage;

import fr.njiv.image.NjivImageModificator;

public class BlurModifier implements NjivImageModificator {
	@Override
	public String getDesc() {
		return "Perform a blur on the image";
	}

	@Override
	public String getName() {
		return "Blur";
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
		
	    float[] matrix = {
	        0.2f, 0.2f, 0.2f, 
	        0.2f, 0.2f, 0.2f, 
	        0.2f, 0.2f, 0.2f
        };

        BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
    	return op.filter(image, null);
	}

}
