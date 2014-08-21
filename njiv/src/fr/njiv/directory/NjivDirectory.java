package fr.njiv.directory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import fr.njiv.NjivLoadingException;
import fr.njiv.Utils;

public class NjivDirectory {

	private String path;
	private File here;
	// Subdirectories
	private ArrayList<NjivDirectory> subDirectories = new ArrayList<>();
	// Image loadable in the directory
	private ArrayList<File> images = new ArrayList<>();
	
	private FilenameFilter filter;
	
	/**
	 * Create a NjivDirectory from a path
	 * @param path
	 * @throws NjivLoadingException
	 */
	public NjivDirectory(String path) throws NjivLoadingException {
		this.path = path;
		update();
	}

	/**
	 * Create a NjivDirectory from a file object
	 * @param here
	 * @throws NjivLoadingException
	 */
	public NjivDirectory(File here) throws NjivLoadingException {
		this.path = here.getAbsolutePath();
		this.here = here;
		update();
	}
	
	/**
	 * Update the list of images contained and subdirectories
	 * @throws NjivLoadingException
	 */
	private void update() throws NjivLoadingException {
		if(here == null)
			here = new File(this.path);
		updateFilter();
		
		if(!here.exists()) {
			throw new NjivLoadingException("Error while loading directory : '"+this.path+"' does not exist");
		}
		
		if(here.isFile()) {
			throw new NjivLoadingException("Error while loading directory : '"+this.path+"' is a file");
		}
		
		// For each result
		for(File f : here.listFiles(filter)) {
			if(f.isDirectory()) {
				this.subDirectories.add(new NjivDirectory(f));
			} else {
				this.images.add(f);
			}
		}
	}
	
	/**
	 * Update the filter based on Utils
	 */
	private void updateFilter() {
		final String[] filters = Utils.getSupportedFormat();
		
		this.filter = new FilenameFilter() {
			public boolean accept(File f, String name) {
				if(f.isDirectory()) {
					// Directory
					return true;
				} else {
					for(String filter : filters) {
						// If its suppported
						if(name.endsWith(filter)) {
							return true;
						}
					}
				}
				return false;
			}
		};
	}

	public String getPath() {
		return path;
	}

	public File getFile() {
		return here;
	}

	public ArrayList<NjivDirectory> getSubDirectories() {
		return subDirectories;
	}

	public ArrayList<File> getImages() {
		return images;
	}

	public FilenameFilter getFilter() {
		return filter;
	}
	
}
