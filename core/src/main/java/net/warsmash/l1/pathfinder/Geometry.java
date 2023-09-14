package net.warsmash.l1.pathfinder;

import java.util.ArrayList;
import java.util.List;

import net.warsmash.l1.pathfinder.contour.Contour;
import net.warsmash.l1.pathfinder.util.GridUtil;
import net.warsmash.l1.pathfinder.util.Point;
import net.warsmash.l1.pathfinder.vertex.IPoint;
import net.warsmash.l1.pathfinder.vertex.Vertex;

public class Geometry {
	public List<IPoint> corners;
	public int[][] grid;

	public Geometry(List<IPoint> corners, int[][] grid) {
		this.corners = corners;
		this.grid = grid;
	}

	public boolean stabRay(double vx, double vy, double x) {
		return this.stabBox(vx, vy, x, vy);
	}

	public boolean stabTile(double x, double y) {
		return this.stabBox(x, y, x, y);
	}

	public double integrate(double x, double y) {
		if (x < 0 || y < 0) {
			return 0;
		}
		return (double) this.grid[(int) Math.min(x, this.grid.length - 1)][(int) Math.min(y, this.grid[0].length - 1)];
	}

	public boolean stabBox(double ax, double ay, double bx, double by) {
		double lox = Math.min(ax, bx);
		double loy = Math.min(ay, by);
		double hix = Math.max(ax, bx);
		double hiy = Math.max(ay, by);

		double s = integrate(lox - 1, loy - 1) - integrate(lox - 1, hiy) - integrate(hix, loy - 1)
				+ integrate(hix, hiy);

		return s > 0;
	}

	public static int[][] createSummedAreaTable(int[][] img) {
		int[][] result = new int[img.length][img[0].length];
		for (int x = 0; x < img.length; x++) {
			int sum = 0;
			for (int y = 0; y < img[0].length; y++) {
				sum += img[x][y];
				if (x == 0) {
					result[x][y] = sum;
				} else {
					result[x][y] = result[x - 1][y] + sum;
				}
			}
		}
		return result;
	}

	public static int comparePoint(IPoint a, IPoint b) {
		int d = (int) Math.signum(a.getX() - b.getX());
		if (d != 0) {
			return d;
		}
		return (int) Math.signum(a.getY() - b.getY());
	}

	public static Geometry createGeometry(int[][] grid) {
		List<List<Point>> loops = Contour.getContours(GridUtil.transpose(grid), false);

		// Extract corners
		List<IPoint> corners = new ArrayList<>();
		for (int k = 0; k < loops.size(); ++k) {
			List<Point> polygon = loops.get(k);
			for (int i = 0; i < polygon.size(); ++i) {
				Point a = polygon.get((i + polygon.size() - 1) % polygon.size());
				Point b = polygon.get(i);
				Point c = polygon.get((i + 1) % polygon.size());
				if (Orientation.orient(a, b, c) > 0) {
					double x = 0, y = 0;
					if (b.x - a.x != 0) {// TODO double 0 check!! check previous impl
						x = b.x - a.x;
					} else {
						x = b.x - c.x;
					}
					x = b.x + Math.min((int) Math.round(x / Math.abs(x)), 0);
					if (b.y - a.y != 0) {
						y = b.y - a.y;
					} else {
						y = b.y - c.y;
					}
					y = b.y + Math.min((int) Math.round(y / Math.abs(y)), 0);
					Vertex offset = new Vertex(x, y);
					if (offset.x >= 0 && offset.x < grid.length && offset.y >= 0 && offset.y < grid[0].length
							&& grid[(int) offset.x][(int) offset.y] == 0) {
						corners.add(offset);
					}
				}
			}
		}

		// Remove duplicate corners
		corners = Unique.uniq(corners, Geometry::comparePoint, false);

		// Create integral image
		int[][] img = new int[grid.length][grid[0].length];
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				img[x][y] = (grid[x][y] > 0 ? 1 : 0);
			}
		}
		img = createSummedAreaTable(img);

		// Return resulting geometry
		return new Geometry(corners, img);
	}
}
