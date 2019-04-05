package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;

/**
 * RenderUnit с полигональной рисовкой.
 * Если передаваемый {@link Batch batch} является {@link SpriteBatch},
 * то ведёт себя как {@link TextureUnit}, Если же это {@link PolygonSpriteBatch},
 * Рисует на экран в соответствии со своими полигонами.
 */
public class PolygonUnit extends RenderUnit<PolygonUnit> {

    public PolygonRegion region;

    public PolygonUnit(PolygonRegion region) {
        this.region = region;
        width = region.getRegion().getRegionWidth();
        height = region.getRegion().getRegionHeight();
    }

    public PolygonUnit(TextureRegion region, float[] vertices, short[] triangles) {
        this.region = new PolygonRegion(region, vertices, triangles);
        width = region.getRegionWidth();
        height = region.getRegionHeight();
    }

    @Override
    public void draw(Batch batch, float x, float y, float angle) {
        if (!(batch instanceof PolygonSpriteBatch)) {
            drawWithoutPolyBatch(batch, x, y, angle);
            return;
        }
        float originX = width * pivotX;
        float originY = height * pivotY;

        Vector2 localPos = tempVec.set(localX, localY).rotate(angle);

        ((PolygonSpriteBatch) batch).draw(region,
                x - originX + localPos.x, y - originY + localPos.y,
                originX, originY,
                width, height,
                scaleX, scaleY,
                angle + this.angle);
    }

    private void drawWithoutPolyBatch(Batch batch, float x, float y, float angle){
        TextureRegion region = this.region.getRegion();

        int width = region.getRegionWidth();
        int height = region.getRegionHeight();
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

    public void moveLocalyByPivot(){
        this.localX = width * pivotX;
        this.localY = height * pivotY;
    }

    @Override
    public TextureRegion getRegion() {
        return region.getRegion();
    }

    @Override
    public void setRegion(TextureRegion region) {
        region.setRegion(region);
    }
}
