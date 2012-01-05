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
import jason.JasonException;
import maro.example.sims.House; // this is the environment class
import maro.example.sims.HouseModel;

public class getItemsAtPlace extends getPlaces {
	@Override public int getMinArgs() { return 2; }
    @Override public int getMaxArgs() { return 2; }

	@Override protected void checkArguments(Term[] args)
					throws JasonException
	{
		super.checkArguments(args);
		if (!args[0].isAtom() && !args[0].isString()) {
            throw JasonException.createWrongArgument(this,"first argument must be a atom or a string");
		}
	}

    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
					throws Exception
	{
		checkArguments(args);

		String place;

        if (args[0].isAtom())
		    place = ((Atom) args[0]).getFunctor();
        else
            place = ((StringTerm)args[0]).getString();
		
		ListTerm ln = new ListTermImpl();
		ListTerm tail = ln;
		HouseModel hm = getTerrain(ts);
		if (hm == null) return un.unifies(args[1], ln);
		for (String itemName : hm.getItemsAtPlace(place)) {
			tail = tail.append(ASSyntax.createString(itemName));
		}
		return un.unifies(args[1], ln);
	}
}

