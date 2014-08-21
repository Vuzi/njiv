package fr.njiv.UI.diaporama;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;

import java.awt.Dimension;

import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JFormattedTextField;

import fr.njiv.UI.UIStyle;

/**
 * The diaporama options
 * @author vuzi
 *
 */
public class DiaporamaOptions {
	
	// Transitions to use
	private Map<NjivDiaporamaTransition, Boolean> transitions;
	// Time between frames
	private int timer = 5;
	
	private boolean infinite = false;
	private boolean random = false;
	
	private JFrame optionPanel;
	private JPanel contentPane;
	
	JFormattedTextField formattedTextField;
	JCheckBox checkBoxRandom;
	JCheckBox checkBoxInfinite;

	/**
	 * Default constructor
	 * @param transitions
	 */
	public DiaporamaOptions(List<NjivDiaporamaTransition> transitions) {

		// Create the map
		this.transitions = new Hashtable<NjivDiaporamaTransition, Boolean>();
		for(NjivDiaporamaTransition transition : transitions) {
			this.transitions.put(transition, new Boolean(true));
		}

		// Default values
		this.random = false;
		this.infinite = false;
		this.setTimer(5);
	}
	
	/**
	 * Constructor using values
	 * @param transitions
	 * @param random
	 * @param infinite
	 * @param timer
	 */
	public DiaporamaOptions(Map<NjivDiaporamaTransition, Boolean> transitions, boolean random, boolean infinite, int timer) {
		
	}
	
	/**
	 * Return the option panel
	 * @return
	 */

	public JFrame getOptionPanel() {
		return getOptionPanel(false);
	}
	
