package maro.example.console;

import jason.asSyntax.Literal;

import maro.core.BBKeeper;
import maro.wrapper.BBAffective;

import java.util.Set;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.BorderFactory;

import jason.architecture.AgArch;

class CharacterInspectorView extends JFrame
{
	HashMap<String, JLabel> msj; // nameLabel X JLabel
    JTextField commander;
    String agName;
    AgArch aa;

    public CharacterInspectorView (String title) {
        super(title);
        agName = title;
        this.aa = null;
        msj = new HashMap<String, JLabel>();

        initComponents();

		pack();
    }

    public void setController(AgArch aa) {
        this.aa = aa;
    }

    protected void initComponents() {
		JPanel data = new JPanel();
		data.setLayout(new BoxLayout(data,BoxLayout.Y_AXIS) );
		data.setBorder(BorderFactory.createTitledBorder("Data"));
		getContentPane().add(data, BorderLayout.NORTH);

        commander = new JTextField();
		commander.setToolTipText("create a belief or perception in belief base. Use `-' to remove...");
        commander.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BBAffective bb = BBKeeper.getInstance().get(agName);
                if (bb == null) {
                    commander.setText("error recovering the belief base");
                    return ;
                }

                Literal lit;
				String s = commander.getText();
				boolean isAdd = true;
				if ( s.startsWith("-") ) {
					lit = Literal.parseLiteral(s.substring(1, s.length()));
					isAdd = false;
				} else {
					lit = Literal.parseLiteral(s);
				}

				if (lit == null) {
					commander.setText("error malformed literal");
					return ;
				}

				lit.apply(null); // resolve ArithExpr as (number)

				if (isAdd) {
					bb.add(lit);
				} else {
					bb.remove(lit);
				}
                commander.setText("");
            }
        });

		JPanel feelings = new JPanel();
		feelings.setBorder(BorderFactory.createTitledBorder("Feelings"));
		GroupLayout gl = new GroupLayout(feelings);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		feelings.setLayout(gl);
		data.add(feelings);
		data.add(commander);

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

        if (sgvl == null) {
            sgvl = gl.createParallelGroup(GroupLayout.Alignment.BASELINE);
            sgv.addGroup(sgvl);
        }

		gl.setHorizontalGroup( sgh );
		gl.setVerticalGroup( sgv );
    }

    public void update() {
        if (aa == null) return ;

		BBAffective bb = null;
        bb = BBKeeper.getInstance().get(agName);
        if (bb == null) return ;

		for (String emotion : msj.keySet()) {
			JLabel l = msj.get(emotion);
			if (bb != null) {
				Integer v = bb.getEmotionValence(emotion);
				updateLabel(l, (v==null)?0:v);
			}
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
};
