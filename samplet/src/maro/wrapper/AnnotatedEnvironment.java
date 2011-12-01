package maro.wrapper;

import maro.core.TimeSteppedEnvironment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;

public class AnnotatedEnvironment extends TimeSteppedEnvironment
{
    protected long sum;
    protected List<Option> options;
    protected Integer lastStep;
    // individual name X type
    protected HashMap<String, InitialAnnotated> annots; // only read this, ok?

    public AnnotatedEnvironment () {
	sum = 0;
	annots = new HashMap<String, InitialAnnotated> ();
	options = new ArrayList<Option> ();
	options.add( new Option<String> (true,
		    "the filename of the ontology of annotations",
		    "isn't a readable file",
		    null));
	options.add( new Option<Integer> (true,
		    "the number for step's timeout(ms)",
		    "isn't a valid number",
		    null));
	options.add( new Option<Integer> (true,
		    "the number for the last step",
		    "isn't a valid number",
		    null));
	options.add( new Option<Integer> (false,
		    "the number for the quantity of agents",
		    "isn't a valid number. Using the value 100.",
		    100));
	options.add( new Option<Boolean> (false,
		    "a boolean to indicate to add next action to queue"
		    +" or else fail the second action",
		    "isn't a valid boolean. Using the value true.",
		    true));
    }

    protected boolean validateOptions(String []args) {
	int mandatories = 0;

	for (Option o : options) {
	    if (o.isRequired) mandatories += 1;
	}
	return (args.length >= mandatories && args.length <= options.size());
    }

	public void setLastStep(int step) {
	    lastStep = step;
	}

