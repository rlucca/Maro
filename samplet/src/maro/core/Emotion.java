package maro.core;

import java.util.Set;
import java.util.HashMap;

class SteppedValence extends HashMap<Integer, Integer>     { }
class AgentValence extends HashMap<String, SteppedValence> { }
class EmotionValence extends HashMap<String, AgentValence> { }

class Emotion {
	protected Set<String> allEmotions;
	protected EmotionValence emotionsByStep;

	public Set<String> getEmotions() { return allEmotions;  }
	public void setEmotions(Set<String> ss) { allEmotions = ss; }

	public boolean
	isEmotion(String emo) {
		String emotion = emo.toLowerCase();
		for (String e: allEmotions) {
			if (e.equals(emotion)) {
				return true;
			}
		}
		return false;
	}

	public void
	setValence(String eType, String aName, int step, int v) {
		SteppedValence sv = getAllValences(eType, aName);
		sv.put(step, v);
	}

	public SteppedValence
	getAllValences(String eType, String aName) {
		AgentValence av;
		SteppedValence sv;

		if (emotionsByStep == null) {
			emotionsByStep = new EmotionValence();
		}

		av = emotionsByStep.get(eType);
		if (av == null) {
			av = new AgentValence();
			emotionsByStep.put(eType, av);
		}

		sv = av.get(aName);
		if (sv == null) {
			sv = new SteppedValence();
			av.put(aName, sv);
		}

		return sv;
	}

	public Integer
	getValence(String eType, String aName, int step) {
		SteppedValence sv = getAllValences(eType, aName);
		Integer valence = sv.get(step);
		return valence;
	}
}

