package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;
import ru.maklas.model.assets.A;
import ru.maklas.model.engine.B;
import ru.maklas.model.engine.M;
import ru.maklas.model.engine.formulas.BiFunctionComponent;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.Utils;

public class FunctionTrackingRenderSystem extends RenderEntitySystem {

    private ImmutableArray<Entity> functions;
    private ImmutableArray<Entity> biFunctions;
    private OrthographicCamera cam;
    private ShapeRenderer sr;
    private Batch batch;
    private Pool<TrackResult> trackResultPool = new Pool<TrackResult>() {
        @Override
        protected TrackResult newObject() {
            return new TrackResult();
        }
    };
    private Array<TrackResult> trackResults = new Array<>();

    private boolean enableTracking = true;
    private boolean printXY = true;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        functions = entitiesFor(FunctionComponent.class);
        biFunctions = entitiesFor(BiFunctionComponent.class);
        cam = engine.getBundler().get(B.cam);
        sr = engine.getBundler().get(B.sr);
        batch = engine.getBundler().get(B.batch);
    }

    @Override
    public void render() {
        if (!enableTracking){
            return;
        }
        Vector2 mouse = Utils.getMouse(cam);

        for (Entity function : functions) {
            FunctionComponent fc = function.get(M.fun);
            if (fc.trackMouse && xWithinCam(mouse.x)){
                trackResults.add(createTrack(fc, mouse));
            }
        }

        if (trackResults.size > 0) {

            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.begin(ShapeRenderer.ShapeType.Line);

            for (TrackResult tr : trackResults) {
                sr.setColor(tr.trackColor);
                sr.getColor().a *= 0.5f;
                sr.line(tr.lineFrom, tr.lineTo);
                if (tr.drawPoint){
                    sr.flush();
                    sr.set(ShapeRenderer.ShapeType.Filled);
                    sr.setColor(Color.PINK);
                    sr.circle(tr.point.x, tr.point.y, 3 * cam.zoom, 8);
                }
            }

            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);


            if (printXY) {
                batch.begin();
                BitmapFont font = A.images.font;

                font.getData().setScale(cam.zoom);

                for (TrackResult tr : trackResults) {
                    font.setColor(tr.trackColor);
                    font.getColor().a = 1;
                    int precision = cam.zoom > 3 ? 1 : cam.zoom > 0.01f ? 2 : 3;
                    font.draw(batch, "(" + StringUtils.df(tr.xVal, precision) + ", " + StringUtils.df(tr.yVal, precision) + ")", tr.textPos.x, tr.textPos.y, 10, Align.left, false);
                }

                batch.end();
            }

            trackResultPool.freeAll(trackResults);
            trackResults.clear();
        }

        if (printXY && biFunctions.size() > 0){

            batch.begin();

            for (Entity biFunction : biFunctions) {
                BiFunctionComponent bfc = biFunction.get(M.biFun);
                A.images.font.setColor(bfc.color);
                if (bfc.fun.absF(mouse.x, mouse.y) < cam.zoom * bfc.resolutionMultiplier * bfc.thickness){
                    batch.setColor(bfc.color);
                    A.images.font.draw(batch, StringUtils.isEmpty(bfc.fun.name()) ? bfc.name : bfc.fun.name(), mouse.x + 15 * cam.zoom, mouse.y + 15 * cam.zoom, 10, Align.left, false);
                }
            }

            batch.end();
        }
    }

    public FunctionTrackingRenderSystem setEnableTracking(boolean enableTracking) {
        this.enableTracking = enableTracking;
        return this;
    }

    public FunctionTrackingRenderSystem setPrintXY(boolean printXY) {
        this.printXY = printXY;
        return this;
    }

    private TrackResult createTrack(FunctionComponent fc, Vector2 mouse) {
        TrackResult tr = trackResultPool.obtain();
        tr.lineFrom.set(mouse);
        double y = fc.graphFunction.f(mouse.x);
        double clampY = MathUtils.clamp(y, Utils.camBotY(cam), Utils.camTopY(cam));
        tr.lineTo.set(mouse.x, ((float) clampY));
        tr.trackColor.set(fc.color);
        tr.yVal = y;
        tr.xVal = mouse.x;
        tr.textPos.set(mouse.x, ((float) clampY));
        if (clampY < Utils.camBotY(cam) + (15 * cam.zoom)){
            tr.textPos.y += 15 * cam.zoom;
        }
        if (clampY == y){
            tr.drawPoint = true;
            tr.point.set(mouse.x, ((float) y));
        }
        return tr;
    }

    private float clampX(float x){
        return MathUtils.clamp(x, Utils.camLeftX(cam), Utils.camRightX(cam));
    }

    private float clampY(float y){
        return MathUtils.clamp(y, Utils.camBotY(cam), Utils.camTopY(cam));
    }

    private boolean xWithinCam(float x){
        return x > Utils.camLeftX(cam) && x < Utils.camRightX(cam);
    }

    private class TrackResult implements Pool.Poolable {

        final Vector2 lineFrom = new Vector2();
        final Vector2 lineTo = new Vector2();
        final Vector2 point = new Vector2();
        final Vector2 textPos = new Vector2();
        final Color trackColor = new Color();
        boolean drawPoint = false;
        double yVal;
        double xVal;

        @Override
        public void reset() {
            lineFrom.setZero();
            lineTo.setZero();
            textPos.setZero();
            point.setZero();
            xVal = 0;
            yVal = 0;
            drawPoint = false;
        }
    }
}
