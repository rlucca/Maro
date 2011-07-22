package maro.Environment;

import jason.environment.Environment;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;

public class EnvHello extends Environment {
	public void init(String[] args) {
		System.out.println(args.length);

		for (String arg: args)
			System.out.println(arg);
	}

    public boolean executeAction(String agName, Structure act) {
		String actName = act.getFunctor();
		System.out.println("Action " + actName + " received!");
		if (actName.equals("hello")) {
			Literal l = Literal.parseLiteral("world[source(lucca)]");
			addPercept(l);
		}
		if (actName.equals("clean")) {
			clearPercepts();
		}
		return true;
	}
}

