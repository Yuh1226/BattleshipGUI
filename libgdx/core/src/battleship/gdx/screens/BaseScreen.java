package battleship.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import battleship.gdx.BattleshipGame;
import battleship.gdx.ui.UiStyles;

public abstract class BaseScreen extends ScreenAdapter {
	protected final BattleshipGame game;
	protected final Stage stage;
	protected final UiStyles styles;

	protected BaseScreen(BattleshipGame game) {
		this.game = game;
		this.stage = new Stage(new ScreenViewport());
		this.styles = new UiStyles();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(1f, 1f, 1f, 1f);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
		styles.dispose();
	}
}
