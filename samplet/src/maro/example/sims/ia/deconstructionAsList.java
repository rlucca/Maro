package sims.ia;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.asSyntax.Atom;
import jason.JasonException;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;

public class deconstructionAsList extends DefaultInternalAction
{
	@Override public int getMinArgs() { return 2; }
    @Override public int getMaxArgs() { return 2; }

    @Override
    protected void checkArguments(Term[] args) throws JasonException {
        super.checkArguments(args); // check number of arguments
        if (!args[0].isStructure())
            throw JasonException.createWrongArgument(this,"first argument '"+args[0]+"'is not a structure.");
        if (!args[1].isVar() && !args[1].isList())
            throw JasonException.createWrongArgument(this,"last argument '"+args[1]+"'is not a list nor a variable.");
		throw JasonException.createWrongArgument(this,"deprecated. Use operator `=..'");
    }

    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
					throws Exception
	{
		checkArguments(args);

        Structure s = (Structure) args[0];

		ListTerm ln = new ListTermImpl();
		ListTerm tail = ln;

        tail.append(new Atom(s.getFunctor()));
		for (Term placeName : s.getTerms()) {
			tail = tail.append(placeName);
		}

		return un.unifies(args[1], ln);
	}
}
