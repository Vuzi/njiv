package fr.njiv;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;

@SuppressWarnings({ "rawtypes", "resource" })

public class NjivPluginLoader<PluginType> {
	/* Tableaux des noms de plugin à traiter */
	private File[] files;
	private File folder;

	private final Class<PluginType> type;
	private ArrayList<Class> plugins;
	
	public NjivPluginLoader(Class<PluginType> typeToLoad, String folder) throws Exception {
		
		plugins = new ArrayList<Class>();
		type = typeToLoad;
		
		this.folder = new File(folder);
		
		if(!this.folder.exists())
			throw new NjivLoadingException("The specified directory doesn't exist");
		
		if(this.folder.isDirectory()) {
			files = this.folder.listFiles();
		} else {
			files = new File[1];
			files[0] = this.folder;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<PluginType> loadPlugins() throws Exception  {
		
		// Initialize the loader
		this.initializeLoader();
		ArrayList<PluginType> plugins = new ArrayList<PluginType>();
		
		for(int i = 0; i < this.plugins.size(); i++)
			plugins.add((PluginType)(this.plugins.get(i)).newInstance());
		
		return plugins;
	}
	
	private void initializeLoader() throws Exception {
		
		if(this.files == null || this.files.length == 0)
			throw new NjivLoadingException("No plugin list to load");
		
		// For each file
		for(int i = 0; i < files.length; i++) {
			
			System.out.println("[P] Start of loading for '"+files[i].getName()+"'");
			
			File file = files[i];

			// If the file doesn't exist, quit
			if(!file.exists())
				break;

			// load the class form the URL
			URL u = file.toURI().toURL();
			URLClassLoader loader = new URLClassLoader(new URL[] {u});
			Enumeration<?> enumeration = new JarFile(file.getAbsolutePath()).entries();
			
			while(enumeration.hasMoreElements()) {
				
				// For each, pick the jar name
				String tmp = enumeration.nextElement().toString();
				
				// If the is a class
				if(tmp.length() > 6 && tmp.substring(tmp.length()-6).compareTo(".class") == 0) {
					
					// class name for the file name
					tmp = tmp.substring(0,tmp.length()-6);
					tmp = tmp.replaceAll("/",".");
					
					// Get the class
					Class<?> tmpClass = Class.forName(tmp, true, (ClassLoader)loader);
					
					// For each interface implemented
					for(Class<?>  interfaceimplemented : tmpClass.getInterfaces()) {
						if(interfaceimplemented.getName().toString().equals(type.getName().toString())) {
							System.out.println("[P] Interface detected, getting class '"+tmpClass.getCanonicalName()+"'...");
							this.plugins.add(tmpClass);
						}
					}
					
				}
			}
			System.out.println("[P] Loading done for "+files[i].getName());
			
		}
	}
	
}
