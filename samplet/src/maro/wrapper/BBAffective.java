package maro.wrapper;

// my only
import maro.core.EmotionKnowledge;

//java only
import java.util.Iterator;
import java.util.Set;

// jason only
import jason.bb.BeliefBase;
import jason.asSyntax.Term;
import jason.asSyntax.Literal;
import jason.bb.ChainBBAdapter;
import jason.asSemantics.Agent;
import jason.asSyntax.NumberTerm;
import jason.asSemantics.Unifier;
import jason.asSyntax.PredicateIndicator;

// others only
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Nao sei, me parece q ta dando problema de acesso com corrente.
// mas nao temos acesso concorrente, temos?
public class BBAffective
				extends ChainBBAdapter //implements Cloneable
{
	protected EmotionKnowledge ek = null;
	private Agent myAgent = null;
	protected String myName = null;

	/**
	 * Called before the MAS execution with the agent that uses this
	 * BB and the args informed in .mas2j project.<br>
	 * Example in .mas2j:<br>
	 *     <code>agent BeliefBaseClass(1,bla);</code><br>
	 * the init args will be ["1", "bla"].
	 */
	public void
	init(Agent ag, String[] args) {
		myAgent = ag; 
		myName = myAgent.getTS().getUserAgArch().getAgName();

		if (args.length == 1) {
			try {
				ek = new EmotionKnowledge(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(22);
			}
		}
	}

	/** Called just before the end of MAS execution */
	public void
	stop() {
		ek.dumpData();
	}

	/** Returns the number of beliefs in BB */
	public int
	size() {
		// nao pareceu ser chamado em nenhum ponto...
		System.err.println("lucca size()");
 		return super.size();
	}

	/** Adds a belief in the begin/end of the BB, returns true if succeed.
	 *  The annots of l may be changed to reflect what was changed in the BB,
	 *  for example, if l is p[a,b] in a BB with p[a], l will be changed to
	 *  p[b] to produce the event +p[b], since only the annotation b is changed
	 *  in the BB. */
    protected boolean
	add(Literal l, boolean addInEnd) {
		Dumper d = null;
		boolean ret = false;
		int arity = l.getArity();

		// Se eh uma percepcao nao temos interesse...
		if (l.hasAnnot(BeliefBase.TPercept)) {
			// ... mas temos interesse no caso da crenca step para atualizar o resumo
			if (l.getFunctor().equals("step") == false)
				return false;

			NumberTerm nt = (NumberTerm) l.getTerm(0);
			int step = (int) nt.solve();
			Set<Literal> ld = ek.summarize(myName, step);

			for (Literal li: ld) {
				super.add(li);
			}

			return false;
		}
		// Se for o functor 'sameAs' aceitamos, independente da aridade
		// Caso contrario, a aridade tem que estar em 1 ou 2.
		if (!l.getFunctor().equals("sameAs") && (arity < 1 || arity > 2))
			return false;

		d = Dumper.dumpLiteral(l);
		ret = ek.add(d);
		//System.err.println("lucca add " + d + " = " + ret);
		return ret;
	}

    public boolean
	add(Literal l) {
		boolean ret = add(l, false);
		if (ret) return true;
		return super.add(l);
    }

    public boolean
	add(int index, Literal l) {
		boolean ret = add(l, index != 0);
		if (ret) return true;
		return super.add(index, l);
    }

	/** Returns an iterator for all beliefs. */
	@Override
	public Iterator<Literal>
	iterator() {
		final Iterator<Literal> ret = super.iterator();
		final Iterator<Dumper> id = ek.iterator();

		return new Iterator<Literal> () {
			@Override
			public boolean hasNext() {
				return id.hasNext() || ret.hasNext();
			}
			@Override
		    public Literal next() {
				if (id.hasNext() == false) return ret.next();

				Literal li = Dumper.fromDumper(id.next(), 43);
				return li;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	protected Iterator<Literal>
	getCandidateBeliefs(String functor, int arity)
	{
		final Iterator<Dumper> id = ek.getCandidatesByFunctorAndArity(functor, arity);
		//System.err.println("lucca iter: " + id + " for " + functor + "/" + arity);
		if (id == null)  return null;
        return new Iterator<Literal> () {
            @Override
            public boolean hasNext() {
                return id.hasNext();
            }
            @Override
            public Literal next() {
                if (hasNext() == false) return null;

                Literal li = Dumper.fromDumper(id.next(), 39);
                return li;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
	}

	/** 
	 * Returns an iterator for all literals in BB that match the functor/arity 
	 * of the parameter.<br>
	 */
	public Iterator<Literal>
	getCandidateBeliefs(PredicateIndicator pi)
	{
		Iterator<Literal> it = getCandidateBeliefs(pi.getFunctor(), pi.getArity());
		if (it != null) return it;
		return super.getCandidateBeliefs(pi);
	}

	/** 
	 * Returns an iterator for all literals relevant for l's predicate
	 * indicator, if l is a var, returns all beliefs.<br>
	 * 
	 * The unifier <i>u</i> may contain values for variables in <i>l</i>.
	 *
	 * Example, if BB={a(10),a(20),a(2,1),b(f)}, then
	 * <code>getCandidateBeliefs(a(5), {})</code> = {a(10),a(20)}.<br>
	 * if BB={a(10),a(20)}, then <code>getCandidateBeliefs(X)</code> =
	 * {a(10),a(20)}. The <code>getCandidateBeliefs(a(X), {X -> 5})</code> 
	 * should also return {a(10),a(20)}.<br>
	 */
	@Override
	public Iterator<Literal>
	getCandidateBeliefs(Literal l, Unifier u)
	{
        if (l.isVar()) {
            // all bels are relevant
            return iterator();
        }

		return getCandidateBeliefs(l.getPredicateIndicator());
	}

	/**
	 * Returns the literal l as it is in BB, this method does not
	 * consider annotations in the search. <br> Example, if
	 * BB={a(10)[a,b]}, <code>contains(a(10)[d])</code> returns
	 * a(10)[a,b].
	 */
	public Literal
	contains(Literal l)
	{
		Iterator<Literal> il = getCandidateBeliefs(l.getPredicateIndicator());

        if (il != null) {
            while (il.hasNext()) {
                Literal lit = il.next();
                Term[] litS = lit.getTermsArray();
                Term[] lS = l.getTermsArray();
                boolean ok = true;

                for (int idx = 0; ok && idx < lS.length; idx++) {
                    if (litS[idx].toString().equals(lS[idx].toString()) == false)
                        ok = false;
                }

                if (ok == true) return lit;
            }
        }

        return super.contains(l);
	}

	/** Returns all beliefs that have "percept" as source */
//	public Iterator<Literal> getPercepts() // LATER: por enquanto nao teremos percepcoes aqui
//	{
//		// passar pela ontologia primeiro e depois pelo default...
//		// ...os dados da ontologia seriam por enquanto so crencas...
//		// ...todavia uma outra poderia ser feita para mapear o 
//		// eu ''externo'' com o eu ''interno'' ?!
//		Iterator<Literal> perceived = super.getPercepts();
//		
//		while (perceived != null && perceived.hasNext()) {
//			Literal lit = perceived.next();
//			System.err.println("lucca "+ myName
//						+".getPercepts(): "+lit);
//		}
//
//		return super.getPercepts();
//	}

	/** Removes a literal from BB, returns true if succeed */
	public boolean
	remove(Literal l)
	{
		Dumper d = null;
		boolean ret = false;
		int arity = l.getArity();
        Literal s;

		// Se eh uma percepcao nao temos interesse...
		if (l.hasAnnot(BeliefBase.TPercept)) {
			return super.remove(l);
		}
		// Se for o functor 'sameAs' aceitamos, independente da aridade...
		// Caso contrario, a aridade tem que estar em 1 ou 2.
		if (!l.getFunctor().equals("sameAs") && (arity < 1 || arity > 2))
			return super.remove(l);

        s = contains(l);
        l.delAnnot( BeliefBase.TSelf );
        if (s != null) s.delAnnots(l.getAnnots());

		d = Dumper.dumpLiteral(s);

        if (s.getAnnots().size() > 1) // ontology and source
            ret = ek.add(d); // remove and add the replacement
        else
            ret = ek.remove(d);
		//System.err.println("lucca del " + d + " = " + ret + " based on " + l);
		if (ret == false) return super.remove(l);
		return ret;
	}

	/** Removes all believes with some functor/arity */
	public boolean
	abolish(PredicateIndicator pi)
	{
		// Visto no codigo e parece que nao ocorre mais,
		// o agente chama direto cada um a ser removido
		// baseado no literal...
		System.err.println("abolish(PredicateIndicator) not implemented");
		System.exit(18);
		return super.abolish(pi);
	}

	// So foi necessario sobrecarregar isso porque o iterator
	// da nossa classe por algum motivo nao era chamado... :/
	@Override
    public Element
	getAsDOM(Document document) {
		// codigo copiado do DefaultBeliefBase
        int tries = 0;
        Element ebels = null;
        while (tries < 10) {
            ebels = (Element) document.createElement("beliefs");
            try { 
                for (Literal l: this) 
                    ebels.appendChild(l.getAsDOM(document));
                break;
            } catch (Exception e) {
                tries++;
            }
        }
        return ebels;
    }

	/*@Override
	public BeliefBase
	clone()
	{
		return (BeliefBase) super.clone();
	}*/
}
