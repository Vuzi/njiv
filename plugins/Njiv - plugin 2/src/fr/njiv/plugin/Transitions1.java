package fr.njiv.plugin;

import java.util.ArrayList;
import java.util.List;

import fr.njiv.UI.diaporama.NjivDiaporamaTransition;
import fr.njiv.UI.diaporama.plugin.NjivDiaporamaPlugin;

public class Transitions1 implements NjivDiaporamaPlugin{

	@Override
	public String getDescription() {
		return "First pack of transitions";
	}

	@Override
	public String getName() {
		return "Transition pack 1";
	}

	@Override
	public List<NjivDiaporamaTransition> getTransitions() {
		ArrayList<NjivDiaporamaTransition> list = new ArrayList<NjivDiaporamaTransition>();
		list.add(new ZoomInEffect());
		return list;
	}

}
