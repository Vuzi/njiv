package fr.njiv.plugin;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import fr.njiv.catalog.NjivCatalog;
import fr.njiv.image.NjivImage;

public class PDFCatalog implements NjivCatalog {

	private String filename = "catalog.pdf";
	
	@Override
	public String getName() {
		return "PDF Catalogue";
	}

	@Override
	public String getDescription() {
		return "Generate a PDF catalogue with one file per page";
	}

	@Override
	public boolean hasPanel() {
		return true;
	}

	@Override
	public JPanel catalogueOptionPanel() {
		JPanel panel = new JPanel();

		JLabel filenamelbl = new JLabel("Filename : ");
		JTextField textfield = new JTextField(filename);
		textfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				filename = textfield.getText();
			}
		});
		textfield.setColumns(25);
		JButton chooseButton = new JButton("Change");
		chooseButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				FileDialog chooser = new java.awt.FileDialog((Frame)null, "Select a name to save", FileDialog.SAVE);
				chooser.setVisible(true);
				
				String choosen = chooser.getDirectory() + chooser.getFile();

				if(chooser.getDirectory() != null) {
					textfield.setText(choosen);
					filename = choosen;
				}
			}
		});
	
		panel.add(filenamelbl);
		panel.add(textfield);
		panel.add(chooseButton);
		
		return panel;
	}

	@Override
	public void generateCatalogue(List<NjivImage> images) {
		
		if(images.size() <= 0) {
			JOptionPane.showMessageDialog(null,
				    "Error while generating PDF catalog : No image selected",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
			
		
		try {
			System.out.println("[i] Generatin PDF catalog to '"+filename+"'");
			// Document
			PDDocument document = new PDDocument();
			
			for(NjivImage image : images) {
				// New page
		    	PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
		    	document.addPage(page); 
		    	
		    	// Load image
		    	PDXObjectImage img = new PDJpeg(document, image.getImage());
		    	
		    	// Draw image
		    	PDPageContentStream contentStream = new PDPageContentStream(document, page);
		    	contentStream.drawImage(img, 0, 0);
		    	contentStream.close();
			}

	    	document.save(filename);
	    	document.close();
	    	
			System.out.println("[i] Generatin of PDF done !");

			JOptionPane.showMessageDialog(null,
				    "Generation of PDF catalog done at '"+filename+"'",
				    "Success",
				    JOptionPane.INFORMATION_MESSAGE);
			
		} catch (IOException | COSVisitorException e) {
			System.out.println("[x] Error while generating PDF catalog : "+e.getLocalizedMessage());

			JOptionPane.showMessageDialog(null,
				    "Error while generating PDF catalog : "+e.getLocalizedMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			
			return;
		}
	}

}