package fr.njiv.UI.diaporama.transition;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.njiv.UI.diaporama.Diaporama.DiaporamaImage;
import fr.njiv.UI.diaporama.NjivDiaporamaTransition;

public class CrossfadingEffect extends NjivDiaporamaTransition {
	
	int timer = 3000; // Time in ms of the transition

	@Override
	public String getName() {
		return "Crossfading";
	}

	@Override
	public String getDescription() {
		return "Perform a cross-fade transition between two images";
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

		float opacity2 = (float)timer / (float)this.timer;
		float opacity1 = 1f - opacity2;
		
		opacity2 = opacity2 > 1f ? 1f : opacity2;
		opacity1 = opacity1 < 0f ? 0f : opacity1;
		
        // Draw just the image
		if(image1 != null) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity1));
			g2.drawImage(image1.getToDisplay(), image1.getBounds().x, image1.getBounds().y, image1.getBounds().width, image1.getBounds().height, null);
		}
		
		if(image2 != null) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity2));
			g2.drawImage(image2.getToDisplay(), image2.getBounds().x, image2.getBounds().y, image2.getBounds().width, image2.getBounds().height, null);
		}
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	
		if(timer < this.timer)
			return true;
		else
			return false;
	}

}
