package ru.maklas.model.user_interface;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

public class BaseStage extends Stage implements View {



    public BaseStage() {
        super(new ScreenViewport());
        if (!VisUI.isLoaded()){
            VisUI.load(VisUI.SkinScale.X1);
        }
    }

    @Override
    public InputProcessor getInput() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }
}
