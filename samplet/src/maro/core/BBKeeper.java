package maro.core;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Collections;
import maro.wrapper.BBAffective;

public class BBKeeper
{
	private Map<String, BBAffective> keeper = null;
	static private BBKeeper instance = null;

	private BBKeeper() {
		keeper = Collections.synchronizedMap(new HashMap<String, BBAffective> ());
	}

	static synchronized
	public BBKeeper getInstance() {
		if (instance == null)
			instance = new BBKeeper();
		return instance;
	}

	public void
	put(String name, BBAffective bb) {
		keeper.put(name, bb);
	}

	public BBAffective
	get(String name) {
		return keeper.get(name);
	}

	public Set<String>
	getEmotionType() {
		// this function return the same data to all BBAffective... then...
		if (keeper == null) return null;

		for (BBAffective bba : keeper.values()) {
			// the index here is not relevant...
			return bba.getEmotionType();
		}

		return null;
	}
}

