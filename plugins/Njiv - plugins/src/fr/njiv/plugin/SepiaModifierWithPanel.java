package fr.njiv.plugin;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.njiv.Utils;
import fr.njiv.image.NjivImage;
import fr.njiv.image.NjivImageModificator;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class SepiaModifierWithPanel implements NjivImageModificator {

	private int sepiaValue = 20;
	
	@Override
	public String getDesc() {
		return "Transform the image to a sepia version";
	}

	@Override
	public String getName() {
		return "Sepia (Options)";
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public JPanel getPanel(NjivImage img) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		panel.setPreferredSize(new Dimension(200, 97));
		
		JPanel topPanel = new JPanel();
		panel.add(topPanel, BorderLayout.NORTH);
		JLabel label = new JLabel("Sepia intensitie");
		topPanel.add(label);
		
		JSpinner spinner = new JSpinner();
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sepiaValue = (int) spinner.getValue();
			}
		});
		spinner.setModel(new SpinnerNumberModel(sepiaValue, 0, 255, 1));
		topPanel.add(spinner);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel textPanel = new JPanel();
		textPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		centerPanel.add(textPanel);
		
		JLabel sepiaTxt = new JLabel("<html>Sepia intensity from <b>0-255</b>,<br/><b>30</b> produces nice results.</html>");
		textPanel.add(sepiaTxt);
		
		return panel;
	}

	@Override
	public boolean hasPanel() {
		return true;
	}

	@Override
	public BufferedImage modifyImage(BufferedImage img) {
		img = Utils.cloneBufferedImage(img);
		Sepia.apply(img, sepiaValue);
		return img;
	}

}
