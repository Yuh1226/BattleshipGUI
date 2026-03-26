package battleship.view.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

public class RoundBorder implements Border {
	private final int radius;
	private final Color color;
	private final int thickness;

	public RoundBorder(int radius, Color color, int thickness) {
		this.radius = radius;
		this.color = color;
		this.thickness = thickness;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(thickness, thickness, thickness, thickness);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(color);
		g2.setStroke(new BasicStroke(thickness));
		int offset = thickness / 2;
		g2.drawRoundRect(x + offset, y + offset, width - thickness, height - thickness, radius, radius);
		g2.dispose();
	}
}
