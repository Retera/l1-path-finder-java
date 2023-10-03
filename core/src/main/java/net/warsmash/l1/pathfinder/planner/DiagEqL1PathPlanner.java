package net.warsmash.l1.pathfinder.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.warsmash.l1.pathfinder.BSearch;
import net.warsmash.l1.pathfinder.DiagEqGraph;
import net.warsmash.l1.pathfinder.Geometry;
import net.warsmash.l1.pathfinder.util.Point;
import net.warsmash.l1.pathfinder.vertex.IPoint;
import net.warsmash.l1.pathfinder.vertex.Vertex;

public class DiagEqL1PathPlanner implements PathPlanner {
	public Geometry geometry;
	public DiagEqGraph graph;
	public INode root;

	private DiagEqL1PathPlanner(Geometry geometry, DiagEqGraph graph, INode root) {
		this.geometry = geometry;
		this.graph = graph;
		this.root = root;
	}

	public static int compareBucket(Bucket bucket, double y) {
		return (int) Math.signum(bucket.y0 - y);
	}

	public static void connectList(List<Vertex> nodes, Geometry geom, DiagEqGraph graph, boolean target, double x,
			double y) {
		for (int i = 0; i < nodes.size(); ++i) {
			Vertex v = nodes.get(i);
			if (!geom.stabBox(v.x, v.y, x, y)) {
				if (target) {
					graph.addT(v);
				} else {
					graph.addS(v);
				}
			}
		}
	}

	public static void connectNodes(Geometry geom, DiagEqGraph graph, INode node, boolean target, double x, double y) {
		// Mark target nodes
		while (node != null) {
			// Check leaf case
			if (node.isLeaf()) {
				List<Vertex> vv = node.getVerts();
				int nn = vv.size();
				for (int i = 0; i < nn; ++i) {
					Vertex v = vv.get(i);
					if (!geom.stabBox(v.x, v.y, x, y)) {
						if (target) {
							graph.addT(v);
						} else {
							graph.addS(v);
						}
					}
				}
				break;
			}

			// Otherwise, glue into buckets
			List<Bucket> buckets = node.getBuckets();
			int idx = BSearch.search(buckets, y, DiagEqL1PathPlanner::compareBucket);
			if (idx >= 0) {
				Bucket bb = buckets.get(idx);
				if (y < bb.y1) {
					// Common case:
					if (node.getX() >= x) {
						// Connect right
						connectList(bb.right, geom, graph, target, x, y);
					}
					if (node.getX() <= x) {
						// Connect left
						connectList(bb.left, geom, graph, target, x, y);
					}
					// Connect on
					connectList(bb.on, geom, graph, target, x, y);
				} else {
					// Connect to bottom of bucket above
					Vertex v = buckets.get(idx).bottom;
					if (v != null && !geom.stabBox(v.x, v.y, x, y)) {
						if (target) {
							graph.addT(v);
						} else {
							graph.addS(v);
						}
					}
					// Connect to top of bucket below
					if (idx + 1 < buckets.size()) {
						Vertex v2 = buckets.get(idx + 1).top;
						if (v2 != null && !geom.stabBox(v2.x, v2.y, x, y)) {
							if (target) {
								graph.addT(v2);
							} else {
								graph.addS(v2);
							}
						}
					}
				}
			} else {
				// Connect to top of box
				Vertex v = buckets.get(0).top;
				if (v != null && !geom.stabBox(v.x, v.y, x, y)) {
					if (target) {
						graph.addT(v);
					} else {
						graph.addS(v);
					}
				}
			}
			if (node.getX() > x) {
				node = node.getLeft();
			} else if (node.getX() < x) {
				node = node.getRight();
			} else {
				break;
			}
		}
	}

	public static void resetSourceList(List<Vertex> nodes, Geometry geom, DiagEqGraph graph, double x, double y) {
		for (int i = 0; i < nodes.size(); ++i) {
			Vertex v = nodes.get(i);
			if (!geom.stabBox(v.x, v.y, x, y)) {
				graph.resetS(v);
			}
		}
	}

