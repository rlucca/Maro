package sims.ia;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.infra.centralised.CentralisedAgArch;
import jason.infra.centralised.CentralisedEnvironment;
import jason.architecture.AgArch;
import maro.example.sims.House; // this is the environment class
import maro.example.sims.HouseModel;

public class getPlaces extends DefaultInternalAction
{
	@Override public int getMinArgs() { return 1; }
    @Override public int getMaxArgs() { return 1; }

	protected HouseModel getTerrain(TransitionSystem ts) {
		AgArch agArch = ts.getUserAgArch();
		while (agArch != null && !(agArch instanceof CentralisedAgArch)) {
			agArch = agArch.getNextAgArch();
		}

		if (agArch == null) return null;

		CentralisedAgArch caa = (CentralisedAgArch) agArch;
		CentralisedEnvironment ce = caa.getEnvInfraTier();
		if (ce == null) return null;
		House h = (House) ce.getUserEnvironment();
		if (h == null) return null;
		return h.getModel();
	}

    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
					throws Exception
	{
		checkArguments(args);

		ListTerm ln = new ListTermImpl();
		ListTerm tail = ln;
		HouseModel hm = getTerrain(ts);
		if (hm == null) return un.unifies(args[0], ln);
		for (String placeName : hm.getPlacesFromPlaceView()) {
			tail = tail.append(ASSyntax.createString(placeName));
		}
		return un.unifies(args[0], ln);
	}
}
