package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Stack;

/**
 * Created by maklas on 23-Nov-17.
 */

public class GSMBackTo implements GSMCommand {

    private final Class stateComeBackTo;

    public GSMBackTo(Class stateComeBackTo) {
        this.stateComeBackTo = stateComeBackTo;
    }

    @Override
    public void execute(GameStateManager gsm, Batch batch, Stack<State> states) {
        final int size = states.size();

        int totalPops = 0;
        boolean stateFound = false;
        for (int i = size - 1; i >= 0; i--) {
            if (states.get(i).getClass() == stateComeBackTo){
                stateFound = true;
                break;
            }
            totalPops++;
        }

        if (!stateFound){
            return;
        }

        if (totalPops == 0){
            return;
        }
        GSMPop[] pops = new GSMPop[totalPops];
        for (int i = 0; i < totalPops; i++) {
            State state = states.get((size - 1) - i);
            pops[i] = new GSMPop(state);
        }

        for (GSMPop pop : pops) {
            pop.execute(gsm, batch, states);
        }
    }

    @Override
    public String toString() {
        return "GSMBackTo{" +
                "state=" + stateComeBackTo +
                '}';
    }
}
