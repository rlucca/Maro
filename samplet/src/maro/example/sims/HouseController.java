package maro.example.sims;

import jason.environment.grid.GridWorldModel;

import maro.wrapper.OwlApi;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import java.util.ArrayList;

public class HouseController
{
    final protected House parent;
    final protected ArrayList<CharacterInspectorController> characters;
    protected int step;

    public HouseController(House owner, OwlApi oapi) {
        parent = owner;
        characters = new ArrayList<CharacterInspectorController> ();
        step = -1;

        parent.model.setView( parent.view );
        parent.model.loadPlacesFromOntology(oapi); // It doesn't have annottion
        parent.model.loadAgentsFromOntology(oapi); // It does have annotation
        parent.model.loadObjectsFromOntology(oapi); // It does have annotation

        parent.view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        parent.view.addWindowListener( new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    parent.tryStop(0);
                }
            });
        parent.view.setVisible(true);
        parent.view.getCanvas().setFocusable(true);
		parent.view.getCanvas().addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					if (e.getID() == KeyEvent.KEY_RELEASED) {
                        handler_keyboard(e.getKeyCode());
                    }
				}
			});
        parent.view.getCanvas().requestFocusInWindow();
        parent.view.repaint();
    }

    protected void handler_keyboard(int keyCode) {
        CharacterInspectorController ch;
        String agName = null;
        int agId;

        if (keyCode < 48 || keyCode > 57)
            return ;

        agId = (keyCode - 49) % 10;
        agName = parent.model.getAgentName(agId);
        
        if (agName == null)
            return ;

        ch = null;
        for (CharacterInspectorController cic : characters) {
            HouseModel.Agent a = cic.getAgent();

            if (a.getName().equals(agName)) {
                ch = cic;
                break;
            }
        }
 
        if (ch == null) {
            ch = new CharacterInspectorController(agId, agName);
            ch.setParent(this);
        }

        characters.add(ch);

        ch.repaint();
        ch.setVisible(true);
    }

    public void newStep(int step) {
        this.step = step;
        updateInspector();
    }

    public int getSimulationTime() { return this.step * 870; }

    public void updateInspector() {
        for (CharacterInspectorController cic : characters) {
            cic.update();
        }
    }

    public GridWorldModel getModel() {
        return parent.model;
    }
}
