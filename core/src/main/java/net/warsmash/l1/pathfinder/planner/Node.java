package net.warsmash.l1.pathfinder.planner;

import java.util.List;

import net.warsmash.l1.pathfinder.vertex.Vertex;

public class Node implements INode {
	public final double x;
	public final List<Bucket> buckets;
	public final INode left;
	public final INode right;

	@Override
	public List<Vertex> getVerts() {
		return null;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public INode getLeft() {
		return left;
	}

	@Override
	public INode getRight() {
		return right;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public List<Bucket> getBuckets() {
		return buckets;
	}

	public Node(double x, List<Bucket> buckets, INode left, INode right) {
		this.x = x;
		this.buckets = buckets;
		this.left = left;
		this.right = right;
	}

}
