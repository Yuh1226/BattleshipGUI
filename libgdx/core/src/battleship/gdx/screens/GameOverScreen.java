package battleship.gdx.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import battleship.gdx.BattleshipGame;
import battleship.gdx.BattleshipGame.ScreenId;

public class GameOverScreen extends BaseScreen {
	private final Label subtitle;
	private final TextButton shotsButton;
	private final TextButton hitsButton;
	private final TextButton accuracyButton;
	private final TextButton sunkButton;
	private final TextButton replay;
	private final TextButton menu;

	public GameOverScreen(BattleshipGame game) {
		super(game);

		Table root = new Table();
		root.setFillParent(true);
		root.top().pad(24);

		TextButton gear = new TextButton("G", styles.outlineButtonSmall);
		gear.addListener(styles.clickListener(() -> game.showScreen(ScreenId.SETTINGS)));
		Table header = new Table();
		header.add().expandX();
		header.add(gear).width(28).height(28);

		Label title = new Label("Ket thuc game", styles.titleStyle);
		subtitle = new Label("Ban thang !", styles.labelStyle);

		Table stats = new Table();
		shotsButton = new TextButton("Luot ban: 0", styles.outlineButtonSmall);
		hitsButton = new TextButton("Ban trung: 0", styles.outlineButtonSmall);
		accuracyButton = new TextButton("Do chinh xac: 0%", styles.outlineButtonSmall);
		sunkButton = new TextButton("Tau da chim: 0", styles.outlineButtonSmall);
		stats.add(shotsButton).width(120).height(28).padRight(8).padBottom(8);
		stats.add(hitsButton).width(120).height(28).padBottom(8).row();
		stats.add(accuracyButton).width(120).height(28).padRight(8);
		stats.add(sunkButton).width(120).height(28);

		replay = new TextButton("Choi lai", styles.outlineButton);
		menu = new TextButton("Ve Menu", styles.outlineButton);
		replay.addListener(styles.clickListener(() -> {
			game.prepareBattle();
			game.showScreen(ScreenId.BATTLE);
		}));
		menu.addListener(styles.clickListener(() -> game.showScreen(ScreenId.MENU)));

		root.add(header).expandX().fillX().row();
		root.add(title).padTop(24).padBottom(8).row();
		root.add(subtitle).padBottom(16).row();
		root.add(stats).padBottom(16).row();
		root.add(replay).width(140).height(32).padBottom(10).row();
		root.add(menu).width(140).height(32).row();

		stage.addActor(root);
	}

	public void updateResult(boolean playerWon, int shots, int hits, int sunk) {
		subtitle.setText(playerWon ? "Ban thang !" : "Ban thua !");
		shotsButton.setText("Luot ban: " + shots);
		hitsButton.setText("Ban trung: " + hits);
		int accuracy = shots > 0 ? Math.round((hits * 100f) / shots) : 0;
		accuracyButton.setText("Do chinh xac: " + accuracy + "%");
		sunkButton.setText("Tau da chim: " + sunk);
	}
}
