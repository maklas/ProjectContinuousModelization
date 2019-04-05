package ru.maklas.model.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Consumer;

public class ImageAssets extends Asset{

    public TextureRegion empty;
    public TextureRegion cell;
    public TextureRegion circle;
    public TextureRegion pixel;
    public TextureRegion laserStart, laserMiddle, laserEnd, laserEffect;
    public TextureRegion background;
    public BitmapFont font;
    public TextureRegion whiteBox10px;
    public TextureRegion whiteBox10pxHalfAlpha;

    @Override
    protected void loadImpl() {
        empty = new TextureRegion(new Texture("default.png"));
        cell = new TextureRegion(new Texture("cell.png"));
        background = new TextureRegion(new Texture("background.png"));
        circle = new TextureRegion(new Texture("circle.png"));

        laserStart = new TextureRegion(new Texture("laser/laser_start.png"));
        laserMiddle = new TextureRegion(new Texture("laser/laser_middle.png"));
        laserEnd = new TextureRegion(new Texture("laser/laser_end.png"));
        laserEffect = new TextureRegion(new Texture("laser/laser_effect.png"));

        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        whiteBox10px = createRectangleImage(10, 10, Color.WHITE);
        whiteBox10pxHalfAlpha = createRectangleImage(10, 10, new Color(1, 1, 1, 0.5f));
        pixel = createRectangleImage(1, 1, Color.WHITE);
    }

    @Override
    protected void disposeImpl() {
        empty.getTexture().dispose();
        whiteBox10px.getTexture().dispose();
    }


    public static TextureRegion createCircleImage(int radius, Color color){
        int size = radius * 2 + 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.fill();
        pixmap.setColor(color);
        pixmap.fillCircle(radius + 1, radius + 1, radius);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createCircleImageNoFill(int radius, Color color){
        int size = radius * 2 + 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.drawCircle(radius + 1, radius + 1, radius);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createImage(int width, int height, Color color, Consumer<Pixmap> pixmapConsumer){
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmapConsumer.accept(pixmap);
        return new TextureRegion(new Texture(pixmap));
    }

    public static TextureRegion createRectangleImage(int width, int height, Color color){
        Pixmap pixmap = new Pixmap(width , height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return new TextureRegion(new Texture(pixmap));
    }


    public static TextureRegion createRectangleImage(int width, int height, int pad, Color color, Color borderColor){
        Pixmap pixmap = new Pixmap(width + 2 * pad, height + 2 * pad, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(pad, pad, width, height);
        pixmap.setColor(borderColor);
        pixmap.drawRectangle(pad, pad, width,  height);
        return new TextureRegion(new Texture(pixmap));
    }

    public static void draw(Batch batch, TextureRegion region, float x, float y, float pivotX, float pivotY, float scaleX, float scaleY, float angle){
        float originX = region.getRegionWidth() * pivotX;
        float originY = region.getRegionHeight() * pivotY;

        batch.draw(region,
                x - originX, y - originY,
                originX, originY,
                region.getRegionWidth(), region.getRegionHeight(),
                scaleX, scaleY,
                angle);
    }
}
