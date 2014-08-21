package fr.njiv.UI.diaporama;

import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.njiv.UI.diaporama.Diaporama.DiaporamaImage;

public abstract class NjivDiaporamaTransition implements Comparable<NjivDiaporamaTransition> {
	
	protected long startTimer = 0;
	
	/**
	 * The name of the diaporama
	 * @return The transition name
	 */
	public abstract String getName();
	
	/**
	 * The description of the diaporama
	 * @return The transition description
	 */
	public abstract String getDescription();
	
	/**
	 * Perform the transition between image 1 and image 2 on g2
	 * @param timer The timer from the launch of the transition
	 * @param image1 The image from where start the transition
	 * @param image2 The new image
	 * @param g2 Where to paint the animation
	 * @return true until the transition ends, and then returns false. If false is return, nothing should be painted on g2
	 */
	public abstract boolean updateTransition(DiaporamaImage image1, DiaporamaImage image2, Graphics2D g2);
	
	public void startTransition() {
		startTimer = System.currentTimeMillis();
	}
	
	public long getTransitionTimer() {
		return System.currentTimeMillis() - startTimer;
	}
	
	/**
	 * Return true if the transition have an option panel, false otherwise
	 * @return
	 */
	public abstract boolean hasOptionPanel();
	
	/**
	 * Get the option panel
	 */
	public abstract JPanel getOptionsPanel(JFrame container);
	
	/**
	 * Used for lists
	 */
	public int compareTo(NjivDiaporamaTransition o) {
		
		if(o != this)
			return 1;
		else
			return 0;
	}
}
