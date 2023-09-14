package net.warsmash.l1.visualizer;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.warsmash.l1.pathfinder.Graph;
import net.warsmash.l1.pathfinder.util.Point;
import net.warsmash.l1.pathfinder.vertex.IPoint;
import net.warsmash.l1.pathfinder.vertex.Vertex;

public class Renderer {

	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private EditorOnChange onButtonChangeAction;
	private EditorOnChange onTileChangeAction;
	int[][] grid;
	private int gridWidth;
	private int gridHeight;
	private ScreenViewport screenViewport;

	int lastButton, lastTileX, lastTileY;

	public Renderer(int width, int height, ScreenViewport screenViewport) {
		this.gridWidth = width;
		this.gridHeight = height;
		this.screenViewport = screenViewport;
	}

	public void onButtonChange(EditorOnChange editorOnChange) {
		this.onButtonChangeAction = editorOnChange;

	}

	public void onTileChange(EditorOnChange editorOnChange) {
		this.onTileChangeAction = editorOnChange;

	}

	public void dataChanged() {
		// TODO Auto-generated method stub

	}

	public void setGrid(int[][] data) {
		this.grid = data;

	}

	public void mouseChange(int button, float x, float y) {
		int tileR = tileDim();
		int tileX = (int) Math.floor(x / tileR);
		int tileY = gridHeight - (int) Math.floor(y / tileR) - 1;

		if (tileX < 0 || tileY < 0 || tileX >= gridWidth || tileY >= gridHeight) {
			tileX = tileY = -1;
		}

		if (tileX != lastTileX || tileY != lastTileY) {
			this.onTileChangeAction.call(tileX, tileY, button);
		}
		if (button != lastButton) {
			this.onButtonChangeAction.call(tileX, tileY, button);
		}

		lastTileX = tileX;
		lastTileY = tileY;
		lastButton = button;
	}

	private float fixY(float y) {
		return Gdx.graphics.getHeight() - y;
	}

	public int tileDim() {
		return Math.round(Math.min(Gdx.graphics.getWidth() / gridWidth, Gdx.graphics.getHeight() / gridHeight));
	}

	public int[][] getGrid() {
		return grid;
	}

	private void setColor(int colorInt) {
		int transparencyBits = (colorInt >> 24) & 0xFF;
		float alpha = (255 - transparencyBits) / 255f;
		shapeRenderer.setColor(((colorInt >> 16) & 0xFF) / 255f, ((colorInt >> 8) & 0xFF) / 255f,
				((colorInt >> 0) & 0xFF) / 255f, alpha);
	}

	public void tile(double x, double y, int colorInt) {
		setColor(colorInt);
		int tileR = tileDim();
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect((float) (x * tileR), fixY((float) (y * tileR)) - tileR, tileR, tileR);
		shapeRenderer.end();
	}

	public void graphDist(Graph graph) {
		double maxDist = 0;
		for (int i = 0; i < graph.verts.size(); ++i) {
			Vertex vert = graph.verts.get(i);
			if (vert.weight < Double.POSITIVE_INFINITY) {
				maxDist = Math.max(vert.weight - vert.heuristic, maxDist);
			}
		}

		for (int i = 0; i < graph.verts.size(); ++i) {
			Vertex v = graph.verts.get(i);
			if (v.weight < Double.POSITIVE_INFINITY) {
				this.tile(v.x, v.y, createGradientColor(Math.floor(maxDist - (v.weight - v.heuristic)), maxDist, 128));
			}
		}
	}

	private int createGradientColor(double floor, double maxDist, int alpha) {
		double ratio = floor / maxDist;
		int value = (int) (ratio * 255);
		return ((alpha & 0xFF) << 24) | ((value & 0xFF) << 16) | ((value & 0xFF) << 8) | ((value & 0xFF) << 0);
	}

	public void graph(Graph graph, int colorInt) {
		for (int i = 0; i < graph.verts.size(); ++i) {
			Vertex v = graph.verts.get(i);
			circle(v.x, v.y, colorInt);
			for (int j = 0; j < v.edges.size(); ++j) {
				Vertex u = v.edges.get(j);
				line(v.x, v.y, u.x, u.y, colorInt);
			}
		}
	}

	private void line(double x, double y, double x2, double y2, int colorInt) {
		int tileR = tileDim();
		setColor(colorInt);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.line((float) ((x + 0.5) * tileR), fixY((float) ((y + 0.5) * tileR)), (float) ((x2 + 0.5) * tileR),
				fixY((float) ((y2 + 0.5) * tileR)));
		shapeRenderer.end();
	}

	public void drawCorners(List<IPoint> corners, int colorInt) {
		for (int i = 0; i < corners.size(); ++i) {
			IPoint corner = corners.get(i);
			this.circle(corner.getX(), corner.getY(), colorInt);
		}
	}

	public void circle(double x, double y, int colorInt) {
		int tileR = tileDim();
		if (tileR <= 2) {
			this.tile(x, y, colorInt);
			return;
		}
		setColor(colorInt);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.circle((float) ((x + 0.5) * tileR), fixY((float) ((y + 0.5) * tileR)), 0.25f * tileR);
		shapeRenderer.end();
	}

	public void path(List<Point> path, int colorInt) {
		int tileR = tileDim();
		double o0 = Math.floor(tileR * 0.4);
		double o1 = Math.ceil(tileR * 0.6);

		setColor(colorInt);

		shapeRenderer.begin(ShapeType.Filled);
		for (int i = 0; i + 1 < path.size(); i += 1) {
			Point source = path.get(i);
			Point target = path.get(i + 1);
			double x0 = Math.min(source.x, target.x) * tileR + o0;
			double y0 = Math.min(source.y, target.y) * tileR + o0;
			double x1 = Math.max(source.x, target.x) * tileR + o1;
			double y1 = Math.max(source.y, target.y) * tileR + o1;
			shapeRenderer.rect((float) x0, fixY((float) y0) - (float) (y1 - y0), (float) (x1 - x0), (float) (y1 - y0));
		}
		shapeRenderer.end();

	}

	public void dispose() {
		shapeRenderer.dispose();
	}

	public void beginDraw() {
		shapeRenderer.setProjectionMatrix(screenViewport.getCamera().combined);
	}

	public void endDraw() {

	}
}
