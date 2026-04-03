package battleship.gdx.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TriangleActor extends Actor {
	private final Color color;
	private final ShapeRenderer renderer;

	public TriangleActor(float width, float height, Color color) {
		setSize(width, height);
		this.color = new Color(color);
		this.renderer = new ShapeRenderer();
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
		float w = getWidth();
		float h = getHeight();

		renderer.line(x, y, x, y + h);
		renderer.line(x, y + h, x + w, y + h / 2f);
		renderer.line(x + w, y + h / 2f, x, y);

		renderer.end();
		batch.begin();
	}

	public void dispose() {
		renderer.dispose();
	}
}
