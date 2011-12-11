package maro.example;

import maro.wrapper.BBAffective;

import jason.infra.centralised.CentralisedEnvironment;
import jason.infra.centralised.CentralisedAgArch;
import jason.architecture.AgArchInfraTier;
import jason.asSemantics.TransitionSystem;
import jason.environment.Environment;
import jason.mas2j.ClassParameters;
import jason.architecture.AgArch;
import jason.runtime.Settings;
import jason.JasonException;

import java.util.Set;

public class LUAgArchViewer extends AgArch
{
	//LU2DEnv env;

	public Environment recoverEnvironment() {
		TransitionSystem ts = getTS();
		// Use of TS here is necessary to recover the actual list of architectures
		AgArch aa = ts.getUserAgArch().getFirstAgArch();
		while (aa != null && !(aa instanceof CentralisedAgArch)) {
			aa = aa.getNextAgArch();
		}

		CentralisedAgArch caa = (CentralisedAgArch) aa;
		CentralisedEnvironment envInfraTier = caa.getEnvInfraTier();
		return envInfraTier.getUserEnvironment();
	}

	private boolean getOcclusion() {
		Settings stts = getTS().getSettings();
		String val = stts.getUserParameter("dontOcclusion");
		if (val != null) return false;
		return true;
	}

	@Override
    public void init() throws Exception
	{
		LU2DEnv env = getEnvironment();
		//LUModel model = env.getModel();
		// NOTE: The responsability of the keep the model is from environment... maybe it's wrong!
		if (env.getDebugViewer() == false) {
			new LU2DPlayerView (this, "Environment", true, getOcclusion());
		}
	}

	public LU2DEnv getEnvironment() {
		Environment e = recoverEnvironment();
		if ( !(e instanceof LU2DEnv) )
			return null; // I don't know this environment
		return (LU2DEnv) e;
	}

	public LUModel getModel() {
		// NOTE: The responsability of the keep the model is from environment... maybe it's wrong!
		return getEnvironment().getModel();
	}
}

