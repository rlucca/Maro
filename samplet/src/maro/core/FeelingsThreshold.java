package maro.core;

import maro.wrapper.OwlApi;
import maro.wrapper.Dumper;

import java.util.HashMap;
import java.util.Iterator;



class FeelingsThreshold {
	protected HashMap<String, Integer> thresholds;
	protected boolean actived;
	protected boolean loaded;

	public FeelingsThreshold() throws Exception {
		actived = false;
		loaded = false;
	}

	public Integer getThreshold(String eType) {
		if (thresholds == null) return null;
		return thresholds.get(eType);
	}

	public boolean isActive() {
		return actived;
	}

	public boolean isLoaded(Emotion e, String agentName, OwlApi oaw) {
		if (loaded == false) {
			load(e, agentName, oaw);
		}
		return loaded;
	}

	protected void load(Emotion e, String myName, OwlApi oaw) {
		Iterator<Dumper> id =
			oaw.getCandidatesByFunctorAndArityIter(1, "setup");
		if (id == null) return ;

		thresholds = new HashMap<String, Integer> ();

		//setup(Setup) pode ter todas essas relacoes:
		//  isSetupOf(Setup, AgentName).
		//  hasThreshold(Setup, Integer).
		//  hasThresholdType(Setup, EmotionType).
		while (id.hasNext()) {
			String emotion = null;
			Integer value = null;
			Dumper du = id.next(); // setup(X), X -> setup individual
			String setupName = du.getTerms()[0];
			Dumper issetupof = locate(oaw, setupName, "isSetupOf", 2);
			Dumper hasthreshold = locate(oaw, setupName, "hasThreshold", 2);
			Dumper hasthresholdType =
				locate(oaw, setupName, "hasThresholdType", 2);
			String name = null;

			try {
				if (issetupof == null || hasthreshold == null
						|| hasthresholdType == null)
					continue; // i am not interessed

				name = issetupof.getTermAsString(1);

				if (name.equals(myName) == false) {
					//System.out.println("skip because isnt mine threshold: "+myName+"!="+name);
					continue; // skip too, i am not interessed
				}

				emotion = hasthresholdType.getTermAsString(1);
				if (e.isEmotion(emotion) == false) {
					//System.out.println("skip because isnt a emotion: "+emotion);
					continue; // skip, emotion invalid
				}

				value = hasthreshold.getTermAsInteger(1);
			} catch (Exception ex) {
				continue; // skip, exception
			}

			//System.out.println("putting "+emotion+" with "+value);
			thresholds.put(emotion, value);
		}

		oaw.remove(2, "hasThresholdType", null, null);
		oaw.remove(2, "hasThreshold", null, null);
		oaw.remove(2, "isSetupOf", null, null);
		oaw.remove(2, "hasSetup", null, null); // inverse of isSetupOf
		// if this is null then we really have a problem!
		oaw.remove(1, "setup", null, null);

		loaded = true;
		if (thresholds.size() == 22) actived = true;
	}

	private Dumper
	locate(OwlApi oaw, String setupName, String relation, int arity) {
		int lookAt = 0;
		Iterator<Dumper> it =
			oaw.getCandidatesByFunctorAndArityIter(arity, relation);
		if (it == null) return null;

		// hasSetup eh o unico que tem o setupName no segundo termo...
		if (relation.equals("hasSetup"))
			lookAt = 1;

		while (it.hasNext()) {
			Dumper d = it.next();

			if (d.getTerms()[lookAt].equals(setupName)) {
				return d;
			}
		}

		return null;
	}
}

