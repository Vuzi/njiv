package fr.njiv.UI.diaporama.transition;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.njiv.UI.diaporama.Diaporama.DiaporamaImage;
import fr.njiv.UI.diaporama.NjivDiaporamaTransition;

public class ZoomInEffect extends NjivDiaporamaTransition {

	int timer = 1500; // Time in ms of the transition

	@Override
	public String getName() {
		return "Zoom In";
	}

	@Override
	public String getDescription() {
		return "Perform a zomm in the next image on the previous image";
	}

	@Override
	public boolean hasOptionPanel() {
		return false;
	}

	@Override
	public JPanel getOptionsPanel(JFrame container) {
		return null;
	}

	@Override
	public boolean updateTransition(DiaporamaImage image1, DiaporamaImage image2, Graphics2D g2) {
		long timer = getTransitionTimer();
		
		float scaleImage2 = (float)timer / ((float)this.timer);
		float scaleImage1 = 1f - scaleImage2;

		scaleImage2 = scaleImage2 > 1f ? 1f : scaleImage2;
		scaleImage1 = scaleImage1 < 0f ? 0f : scaleImage1;

		if(image1 != null) {
			int w1 = (int)(image1.getBounds().width * scaleImage1);
			int h1 = (int)(image1.getBounds().height * scaleImage1);
	
	        int x1 = (g2.getClipBounds().width/2) - (w1/2);
	        int y1 = (g2.getClipBounds().height/2) - (h1/2);
	
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scaleImage1));
	        g2.drawImage(image1.getToDisplay(), x1, y1, w1, h1, null);
		}
        
		if(image2 != null) {
			int w2 = (int)(image2.getBounds().width * scaleImage2);
			int h2 = (int)(image2.getBounds().height * scaleImage2);
	
	        int x2 = (g2.getClipBounds().width/2) - (w2/2);
	        int y2 = (g2.getClipBounds().height/2) - (h2/2);
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, scaleImage2));
	        g2.drawImage(image2.getToDisplay(), x2, y2, w2, h2, null);
		}
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
		if(timer < this.timer)
			return true;
		else
			return false;
	}

}
