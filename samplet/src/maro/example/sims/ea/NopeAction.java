package maro.example.sims.ea;

import maro.core.EnvironmentAction;
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
		// nothing to do
		return true;
	}
}
