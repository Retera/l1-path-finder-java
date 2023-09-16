package net.warsmash.l1.visualizer;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.warsmash.l1.pathfinder.planner.L1PathPlanner;
import net.warsmash.l1.pathfinder.util.Point;
import net.warsmash.l1.pathfinder.vertex.Vertex;

public class Planner {

	private Renderer editor;
	private int[] src;
	private int[] dst;
	private List<Point> path;
	private L1PathPlanner planner;

	public Planner(ScreenViewport screenViewport) {
		Editor editorBuilder = new Editor(screenViewport);
		editor = editorBuilder.getRenderer();
		src = new int[] { 4, 4 };
		dst = new int[] { 28, 28 };
		path = new ArrayList<>();

		for (int i = 1; i < 4; ++i) {
			for (int j = 0; j < 32; ++j) {
				editor.grid[8 * i][j] = 1;
				editor.grid[j][8 * i] = 1;
			}
		}

		for (int i = 1; i < 7; ++i) {
			editor.grid[16][i] = 0;
		}

		for (int i = 8; i <= 24; ++i) {
			editor.grid[i][27] = 1;
			editor.grid[i][28] = 0;
			editor.grid[i][29] = 1;
		}

		editor.grid[8][4] = 0;
		editor.grid[24][4] = 0;
		editor.grid[4][16] = 0;
		editor.grid[4][24] = 0;
		editor.grid[28][8] = 0;
		editor.grid[28][16] = 0;
		editor.grid[8][10] = 0;
		editor.grid[24][22] = 0;

		for (int i = 9; i < 24; ++i) {
			for (int j = 9; j < 24; ++j) {
				editor.grid[i][j] = 0;
			}
		}

		for (int i = 0; i < 4; ++i) {
			editor.grid[(int) (Math.random() * 14 + 10)][(int) (Math.random() * 14 + 10)] = 1;
		}
		if (false) {
			int[][] copy = new int[editor.grid.length][editor.grid[0].length];
			for (int i = 0; i < copy.length; i++) {
				for (int j = 0; j < copy[i].length; j++) {
					copy[i][j] = editor.grid[i][j];
				}
			}
			for (int i = 0; i < copy.length; i++) {
				for (int j = 0; j < copy[i].length; j++) {
					editor.grid[i][j] = copy[i][copy[i].length - j - 1];
				}
			}
		}
	}

	public void calcPath() {
		for (int i = 0; i < planner.graph.verts.size(); ++i) {
			planner.graph.verts.get(i).weight = Double.POSITIVE_INFINITY;
		}
		path.clear();
		if (src[0] < 0 || dst[0] < 0) {
			for (int i = 0; i < planner.graph.verts.size(); ++i) {
				Vertex v = planner.graph.verts.get(i);
				v.state = 0;
//				v.target = false;
//				Vertex.clear(v);
				System.err.println("calcPath negative case, not sure what to do with this!!");
			}
			return;
		}
		planner.search(src[0], src[1], dst[0], dst[1], path);
	}

	public void buttonChange(int tileX, int tileY, int buttons) {
		if (src[0] < 0) {
			src[0] = tileX;
			src[1] = tileY;
		} else if (dst[0] < 0) {
			dst[0] = tileX;
			dst[1] = tileY;
		} else {
			src[0] = tileX;
			src[1] = tileY;
			dst[0] = dst[1] = -10;
		}
		calcPath();
//		drawGeometry();
	}

	public void buildPlanner() {
		this.planner = L1PathPlanner.create(editor.grid);
		calcPath();
//		drawGeometry();
	}

	public void drawGeometry() {
		Gdx.gl.glClearColor(0x13 / 255f, 0x2b / 255f, 0x40 / 255f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		editor.beginDraw();
		int[][] data = editor.grid;
		for (int i = 0; i < data.length; ++i) {
			for (int j = 0; j < data[i].length; j++) {
				if (data[i][j] != 0) {
					editor.tile(i, j, 0xD6A866);
				}
			}
		}

		editor.graphDist(planner.graph);
		editor.graph(planner.graph, 0xB28DC7);
		editor.drawCorners(planner.geometry.corners, 0xD9E6F2);
		for (int i = 0; i < planner.graph.landmarks.size(); ++i) {
			Vertex l = planner.graph.landmarks.get(i);
			editor.circle(l.x, l.y, 0xFFFFB1);
		}
		editor.path(path, 0xFFFFFF);
		editor.circle(src[0], src[1], 0x00FF00);
		editor.circle(dst[0], dst[1], 0xFF0000);
		editor.endDraw();
	}

	public void dispose() {
		editor.dispose();
	}

	public void touchDown(Vector2 unprojected, int button) {
		if (button == Input.Buttons.LEFT) {
			editor.mouseChange(button, unprojected.x, unprojected.y);
		} else if (button == Input.Buttons.RIGHT) {
			int tileR = editor.tileDim();
			int tileX = (int) Math.floor(unprojected.x / tileR);
			int tileY = (int) Math.floor(unprojected.y / tileR);
			buttonChange(tileX, editor.grid[0].length - tileY - 1, button);
		} else if (button == Input.Buttons.MIDDLE) {
			editor.mouseChange(button, unprojected.x, unprojected.y);
		}
		buildPlanner();
	}
}
