package sims.ia;

import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.infra.centralised.CentralisedAgArch;
import jason.infra.centralised.CentralisedEnvironment;
import jason.architecture.AgArch;
import maro.example.sims.House; // this is the environment class
import maro.example.sims.HouseModel;

public class getItems extends getPlaces
{
	@Override public int getMinArgs() { return 1; }
    @Override public int getMaxArgs() { return 1; }

    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
					throws Exception
	{
		checkArguments(args);

		ListTerm ln = new ListTermImpl();
		ListTerm tail = ln;
		HouseModel hm = getTerrain(ts);
		if (hm == null) return un.unifies(args[0], ln);
		for (String placeName : hm.getItemsFromItemView()) {
			tail = tail.append(new Atom(placeName));
		}
		return un.unifies(args[0], ln);
	}
}
