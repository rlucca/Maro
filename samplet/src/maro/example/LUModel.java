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
			case 8: return "Ship";
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
						id.population = 400 + nextInt(100); // pl anet
						break;
                    case 4:
						id.population = 10 + nextInt(50); // ship
						break;
                    case 8:
						id.population = nextInt(5); // intelligent ship
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

    private class InnerData {
        public int population = 0;
    }
}
