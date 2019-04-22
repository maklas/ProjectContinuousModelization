package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.model.engine.B;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.engine.input.EngineInputAdapter;
import ru.maklas.model.engine.input.TouchDownEvent;
import ru.maklas.model.engine.other.EntityDebugSystem;
import ru.maklas.model.engine.rendering.CameraComponent;
import ru.maklas.model.engine.rendering.CameraSystem;
import ru.maklas.model.engine.rendering.FunctionRenderSystem;
import ru.maklas.model.engine.rendering.FunctionTrackingRenderSystem;
import ru.maklas.model.functions.GraphFunction;
import ru.maklas.model.statics.EntityType;
import ru.maklas.model.statics.ID;
import ru.maklas.model.user_interface.FunctionSelectionView;

public class FunctionSelectionState extends AbstractEngineState {


    public GraphFunction selectedFunction;
    private FunctionComponent fc;
    private FunctionSelectionView view;
    private ShapeRenderer sr;
    private OrthographicCamera cam;

    public FunctionSelectionState(GraphFunction selectedFunction) {
        this.selectedFunction = selectedFunction;
    }


    @Override
    protected void loadAssets() {
        view = new FunctionSelectionView(selectedFunction);
        view.onFunctionChange(f -> {
            selectedFunction = f;
            fc.graphFunction = f;
        });
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
    }

    @Override
    protected void fillBundler(Bundler bundler) {
        bundler.set(B.sr, sr);
        bundler.set(B.batch, batch);
        bundler.set(B.gsmState, this);
        bundler.set(B.cam, cam);
    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new FunctionRenderSystem()
                .setNetColor(Color.BLACK)
                .setNumberColor(Color.BLACK)
                .setNetColor(new Color(0.5f, 0.5f, 0.5f, 1)));
        engine.add(new EntityDebugSystem().setTextInfoEnabled(false));
        engine.add(new CameraSystem());
        engine.add(new FunctionTrackingRenderSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, 0).add(new CameraComponent(cam).setControllable()));
        fc = new FunctionComponent(selectedFunction);
        fc.color.set(0.75f, 0.23f, 0.23f, 1);
        fc.lineWidth = 2f;
        engine.add(new Entity().add(fc));
    }

    @Override
    protected void start() {
        engine.subscribe(TouchDownEvent.class, e -> {
            view.unfocusAll();
        });
    }

    @Override
    protected InputProcessor getInput() {
        return new InputMultiplexer(view, new EngineInputAdapter(engine, cam));
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            popState();
        }
        engine.update(dt);
        view.act(dt);
    }

    @Override
    public void resize(int width, int height) {
        view.resize(width, height);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
    }

    @Override
    protected void render(Batch batch) {
        cam.update();
        sr.setProjectionMatrix(cam.combined);
        batch.setProjectionMatrix(cam.combined);

        engine.render();
        view.draw();
    }
}
