package net.warsmash.l1.pathfinder;

import net.warsmash.l1.pathfinder.vertex.IPoint;

public class Orientation {
	public static final double EPSILON = 1.1102230246251565e-16;
	public static final double ERRBOUND3 = (3.0 + 16.0 * EPSILON) * EPSILON;

	public static double orientation3Exact(IPoint m0, IPoint m1, IPoint m2) {
		double p = ((m1.getY() * m2.getX()) + (-m2.getY() * m1.getX()))
				+ ((m0.getY() * m1.getX()) + (-m1.getY() * m0.getX()));
		double n = (m0.getY() * m2.getX()) + (-m2.getY() * m0.getX());
		return p - n;
	}

	public static double orient(IPoint a, IPoint b, IPoint c) {
		double l = (a.getY() - c.getY()) * (b.getX() - c.getX());
		double r = (a.getX() - c.getX()) * (b.getY() - c.getY());
		double det = l - r;
		double s = 0;
		if (l > 0) {
			if (r <= 0) {
				return det;
			} else {
				s = l + r;
			}
		} else if (l < 0) {
			if (r >= 0) {
				return det;
			} else {
				s = -(l + r);
			}
		} else {
			return det;
		}
		double tol = ERRBOUND3 * s;
		if (det >= tol || det <= -tol) {
			return det;
		}
		return orientation3Exact(a, b, c);
	}
}
