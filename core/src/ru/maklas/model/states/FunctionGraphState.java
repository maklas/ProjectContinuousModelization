package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.libs.Timer;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntitySystem;
import ru.maklas.model.assets.A;
import ru.maklas.model.assets.Asset;
import ru.maklas.model.engine.B;
import ru.maklas.model.engine.EntityUtils;
import ru.maklas.model.engine.M;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.engine.input.EngineInputAdapter;
import ru.maklas.model.engine.other.EntityDebugSystem;
import ru.maklas.model.engine.other.TTLSystem;
import ru.maklas.model.engine.rendering.*;

public class FunctionGraphState extends AbstractEngineState {

    private final Array<Entity> entitiesToAdd;
    private final double leftX;
    private final double rightX;
    private OrthographicCamera cam;
    private ShapeRenderer sr;
    private double oldYScale = 1;
    private double targetYScale = 1;
    private Timer smoothScaleTimer;

    public FunctionGraphState(Array<Entity> entities, double leftX, double rightX) {
        this.entitiesToAdd = entities;
        this.leftX = leftX;
        this.rightX = rightX;
    }

    @Override
    protected void loadAssets() {
        A.all().foreach(Asset::load);
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void fillBundler(Bundler bundler) {
        bundler.set(B.cam, cam);
        bundler.set(B.batch, batch);
        bundler.set(B.gsmState, this);
        bundler.set(B.sr, sr);
    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new CameraSystem());
        engine.add(new EntityDebugSystem().setTextInfoEnabled(false).setZoomAtMouse(true));
        engine.add(new UpdatableEntitySystem());
        engine.add(new TTLSystem());
        engine.add(new ScalableFunctionRenderSystem()
                .setDrawFunctions(true)
                .setDrawAxis(true)
                .setAxisColor(Color.BLACK)
                .setNumberColor(Color.BLACK)
                .setYScale(7.5));
        engine.add(new FunctionTrackingRenderSystem()
                .setEnableTracking(true)
                .setPrintXY(true)
                .setPrintFunctionNames(true)
                .setYScale(7.5));
        engine.add(new PointRenderSystem()
                .setYScale(7.5));
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(EntityUtils.camera(cam, CameraMode.BUTTON_CONTROLLED));
    }

    @Override
    protected void start() {
        if (entitiesToAdd != null) {
            engine.addAll(entitiesToAdd);
        }
        smoothScaleTimer = new Timer(1f, false, () -> setYScale(targetYScale));
        smoothScaleTimer.setEnabled(false);
        doScaleAndPosition();
    }

    private void doScaleAndPosition(){
        oldYScale = targetYScale;
        ImmutableArray<Entity> cameras = engine.entitiesFor(CameraComponent.class);
        if (cameras.size() == 0) return;
        cameras.get(0).x = (float) ((rightX + leftX) / 2);
        this.cam.zoom = (float) ((rightX - leftX) / this.cam.viewportWidth) * 1.03f;

        double lowestY = Double.MAX_VALUE;
        double highestY = Double.MIN_VALUE;
        ImmutableArray<Entity> functions = engine.entitiesFor(FunctionComponent.class);
        for (Entity function : functions) {
            FunctionComponent fc = function.get(M.fun);
            for (double x = leftX; x < rightX; x+= this.cam.zoom) {
                double y = fc.graphFunction.f(x);
                if (Double.isNaN(y)) continue;
                if (y > highestY){
                    highestY = y;
                }
                if (y < lowestY){
                    lowestY = y;
                }
            }
        }

        double center = 0;
        double height = 1;
        if (lowestY < highestY){
            center = (highestY + lowestY) / 2;
            height = highestY - lowestY;
        }
        double yScale = (height / (cam.viewportHeight * cam.zoom)) * 1.03;

        cameras.get(0).y = (float) (center / yScale);
        targetYScale = yScale;
        smoothScaleTimer.setEnabled(true);
    }

    private void setYScale(double yScale){
        PointRenderSystem crossSys = engine.getSystemManager().getSystem(PointRenderSystem.class);
        ScalableFunctionRenderSystem sclRendSys = engine.getSystemManager().getSystem(ScalableFunctionRenderSystem.class);
        FunctionTrackingRenderSystem trackSys = engine.getSystemManager().getSystem(FunctionTrackingRenderSystem.class);
        if (crossSys != null){
            crossSys.setYScale(yScale);
        }
        if (sclRendSys != null){
            sclRendSys.setYScale(yScale);
        }
        if (trackSys != null){
            trackSys.setYScale(yScale);
        }
    }

    @Override
    protected void update(float dt) {
        engine.update(dt);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            popState();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)){
            doScaleAndPosition();
        }
        smoothScaleTimer.update(dt);
        if (smoothScaleTimer.isEnabled()){
            float a = smoothScaleTimer.getCurrentTime() / smoothScaleTimer.getUpdateRate();
            double currentYScale = oldYScale + (targetYScale - oldYScale) * a;
            setYScale(currentYScale);
        }
    }

    @Override
    protected InputProcessor getInput() {
        return new EngineInputAdapter(engine, cam);
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(width, height);
    }

    @Override
    protected void render(Batch batch) {
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        sr.setProjectionMatrix(cam.combined);
        engine.render();
    }
}
