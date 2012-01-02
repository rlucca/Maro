package maro.core;

import maro.wrapper.OwlApi;
import jason.asSyntax.Structure;
import java.util.HashMap;
import java.io.File;

public class AnnotatedEnvironment extends IntelligentEnvironment
{
	protected OwlApi oapi;
	private int indexFileName;
	// String X String == AgentName X Action
	protected HashMap<String, String> lastAction;

	public AnnotatedEnvironment () {
		super();
		oapi = null;
		indexFileName = options.size();
		lastAction = new HashMap<String, String> ();
        options.add( new Option<String> (true,
                        " the filename of the annotated ontology",
                        " isn't a readable file",
                        null) );
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean loadOptions(String []args) {
		boolean exit = super.loadOptions(args);
		Option<String> os = options.get(indexFileName);
        try {
            File file = new File(args[indexFileName]);
            if (file.canRead() == false) {
                throw new Exception();
            }
            os.value = args[indexFileName];
        } catch (Exception e) {
            getLogger().warning("The "+indexFileName+"o. argument "+os.help);
            getLogger().warning("\tThis argument is"+os.description);
            if (os.isRequired) exit = false;
        }

		return exit;
	}

    @SuppressWarnings("unchecked")
    public void initOntology() {
        Option<String> os = options.get(indexFileName);
        String filename = os.value;
        oapi = new OwlApi ();

        try {
            oapi.loadOntologyFromFile(filename);
        } catch (Exception e) {
            getLogger().warning("Problem reading ontology from " + filename);
            System.exit(52);
        }
	}

	@Override
	protected int requiredStepsForAction(String agName, Structure action) {
		changeLastAction(agName, action.getFunctor());
		return super.requiredStepsForAction(agName, action);
	}

    // This function is called always after the step finish and before the start of the new step
    // to change the last action. This is called only on the first step of the action...
	protected void changeLastAction(String agName, String actionName) {
		synchronized (lastAction) {
			lastAction.put(agName, actionName);
		}
	}

	public String getLastAction(String agName) {
		String ret;
		synchronized (lastAction) {
			ret = lastAction.get(agName);
		}
		return ret;
	}
}