	public JFrame getOptionPanel(boolean force) {
		if(this.optionPanel == null || force == true) {
			System.out.println("[i] Generating diaporama option panel...");
			
			// Frame
			this.optionPanel = new JFrame();
			this.optionPanel.setResizable(false);
			this.optionPanel.setIconImage(UIStyle.logo);
			this.optionPanel.setTitle("Njiv - Diaporama options");
			this.optionPanel.setBounds(100, 100, 529, 324);
			
			// Content pane
			this.contentPane = new JPanel();
			this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			this.optionPanel.setContentPane(this.contentPane);
			this.contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			
			this.optionPanel.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					closeOptionPanel();
				}
			});
			
			// Panels
			initializeOptionPanel();
			initializeTransitionPanel();
			
			// Buttons
			JButton btnCloseApply = new JButton("Close & apply");
			btnCloseApply.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					closeOptionPanel();
				}
			});
			contentPane.add(btnCloseApply);
			
			JButton btnRevertToDefault = new JButton("Revert to default");
			contentPane.add(btnRevertToDefault);
		} else {
			System.out.println("[i] Using diaporama option panel previously generated");
		}
		updatePanelValues();
		return optionPanel;
	}
	
	/**
	 * Update values
	 */
	private void updatePanelValues() {
		checkBoxInfinite.setSelected(this.infinite);
		checkBoxRandom.setSelected(this.random);
		formattedTextField.setValue(new Integer(this.getTimer()));
	}
	
	/**
	 * Initialize the option panel based on the defined values
	 */
	private void initializeOptionPanel() {
		// Options
		JPanel generalOptions = new JPanel();
		generalOptions.setPreferredSize(new Dimension(230, 250));
		generalOptions.setBorder(new TitledBorder(null, "General options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		FlowLayout flowLayout = (FlowLayout) generalOptions.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		this.contentPane.add(generalOptions);
		
		JLabel timerLabel = new JLabel("Time between frames");
		timerLabel.setFont(UIStyle.fontSmallLight);
		generalOptions.add(timerLabel);

		formattedTextField = new JFormattedTextField();
		formattedTextField.setValue(new Integer(0));
		formattedTextField.setColumns(6);
		((DefaultFormatter) formattedTextField.getFormatter()).setCommitsOnValidEdit(true);
		((DefaultFormatter) formattedTextField.getFormatter()).setAllowsInvalid(false);
		formattedTextField.addPropertyChangeListener("value", new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				updateTimer();
			}
		});
		generalOptions.add(formattedTextField);
		
		checkBoxRandom = new JCheckBox("Random order");
		checkBoxRandom.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				random = ((JCheckBox)(e.getSource())).isSelected();
			}
		});
		generalOptions.add(checkBoxRandom);
		
		checkBoxInfinite = new JCheckBox("Infinite");
		checkBoxInfinite.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				infinite = ((JCheckBox)(e.getSource())).isSelected();
			}
		});
		generalOptions.add(checkBoxInfinite);
	}
	
	/**
	 * Initialize the transition panel based on the transitions given
	 */
	private void initializeTransitionPanel() {
		// Transitions panel
		JPanel transitionsPanel = new JPanel();
		transitionsPanel.setPreferredSize(new Dimension(270, 250));
		transitionsPanel.setBorder(new TitledBorder(null, "Transitions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(transitionsPanel);
		transitionsPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel transitionInternalPanel = new JPanel();
		transitionInternalPanel.setBorder(null);
		FlowLayout flowLayout_1 = (FlowLayout) transitionInternalPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);

		JPanel internalPanel = new JPanel();
		transitionInternalPanel.add(internalPanel);
		
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[]{0};
		gbl.rowHeights = new int[]{0};
		gbl.columnWeights = new double[]{0.0};
		gbl.rowWeights = new double[]{0.0};
		internalPanel.setLayout(gbl);
		
		int i = 0;
		for(Entry<NjivDiaporamaTransition, Boolean> entry : transitions.entrySet()){
			// Checkbox
			GridBagConstraints gbc_chckbxTransitionName = new GridBagConstraints();
			gbc_chckbxTransitionName.anchor = GridBagConstraints.WEST;
			gbc_chckbxTransitionName.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxTransitionName.gridx = 0;
			gbc_chckbxTransitionName.gridy = i;
			
			JCheckBox chckbxTransitionName = new JCheckBox(entry.getKey().getName());
			chckbxTransitionName.setToolTipText(entry.getKey().getDescription());
			chckbxTransitionName.setSelected(entry.getValue());
			chckbxTransitionName.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					transitions.put(entry.getKey(), ((JCheckBox)(e.getSource())).isSelected());
				}
			});
			internalPanel.add(chckbxTransitionName, gbc_chckbxTransitionName);
			
			// Option button
			if(entry.getKey().hasOptionPanel()) {
				JButton btnOptions = new JButton("Options");
				btnOptions.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						JPanel options = entry.getKey().getOptionsPanel(optionPanel);
						JDialog transitionOptionFrame = new JDialog(optionPanel, entry.getKey().getName()+" - Options");
						transitionOptionFrame.setModal(true);
						transitionOptionFrame.setContentPane(options);
						transitionOptionFrame.pack();
						transitionOptionFrame.setVisible(true);
					}
				});
				GridBagConstraints gbc_btnOptions = new GridBagConstraints();
				gbc_btnOptions.anchor = GridBagConstraints.WEST;
				gbc_btnOptions.insets = new Insets(0, 0, 5, 5);
				gbc_btnOptions.gridx = 1;
				gbc_btnOptions.gridy = i;
				internalPanel.add(btnOptions, gbc_btnOptions);
			}
			
			i++;
		}
		
		// Scroll panel
		JScrollPane scrollPane = new JScrollPane(transitionInternalPanel);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportBorder(null);
		transitionsPanel.add(scrollPane, BorderLayout.CENTER);
				
		JLabel transitionLabel = new JLabel("List of transitions");
		transitionLabel.setFont(UIStyle.fontSmallLight);
		transitionsPanel.add(transitionLabel, BorderLayout.NORTH);
	}
	
	/**
	 * Close the panel
	 */
	public void closeOptionPanel() {
		try {
			formattedTextField.commitEdit();
		} catch (ParseException e) {
			e.printStackTrace();
			updateTimer(); // Use the previous value
		}

		System.out.println("[i] Closing the diaporama option panel");
		this.optionPanel.setVisible(false);
	}
	
	/**
	 * True if the infinite option should be use
	 * @return
	 */
	public boolean useInfinite() {
		return this.infinite;
	}
	
	/**
	 * True if random should be used
	 * @return
	 */
	public boolean useRandom() {
		return this.random;
	}

	/**
	 * Return all the transitions
	 * @return
	 */
	public List<NjivDiaporamaTransition> getTransitions() {
		ArrayList<NjivDiaporamaTransition> list = new ArrayList<NjivDiaporamaTransition>();
		for(Entry<NjivDiaporamaTransition, Boolean> entry : this.transitions.entrySet()) {
			list.add(entry.getKey());
		}
		return list;
	}
	
	public Map<NjivDiaporamaTransition, Boolean> getTransitionsUsage() {
		return this.transitions;
	}
	
	/**
	 * Return the transitions to use
	 * @return
	 */
	public List<NjivDiaporamaTransition> getTransitionsToUse() {
		ArrayList<NjivDiaporamaTransition> list = new ArrayList<NjivDiaporamaTransition>();
		// For each transition set to true, use it
		for(Entry<NjivDiaporamaTransition, Boolean> entry : this.transitions.entrySet()) {
			if(entry.getValue() == true)
				list.add(entry.getKey());
		}
		return list;
	}
	
	/**
	 * Update the timer value
	 */
	private void updateTimer() {
		setTimer(((Number)(formattedTextField.getValue())).intValue());
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void setInfinite(boolean b) {
		this.infinite = b;
	}

	public void setRandom(boolean b) {
		this.random = b;
	}
}
