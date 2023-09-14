package net.warsmash.l1.visualizer;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Editor {
	private static final int DEFAULT_GRID_SIZE = 32;
	private int width;
	private int height;
	private Renderer renderer;
	private int[][] data;
	private int tileState = 1;

	public Editor(ScreenViewport screenViewport) {
		this(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE, screenViewport);
	}

	public Editor(int width, int height, ScreenViewport screenViewport) {
		this.width = width;
		this.height = height;
		this.renderer = new Renderer(width, height, screenViewport);
		this.data = new int[width][height];

		this.renderer.onButtonChange(new EditorOnChange() {

			@Override
			public void call(int tileX, int tileY, int button) {
				if (tileX >= 0 && tileY >= 0) {
					tileState = (data[tileX][tileY] ^ 1) & 1;
					data[tileX][tileY] = tileState;
					renderer.dataChanged();
				}
			}
		});
		this.renderer.onTileChange(new EditorOnChange() {
			@Override
			public void call(int tileX, int tileY, int button) {
				if (tileX >= 0 && tileY >= 0) {
					data[tileX][tileY] = tileState;
					renderer.dataChanged();
				}
			}
		});
		this.renderer.setGrid(data);
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
