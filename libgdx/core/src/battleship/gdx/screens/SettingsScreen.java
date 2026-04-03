package battleship.gdx.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import battleship.gdx.BattleshipGame;
import battleship.gdx.BattleshipGame.ScreenId;

public class SettingsScreen extends BaseScreen {
	public SettingsScreen(BattleshipGame game) {
		super(game);

		Table root = new Table();
		root.setFillParent(true);
		root.top().pad(16);

		TextButton back = new TextButton("<", styles.outlineButtonSmall);
		back.addListener(styles.clickListener(() -> game.showScreen(ScreenId.MENU)));
		Table header = new Table();
		header.add(back).width(28).height(28).left();
		header.add().expandX();

		Label title = new Label("Cai dat", styles.titleStyle);
		TextButton continueBtn = new TextButton("Tiep tuc", styles.outlineButton);
		TextButton soundBtn = new TextButton("Am thanh", styles.outlineButton);
		TextButton uiBtn = new TextButton("Giao dien", styles.outlineButton);
		TextButton bottomContinue = new TextButton("Tiep tuc", styles.outlineButton);
		TextButton exitBtn = new TextButton("Thoat", styles.outlineButton);

		continueBtn.addListener(styles.clickListener(() -> game.showScreen(ScreenId.BATTLE)));
		bottomContinue.addListener(styles.clickListener(() -> game.showScreen(ScreenId.BATTLE)));
		exitBtn.addListener(styles.clickListener(() -> game.showScreen(ScreenId.MENU)));

		root.add(header).expandX().fillX().row();
		root.add(title).padTop(20).padBottom(16).row();
		root.add(continueBtn).width(120).height(32).padBottom(10).row();
		root.add(soundBtn).width(120).height(32).padBottom(10).row();
		root.add(uiBtn).width(120).height(32).padBottom(24).row();

		Table footer = new Table();
		footer.add(bottomContinue).width(120).height(32).padRight(40);
		footer.add(exitBtn).width(90).height(32);
		root.add(footer).padTop(16).row();

		stage.addActor(root);
	}
}
