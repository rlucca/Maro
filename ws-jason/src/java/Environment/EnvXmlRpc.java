package maro.Environment;

import maro.xmlrpc.EnvironmentClient;
import maro.xmlrpc.Perceive;

import jason.environment.Environment;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * <p>Execution sequence:   
 *     <ul><li>setEnvironmentInfraTier, 
 *         <li>init, 
 *         <li>(getPercept|executeAction)*, 
 *         <li>stop.
 *     </ul>
 * 
 */
public class EnvXmlRpc extends Environment {
	protected EnvironmentClient ec;
	protected String secret;
	protected String url;

	private EnvironmentClient getClient() throws Exception {
		if (ec == null)
			ec = new EnvironmentClient(url, secret);
		return ec;
	}

	/* TODO
		Test if the server is reliable
	*/
    public void init(String[] args) {
		url = null;
		secret = null;

		if (args.length == 1) {
			url = args[0];
		} else if (args.length == 2) {
			url = args[0];
			secret = args[1];
		} else {
			//throw new Exception("Please, inform a URL and, optionaly, a secret");
		}

		try {
			EnvironmentClient e = getClient();
			if (e == null) throw new Exception("Unknow error");
		} catch (Exception ex) {
			System.err.println("Error connecting to environment: " + ex);
			ex.printStackTrace();
		}
    }

	// This method is called before the end of the simulation.
    /*public void stop() {
		super.stop();
    }*/

	// Nao precisa mudar nada...
    //public boolean isRunning() {
    //    return super.isRunning();
    //}

	/* LUCCCA2 recept this from a WS?
     * Returns percepts for an agent.  A full copy of both common
     * and agent's percepts lists is returned.
     * 
     * It returns null if the agent's perception doesn't changed since
     * last call.
     * 
     * This method is to be called by TS and should not be called 
     * by other objects.
     */
    public List<Literal> getPercepts(String agName) {
		//return super.getPercepts(agName);
		List<Literal> ll = new ArrayList<Literal>();

		try {
			Object[] lo = getClient().doPercepts(agName);
			if (lo == null) return null;
			ll.addAll( Perceive.undump(lo) );
		} catch (Exception ex) {
			System.err.println("Error in getPercepts: " + ex);
			ex.printStackTrace();
			return null;
		}

		return ll;
    }

    /** LUCCCA? recept this from a WS?
     *  Returns a copy of the perception for an agent. 
     * 
     *  It is the same list returned by getPercepts, but 
     *  doesn't consider the last call of the method.
     */
    public List<Literal> consultPercepts(String agName) {
        //return super.consultPercepts(agName);
		List<Literal> ll = new ArrayList<Literal>();

		try {
			Object[] lo = getClient().getPercepts(agName);
			if (lo == null) return null;
			ll.addAll( Perceive.undump(lo) );
		} catch (Exception ex) {
			System.err.println("Error in consultPercepts: " + ex);
			ex.printStackTrace();
			return null;
		}

		return ll;
    }

    /** LUCCCA create new percept in WS
	 Adds a perception for all agents */
    public void addPercept(Literal per) {
		//super.addPercept(per);
		try {
			boolean isOK = getClient().addPercept( Perceive.dumpLiteral(per), null );
			if (isOK == false) throw new Exception("Error in addPercept global");
		} catch (Exception ex) {
			System.err.println("Error in addPercept: " + ex);
			ex.printStackTrace();
		}
    }

    /** LUCCCA
	 Adds a perception for a specific agent */
    public void addPercept(String agName, Literal per) {
		//super.addPercept(agName, per);
		try {
			boolean isOK = getClient().addPercept( Perceive.dumpLiteral(per), agName );
			if (isOK == false) throw new Exception("Error in addPercept local");
		} catch (Exception ex) {
			System.err.println("Error in addPercept: " + ex);
			ex.printStackTrace();
		}
    }

    /** LUCCCA remove percept in WS
	 Removes a perception in the common perception list */
    public boolean removePercept(Literal per) {
        //return super.removePercept(per);
		try {
			boolean isOK = getClient().removePercept( Perceive.dumpLiteral(per), null );
			if (isOK == false) throw new Exception("Error in removePercept global");
			return isOK;
		} catch (Exception ex) {
			System.err.println("Error in removePercept: " + ex);
			ex.printStackTrace();
		}
		return false;
    }

    /** LUCCCA
	 Removes a perception for an agent */
    public boolean removePercept(String agName, Literal per) {
		//return super.removePercept(agName, per);
		try {
			boolean isOK = getClient().removePercept( Perceive.dumpLiteral(per), agName );
			if (isOK == false) throw new Exception("Error in removePercept local");
			return isOK;
		} catch (Exception ex) {
			System.err.println("Error in removePercept: " + ex);
			ex.printStackTrace();
		}
		return false;
    }

    /** LUCCCA erase all percept in WS
	 Clears the list of global percepts */
    public void clearPercepts() {
		//super.clearPercepts();
		try {
			boolean isOK = getClient().clearPercepts( null );
			if (isOK == false) throw new Exception("Error in clearPercepts global");
		} catch (Exception ex) {
			System.err.println("Error in clearPercepts: " + ex);
			ex.printStackTrace();
		}
	}

    /** LUCCCA
	 Clears the list of percepts of a specific agent */
    public void clearPercepts(String agName) {
		//super.clearPercepts(agName);
		try {
			boolean isOK = getClient().clearPercepts( agName );
			if (isOK == false) throw new Exception("Error in clearPercepts local");
		} catch (Exception ex) {
			System.err.println("Error in clearPercepts: " + ex);
			ex.printStackTrace();
		}
	}

    /** LUCCCA2
     * Executes an action on the environment. This method is probably overridden in the user environment class.
     */
    public boolean executeAction(String agName, Structure act) {
		//return super.executeAction(agName, act);
		try {										/** TODO dumpStructure here can be good */
			boolean isOK = getClient().executeAction( Perceive.dumpLiteral((Literal) act), agName );
			if (isOK == false) throw new Exception("Error in action from " + agName);
			return isOK;
		} catch (Exception ex) {
			System.err.println("Error in action from " + agName + ": " + ex);
			ex.printStackTrace();
		}
		return false;
    }

	/* As funcoes abaixo foram consideradas irelevantes para o TI
	   e podem ser desenvolvidas no futuro aos poucos.
	*/

    public int removePerceptsByUnif(Literal per) {
		System.err.println("removePerceptsByUnif/1 do not use!!");
		return 0;
    }

    public int removePerceptsByUnif(String agName, Literal per) {
		System.err.println("removePerceptsByUnif/2 do not use!!");
		return 0;
    }

    public boolean containsPercept(Literal per) {
		System.err.println("containsPercept/2 do not use!!");
		return false;
    }

    public boolean containsPercept(String agName, Literal per) {
		System.err.println("containsPercept/2 do not use!!");
		return false;
    }
}
