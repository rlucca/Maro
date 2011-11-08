package maro.example;

import jason.infra.centralised.RunCentralisedMAS;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class LUModel extends GridWorldModel {
	private Map<String, Integer> agentId = new HashMap<String, Integer>();
	private Map<Integer, Integer> agentType = new HashMap<Integer, Integer>();
    private Map<Integer, InnerData> agentData = new HashMap<Integer, InnerData>();
	private Object[][] map = null;

    public LUModel (int agentsQty) {
        // cell's width and cell's height are 50
        super(50, 50, agentsQty);

		map = new Object[50][50];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = new ArrayList<Integer>();
            }
        }
    }

	public jason.environment.grid.GridWorldView getView() {
		return view;
	}

    public int
    nextInt(int limit) {
        return random.nextInt(limit);
    }

    public void
    randomInitialPosition(int ag) {
        Location l = getAgPos(ag);
        if (l == null) {
            int x = nextInt(getWidth());
            int y = nextInt(getHeight());

            // No inicio nao eh permitido duas coisas no mesmo lugar
            while ( !isFree(x, y) ) {
                x = nextInt(getWidth());
                y = nextInt(getHeight());
            }

            setAgPos(ag, x, y);
        }
    }

	@Override
	public void setAgPos(int ag, int x, int y) {
		Location o = getAgPos(ag);
		ArrayList<Integer> ali;
		Object local;

		if (o != null) {
			synchronized(map) {
				int sz;
				// removendo do antigo...
				local = map[o.x][o.y];
				ali = (ArrayList<Integer>) local;
				sz = ali.size();
				if (sz == 0) {
					// skip only!
				} else if (sz == 1) {
					ali.remove(0);
					super.remove(AGENT, o.x, o.y);
				} else {
					int index = ali.indexOf(ag);
					if (index >= 0) {
						ali.remove(index);
						// nao se remove do tabuleiro pq a outras pecas no local
					}
				}
			}
		}

		// colocando no novo...
		synchronized (map) {
			local = map[x][y];
			ali = (ArrayList<Integer>) local;

			ali.add(ag);
			super.add(AGENT, x, y);
			agPos[ag] = new Location(x, y);
		}
	}

	public boolean havePlanet(int x, int y) {
		for (int id : agentId.values()) {
			Integer type = getTypeById(id);
			if (type != null && type == 1) {
				Location pos = getAgPos(id);
				if (pos != null && x == pos.x && y == pos.y)
					return true;
			}
		}
		return false;
	}

	public Map<Integer, Map<Integer,Location> > findAllOthers(int agId, int range) {
		return findOthers(agId, -1, range);
	}

	// devolve AgId x (AgType x AgLocation)
	public Map<Integer, Map<Integer,Location> > findOthers(int agId, int typeP, int range) {
		Map<Integer, Map<Integer,Location> > others = new HashMap<Integer, Map<Integer,Location> > ();
		Location pos = getAgPos(agId);
		if (pos == null) return others; // nao tem o que dizer, se a posicao eh null
		Location tl = new Location(pos.x - range, pos.y - range);
		Location br = new Location(pos.x + range, pos.y + range);

		for (int id : agentId.values()) {
			if (id != agId) {
				Integer type = getTypeById(id);
				Location posTarget = getAgPos(id);
				if (type == null || (typeP != -1 && type != typeP)) continue;

				if (posTarget.isInArea(tl, br) == true) {
					Map<Integer,Location> il = new HashMap<Integer, Location>();
					il.put(type, posTarget);
					others.put(id, il);
				}
			}
		}
		return others;
	}

	public int countOthers(int agId, int type, int range) {
		Map<Integer, Map<Integer,Location> > mp = findOthers(agId, type, range);
		return countOthers(agId, type, mp);
	}

	public int countOthers(int agId, int type, Map<Integer, Map<Integer,Location>> mp) {
		int count = 0;

		if (mp == null) return count;

		for (Map<Integer,Location> mil : mp.values()) {
			// Mais de um tipo no mesmo id nunca vai acontecer
			// entao eh possivel usar keySet
			for (Integer key : mil.keySet()) {
				if (key == type) count++;
			}
		}
		return count;
	}

	public String attackFrom(int agId) {
		Location pos = getAgPos(agId);
		Location posTarget = pos;
		char orientation = getOrientation(agId);

		switch (orientation) {
			case 'N':
				posTarget.y -= 1;
				break;
			case 'E':
				posTarget.x += 1;
				break;
			case 'S':
				posTarget.y += 1;
				break;
			case 'W':
				posTarget.x -= 1;
				break;
		}

        // Primeiro verifica a posicao atual...
		for (int id : agentId.values()) {
			if (id != agId) {
				Integer type = agentType.get(agId);
				Location target = getAgPos(id);
				if (type != null && type != 1
						&& target != null && target.x == pos.x && pos.y == target.y) {
					String name = getNameById(id);
					return name;
				}
			}
		}

        // Depois verifica a posicao que esta virado...
		for (int id : agentId.values()) {
			if (id != agId) {
				Integer type = agentType.get(agId);
				Location target = getAgPos(id);
				if (type != null && type != 1
						&& target != null && target.x == pos.x && pos.y == target.y) {
					String name = getNameById(id);
					return name;
				}
			}
		}

		return null;
	}

    public String
    getNameById(Integer i) {
        if (i != null) {
            for (Map.Entry<String,Integer> e : agentId.entrySet()) {
                if (e.getValue() == i)
                    return e.getKey();
            }
        }
        return null;
    }

    public Integer
    getIdByName(String agName) {
        return agentId.get(agName);
    }

    public int
    getNextId() {
        return agentId.size();
    }

    public Integer
    getTypeById(Integer agId) {
        return agentType.get(agId);
    }

    public void
    setAgentAndType(String agName, int agId, int type) {
        agentId.put(agName, agId);
        agentType.put(agId, type);

		if (type != 2) {  // 2 -> Person
			int pop = population(agId, 0);
			String orientation = "NESW";
			InnerData id = agentData.get(agId);
			if (id == null) {
				id = new InnerData();
                agentData.put(agId, id);
			}

			id.orientation = orientation.charAt(nextInt(4));
		}
    }

    public Integer
    typeFromLiteral(Term t) {
        Term			iAmPlanet = Literal.parseLiteral("iam(planet)");
        Term			iAmPerson = Literal.parseLiteral("iam(person)");
        Term			iAmShip   = Literal.parseLiteral("iam(ship)");
        Term			iAmAgent  = Literal.parseLiteral("iam(agent)");

		if ( t.equals(iAmPlanet) ) return 1;
		else if ( t.equals(iAmPerson) ) return 2;
		else if ( t.equals(iAmShip) ) return 4;
		else if ( t.equals(iAmAgent) ) return 8;

        return 0; // INVALID
    }

    public String
    typeToString(Integer type) {
		String nulled = "(null)";
		if (type == null) return nulled;

		switch (type) {
			case 1: return "Planet";
			case 2: return "Person";
			case 4: return "Ship";
			case 8: return "Ship"; // Intelligent Ship
			default: break;
		}

		return nulled;
	}

    public int population(int agId) {
        return population(agId, 0);
    }

    public int population(int agId, int val) {
        Integer type = agentType.get(agId);

        if (type == 1 || type == 4 || type == 8) { // Planet, Ship and Intelligent Ship
            InnerData id = agentData.get(agId);
            if (id == null) {
                id = new InnerData();
                switch (type) {
                    case 1:
						id.population = 5000 + nextInt(1000); // planet
						break;
                    case 4:
						id.population = 10 + nextInt(50); // ship
						break;
                    case 8:
						id.population = 1 + nextInt(5); // intelligent ship
						break;
                }
                agentData.put(agId, id);
            } else {
                id.population += val;
            }
            return id.population;
        }

        return 0;
    }

    public char getOrientation(int agId) {
        InnerData id = agentData.get(agId);
		if (id == null) return ' ';
		return id.orientation;
	}
    public void setOrientation(int agId, char val) {
        InnerData id = agentData.get(agId);
		String s = "" + val;
		if (id == null || (val != 'N' && val != 'S' && val != 'W' && val != 'E')) return ;
		id.orientation = s.charAt(0);
	}

	public void disableAgent(String agName) {
		Integer agId = agentId.get(agName);
		if (agId == null) return ;
		Integer type = agentType.get(agId);
		// planetas nao sao apagados!
		if (type == null || type == 1) return ;
		Location pos = getAgPos(agId);
		agentType.remove(agId);
		agentData.remove(agId);

		ArrayList<Integer> ali;
		Object local;

		synchronized (map) {
			local = map[pos.x][pos.y];
			ali = (ArrayList<Integer>) local;
			if (ali.size() == 0) {
				// do nothing...
			} else if (ali.size() == 1) {
				super.remove(AGENT, pos.x, pos.y);
			} else {
				int index = ali.indexOf(agId);
				if (index >= 0) {
					ali.remove(index);
				}
			}

			pos.x = -1; pos.y = -1;
			agPos[agId] = pos;
		}
	}

    public void forward(Integer agId) {
		char orientation = getOrientation(agId);
		Location pos = getAgPos(agId);

		switch (orientation) {
			case 'N':
				pos.y -= 1;
				break;
			case 'E':
				pos.x += 1;
				break;
			case 'S':
				pos.y += 1;
				break;
			case 'W':
				pos.x -= 1;
				break;
		}

		if (pos.x < 0 || pos.y < 0 || pos.x > 49 || pos.y > 49)
			return ;

		setAgPos(agId, pos.x, pos.y);
    }

    private class InnerData {
        public int population = 0;
		public char orientation = 'N'; // North South West East only!
    }
}
