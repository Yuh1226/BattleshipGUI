package battleship.gdx.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class DashedRectActor extends Actor {
	private final float width;
	private final float height;
	private final float dashLength;
	private final float dashGap;
	private final Color color;
	private final ShapeRenderer renderer;

	public DashedRectActor(float width, float height, float dashLength, float dashGap, Color color) {
		this.width = width;
		this.height = height;
		this.dashLength = dashLength;
		this.dashGap = dashGap;
		this.color = new Color(color);
		this.renderer = new ShapeRenderer();
		setSize(width, height);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.setTransformMatrix(batch.getTransformMatrix());
		renderer.begin(ShapeRenderer.ShapeType.Line);
		renderer.setColor(color);

		float x = getX();
		float y = getY();

		drawDashedLine(x, y, x + width, y);
		drawDashedLine(x, y + height, x + width, y + height);
		drawDashedLine(x, y, x, y + height);
		drawDashedLine(x + width, y, x + width, y + height);

		renderer.end();
		batch.begin();
	}

	private void drawDashedLine(float x1, float y1, float x2, float y2) {
		float total = (float) Math.hypot(x2 - x1, y2 - y1);
		float dx = (x2 - x1) / total;
		float dy = (y2 - y1) / total;
		float progress = 0f;

		while (progress < total) {
			float segment = Math.min(dashLength, total - progress);
			float sx = x1 + dx * progress;
			float sy = y1 + dy * progress;
			float ex = x1 + dx * (progress + segment);
			float ey = y1 + dy * (progress + segment);
			renderer.line(sx, sy, ex, ey);
			progress += dashLength + dashGap;
		}
	}

	public void dispose() {
		renderer.dispose();
	}
}
