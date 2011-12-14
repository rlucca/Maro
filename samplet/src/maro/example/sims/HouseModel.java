package maro.example.sims;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import maro.wrapper.OwlApi;
import maro.wrapper.Dumper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

public class HouseModel extends GridWorldModel
{
	public static final int CLOSED_DOOR = 1 <<  3;
	public static final int OPENED_DOOR = 1 <<  4;
	public static final int TABLE       = 1 <<  5;
	public static final int FAUCET      = 1 <<  6;
	public static final int SHOWER      = 1 <<  7;
	public static final int BED         = 1 <<  8;
	public static final int TOILET      = 1 <<  9; // can be a toilet or a gas stove to cook
	public static final int BOOKCASE    = 1 << 10; // Maybe change to wardrobe?
	public static final int SOFA        = 1 << 11;
	public static final int TV          = 1 << 12;
	public static final int REFRIGERATOR= 1 << 13;
    public static final int ALL         = (1 << 14) - 1;

    public HouseModel(int width, int height, int numberOfAgents) {
        super(width, height, numberOfAgents);
		referPlace = new HashMap<String, Place>(); // placeName X Place
		referAgent = new HashMap<Integer, Agent>(); // integerID X Agent
    }

    public void createMap() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                data[i][j] = CLEAN;
            }
        }
        // make the borders
        for (int i = 0; i < width; i++) data[i][0]   = OBSTACLE;
        for (int i = 0; i < width; i++) data[i][height-1]  = OBSTACLE;
        for (int k = 0; k < height; k++) data[0][k]  = OBSTACLE;
        for (int k = 0; k < height; k++) data[width-1][k] = OBSTACLE;
		// make the pool of light == 6x8
        for (int i = 1; i < 7; i++)
			for (int k = 1; k < 9; k++)
				data[i][k]   = OBSTACLE;
        // make two singleroom on bottom left == 7x6
        for (int k = height-7; k < height; k++) data[8][k] = OBSTACLE;
        for (int k = height-7; k < height; k++) data[16][k] = OBSTACLE;
        for (int i = 1; i < width-1; i++) data[i][height-8] = OBSTACLE;
		// make two bathroom on bottom left == the upper 2x2 and other 2x3
        for (int i = width-3; i < width-1; i++) data[i][height-5] = OBSTACLE;
		// wall to make a little hall = 3x1
        for (int i = width-4; i < width-1; i++) data[i][height-11] = OBSTACLE;
		// wall to make a living room on top right == 5x8
        for (int k = 1; k < 11; k++) data[width-7][k] = OBSTACLE;
		// make a bathroom across of the first bedroom = 3x3
        for (int k = 9; k < 12; k++) data[4][k] = OBSTACLE;
		// make a deposit and kitchen on middle == 2x4 and 4x6
        for (int k = 1; k < 11; k++) data[width-12][k] = OBSTACLE;
        for (int i = width-11; i < width-7; i++) data[i][3] = OBSTACLE;
        for (int i = width-11; i < width-7; i++) data[i][10] = OBSTACLE;
		// make the doors == 1x1
		data[7][height-8] = OPENED_DOOR;
		data[9][height-8] = OPENED_DOOR;
		data[4][height-9] = OPENED_DOOR;
		data[3][height-8] = OPENED_DOOR;
		data[13][height-11] = OPENED_DOOR;
		data[13][height-9] = OPENED_DOOR;
		data[width-1][height-10] = CLOSED_DOOR;
		data[width-3][height-8] = OPENED_DOOR;
		data[width-4][height-4] = OPENED_DOOR;
		data[7][0] = CLOSED_DOOR;
		data[8][2] = OPENED_DOOR;

		data[1][height-10] = TOILET;
		data[width-2][height-7] = TOILET;
		data[width-2][height-4] = TOILET;

		data[1][height-9] = FAUCET;
		data[width-2][height-6] = FAUCET;
		data[width-2][height-3] = FAUCET;

		data[1][height-11] = SHOWER;
		data[2][height-11] = SHOWER;
		data[3][height-11] = SHOWER;
		data[width-2][height-2] = SHOWER;
		data[width-3][height-2] = SHOWER;

		// Solteiro 2x4 aproximadamente 1x2m
		for (int i = 1; i < 3; i++)
			for (int k = height-5; k < height-1; k++)
				data[i][k] = BED;
		for (int i = 6; i < 8; i++)
			for (int k = height-5; k < height-1; k++)
				data[i][k] = BED;
		data[3][height-2] = TABLE;
		data[5][height-2] = TABLE;

		for (int i = 4; i < 7; i++)
			data[i][height-7] = BOOKCASE;

		// Casal 4x4 aproximadamente 2x2m
		for (int i = 9; i < 13; i++)
			for (int k = height-6; k < height-2; k++)
				data[i][k] = BED;

		for (int k = height-7; k < height-4; k++)
			data[15][k] = BOOKCASE;

		for (int k = height-3; k < height-1; k++)
			data[15][k] = BOOKCASE;

		// living room
		for (int i = width-3; i < width-1; i++)
			data[i][height-12] = BOOKCASE;

		for (int i = width-6; i < width-4; i++)
			for (int k = 1; k < 6; k++)
				data[i][k] = SOFA;

		for (int k = 2; k < 5; k++)
			data[width-2][k] = TABLE | TV;

		// deposit
        for (int i = width-11; i < width-7; i++) data[i][1] = BOOKCASE;

		// kitchen
		data[width-10][4] = TABLE;
		data[width-9][4] = TABLE;
		data[width-10][5] = TABLE;
		data[width-9][5] = TABLE;

		data[width-11][7] = TABLE;
		data[width-11][8] = TABLE | TOILET; // forge
		data[width-11][9] = TABLE;
		data[width-10][9] = TABLE | FAUCET;
		data[width-8][7] = REFRIGERATOR;

		// calls
		data[width-4][height-12] = TABLE;
		data[5][height-11] = TABLE;

        // position agents
		setAgPos(0, 2, height-3); // in the single room on the left
		setAgPos(1, 6, height-3); // in the single room on the right
		setAgPos(2, 10, height-3); // in the marriage room on the bottom
		setAgPos(3, 10, height-6); // in the marriage room on the top

        for (int id = 4; id < getNumberOfAgents(); id++) {
            Location l = getAgPos(id);
            int x, y;
            if (l != null) continue;
            // No inicio nao eh permitido duas coisas no mesmo lugar
            do {
                x = nextInt(getWidth());
                y = nextInt(getHeight());
            } while ( !isFree(ALL, x, y) );
            setAgPos(id, x, y);
        }
    }

	private String getString(Set<Dumper> names, String base, boolean unquote) {
		if (names == null) return null;
		for (Dumper name: names) {
			String source = name.getTerms()[0];

			if (source.equals(base) == false)
				continue;

			if (unquote == true)
				return name.getTermAsString(1);
			return name.getTerms()[1];
		}
		return null;
	}

	private String getStringUnquoted(Set<Dumper> names, String base) {
		return getString(names, base, true);
	}
	private String getStringQuoted(Set<Dumper> names, String base) {
		return getString(names, base, false);
	}

	private Integer getInteger(Set<Dumper> ids, String base) {
		if (ids == null) return null;
		for (Dumper id : ids) {
			String source = id.getTerms()[0];

			if (source.equals(base) == false)
				continue;

			return id.getTermAsInteger(1);
		}
		return null;
	}

	private Dumper getDumper(Set<Dumper> ids, String base) {
		if (ids == null) return null;
		for (Dumper id : ids) {
			String source = id.getTerms()[0];

			if (source.equals(base) == false)
				continue;

			return id;
		}
		return null;
	}

	public void loadPlacesFromOntology(OwlApi oapi) {
		Set<Dumper> places
			= oapi.getCandidatesByFunctorAndArity(1, "place"); // place(placeName)
		Set<Dumper> ids
			= oapi.getCandidatesByFunctorAndArity(2, "hasIdentifier"); //hasIdentifier(placeName, id)
		Set<Dumper> names
			= oapi.getCandidatesByFunctorAndArity(2, "hasName"); //hasName(placeName, name)
		Set<Dumper> capacities
			= oapi.getCandidatesByFunctorAndArity(2, "hasCapacity");
		Set<Dumper> schedules
			= oapi.getCandidatesByFunctorAndArity(2, "hasScheduleOfFunctioning");
		Set<Dumper> averageTime // hasAverageTimeOfPermanence(schedule, string)
			= oapi.getCandidatesByFunctorAndArity(2, "hasAverageTimeOfPermanence");
		Set<Dumper> closingTime
			= oapi.getCandidatesByFunctorAndArity(2, "hasClosingTime");
		Set<Dumper> openingTime
			= oapi.getCandidatesByFunctorAndArity(2, "hasOpeningTime");
		Set<Dumper> enteringTime
			= oapi.getCandidatesByFunctorAndArity(2, "hasEnteringTimeInterval");
		Set<Dumper> dimensions
			= oapi.getCandidatesByFunctorAndArity(2, "hasDimension"); //hasDimension(placeName, dim)
		Set<Dumper> positionsX
			= oapi.getCandidatesByFunctorAndArity(2, "hasPositionX"); //hasPositionX(dim, intVal)
		Set<Dumper> positionsY
			= oapi.getCandidatesByFunctorAndArity(2, "hasPositionY");
		Set<Dumper> sizesX
			= oapi.getCandidatesByFunctorAndArity(2, "hasSizeX");
		Set<Dumper> sizesY
			= oapi.getCandidatesByFunctorAndArity(2, "hasSizeY");

		if (places == null)
			return ;

		// place -> schedule -> (tempo[medio, fechamento, abertura] e intervaloEntrada)
		// place -> dimensao -> (posicao[X,Y] e tamanho[X,Y])
		// place -> (nome, identificador as type)
		for (Dumper place: places) {
			Place placeData = new Place();
			String base;

			base = place.getTerms()[0];

			placeData.setName(getStringUnquoted(names, base));
			placeData.setType(getInteger(ids, base));
			placeData.setCapacity(getInteger(capacities, base));

			Dumper dimensionRelevant = getDumper(dimensions, base);
			if (dimensionRelevant != null) {
					String target = dimensionRelevant.getTerms()[1];
					
					placeData.setPX(getInteger(positionsX, target));
					placeData.setPY(getInteger(positionsY, target));
					placeData.setWidth(getInteger(sizesX, target));
					placeData.setHeight(getInteger(sizesY, target));
			}

			Dumper scheduleRelevant = getDumper(schedules, base);
			if (scheduleRelevant != null) {
				String target = scheduleRelevant.getTerms()[1];

				placeData.setAverageTime(getStringUnquoted(averageTime, target));
				placeData.setTimeClosing(getStringUnquoted(closingTime, target));
				placeData.setTimeOpening(getStringUnquoted(openingTime, target));
				placeData.setArriveInterval(getStringUnquoted(enteringTime, target));
			}

			if (placeData.getName() == null) {
				// controlled agent were put later in data...
				if (placeData.haveDimension()) {
					int minX = placeData.getPX();
					int minY = placeData.getPY();
					int maxX = minX + placeData.getWidth();
					int maxY = minY + placeData.getHeight();
					int type = placeData.getType();

					for (int x = minX; x < maxX; x++) {
						for (int y = minY; y < maxY; y++) {
							data[x][y] = type;
						}
					}
				}
			} else {
				referPlace.put(placeData.getName(), placeData);
			}
		}
	}

	public void loadAgentsFromOntology(OwlApi oapi) {
		Set<Dumper> agents
			= oapi.getCandidatesByFunctorAndArity(1, "agent");
		Set<Dumper> characters
			= oapi.getCandidatesByFunctorAndArity(2, "hasCharacter");
		Set<Dumper> profiles
			= oapi.getCandidatesByFunctorAndArity(2, "hasProfile");
		Set<Dumper> fixedDestinations
			= oapi.getCandidatesByFunctorAndArity(2, "hasFixedDestination");
		Set<Dumper> randomDestinations
			= oapi.getCandidatesByFunctorAndArity(2, "hasRandomDestination");
		int curr = 0;

		if (agents == null) {
			// no agents....
			if (getNumberOfAgents() > 0) {
				// but we need! throw a null exception
				String waitAgents = agents.toString();
			}
			return ;
		}

		// agent -> character -> profile -> *destinarions
		for (Dumper agent : agents) {
			Agent agentData;
			Profile profileData;
			String character;
			String profile;
			String base;

			base = agent.getTerms()[0];

			character = getStringQuoted(characters, base);
			if (character == null)
				continue;

			profile = getStringQuoted(profiles, character);
			if (profile == null)
				continue;

			profileData = new Profile(profile);
			agentData = new Agent(curr, agent.getTermAsString(0));
			referAgent.put(curr, agentData);

			for (Dumper d : fixedDestinations) {
				if (profile.equals(d.getTerms()[0])) {
					String placeName = d.getTermAsString(1);
					if (placeName.startsWith("Initial")) {
						Place place = referPlace.get(placeName);
						setAgPos(agentData.getID(), place.getPX(), place.getPY());
						// It's the point that put the controlled agent in data!
					} else {
						profileData.addFixedDestination(placeName);
					}
				}
			}
			for (Dumper d : randomDestinations) {
				if (profile.equals(d.getTerms()[0])) {
					String placeName = d.getTermAsString(1);
					profileData.addRandomDestination(placeName);
				}
			}

			agentData.randomOrientation();
			agentData.setProfile(profileData);
			curr += 1;
		}

		boolean excluded = true;

		while (excluded == true) {
			excluded = false;
			for (String key : referPlace.keySet()) {
				if (key.startsWith("Initial")) {
					referPlace.remove(key);
					excluded = true;
					break;
				}
			}
		}
	}

	public void loadObjectsFromOntology(OwlApi oapi) {
		// TODO maybe to load anotations of objects?
		//public Iterator<Dumper> getCandidatesByFunctorAndArityIter(int arity, String functor)
		//public HashSet<Dumper> getCandidatesByFunctorAndArity(int arity, String functor)
	}

	public int
    nextInt(int limit) {
        return random.nextInt(limit);
    }

	public int
	nextNormal(int average, int range) {
		double offset = average;
		double r = range;
		return (int) (offset + random.nextGaussian() * r);
	}

	public String getAgentName(int agentID) {
		Agent a = referAgent.get(agentID);
		if (a == null) return null;
		return new String (a.getName());
	}

	public int getNumberOfAgents() {
		return agPos.length;
	}








	protected HashMap<String, Place> referPlace;
	protected class Place {
		private String placeName;
		private Integer px; // can be out of screen or null
		private Integer py; // can be out of screen or null
		private Integer width;
		private Integer height;
		private Integer type;
		private Integer totalCapacity;
		private Integer[] timeOpening;
		private Integer[] timeClosing;
		private Integer[] averageTime;
		private Integer[] arriveInterval;

		public void setName(String i) { placeName = i; }
		public String getName() { return placeName; }
		public void setPX(Integer i) { px = i; }
		public Integer getPX() { return px; }
		public void setPY(Integer i) { py = i; }
		public Integer getPY() { return py; }
		public void setWidth(Integer i) { width = i; }
		public Integer getWidth() { return width; }
		public void setHeight(Integer i) { height = i; }
		public Integer getHeight() { return height; }
		public void setType(Integer i) { type = i; }
		public Integer getType() { return type; }
		public void setCapacity(Integer i) { totalCapacity = i; }
		public Integer getCapacity() { return totalCapacity; }

		public void setTimeOpening(String line) {
			String [] days = line.split(",");

			timeOpening = new Integer[7];
			if (days[0].equals("-")) {
				for (int i=0; i<7; i++) timeOpening[i] = -1;
				return ;
			}

			for (int i=0; i<7 && i < days.length; i++) {
				try {
					timeOpening[i] = Integer.parseInt(days[i]);
				} catch (Exception e) {
					timeOpening[i] = -2;
				}
			}
		}

		public void setTimeClosing(String line) {
			String [] days = line.split(",");

			timeClosing = new Integer[7];
			if (days[0].equals("-")) {
				for (int i=0; i<7; i++) timeClosing[i] = -1;
				return ;
			}

			for (int i=0; i<7 && i < days.length; i++) {
				try {
					timeClosing[i] = Integer.parseInt(days[i]);
				} catch (Exception e) {
					timeClosing[i] = -2;
				}
			}
		}

		public void setAverageTime(String line) {
			String [] days = line.split(",");

			averageTime = new Integer[7];
			if (days[0].equals("-")) {
				for (int i=0; i<7; i++) averageTime[i] = -1;
				return ;
			}

			for (int i=0; i<7 && i < days.length; i++) {
				try {
					averageTime[i] = Integer.parseInt(days[i]);
				} catch (Exception e) {
					averageTime[i] = -2;
				}
			}
		}

		public void setArriveInterval(String line) {
			String [] days = line.split(",");

			arriveInterval = new Integer[7];
			if (days[0].equals("-")) {
				for (int i=0; i<7; i++) arriveInterval[i] = -1;
				return ;
			}

			for (int i=0; i<7 && i < days.length; i++) {
				try {
					arriveInterval[i] = Integer.parseInt(days[i]);
				} catch (Exception e) {
					arriveInterval[i] = -2;
				}
			}
		}

		public Integer getTimeOpening(int idx) { return timeOpening[idx]; }
		public Integer getTimeClosing(int idx) { return timeClosing[idx]; }
		public Integer getAverageTime(int idx) { return averageTime[idx]; }
		public Integer getArriveInterval(int idx) { return arriveInterval[idx]; }

		public boolean haveDimension() {
			return !(px == null || py == null || width == null || height == null || type == null);
		}

		// today,
		//	0-monday		3-thursday		6-sunday
		//	1-tuesday		4-friday
		//	2-wednesday		5-saturday
		public void update (int weekday, int hour, int step) {
			if (placeName.equals("Home") == true) {
				// When we will create a party, this can be used... but not for now...
				return ;
			}

			// it's between 0 and 100
			int relativeCapacity = 0;

			if (capacity != null && totalCapacity != null)
				relativeCapacity = (capacity.size()*100)/totalCapacity;

			if (opened == false) {
				if (hour >= timeOpening[weekday]) {
					int halfCapacity = (int) (0.5 * totalCapacity);
					int c = nextNormal(halfCapacity, (int)(0.1 * halfCapacity));

					capacity = new ArrayList<Integer> ();

					for (int i = 0; i < (int)c; i++) {
						int forward = nextNormal(averageTime[weekday], arriveInterval[weekday]);
						System.out.println(step+" "+forward+" "+placeName);
						if (forward > 0) {
							capacity.add(step+forward);
						} else {
							capacity.add(step+1);
						}
					}

					opened = true;
				}
			} else {
				//+" close "+ timeClosing[weekday]
			}

			System.out.println("placeName "+placeName
				+" people "+ relativeCapacity
				+" avgT "+ averageTime[weekday]
				+" arvT "+ arriveInterval[weekday]
				+" capacity "+ relativeCapacity +"%"
				+" capacity "+ capacity.size()
				+" weekday "+weekday+" "+hour+" step "+step);
		}

		private boolean opened = false;
		private ArrayList<Integer> capacity;
	}

	protected class Profile {
		private String profileName;
		private ArrayList<String> fixedDestinies;
		private ArrayList<String> randomDestinies;

		public Profile(String name) {
			profileName = name;
			fixedDestinies = new ArrayList<String> ();
			randomDestinies = new ArrayList<String> ();
		}
		public void addFixedDestination(String place) {
			fixedDestinies.add(place);
		}
		public void addRandomDestination(String place) {
			randomDestinies.add(place);
		}
	}

	protected HashMap<Integer, Agent> referAgent;
	protected class Agent {
		private Integer id;
		private String name;
		private Profile profile;

		public Agent(Integer identification, String name) {
			this.name = name;
			id = identification;
		}

		public Integer getID() {
			return id;
		}
		public String getName() {
			return name;
		}
		public void setProfile(Profile p) {
			profile = p;
		}
		public Profile getProfile() {
			return profile;
		}

		// Anotations?
		private Integer energy = 40;
		private Character orientation = ' ';

		public Integer getEnergy() { return energy; }
		public void addEnergy(Integer e) {
			energy += e;
			if (energy < 0) energy = 0;
			if (energy > 105) energy = 105;
		}

		public String getOrientationText() {
			if (orientation == 'N') return "Nort";
			else if (orientation == 'E') return "East";
			else if (orientation == 'S') return "South";
			else if (orientation == 'W') return "West";
			return "Unknow";
		}
		public void randomOrientation() {
			String os = "NESW";
			orientation = os.charAt(nextInt(os.length()));
		}
		public Character getOrientation() {
			return orientation;
		}
		public void setOrientation(char o) {
			orientation = o;
		}
	}



	private class InnerData { // TODO acho que isso vai morrer
		// resource is equivalent to population
		public Integer population = 0;
		// quality is equivalent to life/energy
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
		public String description = null;
		public String name = null;
		//public Boolean position = null; // this value need be putted be the called of getFeatureList

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
			id.description = this.description;
			id.name = this.name;
			//id.position = this.position;
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
			if (orientation != null && orientation!=' ') {
				if (!ret.isEmpty())
					ret += ",lookFor(\""+orientation+"\")";
				else
					ret += "lookFor(\""+orientation+"\")";
			}
			if (population != null && population >= 0) {
				if (!ret.isEmpty())
					ret += ",resource("+population+")";
				else
					ret += "resource("+population+")";
			}
			if (life != null && life >= 0) {
				if (!ret.isEmpty())
					ret += ",quality("+life+")";
				else
					ret += "quality("+life+")";
			}
			if (action != null && !action.isEmpty()) {
				if (!ret.isEmpty())
					ret += ",action(\""+action+"\")";
				else
					ret += "action(\""+action+"\")";
			}
			if (name != null && !name.isEmpty()) {
				if (!ret.isEmpty())
					ret += ",name(\""+name+"\")";
				else
					ret += "name(\""+name+"\")";
			}

			return (ret.isEmpty())?null:ret;
		}
	}
}
