package net.warsmash.l1.pathfinder;

import static net.warsmash.l1.pathfinder.vertex.Vertex.NIL;
import static net.warsmash.l1.pathfinder.vertex.Vertex.NUM_LANDMARKS;

import java.util.ArrayList;
import java.util.List;

import net.warsmash.l1.pathfinder.util.Point;
import net.warsmash.l1.pathfinder.vertex.IPoint;
import net.warsmash.l1.pathfinder.vertex.Vertex;;

public class Graph {
	public Vertex target;
	public List<Vertex> verts = new ArrayList<>();
	public Vertex freeList;
	public Vertex toVisit;
	public Vertex lastS;
	public Vertex lastT;
	public double srcX = 0;
	public double srcY = 0;
	public double dstX = 0;
	public double dstY = 0;
	public List<Vertex> landmarks = new ArrayList<>();
	public double[] landmarkDist;

	public static double heuristic(double[] tdist, double tx, double ty, Vertex node) {
		double nx = +node.x;
		double ny = +node.y;
		double pi = Math.abs(nx - tx) + Math.abs(ny - ty);
		double[] ndist = node.landmark;
		for (int i = 0; i < NUM_LANDMARKS; ++i) {
			pi = Math.max(pi, tdist[i] - ndist[i]);
		}
		return 1.0000009536743164 * pi;
	}

	public Graph() {
		this.target = Vertex.createVertex(0, 0);
		this.freeList = this.target;
		this.toVisit = NIL;
		this.lastS = null;
		this.lastT = null;
		this.landmarkDist = Vertex.LANDMARK_DIST();
	}

	public Vertex addVertex(double x, double y) {
		Vertex v = Vertex.createVertex(x, y);
		this.verts.add(v);
		return v;
	}

	public void setSourceAndTarget(double sx, double sy, double tx, double ty) {
		this.srcX = sx;
		this.srcY = sy;
		this.dstX = tx;
		this.dstY = ty;
	}

	// Mark vertex connected to source
	public void addS(Vertex v) {
		if ((v.state & 2) == 0) {
			v.heuristic = heuristic(this.landmarkDist, this.dstX, this.dstY, v);
			v.weight = Math.abs(this.srcX - v.x) + Math.abs(this.srcY - v.y) + v.heuristic;
			v.state |= 2;
			v.pred = null;
			this.toVisit = Vertex.push(this.toVisit, v);
			this.freeList = Vertex.insert(this.freeList, v);
			this.lastS = v;
		}
	}

	// Mark vertex connected to target
	public void addT(Vertex v) {
		if ((v.state & 1) == 0) {
			v.state |= 1;
			this.freeList = Vertex.insert(this.freeList, v);
			this.lastT = v;

			// Update heuristic
			double d = Math.abs(v.x - this.dstX) + Math.abs(v.y - this.dstY);
			double[] vdist = v.landmark;
			double[] tdist = this.landmarkDist;
			for (int i = 0; i < NUM_LANDMARKS; ++i) {
				tdist[i] = Math.min(tdist[i], vdist[i] + d);
			}
		}
	}

	// Retrieves the path from dst->src
	public List<Point> getPath(List<Point> outpath) {
		if (outpath == null) {
			outpath = new ArrayList<Point>();
		}
		double prevX = this.dstX;
		double prevY = this.dstY;
		outpath.add(new Point(prevX, prevY));
		Vertex head = this.target.pred;
		while (head != null) {
			if (prevX != head.x || prevY != head.y) {
				outpath.add(new Point(head.x, head.y));
			}
			prevX = head.x;
			prevY = head.y;
			head = head.pred;
		}
		if (prevX != this.srcX || prevY != this.srcY) {
			outpath.add(new Point(this.srcX, this.srcY));
		}
		return outpath;
	}

	public List<List<Vertex>> findComponents() {
		List<Vertex> verts = this.verts;
		int n = verts.size();
		for (int i = 0; i < n; ++i) {
			verts.get(i).component = -1;
		}
		List<List<Vertex>> components = new ArrayList<>();
		for (int i = 0; i < n; ++i) {
			Vertex root = verts.get(i);
			if (root.component >= 0) {
				continue;
			}
			int label = components.size();
			root.component = label;
			List<Vertex> toVisit = new ArrayList<>();
			toVisit.add(root);
			int ptr = 0;
			while (ptr < toVisit.size()) {
				Vertex v = toVisit.get(ptr++);
				List<Vertex> adj = v.edges;
				for (int j = 0; j < adj.size(); ++j) {
					Vertex u = adj.get(j);
					if (u.component >= 0) {
						continue;
					}
					u.component = label;
					toVisit.add(u);
				}
			}
			components.add(toVisit);
		}
		return components;
	}

