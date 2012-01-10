package maro.example.console;

import jason.architecture.AgArch;
import jason.asSyntax.Literal;
import java.util.List;

public class AddGui extends AgArch
{
	private boolean initialized = false;
	private CharacterInspectorView civ = null;

    public synchronized void myInit() {
		if (initialized == true) return ;

		civ = new CharacterInspectorView(getAgName());
		civ.setController(this);
		civ.setVisible(true);

		initialized = true;
    }

	@Override
    public List<Literal> perceive() {
		if (initialized == false) {
			myInit();
		}

		civ.update();
		return super.perceive();
    }
}
