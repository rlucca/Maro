package eoaus;

import jason.bb.ChainBBAdapter;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BBAffective extends ChainBBAdapter
{
	final static public String emotionPotenceLabel = "emotionPotence";
	private Map<String,Double> msd = new ConcurrentHashMap<String,Double>();
	private Literal stepLit = (Literal) Literal.parseLiteral("step(X)");
	private int lastStep = -1;

	private class BeliefAffective {
		final private String label;
		final private Double value;

		public BeliefAffective(Literal l) {
			List<Term> lt = l.getTerms();
			NumberTerm nt = (NumberTerm) lt.get(1);
			label = lt.get(0).toString();
			value = new Double(nt.solve());
		}

		public String getName() { return label; }
		public Double getValue() { return value; }
	}

	private boolean myBelief(Literal l) {
		return l.getArity() == 2 && l.getFunctor().equals(emotionPotenceLabel);
	}

	private void updateEmotion(String label, Double value) { // A'(V) = A + V
		Double anterior = msd.get(label);

		if (anterior == null) {
			msd.put(label, value);
		} else {
			Double novo = anterior + value;
			msd.put(label, novo);
		}
	}

	private BeliefAffective tryUpdateEmotion(Literal l) {
		if (myBelief(l)) {
			BeliefAffective ba = new BeliefAffective(l);
			updateEmotion(ba.getName(), ba.getValue());
			return ba;
		}
		return null;
	}

	/*private void tosqueiraDeleteme(Iterator<Literal> percepts) {
		if (percepts == null) return ;

		while (percepts.hasNext()) {
			Literal l = (Literal) percepts.next();
			if (tryUpdateEmotion(l) != null) {
				// ...
				percepts.remove();
			}
		}
	}*/
	private boolean lastStepChange() {
		Iterator<Literal> r = super.getCandidateBeliefs(stepLit, null);

		while (r != null && r.hasNext()) {
			Literal l = (Literal) r.next();
			if (l != null && l.getArity() == 1 ) {
				Term t = l.getTerms().get(0);
				int i = (int) ((NumberTerm)t).solve();

				if ( i == lastStep ) {
					// dont refresh... user called method?
					return false;
				} else {
					lastStep = i;
					return true;
				}
			}
		}

		return false;
	}

	private void refreshEmotion()
	{
		if ( !lastStepChange() ) {
			return ;
		}

		for (String l: msd.keySet()) {
			Double val = msd.get(l);
			// ...
		}
	}

	public BBAffective() {
		super();
	}

	@Override
    public void init(jason.asSemantics.Agent ag, String[] args) {
		super.init(ag, args);

        if (ag != null) {
            System.out.println("Agente BB: " + ag.getTS().getUserAgArch().getAgName() );
		}
	}

	@Override
    public boolean add(Literal l) {
		return this.add(1, l);
	}

	@Override
    public boolean add(int index, Literal l) {
		BeliefAffective ba = tryUpdateEmotion(l);
		if ( ba != null ) {
			return true;
		}
		return super.add(index, l);
	}

	@Override
    public Iterator<Literal> getPercepts() {
		refreshEmotion();
        return super.getPercepts();
    }

	public Map<String,Double> getEmotions() {
		Map<String,Double> m = new ConcurrentHashMap<String,Double>();
		for (String s: msd.keySet()) m.put(s, msd.get(s));
		return m;
	}
}

