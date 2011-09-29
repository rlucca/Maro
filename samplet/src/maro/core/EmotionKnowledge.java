package maro.core;

import jason.asSyntax.Literal;

// my
import maro.wrapper.Dumper;
import maro.wrapper.OwlApi;

//java only
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

public class EmotionKnowledge
{
	protected OwlApi oaw = null;
	protected String filename;
    protected Set<String> allEmotions;
	// emocao X (agent X (step X valenceEmotion))
	protected HashMap<String, HashMap<String, HashMap<Integer, Integer> > > emotions;

	public EmotionKnowledge(String fileName) throws Exception {
		filename = fileName;

		oaw = new OwlApi();
		oaw.loadOntologyFromFile(filename);

        allEmotions = oaw.getAllEmotions();
		emotions = new HashMap<String, HashMap<String, HashMap<Integer, Integer>>>();
	}

	protected boolean
	changeBelief(Dumper dumper, boolean isAdd) {
		String functor;
		String[] terms;
		Dumper[] annots;
		int arity;

		if (dumper == null) return false;

		terms = dumper.getTerms();
		arity = dumper.getArity();
		functor = dumper.getFunctorComplete();

		if ( !oaw.isRelevant(arity, functor) )
			return false;

		annots = dumper.getAnnots();

		if (isAdd)
			return oaw.add(arity, functor, terms, annots);
		//else
		return oaw.remove(arity, functor, terms, annots);
	}

	public boolean
	add(Dumper dumper) {
		return changeBelief(dumper, true);
	}

	public boolean
	remove(Dumper dumper) {
		return changeBelief(dumper, false);
	}

	public Iterator<Dumper>
	getCandidatesByFunctorAndArity(String functor, int arity)
	{
		if (functor.equals("sameAs") || functor.equals("~sameAs"))
			arity = 0;

		if ( !oaw.isRelevant(arity, functor) )
			return null;

		return oaw.getCandidatesByFunctorAndArity(arity, functor);
	}

	public Iterator<Dumper>
	iterator() {
		return oaw.iterator();
	}

	public void
	summarize(String agentName, int step) {
        for (String s: allEmotions) {
			HashMap<String, HashMap<Integer, Integer> > hm1 = emotions.get(s);
			HashMap<Integer, Integer> hm2 = (hm1 == null) ? null : hm1.get(agentName);

            Integer ret = oaw.summaryOf(agentName, s, step, hm2);
            if (ret == null) continue;

			if (hm1 == null) {
				hm1 = new HashMap<String, HashMap<Integer, Integer> > ();
				emotions.put(s, hm1);
			}

			hm2 = hm1.get(agentName);
			if (hm2 == null) {
				hm2 = new HashMap<Integer, Integer>();
				hm1.put(agentName, hm2);
			}

			hm2.put(step, ret);
        }
	}

	public void dumpData() {
		if (System.getenv("emotionKnowledgeDebug") == null) return ;

		try {
			oaw.dumpOntology("/tmp/baka.owl");
		} catch (Exception e) {
			System.err.println("Error dumping ontology");
			e.printStackTrace();
			System.exit(29);
		}
	}
}

