package eoaus;

import jason.environment.TimeSteppedEnvironment;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.ASSyntax;


public class Env extends TimeSteppedEnvironment
{
	public void init(String[] args) {
        super.init(new String[] { "3000" } );
        setOverActionsPolicy(OverActionsPolicy.ignoreSecond);
		updateAgsPercept();

		/*System.out.println(args.length);
		for (String arg: args)
			System.out.println(arg);*/
	}

	@Override
	public boolean executeAction(String agName, Structure act) {
		String actName;
		Literal lit;

		actName = act.getFunctor();
		System.out.println("Action " + actName + " realized by " + agName + "!");

		// source eh percept para ser destruido no proximo ciclo
		lit = Literal.parseLiteral("done("+actName+")[source(percept)]");

		addPercept(agName, lit); // somente agente que fez recebe
		//addPercept(lit); // todos agentes recebem (inclui os que nao fizeram)
		return true;
	}

	@Override
	protected void stepStarted(int step) {
        addPercept(ASSyntax.createLiteral("step", ASSyntax.createNumber(getStep())));
	}

	@Override
    protected void stepFinished(int step, long elapsedTime, boolean byTimeout) {     
		//removePercept(ASSyntax.createLiteral("step", ASSyntax.createNumber(getStep())));
		clearAllPercepts();
    }

}
