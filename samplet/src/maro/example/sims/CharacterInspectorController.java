package maro.example.sims;

import jason.environment.grid.GridWorldModel;
import maro.example.sims.HouseModel.Agent;

public class CharacterInspectorController
{
    private int agId;
    private String agName;
    private CharacterInspectorView view;
    private HouseController parent;
    //private CharacterInspectorModel model; // this is the belief base of the agent

    public CharacterInspectorController(int id, String name) {
        agId = id;
        agName = name;
        parent = null;

        view = new CharacterInspectorView( agName );
        view.setController( this );
    }

    private String getName() { return agName; }
    private int getId() { return agId; }

    public Agent getAgent() {
        HouseModel hm = (HouseModel) getModel();
        if (hm == null || agId < 0) return null;

        Agent a = hm.referAgent.get(agId);
        if (a != null && a.getName().equals(agName))
            return a;
        return null;
    }

    public void repaint() {
        view.repaint();
    }

    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    public void update() {
        view.update();
    }

    public void setParent(HouseController p) {
        parent = p;
    }

    protected HouseController getParent() {
        return parent;
    }

    public GridWorldModel getModel() {
        HouseController hc = getParent();
        if (hc == null) return null;
        return hc.getModel();
    }
}
