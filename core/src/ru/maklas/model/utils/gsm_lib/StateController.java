package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by maklas on 03-Feb-18.
 */

public class StateController {

    private final State state;

    public StateController(State state) {
        this.state = state;
    }

    protected final void popState(){
        this.state.popState();
    }

    protected final void pushState(State state, boolean keepUpdating, boolean keepRendering){
        this.state.pushState(state, keepUpdating, keepRendering);
    }

    protected final GameStateManager getGsm() {
        return state.getGsm();
    }

    protected final Batch getBatch(){
        return state.batch;
    }

    public void onStateResumed() {

    }
}
