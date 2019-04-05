package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Простая имплементация {@link RenderUnit} в которой на экран просто рисуется картинка.
 */
public class TextureUnit extends RenderUnit<TextureUnit> {

    public TextureRegion region;

    public TextureUnit(TextureRegion region) {
        this.region = region;
        width = region.getRegionWidth();
        height = region.getRegionHeight();
    }

    public TextureUnit init(TextureRegion region) {
        this.region = region;
        width = region.getRegionWidth();
        height = region.getRegionHeight();
        return this;
    }

    public void draw(Batch batch, float x, float y, float angle){
        float originX = width * pivotX;
        float originY = height * pivotY;

        Vector2 localPos = tempVec.set(localX, localY).rotate(angle);

        batch.draw(region,
                x - originX + localPos.x, y - originY + localPos.y,
                originX, originY,
                width, height,
                scaleX, scaleY,
                angle + this.angle);
    }

    @Override
    public TextureRegion getRegion() {
        return region;
    }

    @Override
    public void setRegion(TextureRegion region) {
        this.region = region;
    }
}
