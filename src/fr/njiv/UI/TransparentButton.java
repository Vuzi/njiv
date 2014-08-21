package fr.njiv.UI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;


public class TransparentButton extends JButton {
	
	private static final long serialVersionUID = 6860465498530505609L;
	AlphaIcon icon;
	
	public TransparentButton(final AlphaIcon icon){
        super(icon);
		icon.setAlpha(0.1f);
        this.icon = icon;
        setBorder(null);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setBackground(new Color(0,0,0,0));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
			
			public void mouseEntered(MouseEvent e) {
				icon.setAlpha(1.0f);
			}
			
		});
    }
	
	public void highlight() {
		icon.setAlpha(1.0f);
	}
	
	public void show() {
		icon.setAlpha(0.5f);
	}
	
	public void hide() {
		icon.setAlpha(0.1f);
	}
	
}