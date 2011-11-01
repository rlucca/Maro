package maro.wrapper;

import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Circumstance;
import jason.mas2j.ClassParameters;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.runtime.Settings;
import jason.JasonException;
import jason.bb.BeliefBase;

public class AgArchFixBB extends AgArch
{
	@Override
    public void initAg(String agClass, ClassParameters bbPars, String asSrc, Settings stts) throws JasonException {
        try {
            Agent ag = (Agent) Class.forName(agClass).newInstance();

            new TransitionSystem(ag, new Circumstance(), stts, this);

            BeliefBase bb = (BeliefBase) Class.forName(bbPars.getClassName()).newInstance();
            bb.init(ag, bbPars.getParametersArray()); // BEFORE INIT AG!!!

            ag.setBB(bb);
            ag.initAg(asSrc);

            String mindinspec = stts.getUserParameter("mindinspector");
            if (mindinspec != null)
                setupMindInspector(mindinspec);
        } catch (Exception e) {
            throw new JasonException("as2j: error creating the customised Agent class! - ", e);
        }
    }

}