	// Find all landmarks
	public static int compareVert(IPoint a, IPoint b) {
		int d = (int) Math.signum(a.getX() - b.getX());
		if (d != 0) {
			return d;
		}
		return (int) Math.signum(a.getY() - b.getY());
	}

	// For each connected component compute a set of landmarks
	public void findLandmarks(List<Vertex> component) {
		component.sort(Graph::compareVert);
		Vertex v = component.get((int) ((long) component.size() >> 1));
		for (int k = 0; k < NUM_LANDMARKS; ++k) {
			v.weight = 0.0;
			this.landmarks.add(v);
			for (Vertex toVisit = v; toVisit != NIL;) {
				v = toVisit;
				v.state = 2;
				toVisit = Vertex.pop(toVisit);
				double w = v.weight;
				List<Vertex> adj = v.edges;
				for (int i = 0; i < adj.size(); i++) {
					Vertex u = adj.get(i);
					if (u.state == 2) {
						continue;
					}
					double d = w + Math.abs(v.x - u.x) + Math.abs(v.y - u.y);
					if (u.state == 0) {
						u.state = 1;
						u.weight = d;
						toVisit = Vertex.push(toVisit, u);
					} else if (d < u.weight) {
						u.weight = d;
						toVisit = Vertex.decreaseKey(toVisit, u);
					}
				}
			}
			double farthestD = 0.0;
			for (int i = 0; i < component.size(); ++i) {
				Vertex u = component.get(i);
				u.state = 0;
				u.landmark[k] = u.weight;
				double s = Double.POSITIVE_INFINITY;
				for (int j = 0; j <= k; ++j) {
					s = Math.min(s, u.landmark[j]);
				}
				if (s > farthestD) {
					v = u;
					farthestD = s;
				}
			}
		}
	}

	public void init() {
		List<List<Vertex>> components = this.findComponents();
		for (int i = 0; i < components.size(); ++i) {
			this.findLandmarks(components.get(i));
		}
	}

	// Runs a* on the graph
	public double search() {
		Vertex target = this.target;
		Vertex freeList = this.freeList;
		double[] tdist = this.landmarkDist;

		// Initialize target properties
		double dist = Double.POSITIVE_INFINITY;

		// Test for case where S and T are disconnected
		if (this.lastS != null && this.lastT != null && this.lastS.component == this.lastT.component) {
			double sx = +this.srcX;
			double sy = +this.srcY;
			double tx = +this.dstX;
			double ty = +this.dstY;

			for (Vertex toVisit = this.toVisit; toVisit != NIL;) {
				Vertex node = toVisit;
				double nx = +node.x;
				double ny = +node.y;
				double d = Math.floor(node.weight - node.heuristic);

				if (node.state == 3) {
					// If node is connected to target, exit
					dist = d + Math.abs(tx - nx) + Math.abs(ty - ny);
					target.pred = node;
					break;
				}

				// Mark node closed
				node.state = 4;

				// Pop node from toVisit queue
				toVisit = Vertex.pop(toVisit);

				List<Vertex> adj = node.edges;
				int n = adj.size();
				for (int i = 0; i < n; ++i) {
					Vertex v = adj.get(i);
					int state = v.state;
					if (state == 4) {
						continue;
					}
					double vd = d + Math.abs(nx - v.x) + Math.abs(ny - v.y);
					if (state < 2) {
						double vh = heuristic(tdist, tx, ty, v);
						v.state |= 2;
						v.heuristic = vh;
						v.weight = vh + vd;
						v.pred = node;
						toVisit = Vertex.push(toVisit, v);
						freeList = Vertex.insert(freeList, v);
					} else {
						double vw = vd + v.heuristic;
						if (vw < v.weight) {
							v.weight = vw;
							v.pred = node;
							toVisit = Vertex.decreaseKey(toVisit, v);
						}
					}
				}
			}
		}

		// Clear the free list & priority queue
		Vertex.clear(freeList);

		// Reset pointers
		this.freeList = target;
		this.toVisit = NIL;
		this.lastS = this.lastT = null;

		// Reset landmark distance
		for (int i = 0; i < NUM_LANDMARKS; ++i) {
			tdist[i] = Double.POSITIVE_INFINITY;
		}

		// Return target distance
		return dist;
	}
}
