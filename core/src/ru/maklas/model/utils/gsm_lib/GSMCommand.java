package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Stack;

/**
 * Created by maklas on 20.06.2017.
 */

public interface GSMCommand {

    InputProcessor nullInput = new InputAdapter();

    void execute(GameStateManager gsm, Batch batch, Stack<State> states);

}
