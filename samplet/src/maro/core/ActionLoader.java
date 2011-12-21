package maro.core;

import jason.asSyntax.Structure;
import java.util.HashMap;

public class ActionLoader
{
	static private ActionLoader instance = null;
	private HashMap<String, EnvironmentAction> eahm;

	synchronized static public ActionLoader getInstance() {
		if (instance == null) instance = new ActionLoader();
		return instance;
	}

	private ActionLoader () { eahm = new HashMap<String, EnvironmentAction> (); }

	public void loadActions() { loadAllActions("maro.example.sims.ea"); }

	public void loadAllActions(String packet) {
		String [] names // TODO replace this by reflection
			= new String [] {
					"ChangeOrientationAction",
					"ForwardAction",
					"NopeAction"
				};

		for (String name: names) {
			Class c;

			try {
				c = Class.forName(packet+"."+name);
			} catch (Exception e) {
				c = null;
			}

			if (c == null) continue;

			EnvironmentAction ea;

			try {
				ea = (EnvironmentAction) c.newInstance();
			} catch (Exception e) {
				ea = null;
			}

			if (ea == null) continue;
			eahm.put(ea.getName(), ea);
		}
	}

	public Integer requiredStepsForAction(Structure action) {
		EnvironmentAction ea = eahm.get(action.getFunctor());
		if (ea == null) return null;
		return ea.requiredSteps();
	}

	public Boolean executeAction(String agName, Structure action, IntelligentEnvironment ie) {
		EnvironmentAction ea = eahm.get(action.getFunctor());
		if (ea == null) return null;
		return ea.execute(agName, action, ie);
	}
};
