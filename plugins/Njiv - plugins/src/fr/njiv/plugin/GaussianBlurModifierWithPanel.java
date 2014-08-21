package fr.njiv.plugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.njiv.image.NjivImage;
import fr.njiv.image.NjivImageModificator;
import javax.swing.JSlider;
import javax.swing.JCheckBox;

public class GaussianBlurModifierWithPanel implements NjivImageModificator {

	private int radius;
	private boolean horizontal;
	
	@Override
	public String getDesc() {
		return "Perform a gaussian blur on the image";
	}

	@Override
	public String getName() {
		return "Gaussian blur (Option)";
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public JPanel getPanel(NjivImage img) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		
		panel.setPreferredSize(new Dimension(253, 142));
		
		JPanel topPanel = new JPanel();
		panel.add(topPanel, BorderLayout.CENTER);
		JLabel lblBlurRadius = new JLabel("Blur radius");
		topPanel.add(lblBlurRadius);
		
		JLabel lblNewLabel = new JLabel(radius+"");
		topPanel.add(lblNewLabel);
		
		JSlider slider = new JSlider();
		topPanel.add(slider);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				radius = slider.getValue();
				lblNewLabel.setText(radius+"");
			}
		});
		slider.setValue(radius);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMaximum(50);
		slider.setMinimum(1);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Horizontal blur");
		chckbxNewCheckBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				horizontal = chckbxNewCheckBox.isSelected();
			}
		});
		chckbxNewCheckBox.setSelected(horizontal);
		topPanel.add(chckbxNewCheckBox);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(centerPanel, BorderLayout.SOUTH);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel textPanel = new JPanel();
		textPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		centerPanel.add(textPanel);
		
		JLabel sepiaTxt = new JLabel("<html>Change the radius of the gaussian blur</html>");
		textPanel.add(sepiaTxt);
		
		return panel;
	}

	@Override
	public boolean hasPanel() {
		return true;
	}

	@Override
	public BufferedImage modifyImage(BufferedImage image) {
		
        BufferedImageOp op = Blur.getGaussianBlurFilter(radius, horizontal);
    	return op.filter(image, null);
		
	}

}
