package net.warsmash.l1.pathfinder.planner;

import java.util.List;

import net.warsmash.l1.pathfinder.vertex.Vertex;

public class Bucket {
	public double y0;
	public double y1;
	public Vertex top;
	public Vertex bottom;
	public List<Vertex> left;
	public List<Vertex> right;
	public List<Vertex> on;

	public Bucket(double y0, double y1, Vertex top, Vertex bottom, List<Vertex> left, List<Vertex> right,
			List<Vertex> on) {
		this.y0 = y0;
		this.y1 = y1;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.on = on;
	}

}
