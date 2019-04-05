package ru.maklas.model.user_interface;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import ru.maklas.mengine.Engine;
import ru.maklas.model.engine.B;
import ru.maklas.model.engine.input.TouchDownEvent;
import ru.maklas.model.engine.input.TouchUpEvent;
import ru.maklas.model.utils.Utils;

public class GameEngineWidget extends Widget {

    private Engine engine;

    public GameEngineWidget(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void act(float delta) {
        engine.getBundler().set(B.dt, delta);
        engine.update(delta);
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                engine.dispatch(new TouchDownEvent(Utils.getMouse(getCamera()), pointer));
                if (getParent() instanceof Container){
                    return getParent().hit(x, y, true) == GameEngineWidget.this;
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                engine.dispatch(new TouchUpEvent(Utils.getMouse(getCamera()), pointer));
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    public void draw (Batch batch, float parentAlpha) {
        validate();

        boolean batchWasDrawing = batch.isDrawing();
        if (batchWasDrawing) batch.end();
        drawEngine();
        if (batchWasDrawing) batch.begin();
    }

    public OrthographicCamera getCamera(){
        return engine.getBundler().get(B.cam);
    }

    public Engine getEngine() {
        return engine;
    }

    private void drawEngine() {
        Batch engineBatch = engine.getBundler().get(B.batch);
        OrthographicCamera cam = engine.getBundler().get(B.cam);

        cam.update();
        engineBatch.setProjectionMatrix(cam.combined);

        engineBatch.begin();
        engine.render();
        engineBatch.end();
    }

}
