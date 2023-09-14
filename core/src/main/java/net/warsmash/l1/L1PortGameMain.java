package net.warsmash.l1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.warsmash.l1.visualizer.Planner;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class L1PortGameMain extends ApplicationAdapter implements InputProcessor {
	private Planner planner;
	private ScreenViewport screenViewport;

	@Override
	public void create() {
		screenViewport = new ScreenViewport();
		planner = new Planner(screenViewport);
		planner.buildPlanner();
		screenViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		screenViewport.apply();
		planner.drawGeometry();
	}

	@Override
	public void resize(int width, int height) {
		screenViewport.update(width, height, true);
	}

	@Override
	public void dispose() {
		planner.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 unprojected = screenViewport.unproject(new Vector2(screenX, screenY));
		planner.touchDown(unprojected, button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}
}