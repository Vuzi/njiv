package fr.njiv.plugin;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.bouncycastle.util.encoders.Base64;

import fr.njiv.UI.UIStyle;
import fr.njiv.catalog.NjivCatalog;
import fr.njiv.image.NjivImage;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

public class ImgurCatalog implements NjivCatalog{
	
	private String clientID = "c47eb6e794030ae";
	private JTextField textField;
	
	private String title = "";
	private String description = "";

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public JPanel catalogueOptionPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblGalleryTitle = new JLabel("Gallery title");
		GridBagConstraints gbc_lblGalleryTitle = new GridBagConstraints();
		gbc_lblGalleryTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblGalleryTitle.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblGalleryTitle.gridx = 0;
		gbc_lblGalleryTitle.gridy = 0;
		panel.add(lblGalleryTitle, gbc_lblGalleryTitle);
		
		textField = new JTextField(title);
		textField.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				title = textField.getText();
			}
		});
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JLabel lblGalleryDescription = new JLabel("Gallery description");
		GridBagConstraints gbc_lblGalleryDescription = new GridBagConstraints();
		gbc_lblGalleryDescription.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblGalleryDescription.insets = new Insets(0, 0, 0, 5);
		gbc_lblGalleryDescription.gridx = 0;
		gbc_lblGalleryDescription.gridy = 1;
		panel.add(lblGalleryDescription, gbc_lblGalleryDescription);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setText(description);
		editorPane.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				description = editorPane.getText();
			}
		});
		GridBagConstraints gbc_editorPane = new GridBagConstraints();
		gbc_editorPane.fill = GridBagConstraints.BOTH;
		gbc_editorPane.gridx = 1;
		gbc_editorPane.gridy = 1;
		panel.add(editorPane, gbc_editorPane);
		editorPane.setBorder(UIManager.getBorder("PopupMenu.border"));
		
		return panel;
	}

	@Override
	public boolean hasPanel() {
		return true;
	}

	@Override
	public void generateCatalogue(List<NjivImage> images) {
		
		System.out.println("[i] Start of imgur uploader for "+images.size()+" images...");
		
		JFrame pane = new JFrame();
		pane.setTitle("Imgur image uploader");
		pane.setIconImage(UIStyle.logo);
		pane.setBounds(100, 100, 250, 250);
		pane.setResizable(false);	
		pane.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel contentPane = new JPanel();
		pane.setContentPane(contentPane);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(new JLabel("Start of the uploading..."));

		JProgressBar progressBar = new JProgressBar(0, images.size() + 1);
		contentPane.add(progressBar);

		pane.validate();
		pane.pack();
		pane.setVisible(true);
		
		// Create the background NjivImage
		SwingWorker<String, Integer> loading = new SwingWorker<String, Integer>() {
        	
        	String link;

			@Override
			protected String doInBackground() throws Exception {
				
				try {
				
					//create the album
					URL url = new URL("https://api.imgur.com/3/album/");
				    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				    String data = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8") + "&" + 
				                  URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8");

				    conn.setDoOutput(true);
				    conn.setDoInput(true);
				    conn.setRequestMethod("POST");
				    conn.setRequestProperty("Authorization", "Client-ID " + clientID);
				    conn.setRequestMethod("POST");
				    conn.setRequestProperty("Content-Type",
				            "application/x-www-form-urlencoded");
				    conn.connect();
				    StringBuilder stb = new StringBuilder();
				    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				    wr.write(data);
				    wr.flush();
				    
				    // Get the response
				    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				    String line;
				    while ((line = rd.readLine()) != null) {
				        stb.append(line).append("\n");
				    }
				    wr.close();
				    rd.close();
				    
				    // Response parse
					String id = stb.toString();
					int id_start = stb.toString().indexOf("id") + 5;
					int id_stop = id.substring(id_start).indexOf("\"");
					id = id.substring(id_start);
					id = id.substring(0, id_stop);
					
					String hash = stb.toString();
					int hash_start = stb.toString().indexOf("deletehash") + 13;
					int hash_stop = hash.substring(hash_start).indexOf("\"");
					hash = hash.substring(hash_start);
					hash = hash.substring(0, hash_stop);
					
					// Link
					link = "http://imgur.com/a/"+id;
					progressBar.setValue(1);

					System.out.println("[i] Album created");
					
					// images
					for(NjivImage image : images) {
						uploadImage(image.getImage(), hash);
						
						// Update
						System.out.println("[i] Image " + progressBar.getValue() +"/"+images.size()+" uploaded !");
						progressBar.setValue(progressBar.getValue()+1);
					}
					
				} catch (Exception e) {
					pane.dispose();
					
					JOptionPane.showMessageDialog(null,
						    "Upload at imgur failed : " + e.getMessage(),
						    "Error",
						    JOptionPane.INFORMATION_MESSAGE);
				}
				return this.link;
			}
			
			@Override
			protected void done() {
				if(this.link != null) {
					printLink((JPanel) pane.getContentPane(), this.link);
				} else {
					pane.setVisible(false);
					pane.dispose();
					
					JOptionPane.showMessageDialog(null,
						    "Upload at imgur failed",
						    "Error",
						    JOptionPane.INFORMATION_MESSAGE);
				}
	        }
		};
		loading.execute();

	}
	
	private void uploadImage(BufferedImage image, String id) throws Exception {
		
		// Images
		URL url = new URL("https://api.imgur.com/3/image");
		
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	   
	    //create base64 images
	    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	    ImageIO.write(image, "png", byteArray);
	    byte[] byteImage = byteArray.toByteArray();
	    String dataImage = new String(Base64.encode(byteImage));

	    String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(dataImage, "UTF-8") + "&" + 
	    		      URLEncoder.encode("album", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
 
	    // Send everything
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Authorization", "Client-ID " + clientID);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.connect();
	    StringBuilder stb = new StringBuilder();
	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	    
	    wr.write(data);
	    wr.flush();

	    // Get the response
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String line;
	    while ((line = rd.readLine()) != null) {
	        stb.append(line).append("\n");
	    }
	    wr.close();
	    rd.close();
	}
	
	private void printLink(JPanel pane, String the_url) {

		try {
			// Reponse url
		    URI uri = new URI(the_url);
		 
		    JLabel link = new JLabel("<html><a href=\""+the_url+"\">"+the_url+"</a></html>");
		    link.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
			    	  if (Desktop.isDesktopSupported()) {
			    		  try {
			    			  Desktop.getDesktop().browse(uri);
			    		  } catch (IOException ex) {}
			          } 
				}
			});
		    
		    pane.removeAll();
		    pane.add(new JLabel("Images uploaded : "));
		    pane.add(link);

		    pane.repaint();
		    pane.validate();
		    
		} catch (URISyntaxException e) {}
		
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Upload allt the images to imgur, and give back the link";
	}

	@Override
	public String getName() {
		return "Imgur uploader";
	}

}
