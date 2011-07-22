import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;

public class RoomEnv extends Environment {
    Literal ld  = Literal.parseLiteral("locked(door)");
    Literal nld = Literal.parseLiteral("~locked(door)");
    boolean doorLocked = true;
    @Override
    public void init(String[] args) {
        addPercept(ld);
    }
    @Override
    public boolean executeAction(String ag, Structure act) {
	    System.out.println("Agent "+ag+" is doing "+act);
	    clearPercepts();
	    if (act.getFunctor().equals("lock"))
		    doorLocked = true;
	    if (act.getFunctor().equals("unlock"))
		    doorLocked = false;
	    if (doorLocked)  addPercept(ld);
	    else  addPercept(nld);
	    return true;
    }
}

