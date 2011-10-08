package maro.example;

import jason.environment.TimeSteppedEnvironment;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.ASSyntax;
//import java.util.Set;

public class LU2DEnv extends TimeSteppedEnvironment {
	private int lastStep = Integer.MAX_VALUE;
	private boolean policyIsQueue = true;
	private long sum = 0;

	private LUModel model;

    @Override
    public void init(String[] args) {
		boolean debugWithViewer = false;
		int quantity;

		if (args.length < 4 || args.length > 5) {
			getLogger().warning("The first argument is a number for step timeout(ms)");
			getLogger().warning("The second argument is a number for last step possible");
			getLogger().warning("The third argument is a boolean indicate to add next action to queue or else fail the second action");
			getLogger().warning("The fourth argument is a quantity of agents");
			getLogger().warning("The fiveth argument is a optional boolean to indicate if is to show the viewer");
			return ;
		}

		super.init(args);

		try {
			lastStep = Integer.parseInt(args[1]);
		} catch (Exception e) {
			getLogger().warning("The second argument "+args[1]+" isn't a valid number");
		}

		try {
			policyIsQueue = Boolean.parseBoolean(args[2]);
		} catch (Exception e) {
			getLogger().warning("The third argument "+args[2]+" isn't a valid boolean");
		}

		if (policyIsQueue == true) {
			getLogger().fine("Setting over actions to queue second action.");
			super.setOverActionsPolicy(OverActionsPolicy.queue);
		} else {
			getLogger().fine("Setting over actions to fail second action.");
			super.setOverActionsPolicy(OverActionsPolicy.failSecond);
		}

		try {
			quantity = Integer.parseInt(args[3]);
		} catch (Exception e) {
			getLogger().warning("The fourth argument "+args[3]+" isn't a valid number");
			quantity = 100;
		}

		try {
			debugWithViewer = Boolean.parseBoolean(args[4]);
		} catch (Exception e) {
			debugWithViewer = false;
		}

		model = new LUModel ( quantity );
		model.setView( new LU2DView(model, this, "All Environment", debugWithViewer) );
		updateAgsPercept();
	}

    @Override
    public void stop() {
		getLogger().fine("cleaning all to exit");
        super.stop();
	}

	/*@Override
    protected boolean testEndCycle(Set<String> finishedAgs) {
		getLogger().fine("Testing end cycle!");
        return super.testEndCycle(finishedAgs);
    }*/

    /** This method is called after the execution of the action and before to send 'continue' to the agents */
	@Override
    protected void updateAgsPercept() {
        for (int i = 0; i < model.getNbOfAgs(); i++) {
            String name = model.getNameById(i);
            if (name == null) break;
            updateAgPercept(name, i);
        }
    }

    protected void
    updateAgPercept(String name, int id) {
		Integer type = model.getTypeById(id);
        jason.environment.grid.Location location = model.getAgPos(id);

        clearPercepts(name);

		if (type != null && type != 2) { // type and type different of person
			addPercept(name, ASSyntax.createLiteral("population",
						ASSyntax.createNumber(model.population(id))));
		}

        if (location != null) {
            addPercept(name, ASSyntax.createLiteral("position",
                                ASSyntax.createNumber(location.x),
                                ASSyntax.createNumber(location.y)));
        }

        addPercept(name, ASSyntax.createLiteral("step", ASSyntax.createNumber(getStep())));
    }

    /** to be overridden by the user class */
	@Override
    protected void stepStarted(int step) {
		getLogger().info("Started step " + step);
    }

    /** to be overridden by the user class */
	@Override
	protected void stepFinished(int step, long elapsedTime, boolean byTimeout) {
		long mean = (step > 0) ? (sum + elapsedTime) / (step + 1) : 0;
		getLogger().info("Finished step " + step + " after " + elapsedTime + " (mean "+ mean +")");
		sum = elapsedTime;
		if (byTimeout == true) getLogger().warning("Step " + step + " finished by timeout!");

		tryStop(lastStep);
    }

	public void tryStop(int lastStepToStop) {
		if (getStep() >= lastStepToStop) {
			getLogger().fine("Trying stop simulation...");
			try {
				getEnvironmentInfraTier().getRuntimeServices().stopMAS();
			} catch (Exception e) {
				getLogger().warning("Failed trying stop simulation! " + e);
			}
		}
	}

	/*
		NAO RETORNE ZERO, A FUNCAO STARTNEWCYCLE ENLOQUECE!
	*/
	@Override
    protected int requiredStepsForAction(String agName, Structure action) {
        if (getStep() == 0) {
            int hellow = verifyHello(agName, action);

            if (hellow > 0) {
                return hellow;
            } else if (action.getFunctor().equals("nope")) {
                return 1;
            }

            getLogger().warning("Agent "+ agName + " doing unknow action "+action);
            return 10;
        }

        if (action.getFunctor().equals("increasePopulation")) {
            return 3;
        } else if (action.getFunctor().equals("nope")) {
            return 1;
        }

		getLogger().warning("Agent "+ agName + " doing unknow action "+action);
        return 10; // this is called by scheduleAction
    }

	@Override
    public boolean executeAction(String agName, Structure act) {
		if (getStep() == 1) {
			if ( verifyHello(agName, act) == 1 ) {
				Integer agId = model.getIdByName(agName);
				if (agId == null) {
					getLogger().warning("Agent unknow "+ agName);
					return false;
				}
				model.randomInitialPosition(agId);
				return true;
            } else if (act.getFunctor().equals("nope")) {
                return true;
            }

			getLogger().warning("Agent "+ agName + " doing unknow action: " + act);
			return false;
		}

        if (act.getFunctor().equals("increasePopulation")) {
            NumberTerm nt = (NumberTerm) act.getTerm(0);
            double val = nt.solve();
            Integer agId = model.getIdByName(agName);
            model.population(agId, (int)val);
            return true;
        } else if (act.getFunctor().equals("nope")) {
            return true;
        }

		getLogger().warning("Agent "+ agName + " doing action " + act + " not implemented on step " + getStep());
		return false;
	}

	// perceptions in theory are blocked when executing step's actions. good!

	protected int
	verifyHello(String ag, Structure s) {
		int isValid = model.typeFromLiteral(s);

		if ( isValid <= 0 ) {
			//getLogger().warning("Returnin zero: " + s);
			return 0;
		}

		// Mapeamos um agente para um numero unico,
		// se o agente voltar a aparecer o numero eh mantido o mesmo
		Integer i = model.getIdByName(ag);
		if (i == null) {
			int id = model.getNextId();
			model.setAgentAndType(ag, id, isValid);
		}
		return 1;
	}

}