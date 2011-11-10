package maro.example;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import jason.environment.grid.GridWorldView;
import jason.infra.centralised.RunCentralisedMAS;


/** class that implements the View of the Game of Life application */
public class LU2DView extends GridWorldView {

    LU2DEnv controller;
    LUModel model;

    public LU2DView(LUModel model, final LU2DEnv env, String title, boolean isVisible) {
        super(model, title, 800); // java tosco, nao deixa nem criar uma variavel antes! :'(
        controller = env;
        this.model = model;
        setVisible(isVisible);
        repaint();
    }

    @Override
    public void initComponents(int width) {
        super.initComponents(width);

        // configura a saida para encerrar o Jason
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Do something...
                controller.tryStop(0);
            }
        });
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        Integer type = model.getTypeById(id);
		char orientation = ' ';
        if (type == null) return ; // unknow type

		orientation = model.getOrientation(id);
		if (orientation == ' ') return ; // death agent

		if (type != 1) {
			// Quando o tipo for diferente de planeta,
			// perguntamos se ha um planeta para desenha-lo
			boolean isPlanet = model.havePlanet(x, y);
			if (isPlanet == true) return ;
		}

        switch (type) {
			case 1:
                g.setColor(Color.blue);
                g.fillOval(x * cellSizeW + model.nextInt(5), y * cellSizeH + model.nextInt(5),
                        (int) (0.5 * cellSizeW),
                        (int) (0.5 * cellSizeH));
                break;
			case 2: // do not draw
                break;
			case 4:
                g.setColor(Color.green);
                drawTriangle(g, x, y, orientation);
                break;
			case 8:
                g.setColor(Color.darkGray);
                drawTriangle(g, x, y, orientation);
                break;
			default: break;
		}

        g.setColor(Color.red);
        drawString(g, x, y, defaultFont, String.valueOf(id+1));
    }

    private void
    drawTriangle(Graphics g, int x, int y, char orientation) {
        int cx = x * cellSizeW + (int) (0.5 * cellSizeW);
        int cy = y * cellSizeH + (int) (0.5 * cellSizeH);
        int [] xs = new int [] { cx, cx, cx };
        int [] ys = new int [] { cy, cy, cy };
		int [] base = new int [] { 5, 0, -5 };
		int [] top = new int [] { -3, 6, -3 };

		switch (orientation) {
			case 'N': // cima
				xs[0] += base[0];	xs[1] += base[1];
				xs[2] += base[2];	ys[0] += -top[0];
				ys[1] += -top[1]; 	ys[2] += -top[2];
				break;
			case 'E': // direita
				xs[0] += top[0];	xs[1] += top[1];
				xs[2] += top[2];	ys[0] += base[0];
				ys[1] += base[1];	ys[2] += base[2];
				break;
			case 'S':
				xs[0] += base[0];	xs[1] += base[1];
				xs[2] += base[2];	ys[0] += top[0];
				ys[1] += top[1];	ys[2] += top[2];
				break;
			case 'W':
				xs[0] += -top[0];	xs[1] += -top[1];
				xs[2] += -top[2];	ys[0] += base[0];
				ys[1] += base[1];	ys[2] += base[2];
				break;
			default:
				break;
		}

        g.fillPolygon(xs, ys, xs.length);
    }
}

