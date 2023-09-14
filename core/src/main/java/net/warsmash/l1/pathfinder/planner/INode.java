package net.warsmash.l1.pathfinder.planner;

import java.util.List;

import net.warsmash.l1.pathfinder.vertex.Vertex;

public interface INode {
	List<Vertex> getVerts();

	List<Bucket> getBuckets();

	boolean isLeaf();

	INode getLeft();

	INode getRight();

	double getX();
}
