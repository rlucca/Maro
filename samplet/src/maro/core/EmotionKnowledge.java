package maro.core;

import maro.wrapper.Dumper;
import maro.wrapper.OwlApi;
import maro.wrapper.PreferenceLoader;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

public class EmotionKnowledge {

	protected FeelingsThreshold ft;
	protected OwlApi oaw = null;
	protected String filename;
	protected String agName;
	protected Emotion emotion;
	protected Integer lastStepFeeling;

	public EmotionKnowledge(String name, String fileName) throws Exception {
		filename = fileName;
		agName = name;

		oaw = new OwlApi();
		oaw.loadOntologyFromFile(filename);

		Set<String> allEmotions;
		allEmotions = oaw.getAllEmotions();
		if (allEmotions == null || allEmotions.isEmpty()) {
			throw new Exception("Fail on load ontology data");
		}

		emotion = new Emotion();
		emotion.setEmotions(allEmotions);

		ft = new FeelingsThreshold();
        PreferenceLoader.getInstance().prepare(oaw);

		// Erase all itens from setup et al.
		oaw.remove(2, "hasAnnotationValue", null, null);
		oaw.remove(2, "hasAnnotation", null, null);
		oaw.remove(2, "hasName", null, null);

		oaw.remove(1, "static", null, null);
		oaw.remove(1, "dynamic", null, null);
		oaw.remove(1, "annotation", null, null);
		oaw.remove(1, "positive", null, null);
		oaw.remove(1, "negative", null, null);
		oaw.remove(1, "preference", null, null);
	}

	protected boolean changeBelief(Dumper dumper, boolean isAdd) {
		String functor;
		String[] terms;
		Dumper[] annots;
		int arity;

		if (dumper == null) {
			return false;
		}

		terms = dumper.getTerms();
		arity = dumper.getArity();
		functor = dumper.getFunctorComplete();

		if (!oaw.isRelevant(arity, functor)) {
			return false;
		}

		annots = dumper.getAnnots();

		if (isAdd) {
			return oaw.add(arity, functor, terms, annots);
		}
		//else
		return oaw.remove(arity, functor, terms, annots);
	}

	public boolean add(Dumper dumper) {
		return changeBelief(dumper, true);
	}

	public boolean remove(Dumper dumper) {
		return changeBelief(dumper, false);
	}

	public Iterator<Dumper> getCandidatesByFunctorAndArityIter(String functor, int arity) {
		if (functor.equals("sameAs") || functor.equals("~sameAs")) {
			arity = 0;
		}

		if (!oaw.isRelevant(arity, functor)) {
			return null;
		}

		return oaw.getCandidatesByFunctorAndArityIter(arity, functor);
	}

	public Iterator<Dumper> iterator() {
		return oaw.iterator();
	}
	boolean ignoreFlag = false;

	public void summarize(int step) {
		if (ft.isLoaded(emotion, agName, oaw) == true) {
			if (ft.isActive() == false) {
				if (ignoreFlag == false) {
					System.out.println("Threshold of emotions are "
						+ "inconsistent ou fault, ignoring emotions...");
					ignoreFlag = true;
				}
				return; // nao temos threshold pq perder tempo?
			} else {
				if (ignoreFlag == false) {
					oaw.remove(2, "hasThresholdType", null, null);
					oaw.remove(2, "hasThreshold", null, null);
					oaw.remove(2, "isSetupOf", null, null);
					oaw.remove(2, "hasSetup", null, null); // inverse of isSetupOf
					oaw.remove(1, "setup", null, null);
					ignoreFlag = true;
				}
			}
		}

		for (String s : emotion.getEmotions()) {
			HashMap<Integer, Integer> e = emotion.getAllValences(s, agName);
			Integer ret = oaw.summaryOf(agName, s, step, e);
			if (ret == null) {
				continue;
			}
			emotion.setValence(s, agName, step, ret);
		}
	}

	public Set<String> getEmotionType() {
		Set<String> et = new java.util.HashSet<String>();
		for (String s : emotion.getEmotions())
			et.add(s);
		return et;
	}

	public Integer getEmotionPotence(String emotionType, String agentName) {
		Integer valence = emotion.getValence(emotionType, agentName,
				(lastStepFeeling == null) ? 0 : lastStepFeeling);
		return valence;
	}
	public Integer getEmotionValence(String emotionType, String agentName) {
		Integer valence = getEmotionPotence(emotionType, agentName);
		Integer minimum = ft.getThreshold(emotionType);
		if (valence == null) return null;
		return valence - minimum;
	}

	public Set<String> feelings(int step) {
		Set<String> feelingStrLit = new java.util.HashSet<String>();

		if (ft.isActive() == false) {
			return feelingStrLit;
		}

		lastStepFeeling = step;
		for (String s : getEmotionType()) {
			Integer potence = getEmotionValence(s, agName);
			int feeling = (potence != null) ? potence : 0;

			if (feeling > 0) {
				feelingStrLit.add("feeling(" + s + ", " + feeling + ")");
			}
		}

		return feelingStrLit;
	}

	public void dumpData() {
		if (System.getenv("emotionKnowledgeDebug") == null) {
			return;
		}

		try {
			oaw.dumpOntology("/tmp/baka.owl");
		} catch (Exception e) {
			System.err.println("Error dumping ontology");
			e.printStackTrace();
			System.exit(29);
		}
	}
}