	public static void resetSourceNodes(Geometry geom, DiagEqGraph graph, INode node, double x, double y) {
		// Mark target nodes
		while (node != null) {
			// Check leaf case
			if (node.isLeaf()) {
				List<Vertex> vv = node.getVerts();
				int nn = vv.size();
				for (int i = 0; i < nn; ++i) {
					Vertex v = vv.get(i);
					if (!geom.stabBox(v.x, v.y, x, y)) {
						graph.resetS(v);
					}
				}
				break;
			}

			// Otherwise, glue into buckets
			List<Bucket> buckets = node.getBuckets();
			int idx = BSearch.search(buckets, y, DiagEqL1PathPlanner::compareBucket);
			if (idx >= 0) {
				Bucket bb = buckets.get(idx);
				if (y < bb.y1) {
					// Common case:
					if (node.getX() >= x) {
						// Connect right
						resetSourceList(bb.right, geom, graph, x, y);
					}
					if (node.getX() <= x) {
						// Connect left
						resetSourceList(bb.left, geom, graph, x, y);
					}
					// Connect on
					resetSourceList(bb.on, geom, graph, x, y);
				} else {
					// Connect to bottom of bucket above
					Vertex v = buckets.get(idx).bottom;
					if (v != null && !geom.stabBox(v.x, v.y, x, y)) {
						graph.resetS(v);
					}
					// Connect to top of bucket below
					if (idx + 1 < buckets.size()) {
						Vertex v2 = buckets.get(idx + 1).top;
						if (v2 != null && !geom.stabBox(v2.x, v2.y, x, y)) {
							graph.resetS(v2);
						}
					}
				}
			} else {
				// Connect to top of box
				Vertex v = buckets.get(0).top;
				if (v != null && !geom.stabBox(v.x, v.y, x, y)) {
					graph.resetS(v);
				}
			}
			if (node.getX() > x) {
				node = node.getLeft();
			} else if (node.getX() < x) {
				node = node.getRight();
			} else {
				break;
			}
		}
	}

	public static void connectListFailingTarget(List<Vertex> nodes, DiagEqGraph graph, double x, double y) {
		for (int i = 0; i < nodes.size(); ++i) {
			Vertex v = nodes.get(i);
			graph.addT(v);
		}
	}

	public static void locateBackupTarget(Geometry geom, DiagEqGraph graph, INode node, double sourceX, double sourceY,
			double targetX, double targetY) {
		double bestDistance = Double.POSITIVE_INFINITY;
		Vertex bestNode = null;
		for (Vertex vertex : graph.verts) {
			double vertexDistance = DiagEqGraph.heuristicDistance(vertex.x, vertex.y, targetX, targetY);
			if (vertexDistance < bestDistance) {
				bestNode = vertex;
				bestDistance = vertexDistance;
			}
		}
		if (bestNode != null) {
			graph.addT(bestNode);
		}
	}

	public static void locateBackupTargetInSourceComponent(Geometry geom, DiagEqGraph graph, INode node, double sourceX,
			double sourceY, double targetX, double targetY) {
		double bestDistance = Double.POSITIVE_INFINITY;
		Vertex bestNode = null;
		for (Vertex vertex : graph.verts) {
			if (vertex.component == graph.lastS.component) {
				double vertexDistance = DiagEqGraph.heuristicDistance(vertex.x, vertex.y, targetX, targetY);
				if (vertexDistance < bestDistance) {
					bestNode = vertex;
					bestDistance = vertexDistance;
				}
			}
		}
		if (bestNode != null) {
			graph.addT(bestNode);
		}
	}

	@Override
	public double search(double tx, double ty, double sx, double sy, List<Point> outo) {
		Geometry geom = this.geometry;

		// Degenerate case: s and t are equal
		if (tx == sx && ty == sy) {
			// TODO comparing double for "==", should check impl
			if (!geom.stabBox(tx, ty, sx, sy)) {
				if (outo != null) {
					outo.add(new Point(sx, sy));
				}
				return 0;
			}
			return Double.POSITIVE_INFINITY;
		}

		// Check easy case - s and t directly connected
		if (!geom.stabBox(tx, ty, sx, sy)) {
			if (outo != null) {
				outo.add(new Point(tx, ty));
				outo.add(new Point(sx, sy));
			}
			return Math.abs(tx - sx) + Math.abs(ty - sy);
		}

		// Prepare graph
		DiagEqGraph graph = this.graph;
		graph.setSourceAndTarget(sx, sy, tx, ty);

		boolean sourceOrTargetFailed = false;
		// Mark target
		connectNodes(geom, graph, this.root, true, tx, ty);

		if (!graph.foundTarget()) {
			// Mark fake target for the purpose of "best-effort"
			locateBackupTarget(geom, graph, root, sx, sy, tx, ty);
			sourceOrTargetFailed = true;
		}

		// Mark source
		connectNodes(geom, graph, this.root, false, sx, sy);
		if (graph.foundSource() && graph.searchWillFail()) {
			// Mark fake target for the purpose of "best-effort"
			locateBackupTargetInSourceComponent(geom, graph, root, sx, sy, tx, ty);
			resetSourceNodes(geom, graph, root, sx, sy);
			sourceOrTargetFailed = true;
		}

		double dist;
		if (!sourceOrTargetFailed || geom.stabBox(sx, sy, graph.lastT.x, graph.lastT.y)) {
			// Run A*
			dist = graph.search(sourceOrTargetFailed);
		} else {
			dist = -1;
		}

		// Recover path
		if (outo != null) {
			graph.getPath(outo);
		}

		return dist;
	}

