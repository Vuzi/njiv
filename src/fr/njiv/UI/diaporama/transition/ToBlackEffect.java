package fr.njiv.UI.diaporama.transition;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.njiv.UI.diaporama.Diaporama.DiaporamaImage;
import fr.njiv.UI.diaporama.NjivDiaporamaTransition;

public class ToBlackEffect extends NjivDiaporamaTransition {
	
	int timer = 3000; // Time in ms of the transition

	@Override
	public String getName() {
		return "Fade to black";
	}

	@Override
	public String getDescription() {
		return "Perform a cross-fade transition between two images";
	}

	@Override
	public boolean hasOptionPanel() {
		return true;
	}

	@Override
	public JPanel getOptionsPanel(JFrame container) {
		JPanel panel = new JPanel();
		panel.add(new JLabel("Label de test 1"));
		panel.add(new JTextField());
		panel.setPreferredSize(new Dimension(250, 250));
		return panel;
	}

	@Override
	public boolean updateTransition(DiaporamaImage image1, DiaporamaImage image2, Graphics2D g2) {
		long timer = getTransitionTimer();
		
		if(timer < this.timer / 2) {
			if(image1 != null) {
				// Image1 should be printed
				float opacity = 1 - (float)timer / ((float)this.timer / 2f);
				
		        // Draw just the image
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		        g2.drawImage(image1.getToDisplay(), image1.getBounds().x, image1.getBounds().y, image1.getBounds().width, image1.getBounds().height, null);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			}
		} else {
			if(image2 != null) {
				// Image2 should be printed
				float opacity = (((float)timer - ((float)this.timer / 2f))) / ((float)this.timer / 2f);
				opacity = opacity > 1 ? 1 : opacity;

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		        g2.drawImage(image2.getToDisplay(), image2.getBounds().x, image2.getBounds().y, image2.getBounds().width, image2.getBounds().height, null);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			}
		}

		if(timer < this.timer)
			return true;
		else
			return false;
	}

}
