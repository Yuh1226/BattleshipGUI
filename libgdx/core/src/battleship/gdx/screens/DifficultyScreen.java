package battleship.gdx.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import battleship.gdx.BattleshipGame;
import battleship.gdx.BattleshipGame.ScreenId;

public class DifficultyScreen extends BaseScreen {
	public DifficultyScreen(BattleshipGame game) {
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
		Label subtitle = new Label("Chon do kho", styles.labelStyle);
		TextButton easy = new TextButton("De", styles.outlineButton);
		TextButton medium = new TextButton("Vua", styles.outlineButton);
		TextButton hard = new TextButton("Kho", styles.outlineButton);

		easy.addListener(styles.clickListener(() -> {
			game.setDifficulty(battleship.ai.BattleshipAI.EASY);
			game.showScreen(ScreenId.SETUP);
		}));
		medium.addListener(styles.clickListener(() -> {
			game.setDifficulty(battleship.ai.BattleshipAI.MEDIUM);
			game.showScreen(ScreenId.SETUP);
		}));
		hard.addListener(styles.clickListener(() -> {
			game.setDifficulty(battleship.ai.BattleshipAI.HARD);
			game.showScreen(ScreenId.SETUP);
		}));

		root.add(header).expandX().fillX().row();
		root.add(title).padTop(28).row();
		root.add(subtitle).padTop(12).padBottom(12).row();
		root.add(easy).width(90).height(32).padBottom(10).row();
		root.add(medium).width(90).height(32).padBottom(10).row();
		root.add(hard).width(90).height(32).row();
		stage.addActor(root);
	}
}
