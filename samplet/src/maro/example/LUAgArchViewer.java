package maro.example;

import jason.mas2j.ClassParameters;
import maro.wrapper.AgArchFixBB;
import jason.runtime.Settings;
import jason.JasonException;

public class LUAgArchViewer extends AgArchFixBB
{
    LU2DEnv env;

	@Override
    public void initAg(String agClass, ClassParameters bbPars, String asSrc, Settings stts)
					throws JasonException
	{
		super.initAg(agClass, bbPars, asSrc, stts);

        jason.infra.centralised.CentralisedAgArch infra
            = (jason.infra.centralised.CentralisedAgArch) getArchInfraTier();
        jason.infra.centralised.CentralisedEnvironment envInfra
            = infra.getEnvInfraTier();
        env = (maro.example.LU2DEnv) envInfra.getUserEnvironment();

        LUModel player = new LUModel(1);
		LU2DView l2v = new LU2DPlayerView (player, env, "Environment", true);
        player.setView(l2v);
	}
}

