package maro.wrapper;

import jason.asSyntax.ASSyntax;
import jason.bb.BeliefBase;

public class PreferenceLoader
{
    private static PreferenceLoader instance = null;

    public static PreferenceLoader getInstance() {
        if (instance == null) instance = new PreferenceLoader();
        return instance;
    }

    private PreferenceLoader() { }

    public void prepare(OwlApi oa) {
    }

    public void load(BeliefBase bb)
        throws Exception
    {
		bb.add(ASSyntax.createLiteral("chamadoPeloLiteralNaoEhBrincadeira"));
    }
}
