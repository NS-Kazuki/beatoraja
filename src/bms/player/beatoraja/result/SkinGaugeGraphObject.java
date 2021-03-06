package bms.player.beatoraja.result;

import bms.player.beatoraja.MainState;
import bms.player.beatoraja.PlayerResource;
import bms.player.beatoraja.play.GrooveGauge.Gauge;
import bms.player.beatoraja.skin.Skin.SkinObjectRenderer;
import bms.player.beatoraja.skin.SkinObject;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;

/**
 * ゲージ遷移描画オブジェクト
 *
 * @author exch
 */
public class SkinGaugeGraphObject extends SkinObject {

	/**
	 * 背景テクスチャ
	 */
	private Texture backtex;
	/**
	 * グラフテクスチャ
	 */
	private TextureRegion shapetex;
	/**
	 * ゲージ描画を完了するまでの時間(ms)
	 */
	private int delay = 1500;
	/**
	 * グラフ線の太さ
	 */
	private int lineWidth = 2;

	private int currentType = -1;

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	private final Color[] graphcolor = new Color[6];
	private final Color[] graphline = new Color[6];
	private final Color borderline;
	private final Color bordercolor;
	private final int[] typetable = {0,1,2,3,4,5,3,4,5,3};

	public SkinGaugeGraphObject(String assistClearBGColor, String assistAndEasyFailBGColor, String grooveFailBGColor, String grooveClearAndHardBGColor, String exHardBGColor, String hazardBGColor,
	String assistClearLineColor, String assistAndEasyFailLineColor, String grooveFailLineColor, String grooveClearAndHardLineColor, String exHardLineColor, String hazardLineColor,
	String borderlineColor, String borderColor) {
		graphcolor[0] = Color.valueOf(assistClearBGColor);
		graphcolor[1] = Color.valueOf(assistAndEasyFailBGColor);
		graphcolor[2] = Color.valueOf(grooveFailBGColor);
		graphcolor[3] = Color.valueOf(grooveClearAndHardBGColor);
		graphcolor[4] = Color.valueOf(exHardBGColor);
		graphcolor[5] = Color.valueOf(hazardBGColor);
		graphline[0] = Color.valueOf(assistClearLineColor);
		graphline[1] = Color.valueOf(assistAndEasyFailLineColor);
		graphline[2] = Color.valueOf(grooveFailLineColor);
		graphline[3] = Color.valueOf(grooveClearAndHardLineColor);
		graphline[4] = Color.valueOf(exHardLineColor);
		graphline[5] = Color.valueOf(hazardLineColor);
		borderline = Color.valueOf(borderlineColor);
		bordercolor = Color.valueOf(borderColor);
	}

	public SkinGaugeGraphObject() {
		graphcolor[0] = Color.valueOf("440044");
		graphcolor[1] = Color.valueOf("004444");
		graphcolor[2] = Color.valueOf("004400");
		graphcolor[3] = Color.valueOf("440000");
		graphcolor[4] = Color.valueOf("444400");
		graphcolor[5] = Color.valueOf("444444");
		graphline[0] = Color.valueOf("ff00ff");
		graphline[1] = Color.valueOf("0000ff");
		graphline[2] = Color.valueOf("00ff00");
		graphline[3] = Color.valueOf("ff0000");
		graphline[4] = Color.valueOf("ffff00");
		graphline[5] = Color.valueOf("cccccc");
		borderline = Color.valueOf("ff0000");
		bordercolor = Color.valueOf("440000");
	}

	private int color;
	private FloatArray gaugehistory;
	private IntArray section;
	private Gauge gg;

	private float render;
	private boolean redraw;

	public void prepare(long time, MainState state) {
		render = time >= delay ? 1.0f : (float) time / delay;

		final PlayerResource resource = state.main.getPlayerResource();
		int type = resource.getGrooveGauge().getType();
		if(state instanceof AbstractResult) {
			type = ((AbstractResult) state).gaugeType;
		}

		if(currentType != type) {
			redraw = true;
			currentType = type;
			gaugehistory = resource.getGauge()[currentType];
			section = new IntArray();
			if (state instanceof CourseResult) {
				gaugehistory = new FloatArray();
				for (FloatArray[] l : resource.getCourseGauge()) {
					gaugehistory.addAll(l[currentType]);
					section.add((section.size > 0 ? section.get(section.size - 1) : 0) + l[currentType].size);
				}
			}
			gg = resource.getGrooveGauge().getGauge(currentType);
		}
		super.prepare(time, state);
	}

