package fr.njiv.UI.diaporama.plugin;

import java.util.List;

import fr.njiv.UI.diaporama.NjivDiaporamaTransition;

public interface NjivDiaporamaPlugin {
	
	/**
	 * Return the name of the plugin
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the description of the plugin
	 * @return
	 */
	public String getDescription();

	/**
	 * Return the transitions from the plugin
	 * @return
	 */
	public List<NjivDiaporamaTransition> getTransitions();
	
}
