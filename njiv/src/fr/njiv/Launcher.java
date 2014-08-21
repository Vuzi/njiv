package fr.njiv;

import java.awt.EventQueue;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;

import fr.njiv.UI.UIStyle;

// TODO
// Sauvegarde diaporama
// Chargement diaporama
// 

/**
 * Njiv launcher
 * @author vuzi
 *
 */
public class Launcher {

	/**
	 * Launch the application
	 */
	public static void main(String[] args) {
		
		// Init options
		Options.init();
		
		// Init UI components
		UIStyle.init();
		
		// Init plugins
		PluginLoader.init();
		
		String filename = null;
		
		if(args.length < 1) {
			JFileChooser chooser = new JFileChooser(new File("."));
		    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		    chooser.setDialogTitle("Image or directory to open");

			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				String choosen = chooser.getSelectedFile().getAbsolutePath();

				if(choosen != null) {
					filename = choosen;
				}
			}
		} else
			filename = args[0];
		
		if(filename == null) {
			System.out.println("[x] No file/directory selected, aborting...");
    		return;
		}
		
		File f = new File(filename);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if(f.exists()) {
						System.out.println("[i] Starting the new viewer window...");
						if(f.isDirectory())
							Utils.launchDirectoryViewer(f.getAbsolutePath(), (Frame)null);
						else
							Utils.launchImageViewer(f.getAbsolutePath(), (Frame)null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return;
	}

}
