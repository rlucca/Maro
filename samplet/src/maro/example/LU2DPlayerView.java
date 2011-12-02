package maro.example;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import jason.environment.grid.Location;
import jason.asSemantics.Message;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class LU2DPlayerView extends LU2DView {
	Integer playerId;
	Location playerPosition = null;
	LUAgArchViewer player = null;
	boolean waitPosition = true;
	boolean occlusion;
	Map<String, JLabel> msj = new HashMap<String, JLabel>();

	public LU2DPlayerView(LUAgArchViewer arch, String title, boolean isVisible,	boolean occlusion)
	{
		super(arch.getModel(), arch.getEnvironment(), title);
		this.occlusion = occlusion;
		player = arch;

		// on constructor of super class the initComponents/1 is called than player is nulled
		initComponents();

		setVisible(isVisible);
		repaint();
	}

	public void initComponents() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS) );
		p.setBorder(BorderFactory.createTitledBorder("Character"));

		JPanel control = new JPanel();
		control.setBorder(BorderFactory.createTitledBorder("Control"));
		control.setPreferredSize(new Dimension(10,210));
		p.add(control);

		JPanel orientation = new JPanel();
		orientation.setBorder(BorderFactory.createTitledBorder("Orientation"));
		orientation.setLayout(new BorderLayout());
		orientation.setPreferredSize(new Dimension(100,120));
		control.add(orientation, BorderLayout.WEST);

		JButton orientation1 = new JButton("^"); // cima
		orientation.add(orientation1, BorderLayout.NORTH);
		orientation1.setPreferredSize(new Dimension(20,20));
		addClick(orientation1, "orientation", "N");

		JButton orientation2 = new JButton(">"); // direita
		orientation.add(orientation2, BorderLayout.EAST);
		orientation2.setPreferredSize(new Dimension(20,20));
		addClick(orientation2, "orientation", "E");

		JButton orientation3 = new JButton("v"); // baixo
		orientation.add(orientation3, BorderLayout.SOUTH);
		orientation3.setPreferredSize(new Dimension(20,20));
		addClick(orientation3, "orientation", "S");

		JButton orientation4 = new JButton("<"); // esquerda
		orientation.add(orientation4, BorderLayout.WEST);
		orientation4.setPreferredSize(new Dimension(20,20));
		addClick(orientation4, "orientation", "W");

		JPanel actions = new JPanel();
		actions.setBorder(BorderFactory.createTitledBorder("Actions"));
		control.add(actions, BorderLayout.EAST);

		JButton btn1 = new JButton("Fire (normal)"); // fire
		actions.add(btn1);
		addClick(btn1, "fire", null);

		JButton btn2 = new JButton("Fire (special)"); // teleport -- 80% de chance de morrer
		actions.add(btn2);
		final JTextField setupSpecial = new JTextField(3);
		setupSpecial.setText("1");
        btn2.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
				Literal l = ASSyntax.createLiteral("gui", ASSyntax.createAtom("special"),
										ASSyntax.createString(setupSpecial.getText().trim()));
				Message m = new Message("achieve", player.getAgName(), player.getAgName(), l);
				try {
					player.sendMsg(m);
				} catch (Exception ex) {
					System.err.println("Ops...  "+ex.toString());
				}
            }
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });

		JButton btn3 = new JButton("Go straight a head"); // forward
		actions.add(btn3);
		addClick(btn3, "forward", null);

		JButton btn4 = new JButton("Drop in/out"); // offer -2/+2
		actions.add(btn4);
		addClick(btn4, "drop", null);

		JButton btn5 = new JButton("Nothing"); // nope
		actions.add(btn5);
		addClick(btn5, "nope", null);

		GroupLayout actionLayout = new GroupLayout(actions);
		actions.setLayout(actionLayout);
		actionLayout.setHorizontalGroup(
			actionLayout.createSequentialGroup()
				.addGroup( actionLayout.createParallelGroup()
								.addComponent(btn1)
								.addComponent(btn2)
								.addComponent(btn3)
								.addComponent(btn4)
								.addComponent(btn5)
				 )
				.addComponent(setupSpecial)
		);
		actionLayout.setVerticalGroup(
			actionLayout.createSequentialGroup()
				.addComponent(btn1)
				.addGroup(
					actionLayout.createParallelGroup()
							.addComponent(btn2)
							.addComponent(setupSpecial)
				)
				.addComponent(btn3)
				.addComponent(btn4)
				.addComponent(btn5)
		);
/*
changeOrientationTo
	recover -- a idea original era: drop off and keep yourself away from planet
	death -- Automatic
*/

		JPanel data = new JPanel();
		data.setLayout(new BoxLayout(data,BoxLayout.X_AXIS) );
		data.setBorder(BorderFactory.createTitledBorder("Data"));
		p.add(data);

		JPanel feelings = new JPanel();
		//feelings.setBorder(BorderFactory.createTitledBorder("Feelings"));
		GroupLayout gl = new GroupLayout(feelings);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		feelings.setLayout(gl);
		data.add(feelings);

		for (String e : player.getEmotionType()) {
			JLabel j = new JLabel();
			j.setText(e+": 0");
			j.setName(e);
			msj.put(e, j);
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

		gl.setHorizontalGroup( sgh );
		gl.setVerticalGroup( sgv );

		/*JPanel others = new JPanel();
		others.setBorder(BorderFactory.createTitledBorder("Other"));
		others.setLayout(new BoxLayout(others,BoxLayout.Y_AXIS) );
		data.add(others);

			others.add(new JLabel("Planets: 0/0"));*/

		getContentPane().add(p, BorderLayout.SOUTH);
	}

	private void addClick(Component c, final String s1, final String s2) {
        c.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
				Literal l;
				if (s2 == null)
					l = ASSyntax.createLiteral("gui", ASSyntax.createAtom(s1));
				else
					l = ASSyntax.createLiteral("gui", ASSyntax.createAtom(s1),
										ASSyntax.createString(s2));
				Message m = new Message("achieve", player.getAgName(), player.getAgName(), l);
				try {
					player.sendMsg(m);
				} catch (Exception ex) {
					System.err.println("Ops...  "+ex.toString());
				}
            }
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });
	}

	@Override
    public void update() {
		if (playerId == null) {
			playerId = model.getIdByName(player.getAgName());
		}
		if (playerId != null) {
			playerPosition = model.getAgPos(playerId);
		}

		super.update();

		for (String emotion : msj.keySet()) {
			JLabel l = msj.get(emotion);
			Integer v = player.getEmotionValence(emotion);
			updateLabel(l, (v==null)?0:v);
		}
	}
	@Override
    public void update(int x, int y) {
		// do nothing, wait the update/0
	}

	// This function on jason is private... We changed to protected to be more easy do that we need!
	@Override
	protected void draw(Graphics g, int x, int y) {
		int range = 5;
		int delta = range;

		if (occlusion && playerPosition != null) {
			int dx = Math.abs(x - playerPosition.x);
			int dy = Math.abs(y - playerPosition.y);
			delta = (int) Math.sqrt(dx * dx + dy * dy);
			waitPosition = false;
		} else {
			if (waitPosition == false)
				controller.setLastStep(0); // inform that the agent died and to kill the simulation
		}

		if (occlusion && delta >= range) {
			drawObstacle(g, x, y);
			return ;
		}

		List<Integer> lids = model.getListAgPos(x, y);
		for (Integer id : lids) {
			drawAgent(getCanvas().getGraphics(), x, y, Color.blue, id);
		}
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
}