	@Override
	public void draw(SkinObjectRenderer sprite) {

		if (shapetex != null) {
			if (!redraw && shapetex.getTexture().getWidth() == (int) region.getWidth() && shapetex.getTexture().getHeight() == (int) region.getHeight()) {
				// shape.setColor(Color.BLACK);
				// shape.fill();
			} else {
				backtex.dispose();
				backtex = null;
				shapetex.getTexture().dispose();
				shapetex = null;
			}
		}
		if (shapetex == null) {
			redraw = false;
			Pixmap shape = new Pixmap((int) region.width, (int) region.height, Pixmap.Format.RGBA8888);
			// ゲージグラフ描画
			color = typetable[currentType];
			shape.setColor(graphcolor[color]);
			shape.fill();
			final float border = gg.getProperty().border;
			final float max = gg.getProperty().max;
			Color borderline = this.borderline;
			if (border > 0) {
				shape.setColor(bordercolor);
				shape.fillRectangle(0, (int) (region.height * border / max), (int) (region.width),
						(int) (region.height * (max - border) / max));
			} else {
				borderline = graphline[color];
			}
			backtex = new Texture(shape);
			shape.dispose();

			shape = new Pixmap((int) region.width, (int) region.height, Pixmap.Format.RGBA8888);
			Float f1 = null;

			for (int i = 0; i < gaugehistory.size; i++) {
				if (section.contains(i)) {
					shape.setColor(Color.valueOf("ffffff"));
					shape.drawLine((int) (region.width * (i - 1) / gaugehistory.size), 0,
							(int) (region.width * (i - 1) / gaugehistory.size), (int) region.height);
				}
				Float f2 = gaugehistory.get(i);
				if (f1 != null) {
					final int x1 = (int) (region.width * (i - 1) / gaugehistory.size);
					final int y1 = (int) ((f1 / max) * (region.height - lineWidth));
					final int x2 = (int) (region.width * i / gaugehistory.size);
					final int y2 = (int) ((f2 / max) * (region.height - lineWidth));
					final int yb = (int) ((border / max) * (region.height - lineWidth));
					if (f1 < border) {
						if (f2 < border) {
							shape.setColor(graphline[color]);
							shape.fillRectangle(x1, Math.min(y1, y2), lineWidth, Math.abs(y2 - y1) + lineWidth);
							shape.fillRectangle(x1, y2, x2 - x1, lineWidth);
						} else {
							shape.setColor(graphline[color]);
							shape.fillRectangle(x1, y1, lineWidth, yb - y1);
							shape.setColor(borderline);
							shape.fillRectangle(x1, yb, lineWidth, y2 - yb + lineWidth);
							shape.fillRectangle(x1, y2, x2 - x1, lineWidth);
						}
					} else {
						if (f2 >= border) {
							shape.setColor(borderline);
							shape.fillRectangle(x1, Math.min(y1, y2), lineWidth, Math.abs(y2 - y1) + lineWidth);
							shape.fillRectangle(x1, y2, x2 - x1, lineWidth);
						} else {
							shape.setColor(borderline);
							shape.fillRectangle(x1, yb, lineWidth, y1 - yb + lineWidth);
							shape.setColor(graphline[color]);
							shape.fillRectangle(x1, y2, lineWidth, yb - y2);
							shape.fillRectangle(x1, y2, x2 - x1, lineWidth);
						}
					}
				}
				f1 = f2;
			}
			shapetex = new TextureRegion(new Texture(shape));
			shape.dispose();
		}

		sprite.draw(backtex, region.x, region.y + region.height, region.width, -region.height);
		// setRegionにfloatを渡すと表示がおかしくなる
		shapetex.setRegion(0, 0, (int)(region.width * render), (int)region.height);
		sprite.draw(shapetex, region.x, region.y + region.height, (int)(region.width * render), -region.height);
	}

	@Override
	public void dispose() {
		if (shapetex != null) {
			shapetex.getTexture().dispose();
			shapetex = null;
		}
		if (backtex != null) {
			backtex.dispose();
			backtex = null;
		}
	}
}
