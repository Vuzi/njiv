package fr.njiv.catalog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.njiv.UI.UIStyle;
import fr.njiv.UI.directoryViewer.DirectoryViewer;

public class CatalogPanel extends JFrame {

	private static final long serialVersionUID = -4169729525198350787L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public CatalogPanel(List<NjivCatalog> catalogs, DirectoryViewer viewer) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 623, 318);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Catalogue list", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel_3, BorderLayout.WEST);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel catalogOptionPanel = new JPanel();
		catalogOptionPanel.setBorder(new TitledBorder(null, "Catalog options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(catalogOptionPanel);
		
		JList<String> list = new JList<String>();
		panel_3.add(list);
		list.setFont(UIStyle.fontSmallLight);
		list.setPreferredSize(new Dimension(100, 250));
		list.setModel(new AbstractListModel<String>() {

			private static final long serialVersionUID = -8185059669447272084L;
			
			String[] values = new String[catalogs.size()];
			
			{
				for(int i = 0; i < catalogs.size(); i++)
					values[i] = catalogs.get(i).getName();
			}
			
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					catalogOptionPanel.removeAll();
					if(catalogs.get(list.getSelectedIndex()).hasPanel())
						catalogOptionPanel.add(catalogs.get(list.getSelectedIndex()).catalogueOptionPanel());
					catalogOptionPanel.validate();
				}
			}
		});
		list.setSelectedIndex(0);

		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.SOUTH);
		
		JButton btnGenerateTheCatalogue = new JButton("Generate the catalog");
		panel_2.add(btnGenerateTheCatalogue);
		btnGenerateTheCatalogue.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Generate the catalog
				catalogs.get(list.getSelectedIndex()).generateCatalogue(viewer.getImages());
			}
		});
	}

}
