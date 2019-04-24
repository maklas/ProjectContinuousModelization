package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;
import ru.maklas.model.assets.A;
import ru.maklas.model.assets.ImageAssets;
import ru.maklas.model.engine.B;
import ru.maklas.model.engine.M;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.Utils;

public class PointRenderSystem extends RenderEntitySystem {

    private ImmutableArray<Entity> crossPoints;
    private Batch batch;
    private OrthographicCamera cam;
    private float mouseOverDistance = 10;
    private double yScale = 10;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        crossPoints = entitiesFor(PointComponent.class);
        batch = engine.getBundler().get(B.batch);
        cam = engine.getBundler().get(B.cam);
    }

    @Override
    public void render() {
        if (crossPoints.size() == 0) return;
        Vector2 mouse = Utils.getMouse(cam);
        final float MOD = mouseOverDistance * cam.zoom;

        batch.begin();

        for (Entity crossPoint : crossPoints) {
            PointComponent cross = crossPoint.get(M.cross);
            batch.setColor(cross.color);
            float scale = 0.25f * cam.zoom;
            float x = cross.x;
            float y = (float) (cross.y / yScale);
            ImageAssets.draw(batch, A.images.circle, x, y, 0.5f, 0.5f, scale, scale, 0);
            if (mouse.dst(x, y) < MOD){
                String text = Utils.vec1.set(x, cross.y) + (StringUtils.isEmpty(cross.name) ? "" : " | " + cross.name);
                BitmapFont font = A.images.font;
                font.setColor(cross.color);
                font.draw(batch, text, x + 10 * cam.zoom, y - 50 * cam.zoom);
            }
        }

        batch.end();
    }

    public PointRenderSystem setYScale(double yScale){
        this.yScale = yScale;
        return this;
    }
}
