package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Stack;

/**
 * Created by maklas on 06.10.2017.
 */

public class GSMClearAndSet implements GSMCommand {

    private final State newState;

    public GSMClearAndSet(State state) {
        this.newState = state;
    }

    @Override
    public void execute(GameStateManager gsm, Batch batch, Stack<State> states) {
        while (states.size() != 0){
            State popped = states.pop();
            popped.dispose();
        }

        states.push(newState);
        newState.inject(gsm, batch);
        newState.onCreate();
        InputProcessor input = newState.getInput();
        Gdx.input.setInputProcessor(input == null ? nullInput : input);

    }

    @Override
    public String toString() {
        return "GSMClearAndSet{" +
                "state=" + newState +
                '}';
    }
}
