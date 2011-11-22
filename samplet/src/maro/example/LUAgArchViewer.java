package maro.example;

import jason.mas2j.ClassParameters;
import jason.architecture.AgArch;
import jason.runtime.Settings;
import jason.JasonException;

public class LUAgArchViewer extends AgArch
{
    LU2DEnv env;

	@Override
    public void init() throws Exception
	{
        jason.infra.centralised.CentralisedAgArch infra
            = (jason.infra.centralised.CentralisedAgArch) getArchInfraTier();
        jason.infra.centralised.CentralisedEnvironment envInfra
            = infra.getEnvInfraTier();
        env = (maro.example.LU2DEnv) envInfra.getUserEnvironment();

		// TODO LATER
        //LUModel player; // need ask to environment the model
		// NOTE: The responsability of the keep the model is from environment... maybe it's wrong!
		//LU2DView l2v = new LU2DPlayerView (player, env, "Environment", true);
        //player.setView(l2v);
	}
}

