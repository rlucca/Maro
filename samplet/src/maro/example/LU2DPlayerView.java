package maro.example;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class LU2DPlayerView extends LU2DView {
	public LU2DPlayerView(LUModel model, final LU2DEnv env, String title, boolean isVisible) {
		super(model, env, title, isVisible);
	}

	@Override
	public void initComponents(int width) {
		super.initComponents(width);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS) );
		p.setBorder(BorderFactory.createTitledBorder("Character"));

		// ...
		JPanel control = new JPanel();
		control.setBorder(BorderFactory.createTitledBorder("Control")); 
		p.add(control);

		JPanel data = new JPanel();
		data.setBorder(BorderFactory.createTitledBorder("Data")); 
		p.add(data);

		getContentPane().add(BorderLayout.SOUTH, p);

        /*getCanvas().addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int col = e.getX() / cellSizeW;
                int lin = e.getY() / cellSizeH;
                if (col >= 0 && lin >= 0 && col < getModel().getWidth() && lin < getModel().getHeight()) {
                    getModel().add(LifeModel.LIFE, col, lin);
                    env.updateNeighbors(getModel().getAgId(col,lin));
                    update(col, lin);
                }
            }
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });*/
	}
}
