package ru.maklas.model.user_interface;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by amaklakov on 20.10.2017.
 * Стандартный интерфейс для всех Классов отвечающих за GUI
 */
public interface View extends Disposable {

    /** Если наследуем от Stage, то просто всегда <b>return this;</b> **/
    InputProcessor getInput();

    void addActor(Actor actor);

    void act(float dt);

    void draw();

    Batch getBatch();

    Camera getCamera();

    void dispose();

    void resize(int width, int height);

}
