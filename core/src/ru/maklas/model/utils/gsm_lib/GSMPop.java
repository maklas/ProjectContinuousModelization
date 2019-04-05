package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Stack;

/**
 * Created by maklas on 20.06.2017.
 */

public class GSMPop implements GSMCommand{

    private final State state;

    public GSMPop(State state) {
        this.state = state;
    }

    @Override
    public void execute(GameStateManager gsm, Batch batch, Stack<State> states) {
        if (states.size() <= 1){
            return;
        }
        State popped = states.pop();
        popped.dispose();

        State state = states.peek();
        state.setUpdatable(true);
        state.setRender(true);
        InputProcessor ip = state.getInput();
        if (ip == null){
            ip = nullInput;
        }
        Gdx.input.setInputProcessor(ip);
        state.resume(popped);
    }

    @Override
    public String toString() {
        return "GSMPop{" +
                "state=" + state +
                '}';
    }
}
