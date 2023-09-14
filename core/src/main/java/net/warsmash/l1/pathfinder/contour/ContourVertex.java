package net.warsmash.l1.pathfinder.contour;

import net.warsmash.l1.pathfinder.vertex.IPoint;

public class ContourVertex implements IPoint {
	public double x;
	public double y;
	public Segment segment;
	public int orientation;

	public ContourVertex(double x, double y, Segment segment, int orientation) {
		this.x = x;
		this.y = y;
		this.segment = segment;
		this.orientation = orientation;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

}
