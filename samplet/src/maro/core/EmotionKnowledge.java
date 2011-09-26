package maro.core;

import jason.asSyntax.Literal;

// my
import maro.wrapper.Dumper;
import maro.wrapper.OwlApi;

//java only
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class EmotionKnowledge
{
	protected OwlApi oaw = null;
	protected String filename;
    protected Set<String> allEmotions;

	public EmotionKnowledge(String fileName) throws Exception {
		filename = fileName;

		oaw = new OwlApi();
		oaw.loadOntologyFromFile(filename);

        allEmotions = oaw.getAllEmotions();
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

	public Set<Literal>
	summarize(String agentName, int step) {
        Set<Literal> sd = new HashSet<Literal>();
        for (String s: allEmotions) {
            String ret = oaw.summaryOf(agentName, s);
            if (ret == null) continue;

            ret = "hasFeeling("+ret+","+step+")";
            Literal d = Dumper.fromString(ret, 17);
            sd.add(d);
        }
		return sd;
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

