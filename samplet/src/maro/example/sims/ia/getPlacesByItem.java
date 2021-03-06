package sims.ia;

import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.asSyntax.Atom;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.ListTermImpl;
import jason.infra.centralised.CentralisedAgArch;
import jason.infra.centralised.CentralisedEnvironment;
import jason.architecture.AgArch;
import maro.example.sims.House; // this is the environment class
import maro.example.sims.HouseModel;

public class getPlacesByItem extends getItemsAtPlace {
    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
					throws Exception
	{
		checkArguments(args);
		
		String item;

        if (args[0].isAtom())
		    item = ((Atom) args[0]).getFunctor();
        else
            item = ((StringTerm)args[0]).getString();

		ListTerm ln = new ListTermImpl();
		ListTerm tail = ln;
		HouseModel hm = getTerrain(ts);
		if (hm == null) return un.unifies(args[1], ln);
		for (String placeName : hm.getPlacesByItem(item)) {
			tail = tail.append(ASSyntax.createString(placeName));
		}
		return un.unifies(args[1], ln);
	}
}
