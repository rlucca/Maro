package eoaus;

import jason.JasonException;
import jason.architecture.AgArch;
import jason.mas2j.ClassParameters;
import jason.runtime.Settings;
import jason.asSyntax.Literal;

import java.util.List;
import java.util.Map;

public class AgArchAffective extends AgArch
{
	protected GUI gui = null;

    public void initAg(String agClass, ClassParameters bbPars, String asSrc, Settings stts)
				throws JasonException
	{
		super.initAg(agClass, bbPars, asSrc, stts);
		gui = new GUI(this);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gui.setVisible(true);
            }
        });
	}

	public void stopAg()
	{
		super.stopAg();
		gui = null;
	}

    public List<Literal> perceive() {
		if ( gui != null ) gui.update();
        return super.perceive();
    }

	public void request(String s) { // okay, it is not beautiful...
		Literal lit;
		jason.infra.centralised.CentralisedAgArch infra
			= (jason.infra.centralised.CentralisedAgArch) getArchInfraTier();
		jason.infra.centralised.CentralisedEnvironment envInfra
			= infra.getEnvInfraTier();
		jason.environment.Environment env
			= envInfra.getUserEnvironment();

		if ( s.startsWith("#") ) {
			lit = Literal.parseLiteral(s.substring(1, s.length()));
			env.addPercept(lit);
		} else {
			lit = Literal.parseLiteral(s);
			env.addPercept(getArchInfraTier().getAgName(), lit);
		}
	}

	public Map<String,Double> emotion() {
		return ((BBAffective)getTS().getAg().getBB()).getEmotions();
	}
}
