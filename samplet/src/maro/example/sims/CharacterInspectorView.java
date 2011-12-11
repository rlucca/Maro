package maro.example.sims;

import maro.core.BBKeeper;
import maro.wrapper.BBAffective;

import java.util.Set;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.BorderFactory;

class CharacterInspectorView extends JFrame
{
	HashMap<String, JLabel> msj; // nameLabel X JLabel
    CharacterInspectorController cic;
	JLabel energy;
	JLabel lookFor;
	JLabel day;
	JLabel hour;
	JLabel minute;
	JLabel shiftOfDay;

    public CharacterInspectorView (String title) {
        super(title);
        this.cic = null;
        energy = new JLabel();
        lookFor = new JLabel();
        day = new JLabel();
        hour = new JLabel();
        minute = new JLabel();
        shiftOfDay = new JLabel();

        msj = new HashMap<String, JLabel>();
        initComponents();

		pack();
    }

    public void setController(CharacterInspectorController cic) {
        this.cic = cic;
    }

    protected void initComponents() {
		JPanel data = new JPanel();
		data.setLayout(new BoxLayout(data,BoxLayout.X_AXIS) );
		data.setBorder(BorderFactory.createTitledBorder("Data"));
		getContentPane().add(data);

		JPanel feelings = new JPanel();
		//feelings.setBorder(BorderFactory.createTitledBorder("Feelings"));
		GroupLayout gl = new GroupLayout(feelings);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		feelings.setLayout(gl);
		data.add(feelings);

		Set<String> ss = BBKeeper.getInstance().getEmotionType();

		if (ss != null) {
			for (String e : ss) {
				JLabel j = new JLabel();
				j.setText(e+": 0");
				j.setName(e);
				msj.put(e, j);
			}
		}

		int number = 1;
		GroupLayout.SequentialGroup sgh = gl.createSequentialGroup();
		GroupLayout.ParallelGroup sghc1 = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup sghc2 = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup sghc3 = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup sghc4 = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
		sgh.addGroup(sghc1).addGroup(sghc2).addGroup(sghc3).addGroup(sghc4);

		GroupLayout.SequentialGroup sgv = gl.createSequentialGroup();
		GroupLayout.ParallelGroup sgvl = null;

		for (JLabel jl : msj.values()) {
			int mod = number % 4;

			switch (mod) {
				case 1:
					sgvl = gl.createParallelGroup(GroupLayout.Alignment.BASELINE);
					sgv.addGroup(sgvl);
					sgvl.addComponent(jl);
					sghc1.addComponent(jl);
					break;
				case 2:
					sgvl.addComponent(jl);
					sghc2.addComponent(jl);
					break;
				case 3:
					sgvl.addComponent(jl);
					sghc3.addComponent(jl);
					break;
				default:
					sgvl.addComponent(jl);
					sgvl = null;
					sghc4.addComponent(jl);
					break;
			}

			number = number + 1;
		}

		energy.setText("energy: ?");
		energy.setName("energy");
		sgvl.addComponent(energy);
		sghc3.addComponent(energy);

		lookFor.setText("lookFor: ?");
		lookFor.setName("lookFor");
		sgvl.addComponent(lookFor);
		sghc4.addComponent(lookFor);

		day.setText("day: ?");
		day.setName("day");
		hour.setText("hour: ?");
		hour.setName("hour");
		minute.setText("minute: ?");
		minute.setName("minute");
		shiftOfDay.setText("shiftOfDay: ?");
		shiftOfDay.setName("shiftOfDay");

        sgvl = gl.createParallelGroup(GroupLayout.Alignment.BASELINE);
        sgv.addGroup(sgvl);
        sgvl.addComponent(day);
        sghc1.addComponent(day);

        sgvl.addComponent(hour);
        sghc2.addComponent(hour);

        sgvl.addComponent(minute);
        sghc3.addComponent(minute);

        sgvl.addComponent(shiftOfDay);
        sghc4.addComponent(shiftOfDay);

		gl.setHorizontalGroup( sgh );
		gl.setVerticalGroup( sgv );
    }

    public void update() {
        HouseModel.Agent a;

        if (cic == null) return ;

        a = cic.getAgent();

		for (String emotion : msj.keySet()) {
			JLabel l = msj.get(emotion);
			BBAffective bb = BBKeeper.getInstance().get(a.getName());
            if (bb == null) continue;
			Integer v = bb.getEmotionPotence(emotion);
			updateLabel(l, (v==null)?0:v);
		}

        energy.setText("energy: "+a.getEnergy());
        lookFor.setText("lookFor: "+a.getOrientationText());

		Integer secs    = cic.getParent().getSimulationTime();
		Integer day;
		Integer hour;
		Integer mins;
		String shift;
		// secs minus days
		day  = (secs / 86400) + 1;
		secs = secs % 86400;
		// secs minus hours
		hour = (secs / 3600);
		secs = (secs % 3600);
		// secs minus minutes
		mins = (secs / 60);

		if (hour < 5) shift = "Dawn"; // (M) adrugada
		else if (hour < 12) {
			if (hour == 5 && mins <= 30)
				shift = "Dawn";
			else
				shift = "Noon"; // M (a) nha
		} else if (hour < 18) {
			if (hour == 12 && mins == 0)
				shift = "Noon";
			else
				shift = "Afternoon"; // (T) arde
		} else { // 18 - 23h59
			if (hour == 18 && mins == 0)
				shift = "Afternoon";
			else
				shift = "Night"; // (N) oite
		}

		this.day.setText("day: "+day);
		this.hour.setText("hour: "+hour);
		this.minute.setText("minute: "+mins);
		this.shiftOfDay.setText("shift: "+shift);
    }

	protected void updateLabel(javax.swing.JLabel label, Integer d) {
		String s = label.getText().split(":")[1]; // pega o segundo
		Integer val;

		if (d == null) {
			label.setText(label.getName() + ": 0");
        	label.setForeground(new java.awt.Color(0, 0, 0));
			return ;
		}

		try {
			val = Integer.parseInt(s.trim());
		} catch (Exception e) {
			val = 0;
		}

		if ( d > val )
        	label.setForeground(new java.awt.Color(0, 0, 255));
		else if ( d < val )
        	label.setForeground(new java.awt.Color(255, 0, 0));
		else
        	label.setForeground(new java.awt.Color(0, 0, 0));

		label.setText(label.getName() + ": " + d);
	}
};
