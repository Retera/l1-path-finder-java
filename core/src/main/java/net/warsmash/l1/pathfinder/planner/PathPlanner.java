package net.warsmash.l1.pathfinder.planner;

import java.util.List;

import net.warsmash.l1.pathfinder.AbstractGraph;
import net.warsmash.l1.pathfinder.Geometry;
import net.warsmash.l1.pathfinder.util.Point;

public interface PathPlanner extends TransformedSpace {
	AbstractGraph getGraph();

	Geometry getGeometry();

	public double search(double tx, double ty, double sx, double sy, List<Point> outo);
}
