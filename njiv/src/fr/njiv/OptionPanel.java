package fr.njiv;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JButton;

import fr.njiv.UI.UIStyle;

/**
 * 
 * @author vuzi
 *
 */
public class OptionPanel extends JFrame {

	private static final long serialVersionUID = -5277792360095728299L;
	
	private JPanel contentPane;
	
	private class OptionInternalPanel {
		
		String name;
		JPanel panel;
		
		public OptionInternalPanel(String name, JPanel panel) {
			this.name = name;
			this.panel = panel;
		}
	}
	
	private ArrayList<OptionInternalPanel> panels;

	/**
	 * Create the frame
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OptionPanel() {
		
		// Option entries
		this.panels = new ArrayList<>();
		
		// Panels
		this.createOptionPanel("General", (Map<String, Object>) Options.all, false);
		
		// Main panel
		setTitle("Options");
		setIconImage(UIStyle.logo);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 614, 353);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Modules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel, BorderLayout.WEST);
		
		JList list = new JList();
		list.setBorder(null);
		list.setPreferredSize(new Dimension(130, 250));
		list.setFont(UIStyle.fontSmallLight);
		list.setModel(new AbstractListModel<String>() {

			private static final long serialVersionUID = -6311209494931453925L;

			@Override
			public String getElementAt(int index) {
				return panels.get(index).name;
			}

			@Override
			public int getSize() {
				return panels.size();
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					scrollPane.setViewportView(panels.get(list.getSelectedIndex()).panel);
				}
			}
		});
		list.setSelectedIndex(0);

		panel.add(list);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.SOUTH);
		
		JButton btnSaveChanges = new JButton("Save changes");
		btnSaveChanges.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Options.save();

				JOptionPane.showMessageDialog(contentPane,
					    "<html>Option changes have been saved<br/><br/><i>Note that some option may need to restart the program to be effective.</i></html>",
					    "Changes saved",
					    JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panel_2.add(btnSaveChanges);
		
		JButton btnRevertToDefault = new JButton("Revert to default");
		btnRevertToDefault.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Options.init(fr.njiv.Options.class.getResource("conf.json"));
				Options.save();

				JOptionPane.showMessageDialog(contentPane,
					    "<html>Option have been reseted<br/><br/><i>Note that some option reset may need to restart the program to be effective.</i></html>",
					    "Option reseted",
					    JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panel_2.add(btnRevertToDefault);
	}
	
	/**
	 * Get the values of an option
	 * @param key
	 * @return
	 */
	private String[] getValueOptions(String key, Map<String, Object> entries) {
		if(entries.containsKey("val:"+key)) {
			return ((String)(entries.get("val:"+key))).split("\\|");
		}
		return null;
	}
	
	private String getInfoTip(String key, Map<String, Object> entries) {
		if(entries.containsKey("des:"+key)) {
			return ((String)(entries.get("des:"+key)));
		}
		return null;
	}
	
	private String getName(String key, Map<String, Object> entries) {
		if(entries.containsKey("id:"+key)) {
			return ((String)(entries.get("id:"+key)));
		}
		return key;
	}

