package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Stack;

/**
 * Created by maklas on 20.06.2017.
 */

public class GSMPush implements GSMCommand {


    private final State oldState;
    private final State newState;
    private final boolean stopUpdatingOld;
    private boolean stopRnderingOld;

    public GSMPush(State oldState, State newState, boolean stopUpdatingOld, boolean stopRnderingOld) {
        this.oldState = oldState;

        this.newState = newState;
        this.stopUpdatingOld = stopUpdatingOld;
        this.stopRnderingOld = stopRnderingOld;
    }

    @Override
    public void execute(GameStateManager gsm, Batch batch, Stack<State> states) {
        if (oldState != null){
            if (stopUpdatingOld){
                oldState.setUpdatable(false);
            }
            if (stopRnderingOld){
                oldState.setRender(false);
            }

            oldState.onPause();

        }

        states.push(newState);
        newState.inject(gsm, batch);
        newState.onCreate();
        InputProcessor ip = newState.getInput();
        if (ip == null){
            ip = nullInput;
        }
        Gdx.input.setInputProcessor(ip);
    }

    @Override
    public String toString() {
        return "GSMPush{" +
                "oldState=" + oldState +
                ", newState=" + newState +
                '}';
    }
}
