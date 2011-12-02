package maro.example;

import maro.wrapper.AnnotatedEnvironment;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class LUModel extends GridWorldModel {
	private Map<String, Integer> agentId = new ConcurrentHashMap<String, Integer>();
	private Map<Integer, Integer> agentType = new ConcurrentHashMap<Integer, Integer>();
	private Map<Integer, InnerData> agentData = new ConcurrentHashMap<Integer, InnerData>();
	private InnerData[] templateData
		// 0 border and the rest is based on type of character
		= new InnerData[] { null, null,  null,  null,  null,  null,  null,  null, null }; 
	private Object[][] map = null;

	public LUModel (AnnotatedEnvironment ae) {
		// cell's width and cell's height are 50
		super(50, 50, ae.getNumberAgentsSettings());

		map = new Object[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				map[i][j] = new ArrayList<Integer>();
			}
		}

		loadDefaultSetting(ae, "border", 0);
		loadDefaultSetting(ae, "planet", 1);
		loadDefaultSetting(ae, "person", 2);
		loadDefaultSetting(ae, "ship", 4);
		loadDefaultSetting(ae, "agent", 8);
	}

	private void loadDefaultSetting(AnnotatedEnvironment ae, String annot, int idx) {
		InnerData idref;
		Integer pos = ae.getAnnot(annot, "positionX", -1);

		if (idx == 8) {
			idref = templateData[4].makeCopy();
		} else {
			idref = new InnerData();
		}

		if (pos != null) {
			if (pos < 0) idref.position = false; // show the value
			else idref.position = true; // fixed value?
		}

		// filling the data to keep in memory
		idref.attraction = ae.getAnnot(annot, "attraction", nextInt(201) - 100);
		idref.beauty = ae.getAnnot(annot, "beauty", nextInt(201) - 100);
		idref.conflict = ae.getAnnot(annot, "conflict", nextInt(201) - 100);
		idref.heat = ae.getAnnot(annot, "heat", nextInt(201) - 100);
		idref.luminousness = ae.getAnnot(annot, "luminousness", nextInt(201) - 100);
		idref.security = ae.getAnnot(annot, "security", nextInt(201) - 100);

		switch (idx) {
					case 1:
						idref.capacity = ae.getAnnot(annot, "capacity", 10000+nextInt(101)); // planet
						break;
					case 4:
						idref.capacity = ae.getAnnot(annot, "capacity", 250+nextInt(101)); // ship
						break;
					case 8:
						idref.capacity = ae.getAnnot(annot, "capacity", 6+nextInt(11)); // intelligent ship
						break;
		}

		idref.utility = ae.getAnnot(annot, "utility", idref.utility);
		idref.description = ae.getAnnot(annot, "description", idref.description);
		idref.name = ae.getAnnot(annot, "name", idref.name);
		// If we don't have, put what have...
		idref.population = ae.getAnnot(annot, "resource", idref.population);
		idref.life = ae.getAnnot(annot, "quality", idref.life);
		idref.orientation = ae.getAnnot(annot, "lookFor", idref.orientation);
		// actions need more work...
		if (idref.action != null) {
			Set<String> ss = new HashSet<String>();
			String [] sa = idref.action.split(",");
			String [] sn = null;
			String n = ae.getAnnot(annot, "action", "");
			if (n != null && !n.isEmpty())
				sn = n.split(",");
			for (String s : sa) ss.add(s);
			if (sn != null)
				for (String s : sn) ss.add(s);
			n = null;
			for (String s : ss.toArray(new String[] { })) {
				if (n == null) n = s;
				else n += "," + s;
			}
			idref.action = n;
		} else
			idref.action = ae.getAnnot(annot, "action", idref.action);

		if (idref.actionInner != null) {
			Set<String> ss = new HashSet<String>();
			for (String s : idref.actionInner.split(",")) ss.add(s);
			String aux = ae.getActionsToDo(annot);
			if (aux != null)
				for (String s: aux.split(",")) ss.add(s);
			aux = null;
			for (String s : ss.toArray(new String[] { })) {
				if (aux == null) aux = s;
				else aux += "," + s;
			}
			idref.actionInner = aux;
		} else
			idref.actionInner = ae.getActionsToDo(annot);

		if (idref.actionOuter != null) {
			Set<String> ss = new HashSet<String>();
			for (String s : idref.actionOuter.split(",")) ss.add(s);
			String aux = ae.getActionsToServing(annot);
			if (aux != null)
				for (String s: aux.split(",")) ss.add(s);
			aux = null;
			for (String s : ss.toArray(new String[] { })) {
				if (aux == null) aux = s;
				else aux += "," + s;
			}
			idref.actionOuter = aux;
		} else
			idref.actionOuter = ae.getActionsToServing(annot);
		// loaded complete!
		templateData[idx] = idref;
	}

	public jason.environment.grid.GridWorldView getView() {
		return view;
	}

	public int
		nextInt(int limit) {
			return random.nextInt(limit);
		}

    public List<Integer> getListAgPos(int x, int y) {
        List<Integer> li = new ArrayList<Integer>();
        int id = 0;

        while (id < agPos.length) {
            Location l = agPos[id];

            if (l.x >= 0 && l.y >= 0 && x == l.x && y == l.y) {
                li.add(id);
            }

            id = id + 1;
        }

        return li;
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
	@SuppressWarnings("unchecked")
	public void setAgPos(int ag, int x, int y) {
		Location o = getAgPos(ag);
		ArrayList<Integer> ali;
		Object local;

		if (o != null) {
			int sz;
			// removendo do antigo...
			local = map[o.x][o.y];
			ali = (ArrayList<Integer>) local;
			sz = ali.size();
			if (sz == 1) {
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

		// colocando no novo...
		local = map[x][y];
		ali = (ArrayList<Integer>) local;

		ali.add(ag);
		super.add(AGENT, x, y);
		agPos[ag] = new Location(x, y);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int getAgAtPos(int x, int y) {
		ArrayList<Integer> ali;
		Object local;

		local = map[x][y];
		ali = (ArrayList<Integer>) local;

		if (ali.size() == 1)
			return ali.get(0);

		for (Integer i : ali) {
			if (agentType.get(i) == 1)
				return i;
		}

		return ali.get(0);
	}

	public boolean have(int ptype, int x, int y) {
		for (int id : agentId.values()) {
			Integer type = getTypeById(id);
			if (type != null && type == ptype) {
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
		Map<Integer, Map<Integer,Location> > others = new ConcurrentHashMap<Integer, Map<Integer,Location> > ();
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
					Map<Integer,Location> il = new ConcurrentHashMap<Integer, Location>();
					il.put(type, posTarget);
					others.put(id, il);
				}
			}
		}
		return others;
	}

	// devolve X x Y x annotations
	public Map<Integer, Map<Integer, String> > findBorder(Location source, Location target) {
		Map<Integer, Map<Integer, String> > mimil = new ConcurrentHashMap<Integer, Map<Integer, String> > ();
		String ret = templateData[0].getFeatureList();

		if (target.x >= 0) {
			Location [] pos = new Location [] {
				new Location(target.x, source.y - 1),
				new Location(target.x, source.y + 0),
				new Location(target.x, source.y + 1)
			};

			for (Location position: pos) {
				if (position.y < 0 || position.y > 49) continue;
				Map<Integer, String> mil = mimil.get(position.x);
				String data = ret;
				if (mil == null) {
					mil = new ConcurrentHashMap<Integer, String> ();
					mimil.put(position.x, mil);
				}
				if (templateData[0].position == false) {
					data += ",positionX("+position.x+")";
					data += ",positionY("+position.y+")";
				}
				mil.put(position.y, data);
			}
		}

		if (target.y >= 0) {
			Location [] pos = new Location [] {
				new Location(source.x - 1, target.y),
				new Location(source.x + 0, target.y),
				new Location(source.x + 1, target.y)
			};

			for (Location position: pos) {
				if (position.x < 0 || position.x > 49) continue;
				Map<Integer, String> mil = mimil.get(position.x);
				String data = ret;
				if (mil == null) {
					mil = new ConcurrentHashMap<Integer, String> ();
					mimil.put(position.x, mil);
				}
				if (templateData[0].position == false) {
					data += ",positionX("+position.x+")";
					data += ",positionY("+position.y+")";
				}
				mil.put(position.y, data);
			}
		}
		return mimil;
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

	public Integer attackFrom(int agId) {
		Location pos = getAgPos(agId);
		char orientation = getOrientation(agId);

		// Primeiro verifica a posicao atual...
		for (int id : agentId.values()) {
			if (id != agId) {
				Integer type = agentType.get(id);
				Location target = getAgPos(id);
				if (type != null && type != 1
						&& target != null && target.x == pos.x && pos.y == target.y) {
					return id;
				}
			}
		}

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

		// Depois verifica a posicao que esta virado...
		for (int id : agentId.values()) {
			if (id != agId) {
				Integer type = agentType.get(id);
				Location target = getAgPos(id);
				if (type != null && type != 1
						&& target != null && target.x == pos.x && pos.y == target.y) {
					return id;
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
				id = templateData[type].makeCopy();
				switch (type) {
					case 1:
						id.population += 5000 + nextInt(1000); // planet
						break;
					case 4:
						id.population += 10 + nextInt(50); // ship
						break;
					case 8:
						id.population += 1 + nextInt(5); // intelligent ship
						break;
				}
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

		if (type == null || type == 2) // type nulled or person are reject...
			return 0;

		InnerData id = agentData.get(agId);
		if (id != null) {
			id.population += val;
			return id.population;
		}

		return 1;
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

	@SuppressWarnings("unchecked")
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

		local = map[pos.x][pos.y];
		ali = (ArrayList<Integer>) local;
		if (ali.size() == 1) {
			ali.remove(0);
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

		if (pos.x < 0 || pos.y < 0 || pos.x > width-1 || pos.y > height-1)
			return ;

		setAgPos(agId, pos.x, pos.y);
	}

	public void fire(Integer source, Integer target) {
		Integer sType = agentType.get(source);
		Integer tType = agentType.get(target);
		int damage = 1 + nextInt(5);

		if (sType == null || tType == null) return ;

		getLife(target, -damage);
	}

	public int getLife(Integer agId, int value) {
		InnerData data = agentData.get(agId);

		if (data == null) return 0;

		if (value >= data.life)
			data.life = 0;
		else
			data.life += value;

		return data.life;
	}

	public String getActions(Integer agId) {
		InnerData data = agentData.get(agId);
		if (data == null) return null;
		return data.action;
	}

	public String getMyActions(Integer agId) {
		InnerData data = agentData.get(agId);
		if (data == null) return null;
		return data.actionInner;
	}

	public String getServingActions(Integer agId) {
		InnerData data = agentData.get(agId);
		if (data == null) return null;
		return data.actionOuter;
	}

	protected String getJasonAnnotations(Integer agId) {
		InnerData data = agentData.get(agId);
		if (data == null) return null;

		Location pos = getAgPos(agId);
    	String name = getNameById(agId);
		Integer type = getTypeById(agId);
		String ret = null;
		int resource = population(agId);
		char lookFor = getOrientation(agId);

		ret = data.getFeatureList();

		if (name != null) {
			if (ret == null) ret = "name(\""+name+"\")";
			else ret += ",name(\""+name+"\")";
		}

		if (data.position == false && pos != null) {
			if (ret == null) {
				ret = "positionX("+pos.x+")";
				ret += ",positionY("+pos.y+")";
			} else {
				ret += ",positionX("+pos.x+")";
				ret += ",positionY("+pos.y+")";
			}
		}

		if (resource > 0) {
			if (ret == null) ret = "resource("+resource+")";
			else ret += ",resource("+resource+")";
		}

		if (type != null && type == 1) {
			// When it is a planet finish
			return ret;
		}

		if (ret == null) ret = "lookFor(\""+lookFor+"\")";
		else ret += ",lookFor(\""+lookFor+"\")";

		return ret;
	}

	public String getSourceJasonAnnotations(Integer agId) {
		String ret = getJasonAnnotations(agId);
		String actions = getMyActions(agId);
		int quality = getLife(agId, 0);

		if (actions != null && actions.isEmpty() == false) {
			if (ret == null) ret = "action(\""+actions+"\")";
			else ret += ",action(\""+actions+"\")";
		}

		if (ret == null) ret = "quality("+quality+")";
		else ret += ",quality("+quality+")";

		return ret;
	}

	public String getTargetJasonAnnotations(Integer agId) {
		String ret = getJasonAnnotations(agId);
		String actions = getServingActions(agId);

		if (actions != null && actions.isEmpty() == false) {
			if (ret == null) ret = "action(\""+actions+"\")";
			else ret += ",action(\""+actions+"\")";
		}

		return ret;
	}



	private class InnerData {
		// resource is equivalent to population
		public Integer population = 0;
		// quality is equivalent to life
		public Integer life = 40;
		// lookFor is equivalent to orientation
		public Character orientation = 'N'; // North South West East only!
		public Integer attraction = null;
		public Integer beauty = null;
		public Integer capacity = null;
		public Integer conflict = null;
		public Integer heat = null;
		public Integer luminousness = null;
		public Integer security = null;
		public String utility = null;
		public String action = null;
		public String actionInner = null;
		public String actionOuter = null;
		public String description = null;
		public String name = null;
		public Boolean position = null;

		public InnerData makeCopy () {
			InnerData id = new InnerData ();
			id.population = this.population;
			id.life = this.life;
			id.orientation = this.orientation;
			id.attraction = this.attraction;
			id.beauty = this.beauty;
			id.capacity = this.capacity;
			id.conflict = this.conflict;
			id.heat = this.heat;
			id.luminousness = this.luminousness;
			id.security = this.security;
			id.utility = this.utility;
			id.action = this.action;
			id.actionInner = this.actionInner;
			id.actionOuter = this.actionOuter;
			id.description = this.description;
			id.name = this.name;
			id.position = this.position;
			return id;
		}

		public String getFeatureList() {
			String ret = "";
			if (attraction != null) {
				ret += "attraction("+attraction+")";
			}
			if (beauty != null) {
				if (!ret.isEmpty())
					ret += ",beauty("+beauty+")";
				else
					ret += "beauty("+beauty+")";
			}
			if (capacity != null) {
				if (!ret.isEmpty())
					ret += ",capacity("+capacity+")";
				else
					ret += "capacity("+capacity+")";
			}
			if (conflict != null) {
				if (!ret.isEmpty())
					ret += ",conflict("+conflict+")";
				else
					ret += "conflict("+conflict+")";
			}
			if (heat != null) {
				if (!ret.isEmpty())
					ret += ",heat("+heat+")";
				else
					ret += "heat("+heat+")";
			}
			if (luminousness != null) {
				if (!ret.isEmpty())
					ret += ",luminousness("+luminousness+")";
				else
					ret += "luminousness("+luminousness+")";
			}
			if (security != null) {
				if (!ret.isEmpty())
					ret += ",security("+security+")";
				else
					ret += "security("+security+")";
			}
			if (utility != null && !utility.isEmpty()) {
				if (!ret.isEmpty())
					ret += ",utility(\""+utility+"\")";
				else
					ret += "utility(\""+utility+"\")";
			}
			if (description != null && !description.isEmpty()) {
				if (!ret.isEmpty())
					ret += ",description(\""+description+"\")";
				else
					ret += "description(\""+description+"\")";
			}

			return (ret.isEmpty())?null:ret;
		}
	}
/*
  action
  attraction
  beauty
  capacity
  conflict
  description
  heat
  lookFor
  luminousness
  name
  positionX
  positionY
  quality
  resource
  security
  utility
  -- end features. started actions list
    absorve
    changeOrientationTo
    death
    fire
    forward
    increasePopulation
    nope
    offer
    recover
    teleport
  -- ended all listings.
*/
}
