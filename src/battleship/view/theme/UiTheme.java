package battleship.view.theme;

import java.awt.Color;
import java.awt.Font;

public final class UiTheme {
	public static final Color NAVY = new Color(0x1B, 0x2A, 0x41);
	public static final Color OLIVE = new Color(0x65, 0x71, 0x53);
	public static final Color SAND = new Color(0xE6, 0xD5, 0xB8);
	public static final Color OFF_WHITE = new Color(0xF9, 0xF5, 0xEF);
	public static final Color ACCENT = new Color(0xC7, 0x6D, 0x2D);

	private UiTheme() {
	}

	public static Font titleFont() {
		return new Font("Courier New", Font.BOLD, 30);
	}

	public static Font bodyFont() {
		return new Font("Courier New", Font.PLAIN, 14);
	}

	public static Font buttonFont() {
		return new Font("Courier New", Font.BOLD, 14);
	}
}
