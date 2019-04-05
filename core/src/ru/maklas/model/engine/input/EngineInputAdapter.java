package ru.maklas.model.engine.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import ru.maklas.mengine.Engine;
import ru.maklas.model.utils.Utils;

public class EngineInputAdapter implements InputProcessor {

    private final Engine engine;
    private final Camera cam;
    private final Vector2[] fingerPositions = new Vector2[10];
    private final Vector2[] fingerDownPositions = new Vector2[10];
    private final TouchDraggedEvent tde = new TouchDraggedEvent();

    public EngineInputAdapter(Engine engine, OrthographicCamera cam) {
        this.engine = engine;
        this.cam = cam;

        for (int i = 0; i < 10; i++) {
            fingerPositions[i] = new Vector2();
            fingerDownPositions[i] = new Vector2();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 mouse = Utils.toScreen(screenX, screenY, cam);
        fingerPositions[pointer].set(mouse);
        fingerDownPositions[pointer].set(mouse);
        engine.dispatch(new TouchDownEvent(mouse, pointer));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector2 mouse = Utils.toScreen(screenX, screenY, cam);
        fingerPositions[pointer].set(mouse);
        engine.dispatch(new TouchUpEvent(mouse, pointer));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector2 mouse = Utils.toScreen(screenX, screenY, cam);
        Vector2 prevPos = fingerPositions[pointer];
        Vector2 firstPos = fingerDownPositions[pointer];
        float dx = mouse.x - prevPos.x;
        float dy = mouse.y - prevPos.y;
        prevPos.set(mouse);
        engine.dispatch(tde.set(mouse.x, mouse.y, dx, dy, firstPos.x, firstPos.y, pointer));
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        engine.dispatch(new ScrollEvent(amount == -1));
        return false;
    }
}
