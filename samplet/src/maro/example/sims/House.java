package maro.example.sims;

import maro.core.AnnotatedEnvironment;

public class House extends AnnotatedEnvironment
{
    protected HouseModel model = null; // TODO maybe local on init?
    protected HouseView view = null; // TODO maybe local on init?
    protected HouseController controller = null;

    @Override
    public void init(String[] args) {
        initOptions(args);

		super.initOntology();
        model = new HouseModel(20, 20, getNumberAgentsSettings());
        view = new HouseView(model, "House", 400);
        controller = new HouseController(this, oapi);
		oapi = null;

        initDefault();
    }
	@Override
	protected void stepStarted(int step) {
		getLogger().info("Started step " + step);

		controller.newStep(step, this);

		if (step > 0) {
			updateNumberOfAgents();
		}
	}

	public HouseModel getModel() { return model; }
}