	/**
	 * Create the option panel
	 * @param panelName
	 * @param entries
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JPanel createOptionPanel(String panelName, Map<String, Object> entries, Boolean keybinding) {
		// Panel
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(7, 5, 3, 5));
		
		// Layout
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {200, 200};
		gbl_panel_1.rowHeights = new int[] {0};
		gbl_panel_1.columnWeights = new double[]{0.0};
		gbl_panel_1.rowWeights = new double[]{0.0};
		panel.setLayout(gbl_panel_1);

		panels.add(new OptionInternalPanel(panelName, panel));
		
		int i = 0;
		
		if(keybinding) {
			for(Entry<?, ?> entry : entries.entrySet()) {
				if(entry.getValue() instanceof String) {
					
					// Label
					JLabel optionLabel = new JLabel(getName((String) entry.getValue(), entries));
					optionLabel.setFont(UIStyle.fontSmallLight);
					GridBagConstraints gbc_optionLabel = new GridBagConstraints();
					gbc_optionLabel.fill = GridBagConstraints.BOTH;
					gbc_optionLabel.insets = new Insets(5, 5, 5, 5);
					gbc_optionLabel.gridx = 0;
					gbc_optionLabel.gridy = i;
					panel.add(optionLabel, gbc_optionLabel);
			
					JTextField textfield = new JTextField((String) entry.getKey());
					
					textfield.getDocument().addDocumentListener(new DocumentListener() {
						
						private void updateData() {
							entries.remove(entry.getKey());
							entries.put(textfield.getText(), entry.getValue());
						}
						
						public void removeUpdate(DocumentEvent e) {
							updateData();
						}
						
						public void insertUpdate(DocumentEvent e) {
							updateData();
						}
						
						public void changedUpdate(DocumentEvent e) {}
					});

					String toolTipText = getInfoTip((String) entry.getKey(), entries);
					if(toolTipText != null) {
						optionLabel.setToolTipText(toolTipText);
						textfield.setToolTipText(toolTipText);
					}
					
					textfield.setFont(UIStyle.fontSmallLight);
					GridBagConstraints gbc_optionValue = new GridBagConstraints();
					gbc_optionValue.fill = GridBagConstraints.BOTH;
					gbc_optionValue.insets = new Insets(5, 5, 5, 5);
					gbc_optionValue.gridx = 1;
					gbc_optionValue.gridy = i;
					panel.add(textfield, gbc_optionValue);
					
					i++;
				}
			}
		} else {
			
			for(Entry<?, ?> entry : entries.entrySet()) {
				if(entry.getValue() instanceof String && !((String)entry.getKey()).startsWith("val:") && !((String)entry.getKey()).startsWith("des:") && !((String)entry.getKey()).startsWith("id:")) {
					
					// Label
					JLabel optionLabel = new JLabel(getName((String) entry.getKey(), entries));
					optionLabel.setFont(UIStyle.fontSmallLight);
					GridBagConstraints gbc_optionLabel = new GridBagConstraints();
					gbc_optionLabel.fill = GridBagConstraints.BOTH;
					gbc_optionLabel.insets = new Insets(5, 5, 5, 5);
					gbc_optionLabel.gridx = 0;
					gbc_optionLabel.gridy = i;
					panel.add(optionLabel, gbc_optionLabel);
					
					// Value
					String[] values = getValueOptions((String) entry.getKey(), entries);
					Component optionValue;
					
					if(values == null) {
						JTextField textfield = new JTextField((String) entry.getValue());
						optionValue = textfield;
						
						textfield.getDocument().addDocumentListener(new DocumentListener() {
							
							private void updateData() {
								System.out.println(textfield.getText());
								entries.put((String)entry.getKey(), textfield.getText());
								System.out.println(entries.get((String)entry.getKey()));
							}
							
							@Override
							public void removeUpdate(DocumentEvent e) {
								updateData();
							}
							
							@Override
							public void insertUpdate(DocumentEvent e) {
								updateData();
							}
							
							@Override
							public void changedUpdate(DocumentEvent e) {}
						});
	
						String toolTipText = getInfoTip((String) entry.getKey(), entries);
						if(toolTipText != null) {
							optionLabel.setToolTipText(toolTipText);
							textfield.setToolTipText(toolTipText);
						}
						
					} else {
						JComboBox<String> combo = new JComboBox<String>(values);
						combo.setSelectedItem((String) entry.getValue());
						optionValue = combo;
						
						combo.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								entries.put((String)entry.getKey(), (Object)combo.getSelectedItem());
							}
						});
						
						String toolTipText = getInfoTip((String) entry.getKey(), entries);
						if(toolTipText != null) {
							optionLabel.setToolTipText(toolTipText);
							combo.setToolTipText(toolTipText);
						}
					}
					
					optionValue.setFont(UIStyle.fontSmallLight);
					GridBagConstraints gbc_optionValue = new GridBagConstraints();
					gbc_optionValue.fill = GridBagConstraints.BOTH;
					gbc_optionValue.insets = new Insets(5, 5, 5, 5);
					gbc_optionValue.gridx = 1;
					gbc_optionValue.gridy = i;
					panel.add(optionValue, gbc_optionValue);
					
					i++;
				} else if(entry.getValue() instanceof Map) {
					if(((String)entry.getKey()).equals("key_binding")) {
						createOptionPanel("   "+panelName+" keys", (Map<String, Object>) entry.getValue(), true);
					} else {
						createOptionPanel(getName((String) entry.getKey(), entries), (Map<String, Object>) entry.getValue(), false);
					}
				}
			}
		}
		
		return panel;
	}

}
