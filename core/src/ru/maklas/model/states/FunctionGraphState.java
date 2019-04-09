package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntitySystem;
import ru.maklas.model.assets.A;
import ru.maklas.model.assets.Asset;
import ru.maklas.model.engine.B;
import ru.maklas.model.engine.EntityUtils;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.engine.input.EngineInputAdapter;
import ru.maklas.model.engine.other.EntityDebugSystem;
import ru.maklas.model.engine.other.TTLSystem;
import ru.maklas.model.engine.rendering.CameraMode;
import ru.maklas.model.engine.rendering.CameraSystem;
import ru.maklas.model.engine.rendering.FunctionRenderSystem;
import ru.maklas.model.engine.rendering.FunctionTrackingRenderSystem;
import ru.maklas.model.functions.FunctionUtils;
import ru.maklas.model.functions.GraphFunction;

public class FunctionGraphState extends AbstractEngineState {

    private final Array<GraphFunction> functions;
    private final Array<Array<Vector2>> pointFunctions;
    private OrthographicCamera cam;
    private ShapeRenderer sr;

    public FunctionGraphState(Array<GraphFunction> functions, Array<Array<Vector2>> pointFunctions) {
        this.functions = functions;
        this.pointFunctions = pointFunctions;
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
        engine.add(new FunctionTrackingRenderSystem().setEnableTracking(true).setPrintXY(true));
        engine.add(new EntityDebugSystem().setTextInfoEnabled(false).setZoomAtMouse(true));
        engine.add(new FunctionRenderSystem().setDrawFunctions(true).setDrawNet(true).setDrawPortions(true).setFillNet(false).setNetColor(Color.BLACK).setNumberColor(Color.BLACK));
        engine.add(new UpdatableEntitySystem());
        engine.add(new TTLSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(EntityUtils.camera(cam, CameraMode.BUTTON_CONTROLLED));
    }

    @Override
    protected void start() {
        for (int i = 0; i < functions.size; i++) {
            FunctionComponent fc = new FunctionComponent(functions.get(i));
            fc.color = FunctionUtils.goodFunctionColor(i);
            fc.lineWidth = 2f;
            engine.add(new Entity().add(fc));
        }
    }

    @Override
    protected void update(float dt) {
        engine.update(dt);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            popState();
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


        sr.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < pointFunctions.size; i++) {
            sr.setColor(FunctionUtils.goodFunctionColor(i));
            Array<Vector2> f = pointFunctions.get(i);
            FunctionUtils.renderPoints(sr, f);
        }
        sr.end();
    }
}
