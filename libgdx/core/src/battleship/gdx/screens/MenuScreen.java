package battleship.gdx.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import battleship.gdx.BattleshipGame;
import battleship.gdx.BattleshipGame.ScreenId;

public class MenuScreen extends BaseScreen {
	public MenuScreen(BattleshipGame game) {
		super(game);

		Table root = new Table();
		root.setFillParent(true);
		root.top().pad(24);

		TextButton gear = new TextButton("G", styles.outlineButtonSmall);
		gear.addListener(styles.clickListener(() -> game.showScreen(ScreenId.SETTINGS)));

		Table header = new Table();
		header.add().expandX();
		header.add(gear).width(28).height(28);

		Label title = new Label("BATTLESHIP", styles.titleStyle);
		TextButton start = new TextButton("Bat dau game", styles.outlineButton);
		start.addListener(styles.clickListener(() -> game.showScreen(ScreenId.DIFFICULTY)));

		root.add(header).expandX().fillX().row();
		root.add(title).padTop(40).padBottom(32).row();
		root.add(start).width(160).height(52).row();
		stage.addActor(root);
	}
}
