package net.warsmash.l1.pathfinder.planner;

import java.util.ArrayList;
import java.util.List;

import net.warsmash.l1.pathfinder.Geometry;
import net.warsmash.l1.pathfinder.Graph;
import net.warsmash.l1.pathfinder.util.Point;

public class DiagonalL1PathPlanner implements PathPlanner {
	private L1PathPlanner planner;
	int maxOffset;

	private DiagonalL1PathPlanner(int[][] grid) {
		planner = L1PathPlanner.create(applyShearTransformation(grid));
	}

	public static DiagonalL1PathPlanner create(int[][] grid) {
		return new DiagonalL1PathPlanner(grid);
	}

	@Override
	public double search(double tx, double ty, double sx, double sy, List<Point> outo) {
		// Apply the shear transformation to source and target coordinates
		double transformedTx = tx + ty;
		double transformedTy = tx - ty + maxOffset;
		double transformedSx = sx + sy;
		double transformedSy = sx - sy + maxOffset;

		// Perform L1 shortest path search in the transformed space using the updated
		// grid
		List<Point> transformedPath = new ArrayList<>();
		planner.search(transformedTx, transformedTy, transformedSx, transformedSy, transformedPath);

		// Invert the transformed path to obtain the diagonal shortest path in the
		// original space
		for (Point point : transformedPath) {
			double originalX = getUnprojectedX(point.x, point.y);
			double originalY = getUnprojectedY(point.x, point.y);
			outo.add(new Point(originalX, originalY));
		}

		return Double.NaN; // currently I'm not using this
	}

	@Override
	public double getUnprojectedY(double x, double y) {
		double originalY = (x - (y - maxOffset)) / 2.0;
		return originalY;
	}

	@Override
	public double getUnprojectedX(double x, double y) {
		double originalX = (x + (y - maxOffset)) / 2.0;
		return originalX;
	}

	private int[][] applyShearTransformation(int[][] inputGrid) {
		int numRows = inputGrid.length;
		int numCols = inputGrid[0].length;

		// Calculate the size of the transformed grid
		maxOffset = Math.max(numRows, numCols) - 1; // Maximum offset needed
		int transformedSize = numRows + numCols + maxOffset;
		int[][] transformedGrid = new int[transformedSize][transformedSize];

		// Initialize the transformed grid with zeros
		for (int i = 0; i < transformedSize; i++) {
			for (int j = 0; j < transformedSize; j++) {
				transformedGrid[i][j] = 0;
			}
		}

		// Apply shear transformation to grid values
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				int x = i + j;
				int y = i - j;

				// Adjust transformed coordinates to accommodate negative values of y
				y += maxOffset; // Add the maximum possible offset

				// Ensure the transformed indices are within bounds
				if (x >= 0 && x < transformedSize && y >= 0 && y < transformedSize) {
					transformedGrid[x][y] = inputGrid[i][j];
				}
			}
		}

		return transformedGrid;
	}

	@Override
	public Graph getGraph() {
		return planner.graph;
	}

	@Override
	public Geometry getGeometry() {
		return planner.geometry;
	}

	public static void main(String[] args) {
		int[][] grid = { /* Your grid initialization here */ };
		DiagonalL1PathPlanner diagonalPlanner = DiagonalL1PathPlanner.create(grid);
		List<Point> path = new ArrayList<>();
		diagonalPlanner.search(5.0, 5.0, 0.0, 0.0, path); // Example coordinates
		System.out.println("Diagonal Shortest Path: " + path);
	}
}