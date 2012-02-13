package maro.example.sims;

//import jason.environment.grid.GridWorldModel;

import java.util.PriorityQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Stack;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class AStar {
	class Point implements java.lang.Comparable {
		private int x;
		private int y;
		private int f;
		private int g;
		private int h;
		private Point parent;
		public Point(int px, int py, int tx, int ty) {
			x = px;
			y = py;
			g = 0;
			h = Math.abs(tx-px) + Math.abs(ty-py);
			f = g+h;
			parent = null;
		}

		public Point getParent() { return parent; }
		public void setParent(Point p) {
			parent = p;
			g = p.getF();
			f = g + h;
		}

		public int getX() { return x; }
		public int getY() { return y; }
		public int getF() { return f; }
		public int getG() { return g; }
		public int getH() { return h; }

		@Override
		public int compareTo(Object o) {
			Point point = (Point) o;
			if (getF() < point.getF()) return -1;
			else if (getF() > point.getF()) return 1;
			return 0;
		}
	}

	private int gx, gy;
	private int gsx, gsy;

	public String[] astar(int mx, int my, int tx, int ty, int tsx, int tsy,
			int[][] matrix) {
		PriorityQueue<Point> open = new PriorityQueue<Point>();
		List<Point> close = new LinkedList<Point>();
		Point next;
		boolean first = true;

		gx = tx; gy = ty;
		gsx = tsx - 1; gsy = tsy - 1;
		open.add(new Point(mx, my, gx, gy));

		while (open.isEmpty() == false) {
			next = (Point) open.poll();

			if (next == null) break; // empty!

			if (checkGoal(next)) {
				// Walk back from goal to start
				Stack<Point> path = new Stack<Point>();
				Point current = next;
				do {
					path.push(current);
					current = (Point)current.getParent();
				} while (current!=null);
				String[] result = new String[path.size()-1];
				current = (Point)path.pop();
				for (int i=0;i<result.length;++i) {
					next = (Point)path.pop();
					if (next.getX() > current.getX()) {
						result[i] = "E";
					} else if (next.getX() < current.getX()) {
						result[i] = "W";
					} else if (next.getX() == current.getX()) {
						if (next.getY() > current.getY()) {
							result[i] = "S";
						} else if (next.getY() < current.getY()) {
							result[i] = "N";
						} else {
							// throw exception nulled
							String str = null;
							str.compareTo("EH POSSIVEL?");
						}
					}
					current = next;
				}
				return result;
			}

			if (first == false
					//&& matrix[next.getX()][next.getY()] != GridWorldModel.AGENT
					&& matrix[next.getX()][next.getY()] > 0)
				continue;

			close.add(next);
			first = false;

			Point [] points = findNeighbours(next, matrix);
			for (Point point : points) {
				boolean exists = false;

				for (Iterator<Point> iter = close.iterator(); iter.hasNext(); ) {
					Point p = (Point) iter.next();
					if (p.getX() == point.getX() && p.getY() == point.getY()) {
						if ( point.getG() < p.getG() ) {
							p.setParent( point.getParent() ); // this change G and F
						}
						exists = true;
						break;
					}
				}

				if (exists == true) continue;
				for (Iterator<Point> iter = open.iterator(); iter.hasNext(); ) {
					Point p = (Point) iter.next();
					if (p.getX() == point.getX() && p.getY() == point.getY()) {
						if ( point.getG() < p.getG() ) {
							p.setParent( point.getParent() ); // this change G and F
						}
						exists = true;
						break;
					}
				}
				if (exists == true) continue;
				open.add(point);
			}
		}

		return null;
	}

	public Point[] findNeighbours(Point p, int [][]matrix) {
		List<Point> points = new LinkedList<Point> ();
		int[] xs = { 0, 0,-1, 1};
		int[] ys = {-1, 1, 0, 0};
		int size = matrix.length;

		for (int pos = 0; pos < xs.length; pos++) {
			int cx = xs[pos] + p.getX();
			int cy = ys[pos] + p.getY();

			if (cx >= size || cx < 0 || cy >= size || cy < 0)
				continue;

			//if (matrix[cx][cy] == 0 || (matrix[cx][cy] & (8|16|256)) > 0) {
				Point point = new Point(cx, cy, gx, gy);
				point.setParent(p);
				points.add(point);
			//}
		}

		return points.toArray(new Point[0]);
	}

	protected boolean checkGoal(Point p) {
		int x = p.getX();
		int y = p.getY();
		if (gy <= y && y <= (gy+gsy) &&
				(x == gx || x == (gx+gsx)) ) {
			return true;
		} else {
			if ( gx <= x && x <= (gx+gsx) &&
					(y == gy || y == (gy+gsy))) {
				return true;
			}
		}
		return false;
	}
}

