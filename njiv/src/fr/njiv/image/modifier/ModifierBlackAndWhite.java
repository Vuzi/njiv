package fr.njiv.image.modifier;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.swing.JPanel;

import fr.njiv.Utils;
import fr.njiv.image.NjivImage;
import fr.njiv.image.NjivImageModificator;

public class ModifierBlackAndWhite implements NjivImageModificator {

	@Override
	public BufferedImage modifyImage(BufferedImage image) {
		
		BufferedImage bi = Utils.cloneBufferedImage(image);
		ColorConvertOp op =  new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		return op.filter(bi, bi);
	}

	@Override
	public String getName() {
		return "black and white";
	}

	@Override
	public String getDesc() {
		return "Convert the color of the image to shades of greys";
	}

	@Override
	public boolean hasPanel() {
		return false;
	}

	@Override
	public JPanel getPanel(NjivImage image) {
		return null;
	}

}
