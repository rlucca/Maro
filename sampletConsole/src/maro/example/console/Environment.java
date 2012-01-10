package maro.example.console;

import maro.core.IntelligentEnvironment;

public class Environment extends IntelligentEnvironment
{
	@Override
	protected void stepStarted(int step) {
		clearAllPercepts();
		super.stepStarted(step);
	}

    @Override
    protected void stepFinished(int step, long elapsedTime, boolean byTimeout) {
        tryStop(lastStep);
	}
}
