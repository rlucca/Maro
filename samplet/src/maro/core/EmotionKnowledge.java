package maro.core;

// my
import maro.wrapper.Dumper;
import maro.wrapper.OwlApi;

//java only
import java.text.DecimalFormat;
import java.util.Iterator;

public class EmotionKnowledge
{
	protected OwlApi oaw = null;
	protected String filename;

	public EmotionKnowledge(String fileName) throws Exception {
		filename = fileName;

		oaw = new OwlApi();
		oaw.loadOntologyFromFile(filename);
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

