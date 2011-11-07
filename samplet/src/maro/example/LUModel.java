package maro.example;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import java.util.HashMap;
import java.util.Map;

public class LUModel extends GridWorldModel {
	private Map<String, Integer> agentId = new HashMap<String, Integer>();
	private Map<Integer, Integer> agentType = new HashMap<Integer, Integer>();
    private Map<Integer, InnerData> agentData = new HashMap<Integer, InnerData>();

    public LUModel (int agentsQty) {
        // cell's width and cell's height are 50
        super(50, 50, agentsQty);
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

	// devolve AgId x (AgType x AgLocation)
	public Map<Integer, Map<Integer,Location> > findOthers(int agId, int range) {
		Location pos = getAgPos(agId);
		Location tl = new Location(pos.x - range, pos.y - range);
		Location br = new Location(pos.x + range, pos.y + range);
		Map<Integer, Map<Integer,Location> > others = new HashMap<Integer, Map<Integer,Location> > ();

		for (int id : agentId.values()) {
			if (id != agId) {
				Integer type = getTypeById(id);
				Location posTarget = getAgPos(id);
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
		Map<Integer, Map<Integer,Location> > mp = findOthers(agId, range);
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
		if (id == null && val != 'N' && val != 'S' && val != 'W' && val != 'E') return ;
		id.orientation = s.charAt(0);
	}

    private class InnerData {
        public int population = 0;
		public char orientation = 'N'; // North South West East only!
    }
}
