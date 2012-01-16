package maro.example.soccer.ea;

import maro.core.EnvironmentAction;
import maro.core.IntelligentEnvironment;
import jason.asSyntax.Structure;

public class OkAction extends EnvironmentAction
{
	@Override
	public String getName() { return "ok"; }

	//@Override
	//public int requiredSteps() { return 1; }

	@Override
	public boolean execute(String agName, Structure action, IntelligentEnvironment ie) {
		return true;
	}
}
