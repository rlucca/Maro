package maro.example.soccer.ia;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;
import jason.asSyntax.Literal;
import jason.asSyntax.StringTerm;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.infra.centralised.CentralisedAgArch;
import jason.environment.Environment;

public class addPerception extends DefaultInternalAction
{
	@Override public int getMinArgs() { return 1; }
    @Override public int getMaxArgs() { return 2; }

    @Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
					throws Exception
	{
		checkArguments(args);

		AgArch aa = ts.getUserAgArch();
		if (!(aa instanceof CentralisedAgArch)) {
			throw new JasonException("Architecture is not centralised");
		}

		CentralisedAgArch caa = (CentralisedAgArch) aa;
		Environment e = caa.getEnvInfraTier().getUserEnvironment();

        if (args.length == 2) {
            String name = ((StringTerm)args[0]).getString();
			for (Term t: ((Literal)args[1]).getTerms())
				t.apply(un);
            e.addPercept(name, (Literal) args[1]);
        } else if (args.length == 1) {
			for (Term t: ((Literal)args[0]).getTerms())
				t.apply(un);
            e.addPercept((Literal) args[0]);
        } else {
			return false;
		}

		return true;
	}
}
