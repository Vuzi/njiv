package fr.njiv.image.modifier;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import fr.njiv.UI.imageViewer.ImageViewerModifierPanel;
import fr.njiv.image.NjivImage;
import fr.njiv.image.NjivImageModificator;

public class ModifierRotate implements NjivImageModificator {

	@Override
	public BufferedImage modifyImage(BufferedImage image,
			HashMap<String, Object> options) {
		
		double angle = Math.toRadians(90);
        return tilt(image, angle);
	}
	
	public static BufferedImage tilt(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int)Math.floor(h*cos+w*sin);
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(angle, w/2, h/2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }
	
    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }
    
	@Override
	public String getName() {
		return "rotate";
	}
	
	@Override
	public String getDesc() {
		return "Rotate the image by 90°";
	}

	@Override
	public ImageViewerModifierPanel modifierPanel(NjivImage image) {
		return null;
	}

}
