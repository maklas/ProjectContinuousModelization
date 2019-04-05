package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Arrays;
import java.util.Stack;

/**
 * Created by maklas on 08.10.2017.
 */

public class GSMMulti implements GSMCommand {

    private final GSMCommand[] commands;

    public GSMMulti(GSMCommand... commands) {
        this.commands = commands;
    }

    @Override
    public void execute(GameStateManager gsm, Batch batch, Stack<State> states) {
        for (GSMCommand command : commands) {
            command.execute(gsm, batch, states);
        }

    }

    @Override
    public String toString() {
        return "GSMMulti{" +
                "commands=" + Arrays.toString(commands) +
                '}';
    }
}
