package maro.core;

import maro.wrapper.OwlApi;
import java.util.HashMap;
import java.io.File;

public class AnnotatedEnvironment extends IntelligentEnvironment
{
	protected Annotations annots;
	protected OwlApi oapi;
	private int indexFileName;

	public AnnotatedEnvironment () {
		super();
		oapi = null;
		annots = new Annotations();
		indexFileName = options.size();
        options.add( new Option<String> (true,
                        " the filename of the annotated ontology",
                        " isn't a readable file",
                        null) );
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean loadOptions(String []args) {
		boolean exit = super.loadOptions(args);
		Option<String> os = options.get(indexFileName);
        try {
            File file = new File(args[indexFileName]);
            if (file.canRead() == false) {
                throw new Exception();
            }
            os.value = args[indexFileName];
        } catch (Exception e) {
            getLogger().warning("The "+indexFileName+"o. argument "+os.help);
            getLogger().warning("\tThis argument is"+os.description);
            if (os.isRequired) exit = false;
        }

		return exit;
	}

    @SuppressWarnings("unchecked")
    public void initOntology() {
        Option<String> os = options.get(indexFileName);
        String filename = os.value;
        oapi = new OwlApi ();

        try {
            oapi.loadOntologyFromFile(filename);
        } catch (Exception e) {
            getLogger().warning("Problem reading ontology from " + filename);
            System.exit(52);
        }
	}


	protected class DataAnnotation {
	}

	protected class Annotations {
		protected HashMap<String, DataAnnotation> annotation;
	}
}
