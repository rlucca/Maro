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
        //c = Color.darkGray;
        //g.setColor(c);
        //g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
        //g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        Integer type = model.getTypeById(id);
        if (type == null) return ; // unknow type

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
                drawTriangle(g, x, y);
                break;
			case 8:
                g.setColor(Color.darkGray);
                drawTriangle(g, x, y);
                break;
			default: break;
        }        
    }

    private void
    drawTriangle(Graphics g, int x, int y) { // TODO OLHAR PARA FRENTE
        int cx = x * cellSizeW + (int) (0.5 * cellSizeW);
        int cy = y * cellSizeH + (int) (0.5 * cellSizeH);
        int [] xs = new int [] { cx + 5, cx, cx - 5 };
        int [] ys = new int [] { cy - 3, cy + 6, cy - 3 };
        g.fillPolygon(xs, ys, xs.length);
    }
}

