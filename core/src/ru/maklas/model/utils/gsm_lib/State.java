package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.HashMap;

/**
 *  Created on 03.02.2017.
 *  @author maklas
 *
 *  Represents some kind of state in {{@link GameStateManager}}
 */
public abstract class State {

    boolean updatable = true;
    boolean render = true;

    protected GameStateManager gsm;
    protected Batch batch;

    protected HashMap<PromiseState, PromiseHandler> promiseMap = new HashMap<>();


    void inject(GameStateManager gsm, Batch batch){
        this.gsm = gsm;
        this.batch = batch;
    }


    abstract protected void onCreate();

    /**
     *  automatically sets input processor upon creation and resuming to the state.
     *  Must be set in order to prevent problems with input system
     *
     * @return InputAdapter/InputMultiplexer/InputProcessor
     */
    protected InputProcessor getInput(){
        return null;
    }


    /**
     * Triggers 60 times a second to update game logic every frame
     * @param dt how much time last last update selectedFunction took in seconds
     */
    abstract protected void update (float dt);

    abstract protected void render(Batch batch);

    /**  Triggers after pushing StateManager to the new State **/
    protected void onPause(){}

    /**  Called every time when in StateManager pop() is called and this State becomes the current again **/
    protected void onResume(State from){}


    @SuppressWarnings("all")
    final void resume(State from){
        if (from instanceof PromiseState) {
            PromiseState promiseState = ((PromiseState) from);
            PromiseHandler handler = promiseMap.remove(promiseState);
            if (handler != null) {

                if (handler.triggerAfterResume){
                    onResume(from);
                    handler.handle(promiseState.getPromise());
                } else {
                    handler.handle(promiseState.getPromise());
                    onResume(from);
                }

            }
        } else {
            onResume(from);
        }

    }

    /** Triggers when application in Android goes to background and not visible any more **/
    protected void toBackground(){}

    /** Triggers when application in Android goes to foreground and is visible again **/
    protected void toForeground(){}

    /** Triggered when application size is changed **/
    public void resize(int width, int height) { }

    /** Вызывается когда данный State уже не нужен и будет удалён со стака**/
    abstract protected void dispose();

    /** Determines whether to update this State. **/
    public final void setUpdatable(boolean updatable){
        this.updatable = updatable;
    }

    /** Determines whether to render this State **/
    public final void setRender(boolean enable){
        this.render = enable;
    }

    /** @return if this state is getting updates from loop **/
    public final boolean isUpdating(){
        return updatable;
    }

    /** @return if this state is getting rendered **/
    public final boolean isRendering(){
        return render;
    }

    protected final void setState(State state){
        gsm.setCommand(new GSMSet(this, state));
    }

    protected final void popState(){
        gsm.setCommand(new GSMPop(this));
    }

    protected final void pushState(State state, boolean keepUpdating, boolean keepRendering){
        gsm.setCommand(new GSMPush(this, state, !keepUpdating, !keepRendering));
    }

    protected final void pushState(State state){
        this.pushState(state, false, false);
    }

    protected final <T> void pushPromiseState(boolean keepUpdating, boolean keepRendering, PromiseState<T> state, PromiseHandler<T> handler){
        promiseMap.put(state, handler);
        pushState(state, keepUpdating, keepRendering);
    }

    public GameStateManager getGsm() {
        return gsm;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
