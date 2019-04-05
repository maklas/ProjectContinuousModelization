package ru.maklas.model.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.model.user_interface.IDEView;
import ru.maklas.model.utils.gsm_lib.State;

public class MainMenuState extends State {

    IDEView view;

    @Override
    protected void onCreate() {

    }

    @Override
    protected void update(float dt) {
        view.act();
    }

    @Override
    protected InputProcessor getInput() {
        return view;
    }

    @Override
    public void resize(int width, int height) {
        view.resize(width, height);
    }

    @Override
    protected void render(Batch batch) {
        view.draw();
    }

    @Override
    protected void dispose() {
        view.dispose();
    }
}
