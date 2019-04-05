package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.IterableZSortedRenderSystem;
import ru.maklas.model.engine.B;
import ru.maklas.model.statics.Layers;
import ru.maklas.model.utils.Config;
import ru.maklas.model.utils.Log;

/**
 * Рендерит все RenderComponent у всех Entity на экран.
 */
public class RenderingSystem extends IterableZSortedRenderSystem<RenderComponent> {

    private Batch batch;
    private OrthographicCamera cam;
    private int maxDrawCalls = 0;
    private int currentLayer;
    private static final int minLayer = -2_000_000_000;
    private static final int maxLayer =  2_000_000_000;

    public RenderingSystem() {
        super(RenderComponent.class, false);
        setAlwaysInvalidate(true);
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        batch = engine.getBundler().getAssert(B.batch);
        cam = engine.getBundler().getAssert(B.cam);
        maxDrawCalls = 0;
    }

    @Override
    protected void renderStarted() {
        currentLayer = minLayer;
    }

    private void onLayerChanged(int oldLayer, int newLayer) {
        //перечислять строго в порядке повышения слоя.
        // не делать if-else цепочек, так как можно пропустить смену нужного слоя
        if (Layers.background > oldLayer && Layers.background <= newLayer){
            //before rendering background
        }

        if (Layers.generation > oldLayer && Layers.generation <= newLayer){
            //before rendering generation
        }
    }

    @Override
    protected void renderEntity(Entity entity, RenderComponent rc) {
        if (!rc.visible) return;
        checkLayerChange(entity.layer);
        Batch batch = this.batch;
        batch.setColor(rc.color);

        int size = rc.renderUnits.size;
        Object[] units = rc.renderUnits.items;

        for (int i = 0; i < size; i++) {
            ((RenderUnit) units[i]).draw(batch, entity.x, entity.y, entity.getAngle());
        }
    }

    private void checkLayerChange(int newLayer) {
        if (newLayer == currentLayer) return;
        int oldLayer = currentLayer;
        currentLayer = newLayer;
        onLayerChanged(oldLayer, newLayer);
    }

    @Override
    public void onRemovedFromEngine(Engine e) {
        super.onRemovedFromEngine(e);
        batch = null;
        cam = null;
    }

    @Override
    protected void renderFinished() {
        if (Config.LOG_DRAWCALLS && (batch instanceof SpriteBatch || batch instanceof PolygonSpriteBatch)) {
            int renderCalls = batch instanceof SpriteBatch ? ((SpriteBatch) batch).renderCalls : ((PolygonSpriteBatch) batch).renderCalls;
            if (renderCalls > maxDrawCalls) {
                maxDrawCalls = renderCalls;
                Log.trace("RenderingSystem", "Max drawcalls: " + maxDrawCalls);
            }
        }
        onLayerChanged(currentLayer, maxLayer);
        batch.setColor(1, 1, 1, 1);
    }

    public OrthographicCamera getCamera() {
        return cam;
    }
}
