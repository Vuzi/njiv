package fr.njiv.image.modifier;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.HashMap;

import fr.njiv.Utils;
import fr.njiv.UI.imageViewer.ImageViewerModifierPanel;
import fr.njiv.image.NjivImage;
import fr.njiv.image.NjivImageModificator;

public class ModifierBlackAndWhite implements NjivImageModificator {

	@Override
	public BufferedImage modifyImage(BufferedImage image,
			HashMap<String, Object> options) {
		
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
	public ImageViewerModifierPanel modifierPanel(NjivImage image) {
		return null;
	}

}
