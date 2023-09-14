package net.warsmash.l1.pathfinder.planner;

import java.util.List;

import net.warsmash.l1.pathfinder.vertex.IPoint;

public class Partition {
	public double x;
	public List<IPoint> left;
	public List<IPoint> right;
	public List<IPoint> on;
	public List<IPoint> vis;

	public Partition(double x, List<IPoint> left, List<IPoint> right, List<IPoint> on, List<IPoint> vis) {
		this.x = x;
		this.left = left;
		this.right = right;
		this.on = on;
		this.vis = vis;
	}
}
