package maro.example.sims;

import jason.environment.grid.GridWorldView;

import java.awt.Color;
import java.awt.Graphics;

public class HouseView extends GridWorldView
{
    private HouseModel hm;

    public HouseView(HouseModel model, String windowTitle, int windowSize) {
        super(model, windowTitle, windowSize);

        hm = model; // okay, okay.. two reference, but always doing cast is worst...
    }

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
    }

	@Override
    public void draw(Graphics g, int x, int y, int object) {
		switch (object) {
			case HouseModel.OPENED_DOOR:
				g.setColor(Color.lightGray);
				g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
				g.setColor(Color.black);
				g.drawRect(x * cellSizeW + 2, y * cellSizeH+2, cellSizeW-4, cellSizeH-4);
				break;
			case HouseModel.CLOSED_DOOR:
				g.setColor(Color.black);
				g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
				g.setColor(Color.lightGray);
				g.drawRect(x * cellSizeW + 2, y * cellSizeH+2, cellSizeW-4, cellSizeH-4);
				break;
			case HouseModel.TOILET: // vaso sanitario
				drawAgent(g, x, y, Color.orange, -1);
				break;
			case HouseModel.FAUCET: // vaso sanitario
				g.setColor(Color.blue);
				g.fillRect(
						x * cellSizeW + 1,
						(int) (y * cellSizeH + cellSizeH * 0.6),
						cellSizeW - 1,
						(int) (cellSizeH * 0.38 - 1)
				);
				g.fillRect(
						(int) (x * cellSizeW + cellSizeW * 0.1),
						(int) (y * cellSizeH + cellSizeH * 0.3),
						(int) (cellSizeW * 0.5 - 1),
						(int) (cellSizeH * 0.2 - 1)
				);
				g.fillRect(
						(int) (x * cellSizeW + cellSizeW * 0.2),
						(int) (y * cellSizeH + cellSizeH * 0.4 - 1),
						(int) (cellSizeW * 0.2 - 1),
						(int) (cellSizeH * 0.3 - 1)
				);
				break;
			case HouseModel.SHOWER:
				g.setColor(Color.cyan);
				g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
				break;
			case HouseModel.BED:
				g.setColor(Color.pink);
				g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
				break;
			case HouseModel.TABLE:
				g.setColor(Color.pink.darker());
				g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
				break;
			case HouseModel.BOOKCASE:
				g.setColor(Color.red.darker());
				g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
				break;
			case HouseModel.SOFA:
				g.setColor(Color.orange.darker());
				g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
				break;
			case HouseModel.TV:
				g.setColor(Color.yellow);
				g.fillRect(
					x * cellSizeW + (int) (0.3 * cellSizeW),
					y * cellSizeH + 1,
					(int) (cellSizeW * 0.3),
					cellSizeH-1);
				break;
			case HouseModel.REFRIGERATOR:
				drawAgent(g, x, y, Color.green.darker(), -1);
				break;
			default:
				System.out.println(object+" don't have a representation to draw");
				break;
		}
    }

	@Override
	public void drawAgent(Graphics g, int x, int y, Color c, int id) {
		if (id < 0) {
			super.drawAgent(g, x, y, c, id);
			return ;
		}

        HouseModel.Agent a = hm.referAgent.get(id);
        if (a == null || id < 0) return ;

		g.setColor(c);
		drawTriangle(g, x, y, a.getOrientation());

        String agName = a.getName();
        if (agName != null) {
            g.setColor(Color.red);
            drawString(g, x, y, defaultFont, agName);
        }
    }

	private void
	drawTriangle(Graphics g, int x, int y, char orientation) {
		int cx = x * cellSizeW + (int) (0.5 * cellSizeW);
		int cy = y * cellSizeH + (int) (0.5 * cellSizeH);
		int [] xs = new int [] { cx, cx, cx };
		int [] ys = new int [] { cy, cy, cy };
		int [] base = new int [] {
								(int) (0.4 * cellSizeW),
								(int) (0 * cellSizeW),
								(int) (-0.4 * cellSizeW)
							};
		int [] top = new int [] {
								(int) (-0.4 * cellSizeW),
								(int) (0.4 * cellSizeW),
								(int) (-0.4 * cellSizeW)
							};

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

    @Override
    public void update() {
        System.out.println("update/0");
    }

	@Override
    public void update(int x, int y) {
        int agId = hm.getAgAtPos(x, y);

        if (agId >= 0) {
            System.out.println("updateCharacterInspector of ag: "+agId);
        }

        super.update(x, y);
        System.out.println("update/2");
	}

}
