package net.warsmash.l1.pathfinder.planner;

import java.util.List;

import net.warsmash.l1.pathfinder.vertex.IPoint;

public class BucketInfo {
	public List<IPoint> left;
	public List<IPoint> right;
	public List<IPoint> on;
	public IPoint steiner0;
	public IPoint steiner1;
	public double y0;
	public double y1;

	public BucketInfo(List<IPoint> left, List<IPoint> right, List<IPoint> on, IPoint steiner0, IPoint steiner1,
			double y0, double y1) {
		this.left = left;
		this.right = right;
		this.on = on;
		this.steiner0 = steiner0;
		this.steiner1 = steiner1;
		this.y0 = y0;
		this.y1 = y1;
	}
}
