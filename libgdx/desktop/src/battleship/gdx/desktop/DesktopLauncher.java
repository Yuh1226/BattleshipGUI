package battleship.gdx.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import battleship.gdx.BattleshipGame;

public class DesktopLauncher {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("BattleshipGDX");
		config.setWindowedMode(960, 720);
		config.setResizable(true);
		new Lwjgl3Application(new BattleshipGame(), config);
	}
}
