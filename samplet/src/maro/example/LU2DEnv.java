package maro.example;

import maro.wrapper.AnnotatedEnvironment;

import jason.environment.grid.Location;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class LU2DEnv extends AnnotatedEnvironment {
	private List<String> deathRequire;
	private LUModel model;

	public LU2DEnv () {
		super();

		deathRequire = new ArrayList<String> ();

		options.add( new Option<Boolean> (false,
					null,
					"isn't a valid boolean.",
					false));
	}

	@SuppressWarnings("unchecked")
	protected boolean getDebugViewer() {
		Option<Boolean> ob = options.get(5);
		if (ob != null) return ob.value;
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean loadOptions(String []args) {
		boolean exit = super.loadOptions(args);
		Option<Boolean> ob;

		ob = options.get(5);
		try {
			ob.value = Boolean.parseBoolean(args[5]);
		} catch (Exception e) {
			getLogger().warning("The fiveth argument "+ob.help);
			getLogger().warning("\tThis argument is"+ob.description);
			if (ob.isRequired) exit = false;
		}

		return exit;
	}

	@Override
	public void init(String[] args) {
		super.init(args);

		boolean debugWithViewer = getDebugViewer();
		model = new LUModel ( this );
		model.setView( new LU2DView(model, this, "All Environment", debugWithViewer) );

		super.destroyAnnots();
		updateAgsPercept();
		super.initDefault();
	}

	@Override
	public void stop() {
		getLogger().fine("cleaning all to exit");
		super.stop();
	}

	/*@Override
	  public void addPercept(Literal per) {
	  Literal l = putAnnotations(null, per);
	  super.addPercept(l);
	  }

	  @Override
	  public void addPercept(String agName, Literal per) {
	  Literal l = putAnnotations(agName, per);
	  super.addPercept(agName, l);
	  }*/

	/** This method is called after the execution of the action and before to send 'continue' to the agents */
	@Override
	protected void updateAgsPercept() {
		if (getStep() == 0) return ; // not to send. This is the handshake step

		for (int i = 0; i < model.getNbOfAgs(); i++) {
			String name = model.getNameById(i);
			Integer type;
			if (name == null) {
				getLogger().warning("agent id "+i+" is unknow now... not sending perceptions on "+getStep());
				continue;
			}

			type = model.getTypeById(i);
			if (type != null)
				updateAgPercept(name, i, type);
		}
	}

	protected void
	updateAgPercept(String name, int id, Integer ptype) {
		Integer type = ptype;
		Location location = model.getAgPos(id);
		char orientation = model.getOrientation(id);
		int range = 2;
		Map<Integer, Map<Integer, Location> > others = model.findAllOthers(id, range);
		Map<Integer, Map<Integer, Location> > planets = model.findOthers(id, 1, range);
		int countPlanets = model.countOthers(id,  1, others);
		int countShips = model.countOthers(id,    4, others);
		int countIShips = model.countOthers(id,   8, others);
		int countAllShips = countShips + countIShips;
		int life = model.getLife(id, 0);
		Literal lit = null;
		Location borderPos = new Location(-1, -1);

		clearPercepts(name);

		try {
			lit = ASSyntax.parseLiteral("myself(\""+
						model.typeToString(ptype)+"\", \""+
						name+"\","+
						id+")["+model.getSourceJasonAnnotations(id)+"]");

		} catch (Exception e) {
			lit = ASSyntax.createLiteral("myself",
						ASSyntax.createString(model.typeToString(ptype)),
						ASSyntax.createString(name),
						ASSyntax.createNumber(id));
		}

		addPercept(name, lit);

		if (location.x <= 2 && location.x >= 0)
			borderPos.x = 0;
		else if (location.x <= 49 && location.x >= 47)
			borderPos.x = 49;
			
		if (location.y <= 2 && location.y >= 0)
			borderPos.y = 0;
		else if (location.y <= 49 && location.y >= 47)
			borderPos.y = 49;

		if (borderPos.x >= 0 || borderPos.y >= 0) {
			Map<Integer, Map<Integer, String> > borderMap = model.findBorder(location, borderPos);
			for (Integer x : borderMap.keySet()) {
				Map<Integer, String> subBorderMap = borderMap.get(x);

				for (Integer y : subBorderMap.keySet()) {
					String annotation = subBorderMap.get(y);
					Literal lPosition;
					try {
						lPosition = ASSyntax.parseLiteral("border("+
								x + "," + y
								+")["+annotation+"]");

					} catch (Exception e) {
						lPosition = ASSyntax.createLiteral("border",
								ASSyntax.createNumber(x),
								ASSyntax.createNumber(y));
					}
					addPercept(name, lPosition);
				}
			}
		}

		addPercept(name, ASSyntax.createLiteral("qtyPlanets",
					ASSyntax.createNumber(countPlanets)));

        for (Integer key : others.keySet()) {
            Map<Integer, Location> mil = others.get(key);

            for (Integer keyType : mil.keySet()) {
                Location pos = mil.get(keyType);
                boolean add = true;

                switch (keyType) {
                    case 1: // planets
                        addPercept(name, ASSyntax.createLiteral("planet",
                                    ASSyntax.createNumber(pos.x),
                                    ASSyntax.createNumber(pos.y)));
                        if (pos.x == location.x && pos.y == location.y)
                            addPercept(name, ASSyntax.createLiteral("onPlanet"));
                        break;
                    case 4: // ships and iShips are the same!
                    case 8: 
                        for (Map<Integer, Location> mil2: planets.values()) {
                            for (Integer key2 : mil2.keySet()) {
                                Location pos2 = mil2.get(key2);
                                if (pos2.x == pos.x && pos2.y == pos.y) {
                                    add = false;
                                    countAllShips -= 1;
                                    break;
                                }
                            }
                        }

                        if (add == true) {
                            Literal ship = null;
                            try {
                                ship = ASSyntax.parseLiteral("ship("
                                        +pos.x+","
                                        +pos.y+")["
                                        +model.getTargetJasonAnnotations(key)+"]");
                            } catch (Exception e) {
                                ship = ASSyntax.createLiteral("ship",
                                        ASSyntax.createNumber(pos.x),
                                        ASSyntax.createNumber(pos.y));
                            }

                            if ( ship != null ) addPercept(name, ship);
                        }
                        break;
                }
            }
        }
		addPercept(name, ASSyntax.createLiteral("qtyShips",
					ASSyntax.createNumber(countAllShips)));


		Literal step    = ASSyntax.createLiteral("step",
							ASSyntax.createNumber(getStep()));
		Integer secs    = getStep() * 870;
		Integer day;
		Integer hour;
		Integer mins;
		String shift;

		// secs minus days
		day  = (secs / 86400) + 1;
		secs = secs % 86400;

		// secs minus hours
		hour = (secs / 3600);
		secs = (secs % 3600);

		// secs minus minutes
		mins = (secs / 60);
		// the remain is not important
		//secs = (secs % 60);

		if (hour < 5) shift = "M"; // (M) adrugada
		else if (hour < 12) {
			if (hour == 5 && mins <= 30)
				shift = "M";
			else
				shift = "A"; // M (a) nha
		} else if (hour < 18) {
			if (hour == 12 && mins == 0)
				shift = "A";
			else
				shift = "T"; // (T) arde
		} else { // 18 - 23h59
			if (hour == 18 && mins == 0)
				shift = "T";
			else
				shift = "N"; // (N) oite
		}


		step.addAnnot(ASSyntax.createLiteral("day", ASSyntax.createNumber(day)));
		step.addAnnot(ASSyntax.createLiteral("hour", ASSyntax.createNumber(hour)));
		step.addAnnot(ASSyntax.createLiteral("minute", ASSyntax.createNumber(mins)));
		step.addAnnot(ASSyntax.createLiteral("shiftOfDay", ASSyntax.createString(shift)));


		addPercept(name, step);
	}

	/** to be overridden by the user class */
	@Override
	protected void stepStarted(int step) {
		getLogger().info("Started step " + step);

		jason.environment.grid.GridWorldView v = null;
		if (model != null) {
			v = model.getView();
			if (v != null) v.update();
		}

		synchronized (deathRequire) {
			if (deathRequire.isEmpty()) {
				return ;
			}

			while (deathRequire.isEmpty() == false) {
				String agName = deathRequire.get(0);
				getEnvironmentInfraTier().getRuntimeServices().killAgent(agName);
				deathRequire.remove(0);
			}
		}

		updateNumberOfAgents();
	}

	/** to be overridden by the user class */
	@Override
	protected void stepFinished(int step, long elapsedTime, boolean byTimeout) {
		super.stepFinished(step, elapsedTime, byTimeout);
	}

	/*
	   NAO RETORNE ZERO, A FUNCAO STARTNEWCYCLE ENLOQUECE!
	 */
	@Override
	protected int requiredStepsForAction(String agName, Structure action) {
		if (getStep() == 0) {
			if (action.getFunctor().equals("iam")) {
				return 1;
			} else if (action.getFunctor().equals("nope")) {
				return 1;
			}

			getLogger().warning("Agent "+ agName + " doing unknow action "+action);
			return 10;
		}

		if (action.getFunctor().equals("increasePopulation")) {
			return 3;
		} else if (action.getFunctor().equals("changeOrientationTo")) {
			return 1;
		} else if (action.getFunctor().equals("recover")) {
			return 1;
		} else if (action.getFunctor().equals("fire")) {
			return 1;
		} else if (action.getFunctor().equals("death")) {
			return 1;
		} else if (action.getFunctor().equals("forward")) {
			return 1;
		} else if (action.getFunctor().equals("nope")) {
			return 1;
		}

		getLogger().warning("Agent "+ agName + " doing unknow action "+action);
		return 10; // this is called by scheduleAction
	}

	@Override
	public boolean executeAction(String agName, Structure act) {
		if (getStep() == 0) {
			if ( verifyHello(agName, act) == 1 ) {
				Integer agId = model.getIdByName(agName);
				if (agId == null) {
					getLogger().warning("Agent unknow "+ agName);
					return false;
				}
				synchronized (model) {
					model.randomInitialPosition(agId);
				}
				return true;
			} else if (act.getFunctor().equals("nope")) {
				return true;
			}

			getLogger().warning("Agent "+ agName + " doing unknow action: " + act);
			return false;
		}

		if (act.getFunctor().equals("death")) {
			synchronized (model) {
				model.disableAgent(agName);
				synchronized (deathRequire) {
					deathRequire.add(agName);
				}
			}
			return true;
		} else if (act.getFunctor().equals("increasePopulation")) {
			NumberTerm nt = (NumberTerm) act.getTerm(0);
			double val = nt.solve();
			Integer agId = model.getIdByName(agName);
			synchronized (model) {
				model.population(agId, (int)val);
			}
			return true;
		} else if (act.getFunctor().equals("changeOrientationTo")) {
			StringTerm nt = (StringTerm) act.getTerm(0);
			String val = nt.getString();
			Integer agId = model.getIdByName(agName);
			if (val != null)
				synchronized (model) {
					model.setOrientation(agId, val.charAt(0));
				}
			return true;
		} else if (act.getFunctor().equals("forward")) {
			Integer agId = model.getIdByName(agName);
			synchronized (model) {
				model.forward(agId);
			}
			return true;
		} else if (act.getFunctor().equals("recover")) {
			Integer agId = model.getIdByName(agName);
			if (agId == null) return true;
			synchronized (model) {
				model.getLife(agId, 3+model.nextInt(7));
			}
			return true;
		} else if (act.getFunctor().equals("fire")) {
			Integer agId = model.getIdByName(agName);
			Integer agTarget = model.attackFrom(agId);
			if (agTarget == null) return true;
			synchronized (model) {
				model.fire(agId, agTarget);
			}
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
			synchronized (model) {
				int id = model.getNextId();
				model.setAgentAndType(ag, id, isValid);
			}
		}
		return 1;
	}
}
