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
	protected int day;
	protected int hour;
	protected int lsth;
	protected int mins;
	protected int secs;
	protected String shift;
	static protected String[] shiftDay = {
			"Dawn", "Noon", "Afternoon", "Night"
		};
	protected String today;
	static protected String[] weekDays = {
			"Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday", "Sunday"
		};

    public HouseController(House owner, OwlApi oapi) {
        parent = owner;
        characters = new ArrayList<CharacterInspectorController> ();
        step = -1; lsth = -1;

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
			return ;
        }

        ch.repaint();
        ch.setVisible(true);
    }

    public void newStep(int step, House h) {
        this.step = step;

		secs = getSimulationTime();
		// secs minus days
		day  = (secs / 86400);
		secs = secs % 86400;
		// secs minus hours
		hour = (secs / 3600);
		secs = (secs % 3600);
		// secs minus minutes
		mins = (secs / 60);
		secs = (secs % 60);

		updateDay();

		if (step == 1) {
			for (HouseModel.Agent a : parent.model.referAgent.values()) {
				CharacterInspectorController ch
					= new CharacterInspectorController(a.getID(), a.getName());
				ch.setParent(this);
				characters.add(ch);
			}
		}

		if (lsth != hour) {
			lsth = hour;
			for (HouseModel.Place p : parent.model.referPlace.values()) {
				p.update(day % 7, hour, step);
			}
		}

        updateInspector();
		updatePercepts(h);
    }

    public int getStep() { return this.step; }
    public int getSimulationTime() { return this.step * 870; }

    protected void updateDay() {
		today = weekDays[day % 7];

		if (hour < 5) shift = shiftDay[0]; // (M) adrugada
		else if (hour < 12) {
			if (hour == 5 && mins <= 30)
				shift = shiftDay[0];
			else
				shift = shiftDay[1]; // M (a) nha
		} else if (hour < 18) {
			if (hour == 12 && mins == 0)
				shift = shiftDay[1];
			else
				shift = shiftDay[2]; // (T) arde
		} else { // 18 - 23h59
			if (hour == 18 && mins == 0)
				shift = shiftDay[2];
			else
				shift = shiftDay[3]; // (N) oite
		}
	}


    public void updateInspector() {
        for (CharacterInspectorController cic : characters) {
            cic.update();
        }
    }

	public void updatePercepts(House h) {
		for (HouseModel.Agent a : parent.model.referAgent.values()) {
			if (a == null) {
				continue; // death
			}
			a.updatePerception(h);
		}
	}

    public GridWorldModel getModel() {
        return parent.model;
    }
}
