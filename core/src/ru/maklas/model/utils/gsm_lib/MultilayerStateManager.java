package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import ru.maklas.model.utils.Config;
import ru.maklas.model.utils.Log;

import java.util.Iterator;
import java.util.Stack;

/**
 * @author maklas. Created on 11.05.2017.
 */

public class MultilayerStateManager implements GameStateManager {

    private final Stack<State> states;
    private Batch batch;
    private boolean useCommand = false;
    private GSMCommand command;
    private InputProcessor noInput = new InputAdapter();
    private float frameMillis = 1/60f;

    public MultilayerStateManager(){
        states = new Stack<>();
    }

    @Override
    public void launch(State firstState, Batch batch) {
        this.batch = batch;
        if (Config.LOG_STATES) Log.trace("GSM", "Launching state: " +  firstState);
        states.push(firstState);
        firstState.inject(this, batch);
        firstState.onCreate();
        InputProcessor ip = firstState.getInput();
        if (ip == null){
            ip = noInput;
        }
        Gdx.input.setInputProcessor(ip);
    }

    @Override
    public void setCommand(GSMCommand command) {
        if (Config.LOG_STATES) Log.trace("GSM", "Setting command " + command);
        useCommand = true;
        this.command = command;
    }


    public void update(float dt){
        long start = System.nanoTime();
        processCommand();

         Stack<State> states = this.states;
         int size = states.size();
         for (int i = 0; i < size; i++) {
             State state = states.get(i);
             if (state.updatable){
                 state.update(dt);
             }
         }

         for (int i = 0; i < size; i++) {
             State state = states.get(i);
             if (state.render){
                 state.render(batch);
             }
         }

         renderMsgs(dt);
         processCommand();
         long end = System.nanoTime();
         frameMillis = (float)((end - start) / 1_000_000d);
         if (frameMillis <= 0.000001f){//не делаем == 0, чтобы избежать деления на ноль.
             frameMillis = 0.000001f;
         }
    }

    public void toBackground(){
        if (Config.LOG_STATES) Log.trace("GSM", "To Background");

        Stack<State> states = this.states;
        int size = states.size();
        for (int i = 0; i < size; i++) {
            State state = states.get(i);
            state.toBackground();
        }
    }

    public void toForeground(){
        if (Config.LOG_STATES) Log.trace("GSM", "ToForeground");
        Stack<State> states = this.states;
        int size = states.size();
        for (int i = 0; i < size; i++) {
            State state = states.get(i);
            state.toForeground();
        }
    }

    @Override
    public void resize(int width, int height) {
        Stack<State> states = this.states;
        for (int i = states.size() - 1; i >= 0; i--) {
            states.get(i).resize(width, height);
        }
    }

    private void processCommand() {
        if (useCommand){
            useCommand = false;
            final GSMCommand command = this.command;
            this.command = null;
            if (Config.LOG_STATES) Log.trace("GSM", "Starting processing command: " + command);
            command.execute(this, batch, states);
            if (useCommand) processCommand();
        }
    }

    public State getCurrentState(){
        return states.peek();
    }

    public void printStackTrace(){
        for (int i = states.size() - 1; i >= 0; i--){
            System.out.println("State # " + i + " is " + states.get(i));
        }
    }

    @Override
    public State getState(int number) {
        if (number < 0){
            throw new RuntimeException("Can't get you negative state");
        }
        if (number >= states.size()){
            return null;
        }
        return states.get(number);
    }

    public void dispose(){
        for (int i = states.size() - 1; i >= 0; i--) {
            states.get(i).dispose();
        }
        states.clear();
        if (initialized) msgsFont.dispose();
        Gdx.input.setInputProcessor(noInput);
    }

    @Override
    public int stackSize() {
        return states.size();
    }

    @Override
    public Batch getBatch() {
        return batch;
    }

    private BitmapFont msgsFont;
    private Array<Msg> msgs;
    private float scale;
    private OrthographicCamera camera;
    private int maxMsgs;
    private boolean initialized = false;

    private void renderMsgs(float dt) {
        if (!initialized || msgs.size == 0) return;

        Batch batch = this.batch;
        batch.setProjectionMatrix(camera.combined);

        float x = 10 - (Gdx.graphics.getWidth()/2) * scale;
        float y = -15 + (Gdx.graphics.getHeight()/2) * scale;

        Iterator<Msg> iterator = msgs.iterator();

        batch.begin();
        while (iterator.hasNext()) {
            Msg next = iterator.next();
            msgsFont.setColor(next.color);
            msgsFont.draw(batch, next.message.toString(), x, y);
            next.ttl -= dt;
            if (next.ttl < 0){
                iterator.remove();
            }
            y -= 25;
        }
        batch.end();

    }


    @Override
    public void print(Object msg){
        print(msg, 1);
    }

    public void clearPrints(){
        if (msgs != null){
            msgs.clear();
        }
    }

    @Override
    public void print(Object msg, float ttl){
        print(msg, ttl, Color.RED);
    }

    @Override
    public void print(Object msg, float ttl, Color color){
        if (!initialized) initializePrinting();
        if (msg == null) msg = "null";
        Msg m = new Msg(ttl, msg, color);
        if (msgs.size > maxMsgs){
            msgs.removeIndex(0);
        }
        msgs.add(m);
    }

    @Override
    public float getLastFrameMillis() {
        return frameMillis;
    }

    private void initializePrinting() {
        msgsFont = new BitmapFont();
        msgs = new Array<>(true, 16);
        scale = 360.0f / Gdx.graphics.getHeight();
        camera = new OrthographicCamera(Gdx.graphics.getWidth() * scale, Gdx.graphics.getHeight() * scale);
        maxMsgs = 14;
        initialized = true;
    }

    @Override
    public void printAsync(final Object msg, final float ttl) {
        Gdx.app.postRunnable(() -> print(msg, ttl));
    }

    private class Msg{

        float ttl;
        final Object message;
        final Color color;

        public Msg(float ttl, Object message, Color color) {
            this.ttl = ttl;
            this.message = message;
            this.color = color;
        }
    }




}