	public static int comparePoint(IPoint a, IPoint b) {
		double d = a.getY() - b.getY();
		if (d != 0) {
			return (int) Math.signum(d);
		}
		return (int) Math.signum(a.getX() - b.getX());
	}

	public static Partition makePartition(double x, List<IPoint> corners, Geometry geom, Object edges) {
		List<IPoint> left = new ArrayList<>();
		List<IPoint> right = new ArrayList<>();
		List<IPoint> on = new ArrayList<>();

		// Intersect rays along x horizontal line
		for (int i = 0; i < corners.size(); i++) {
			IPoint c = corners.get(i);
			if (!geom.stabRay(c.getX(), c.getY(), x)) {
				on.add(c);
			}
			if (c.getX() < x) {
				left.add(c);
			} else if (c.getX() > x) {
				right.add(c);
			}
		}

		// Sort on events by y then x
		on.sort(DiagEqL1PathPlanner::comparePoint);

		// Construct vertices and horizontal edges
		List<IPoint> vis = new ArrayList<>();
		List<IPoint> rem = new ArrayList<>();
		for (int i = 0; i < on.size();) {
			double l = x;
			double r = x;
			IPoint v = on.get(i);
			double y = v.getY();
			while (i < on.size() && on.get(i).getY() == y && on.get(i).getX() < x) {
				l = on.get(i++).getX();
			}
			if (l < x) {
				vis.add(new Vertex(l, y));
			}
			while (i < on.size() && on.get(i).getY() == y && on.get(i).getX() == x) {
				rem.add(on.get(i));
				vis.add(on.get(i));
				++i;
			}
			if (i < on.size() && on.get(i).getY() == y) {
				r = on.get(i++).getX();
				while (i < on.size() && on.get(i).getY() == y) {
					++i;
				}
			}

			if (r > x) {
				vis.add(new Vertex(r, y));
			}
		}

		return new Partition(x, left, right, rem, vis);
	}

	public static DiagEqL1PathPlanner create(int[][] grid) {
		Builder builder = new Builder(grid);
		return builder.build();
	}

	private static final class Builder {
		private int[][] grid;
		Geometry geom;
		DiagEqGraph graph = new DiagEqGraph();
		Map<IPoint, Vertex> verts = new HashMap<IPoint, Vertex>();
		List<IPoint[]> edges = new ArrayList<>();

		public Builder(int[][] grid) {
			this.grid = grid;
		}

		public Vertex makeVertex(IPoint point) {
			if (point == null) {
				return null;
			}
			Vertex vertex = verts.get(point);
			if (vertex == null) {
				vertex = graph.addVertex(point.getX(), point.getY());
			}
			return vertex;
		}

