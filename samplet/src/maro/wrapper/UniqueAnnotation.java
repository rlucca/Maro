package maro.wrapper;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.bb.BeliefBase;
import java.util.Iterator;
import java.util.List;

public class UniqueAnnotation extends Agent
{
    @Override
    public void buf(List<Literal> percepts) {
        if (percepts == null) return ;

        Iterator<Literal> perceptsInBB = getBB().getPercepts();
        while (perceptsInBB.hasNext()) { 
            Literal l = perceptsInBB.next();
            if (l.hasAnnot() && l.getAnnots().size() >= 4) {
                Iterator<Literal> ip = percepts.iterator();
                while (ip.hasNext()) {
                    Literal t = ip.next();
                    if (l.equalsAsStructure(t) && l.negated() == t.negated()) {
                        l.clearAnnots();
						// When don't have annotation the default functions
						// remove the new perceptions silently
						l.addAnnot(BeliefBase.TPercept);
                        break;
                    }
                }
            }
        }

        super.buf(percepts);
    }
}
