package maro.example.sims.ea;

import maro.example.sims.House;
import maro.core.EnvironmentAction;
import maro.example.sims.HouseModel;
import maro.core.IntelligentEnvironment;
import jason.asSyntax.Structure;

public class NopeAction extends EnvironmentAction
{
	@Override
	public String getName() { return "nope"; }

	//@Override
	//public int requiredSteps() { return 1; }

	@Override
	public boolean execute(String agName, Structure action, IntelligentEnvironment ie) {
		House h = (House) ie;
		if (h == null) return false;
		HouseModel hm = h.getModel();
		if (hm == null) return false;
		return hm.nope(agName);
	}
}
