package sims.ia;

import maro.example.sims.HouseModel;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Atom;
import jason.JasonException;
import jason.asSyntax.StringTerm;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;

public class planRoute extends getPlaces
{
	@Override public int getMinArgs() { return 2; }
    @Override public int getMaxArgs() { return 2; }

    @Override
    protected void checkArguments(Term[] args) throws JasonException {
        super.checkArguments(args); // check number of arguments
        if (!args[0].isString())
            throw JasonException.createWrongArgument(this,"first argument '"+args[0]+"'is not a structure.");
        if (!args[1].isVar() && !args[1].isList())
            throw JasonException.createWrongArgument(this,"last argument '"+args[1]+"'is not a list nor a variable.");
    }

    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
					throws Exception
	{
		checkArguments(args);

        StringTerm st = (StringTerm) args[0];
        HouseModel hm = getTerrain(ts);
		if (hm == null) return false;
        String[] array = hm.planRoute(ts.getUserAgArch().getAgName(), st.getString());
        if (array == null) return false;

		ListTerm ln = new ListTermImpl();
		ListTerm tail = ln;

		for (String orientation : array) {
			tail = tail.append(ASSyntax.createString(orientation));
		}

		return un.unifies(args[1], ln);
	}
}

