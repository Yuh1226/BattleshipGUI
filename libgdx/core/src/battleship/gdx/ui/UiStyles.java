package battleship.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UiStyles {
	public final BitmapFont titleFont;
	public final BitmapFont bodyFont;
	public final BitmapFont smallFont;
	public final Label.LabelStyle titleStyle;
	public final Label.LabelStyle labelStyle;
	public final Label.LabelStyle smallStyle;
	public final TextButton.TextButtonStyle outlineButton;
	public final TextButton.TextButtonStyle outlineButtonSmall;

	private final Texture outlineUp;
	private final Texture outlineDown;

	public UiStyles() {
		titleFont = new BitmapFont();
		titleFont.getData().setScale(1.4f);
		bodyFont = new BitmapFont();
		bodyFont.getData().setScale(1.0f);
		smallFont = new BitmapFont();
		smallFont.getData().setScale(0.9f);

		outlineUp = createOutlineTexture(Color.BLACK, Color.WHITE);
		outlineDown = createOutlineTexture(Color.BLACK, new Color(0.92f, 0.92f, 0.92f, 1f));

		outlineButton = new TextButton.TextButtonStyle();
		outlineButton.up = new TextureRegionDrawable(new TextureRegion(outlineUp));
		outlineButton.down = new TextureRegionDrawable(new TextureRegion(outlineDown));
		outlineButton.checked = new TextureRegionDrawable(new TextureRegion(outlineDown));
		outlineButton.font = bodyFont;
		outlineButton.fontColor = Color.BLACK;

		outlineButtonSmall = new TextButton.TextButtonStyle();
		outlineButtonSmall.up = new TextureRegionDrawable(new TextureRegion(outlineUp));
		outlineButtonSmall.down = new TextureRegionDrawable(new TextureRegion(outlineDown));
		outlineButtonSmall.checked = new TextureRegionDrawable(new TextureRegion(outlineDown));
		outlineButtonSmall.font = smallFont;
		outlineButtonSmall.fontColor = Color.BLACK;

		titleStyle = new Label.LabelStyle(titleFont, Color.BLACK);
		labelStyle = new Label.LabelStyle(bodyFont, Color.BLACK);
		smallStyle = new Label.LabelStyle(smallFont, Color.BLACK);
	}

	public void dispose() {
		titleFont.dispose();
		bodyFont.dispose();
		smallFont.dispose();
		outlineUp.dispose();
		outlineDown.dispose();
	}

	public InputListener clickListener(Runnable action) {
		return new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				action.run();
			}
		};
	}

	private Texture createOutlineTexture(Color border, Color fill) {
		Pixmap pixmap = new Pixmap(64, 32, Pixmap.Format.RGBA8888);
		pixmap.setColor(fill);
		pixmap.fill();
		pixmap.setColor(border);
		pixmap.drawRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		return texture;
	}
}
