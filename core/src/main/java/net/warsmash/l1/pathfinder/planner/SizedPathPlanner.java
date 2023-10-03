package net.warsmash.l1.pathfinder.planner;

import java.util.ArrayList;
import java.util.List;

import net.warsmash.l1.pathfinder.AbstractGraph;
import net.warsmash.l1.pathfinder.Geometry;
import net.warsmash.l1.pathfinder.util.Point;

public class SizedPathPlanner implements PathPlanner {
	private final PathPlanner delegate;
	private final int size;
	private final double offset;

	public SizedPathPlanner(int[][] grid, int size) {
		this.delegate = DiagEqL1PathPlanner.create(createCornerGrid(grid, size));
		this.size = size;
		this.offset = (size - 1.0) / 2.0;
	}

	@Override
	public double getUnprojectedX(double x, double y) {
		return x + this.offset;
	}

	@Override
	public double getUnprojectedY(double x, double y) {
		return y + this.offset;
	}

	@Override
	public AbstractGraph getGraph() {
		return delegate.getGraph();
	}

	@Override
	public Geometry getGeometry() {
		return delegate.getGeometry();
	}

	@Override
	public double search(double tx, double ty, double sx, double sy, List<Point> outo) {
		// Perform L1 shortest path search in the transformed space using the updated
		// grid
		List<Point> transformedPath = new ArrayList<>();
		double result = delegate.search(tx - offset, ty - offset, sx - offset, sy - offset, transformedPath);

		// Invert the transformed path to obtain the diagonal shortest path in the
		// original space
		for (Point point : transformedPath) {
			double originalX = getUnprojectedX(point.x, point.y);
			double originalY = getUnprojectedY(point.x, point.y);
			outo.add(new Point(originalX, originalY));
		}
		return result;
	}

	public static SizedPathPlanner create(int[][] grid) {
		return new SizedPathPlanner(grid, 2);
	}

	private static int[][] createCornerGrid(int[][] grid, int size) {
		int[][] cornerGrid = new int[grid.length - (size - 1)][grid[0].length - (size - 1)];
		for (int i = 0; i < cornerGrid.length; i++) {
			for (int j = 0; j < cornerGrid[i].length; j++) {
				for (int x = 0; x < size; x++) {
					for (int y = 0; y < size; y++) {
						cornerGrid[i][j] |= grid[i + x][j + y];
					}
				}
			}
		}
		return cornerGrid;
	}
}