    @SuppressWarnings("unchecked")
    public boolean loadOptions(String []args) {
	Option<String> os;
	Option<Integer> oi;
	Option<Boolean> ob;
	boolean exit = true;

	if ( validateOptions(args) == false ) {
	    printOptions();
	    return false;
	}

	// argh... I like transform this in foreach code... but someone will
	// not understanding the code... than I will keep this trash... :'(
	os = options.get(0);
	try {
	    File file = new File(args[0]);
	    if (file.canRead() == false) {
		throw new Exception();
	    }
	    os.value = args[0];
	} catch (Exception e) {
	    getLogger().warning("The first argument "+os.help);
	    getLogger().warning("\tThis argument is"+os.description);
	    if (os.isRequired) exit = false;
	}

	oi = options.get(1);
	try {
	    oi.value = Integer.parseInt(args[1]);
	} catch (Exception e) {
	    getLogger().warning("The second argument "+oi.help);
	    getLogger().warning("\tThis argument is"+oi.description);
	    if (oi.isRequired) exit = false;
	}

	oi = options.get(2);
	try {
	    oi.value = Integer.parseInt(args[2]);
		setLastStep(oi.value);
	} catch (Exception e) {
	    getLogger().warning("The third argument "+oi.help);
	    getLogger().warning("\tThis argument is"+oi.description);
	    if (oi.isRequired) exit = false;
	}

	oi = options.get(3);
	try {
	    oi.value = Integer.parseInt(args[3]);
	} catch (Exception e) {
	    getLogger().warning("The fourth argument "+oi.help);
	    getLogger().warning("\tThis argument is"+oi.description);
	    if (oi.isRequired) exit = false;
	}

	ob = options.get(4);
	try {
	    ob.value = Boolean.parseBoolean(args[4]);
	} catch (Exception e) {
	    getLogger().warning("The fiveth argument "+ob.help);
	    getLogger().warning("\tThis argument is"+ob.description);
	    if (ob.isRequired) exit = false;
	}

	return exit;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(String[] args) {
	boolean exit = false;

	if (loadOptions(args) == false) {
	    System.exit(50);
	    return ;
	}

	Option<Boolean> ob = options.get(4);
	Boolean queue = ob.value;
	if (queue != null) {
	    if (queue == true)
		super.setOverActionsPolicy(OverActionsPolicy.queue);
	    else
		super.setOverActionsPolicy(OverActionsPolicy.failSecond);
	}

	Option<String> os2 = options.get(0);
	String filename = os2.value;
	OwlApi oapi = new OwlApi ();

	try {
	    oapi.loadOntologyFromFile(filename);
	} catch (Exception e) {
	    getLogger().warning("Problem reading ontology from " + filename);
	    System.exit(52);
	}

	annots.put("agent", new InitialAnnotated("agent"));
	annots.put("border", new InitialAnnotated("border"));
	annots.put("planet", new InitialAnnotated("planet"));
	annots.put("ship", new InitialAnnotated("ship"));
	annots.put("world", new InitialAnnotated("world"));

	Iterator<Dumper> it = oapi.iterator();
	List<Dumper> ld = new ArrayList<Dumper> ();
	if (it == null || ld == null) return ;
	while (it.hasNext()) {
	    Dumper dumper = it.next();
	    if (dumper.getArity() <= 1)
		continue;

	    if (!dumper.getFunctor().equals("hasInner") && !dumper.getFunctor().equals("hasOuter")) {
		ld.add(dumper);
		continue;
	    }

	    try {
		String name = dumper.getTerms()[0];
		String value = dumper.getTerms()[1];
		if (name.charAt(0) == '"')
		    name = name.substring(1, name.length()-1); // unquote
		if (value.charAt(0) == '"')
		    value = value.substring(1, value.length()-1); // unquote

		InitialAnnotated ia = annots.get(name);
		if (dumper.getFunctor().equals("hasInner"))
		    ia.addIndividualInner(value);
		else
		    ia.addIndividualOuter(value);
	    } catch (Exception e) {
		getLogger().warning("Exception: "+e.toString());
		e.printStackTrace();
		System.exit(54);
	    }
	}

	for (String key: annots.keySet()) {
	    InitialAnnotated ia = annots.get(key);
	    if (ia.individualsInner != null) {
		for (String s: ia.individualsInner) {
		    exit = !individualsTreatment(false, s, ld, ia) || exit;
		}

		if (ld.isEmpty()) break;
	    }

	    if (ia.individualsOuter != null) {
		for (String s: ia.individualsOuter) {
		    exit = !individualsTreatment(true, s, ld, ia) || exit;
		}
		if (ld.isEmpty()) break;
	    }
	}

	if (exit) System.exit(56);
    }

    @SuppressWarnings("unchecked")
    protected void initDefault() {
	Option<Integer> os = options.get(1);
	String [] parms;
	if (os.value != null) {
	    parms = new String[] { os.value.toString() };
	} else {
	    parms = new String[] { };
	}

	super.init( parms );
    }

    private boolean individualsTreatment(boolean isOuter, String individualName, List<Dumper> dumperList, InitialAnnotated ia)
    {
	String annotation = null;
	String  value1 = null;
	Integer value2 = null;
	int mode = 0;	// 0, nulled mode
	// 1, readed one int (wait annotation)
	// 2, readed one string (wait annotation or strings)
	// 4, readed two annotations (wait annotation only)
	// 8, error mode

	for (Dumper d: dumperList) {
	    String functor = d.getFunctor();
	    String name = d.getTerms()[0];
	    String value = d.getTerms()[1];
	    if (name.charAt(0) == '"')
		name = name.substring(1, name.length()-1); // unquote
	    if (value.charAt(0) == '"')
		value = value.substring(1, value.length()-1); // unquote
	    if (!name.equals(individualName))
		continue;

	    switch (mode) {
		case 0:
		    if (functor.equals("hasInt")) {
			mode = 1; // allowed only one element
			try {
			    if (value2 != null) value2 = value2 / 0; // hahahah
			    value2 = Integer.parseInt(value);
			    if (annotation != null) ia.addInitialValue(annotation, value2);
			} catch (Exception e) {
			    getLogger().warning("Target of `"+d+"' isn't a integer!");
			    mode = 8;
			}
		    } else if (functor.equals("hasString")) {
			mode = 2; // 2, we yet don't know if is one or more element but we presume that is more
			value1 = value;
		    } else if (functor.equals("hasAnnotation")) {
			if (annotation != null) {
			    mode = 4;
			}
			annotation = value;
			ia.addFeature(value, isOuter);
		    }
		    break;
		case 1: // i'm readed a int before
		    if (functor.equals("hasInt")) {
			getLogger().warning("Multiple targets of `hasInt' isn't allow!");
			mode = 8;
		    } else if (functor.equals("hasString")) {
			getLogger().warning("Multiple types of data aren't allow! Integer or String data are allow not both.");
			mode = 8;
		    } else if (functor.equals("hasAnnotation")) {
			if (annotation != null) {
			    getLogger().warning("Multiple annotations aren't allow here!");
			    mode = 8;
			} else  {
			    annotation = value;
			    ia.addFeature(value, isOuter);
			    ia.addInitialValue(value, value2);
			}
		    }
		    break;
		case 2: // i'm readed a string before
		    if (functor.equals("hasInt")) {
			getLogger().warning("Multiple types of data isn't allow! Integer or String data are allow not both.");
			mode = 8;
		    } else if (functor.equals("hasString")) {
			value1 = value1 + "," + value;
		    } else if (functor.equals("hasAnnotation")) {
			if (annotation != null) {
			    getLogger().warning("Multiple annotations aren't allow here!");
			    mode = 8;
			} else  {
			    annotation = value;
			    ia.addFeature(value, isOuter);
			}
		    }
		    break;
		case 4: // i'm readed two annotations
		    if (functor.equals("hasInt")) {
			getLogger().warning("Integer element is not allow with multiple annotations");
			mode = 8;
		    } else if (functor.equals("hasString")) {
			getLogger().warning("String element is not allow with multiple annotations");
			mode = 8;
		    } else if (functor.equals("hasAnnotation")) {
			ia.addFeature(value, isOuter);
		    }
		    break;
		case 8:
		    break;
		default:
		    mode = 8;
		    getLogger().warning("Element not waited `"+d+"'");
		    break;
	    }
	}

	if (mode != 8 && mode != 0) {
	    if (value1 != null && annotation != null) {
		ia.addInitialValue(annotation, value1);
		if (annotation.equals("action")) {
		    String[] sa = value1.split(",");
		    for (String s : sa) {
			ia.addActions(s, isOuter);
		    }
		}
	    }

	    Dumper removed = new Dumper();
	    while (removed != null) {
		removed = null;
		for (Dumper d: dumperList) {
		    String name = d.getTerms()[0];
		    if (name.charAt(0) == '"')
			name = name.substring(1, name.length()-1); // unquote
		    if (!name.equals(individualName))
			continue;

		    removed = d;
		    break;
		}
		if (removed != null) 	dumperList.remove(removed);
	    }
	}

	if (mode == 8) return false;
	return true;
    }

    @SuppressWarnings("unchecked")
    public int getNumberAgentsSettings() {
	Option<Integer> oi = options.get(3);
	return oi.value;
    }

    protected void printOptions() {
	int param = 0;
	for (Option o : options) {
	    param += 1;
	    if (o.description != null) {
		getLogger().warning("The "+param+"o. argument is "+
			((o.isRequired) ? "mandatory" : "optional")
			+" and is "+o.description);
	    }
	}
    }

    @Override
    protected void stepFinished(int step, long elapsedTime, boolean byTimeout) {
	long mean = (step > 0) ? (sum + elapsedTime) / (step + 1) : 0;
	getLogger().info("Finished step " + step + " after " + elapsedTime + " (mean "+ mean +")");
	sum += elapsedTime;
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




    protected class Option<T> {
	public T value;
	final public String description;
	final public String help;
	final public boolean isRequired;

	public Option ( boolean required, String descr, String error, T valured )
	{
	    value = valured;
	    help = error;
	    description = descr;
	    isRequired = required;
	}
    }

    public String getAnnot(String name, String attribute, String defVal) {
	InitialAnnotated ia = annots.get(name);
	if (ia == null) return null;
	return ia.getValue(attribute, defVal);
    }

    public Integer getAnnot(String name, String attribute, Integer defVal) {
	InitialAnnotated ia = annots.get(name);
	if (ia == null) return null;
	return ia.getValue(attribute, defVal);
    }

    public Character getAnnot(String name, String attribute, Character defVal) {
	InitialAnnotated ia = annots.get(name);
	if (ia == null) return null;
	return ia.getValue(attribute, defVal);
    }

    public String getActionsToServing(String name) {
	InitialAnnotated ia = annots.get(name);
	if (ia == null) return null;
	String [] ss = ia.getActionsToOthersDo();
	String ret = null;

	if (ss == null)
	    return null;

	for (String st : ss) {
	    if (ret == null)
		ret = st;
	    else
		ret += "," + st;
	}
	return ret;
    }

    public String getActionsToDo(String name) {
	InitialAnnotated ia = annots.get(name);
	if (ia == null) return null;
	String [] ss = ia.getActionsToMyDo();
	String ret = null;

	if (ss == null)
	    return null;

	for (String st : ss) {
	    if (ret == null)
		ret = st;
	    else
		ret += "," + st;
	}
	return ret;
    }

    protected void destroyAnnots() {
	// free(annots); // snif snif... it's not C :'(
	annots = null;
    }


    protected class InitialAnnotated {
	// What features are allow? Note: String x (isOuter)
	protected HashMap<String, Boolean> features = new HashMap<String,Boolean>();
	// What the others can do to my?
	protected HashMap<String, Integer> actionsOthers = new HashMap<String,Integer>();
	// What I can do?
	protected HashMap<String, Integer> actionsMyself = new HashMap<String,Integer>();
	// What I have to look after the load initial?
	protected List<String> individualsOuter = null;
	// See above...
	protected List<String> individualsInner = null;
	// default elements
	protected HashMap<String, Integer> dsiFeatures = new HashMap<String,Integer>();
	protected HashMap<String, String> dssFeatures = new HashMap<String,String>();
	protected String name;

	public InitialAnnotated ( String prefix )
	{
	    name = prefix;
	}

	public void addIndividualInner(String s) {
	    if (individualsInner == null) individualsInner = new ArrayList<String> ();
	    if (individualsInner.contains(s) == false)
		individualsInner.add(s);
	}

	public void addIndividualOuter(String s) {
	    if (individualsOuter == null) individualsOuter = new ArrayList<String> ();
	    if (individualsOuter.contains(s) == false)
		individualsOuter.add(s);
	}

	public void addInitialValue(String name, Integer value) {
	    Integer i = dsiFeatures.get(name);
	    if (i == null) {
		dsiFeatures.put(name, value);
	    }
	}

	public Integer getValue(String name, Integer defaultValue) {
	    Integer i = dsiFeatures.get(name);
	    if (i == null)  return defaultValue;
	    return new Integer(i);
	}

	public void addInitialValue(String name, String value) {
	    String s = dssFeatures.get(name);
	    if (s == null) {
		s = value;
	    } else {
		s = s + "," + value;
	    }
	    dssFeatures.put(name, s);
	}

	public String getValue(String name, String defaultValue) {
	    String i = dssFeatures.get(name);
	    if (i == null)  return defaultValue;
	    return new String(i);
	}

	public char getValue(String name, char defaultValue) {
	    String i = dssFeatures.get(name);
	    if (i == null)  return defaultValue;
	    return i.charAt(0);
	}

	public void addFeature(String name, boolean isOuter) {
	    features.put(name, isOuter);
	}

	public boolean isFeature(String name, boolean isOuter) {
	    Boolean flag = features.get(name);
	    if (flag == null) return false;
	    return (flag == isOuter)?true:false;
	}

	public String[] getAllFeatures() {
	    return features.keySet().toArray(new String[] { });
	}

	public void addActions(String name, boolean isOuter) {
	    // What I provider need be similar to what I can do...
	    // We can't put together because the key values can be repeated...
	    if (isOuter) {
		actionsOthers.put(name, 1);
	    } else {
		actionsMyself.put(name, 0);
	    }
	}

	public String[] getAllActions() {
	    Set<String> ss = new HashSet<String>();
	    ss.addAll(actionsOthers.keySet());
	    ss.addAll(actionsMyself.keySet());
	    return ss.toArray(new String[] { });
	}

	public String[] getActionsToMyDo() {
	    return actionsMyself.keySet().toArray(new String[] { });
	}

	public String[] getActionsToOthersDo() {
	    return actionsOthers.keySet().toArray(new String[] { });
	}
    }
}
