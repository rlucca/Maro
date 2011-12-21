package maro.core;

import jason.asSyntax.Structure;

public abstract class EnvironmentAction
{
	abstract public String getName();

	public int requiredSteps() { return 1; }

	public boolean execute(String agName, Structure action, IntelligentEnvironment ie) {
		return false;
	}
}