		public DiagEqL1PathPlanner build() {
			geom = Geometry.createGeometry(grid);
			INode root = makeTree(geom.corners, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

			// Link edges
			for (int i = 0; i < edges.size(); ++i) {
				Vertex.link(verts.get(edges.get(i)[0]), verts.get(edges.get(i)[1]));
			}

			// Initialized graph
			graph.init();

			// Return resulting tree
			return new DiagEqL1PathPlanner(geom, graph, root);
		}

		public List<Vertex> makeVertexList(List<IPoint> inList) {
			List<Vertex> output = new ArrayList<>(inList.size());
			for (int i = 0; i < inList.size(); i++) {
				output.add(makeVertex(inList.get(i)));
			}
			return output;
		}

		public Leaf makeLeaf(List<IPoint> corners, double x0, double x1) {
			List<Vertex> localVerts = new ArrayList<>();
			for (int i = 0; i < corners.size(); ++i) {
				IPoint u = corners.get(i);
				Vertex ux = graph.addVertex(u.getX(), u.getY());
				localVerts.add(ux);
				verts.put(u, ux);
				for (int j = 0; j < i; ++j) {
					IPoint v = corners.get(j);
					if (!geom.stabBox(u.getX(), u.getY(), v.getX(), v.getY())) {
						edges.add(new IPoint[] { u, v });
					}
				}
			}
			return new Leaf(localVerts);
		}

		BucketInfo makeBucket(List<IPoint> corners, double x) {
			// Split visible corners into 3 cases
			List<IPoint> left = new ArrayList<>();
			List<IPoint> right = new ArrayList<>();
			List<IPoint> on = new ArrayList<>();
			for (int i = 0; i < corners.size(); ++i) {
				if (corners.get(i).getX() < x) {
					left.add(corners.get(i));
				} else if (corners.get(i).getX() > x) {
					right.add(corners.get(i));
				} else {
					on.add(corners.get(i));
				}
			}

			// Add Steiner vertices if needed
			double y0 = corners.get(0).getY();
			double y1 = corners.get(corners.size() - 1).getY();
			IPoint loSteiner = addSteiner(on, x, y0, true);
			IPoint hiSteiner = addSteiner(on, x, y1, false);

			bipartite(left, right);
			bipartite(on, left);
			bipartite(on, right);

			// Connect vertical edges
			for (int i = 1; i < on.size(); ++i) {
				IPoint u = on.get(i - 1);
				IPoint v = on.get(i);
				if (!geom.stabBox(u.getX(), u.getY(), v.getX(), v.getY())) {
					edges.add(new IPoint[] { u, v });
				}
			}

			return new BucketInfo(left, right, on, loSteiner, hiSteiner, y0, y1);
		}

		IPoint addSteiner(List<IPoint> on, double x, double y, boolean first) {
			if (!geom.stabTile(y, y)) {
				for (int i = 0; i < on.size(); i++) {
					if (on.get(i).getX() == x && on.get(i).getY() == y) {
						return on.get(i);
					}
				}
				Vertex pair = new Vertex(x, y);
				if (first) {
					on.add(0, pair);
				} else {
					on.add(pair);
				}
				if (!verts.containsKey(pair)) {
					verts.put(pair, graph.addVertex(x, y));
				}
				return pair;
			}
			return null;
		}

		void bipartite(List<IPoint> a, List<IPoint> b) {
			for (int i = 0; i < a.size(); ++i) {
				IPoint u = a.get(i);
				for (int j = 0; j < b.size(); ++j) {
					IPoint v = b.get(j);

					if (!geom.stabBox(u.getX(), u.getY(), v.getX(), v.getY())) {
						edges.add(new IPoint[] { u, v });
					}
				}
			}
		}

		INode makeTree(List<IPoint> corners, double x0, double x1) {
			if (corners.isEmpty()) {
				return null;
			}

			if (corners.size() < Constants.LEAF_CUTOFF) {
				return makeLeaf(corners, x0, x1);
			}

			double x = corners.get((int) ((long) corners.size() >> 1)).getX();
			Partition partition = makePartition(x, corners, geom, edges);
			INode left = makeTree(partition.left, x0, x);
			INode right = makeTree(partition.right, x, x1);

			// Construct vertices
			for (int i = 0; i < partition.on.size(); ++i) {
				IPoint partitionOnAtI = partition.on.get(i);
				verts.put(partitionOnAtI, graph.addVertex(partitionOnAtI.getX(), partitionOnAtI.getY()));
			}

			// Build buckets
			List<IPoint> vis = partition.vis;
			List<Bucket> buckets = new ArrayList<>();
			IPoint lastSteiner = null;
			for (int i = 0; i < vis.size();) {
				int v0 = i;
				int v1 = Math.min(i + Constants.BUCKET_SIZE - 1, vis.size() - 1);
				while (++v1 < vis.size() && vis.get(v1 - 1).getY() == vis.get(v1).getY()) {
				}
				i = v1;
				BucketInfo bb = makeBucket(vis.subList(v0, v1), x);
				if (lastSteiner != null && bb.steiner0 != null && !geom.stabBox(lastSteiner.getX(), lastSteiner.getY(),
						bb.steiner0.getX(), bb.steiner0.getY())) {
					edges.add(new IPoint[] { lastSteiner, bb.steiner0 });
				}
				lastSteiner = bb.steiner1;
				buckets.add(new Bucket(bb.y0, bb.y1, makeVertex(bb.steiner0), makeVertex(bb.steiner1),
						makeVertexList(bb.left), makeVertexList(bb.right), makeVertexList(bb.on)));
			}
			return new Node(x, buckets, left, right);
		}
	}

	@Override
	public DiagEqGraph getGraph() {
		return graph;
	}

	@Override
	public Geometry getGeometry() {
		return geometry;
	}

	@Override
	public double getUnprojectedX(double x, double y) {
		return x;
	}

	@Override
	public double getUnprojectedY(double x, double y) {
		return y;
	}
}
