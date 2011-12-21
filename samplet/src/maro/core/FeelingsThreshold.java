package maro.core;

import maro.wrapper.OwlApi;
import maro.wrapper.Dumper;

import java.util.HashMap;
import java.util.Set;



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
		Set<Dumper> ids =
			oaw.getCandidatesByFunctorAndArity(1, "setup");
		if (ids == null) return ;
		Set<Dumper> isSetupOfs =
			oaw.getCandidatesByFunctorAndArity(2, "isSetupOf");
		Set<Dumper> hasThresholds =
			oaw.getCandidatesByFunctorAndArity(2, "hasThreshold");
		Set<Dumper> hasThresholdTypes =
			oaw.getCandidatesByFunctorAndArity(2, "hasThresholdType");

		thresholds = new HashMap<String, Integer> ();

		//setup(Setup) pode ter todas essas relacoes:
		//  isSetupOf(Setup, AgentName).
		//  hasThreshold(Setup, Integer).
		//  hasThresholdType(Setup, EmotionType).
		for (Dumper du : ids) {
			String emotion = null;
			Integer value = null;
			String setupName = du.getTerms()[0];
			Dumper issetupof = locate(setupName, isSetupOfs, 0);
			Dumper hasthreshold = locate(setupName, hasThresholds, 0);
			Dumper hasthresholdType = locate(setupName, hasThresholdTypes, 0);
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

		loaded = true;
		if (thresholds.size() == 22) actived = true;
	}

	private Dumper
	locate(String setupName, Set<Dumper> it, int lookAt) {
		if (it == null) return null;

		// hasSetup eh o unico que tem o setupName no segundo termo...
		//if (relation.equals("hasSetup"))
		//	lookAt = 1;

		for (Dumper d : it) {
			if (d.getTerms()[lookAt].equals(setupName)) {
				return d;
			}
		}

		return null;
	}
}

