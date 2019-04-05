package ru.maklas.model.user_interface.windows;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import ru.maklas.model.statics.Game;
import ru.maklas.model.utils.Utils;

public class MNWwindow extends Window {

    public enum State {
        CLOSE, OPEN, OPENING, CLOSING
    }

    private State state = State.CLOSE;
    private boolean removeOnOutsideClick = true;
    private boolean removeOnAnyClick = false;
    private boolean canBeRemovedOnBack = true; //Можно ли закрыть окно нажав кнопку BACK на андроиде. Если false, то выйти из приложения не получится!!!

    private TextureRegion bg;
    public float bgA = 0f;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, bgA);
        batch.draw(bg, 0, 0, Game.width, Game.height);
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
        super.draw(batch, parentAlpha);
    }

    public MNWwindow(String title, Skin skin) {
        super(title, skin);
        setScale(0.1f, 0.1f);
        setModal(true);
        setMovable(false);
        setKeepWithinStage(false);
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (removeOnAnyClick){
                    return remove();
                }
                return false;
            }
        });
        Utils.onOutsideClick(this, () -> {
            if (removeOnOutsideClick && !removeOnAnyClick) remove();
        });

        //TODO bg = A.reward.bg;
    }

    public boolean isRemoveOnOutsideClick() {
        return removeOnOutsideClick;
    }

    public void setRemoveOnOutsideClick(boolean removeOnOutsideClick) {
        this.removeOnOutsideClick = removeOnOutsideClick;
    }

    public boolean isCanBeRemovedOnBack() {
        return canBeRemovedOnBack;
    }

    public void setCanBeRemovedOnBack(boolean canBeRemovedOnBack) {
        this.canBeRemovedOnBack = canBeRemovedOnBack;
    }

    public boolean isRemoveOnAnyClick() {
        return removeOnAnyClick;
    }

    public void setRemoveOnAnyClick(boolean removeOnAnyClick) {
        this.removeOnAnyClick = removeOnAnyClick;
    }

    @Override
    public void pack() {
        super.pack();
        setPosition(Game.hWidth, Game.hHeight, Align.center);
        setOrigin(Align.center);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        onAdded();
    }

    public void onAdded(){
        state = State.OPENING;
    }

    @Override
    public boolean remove() {
        state = State.CLOSING;
        return getParent() != null;
    }

    public void removeNow(){
        close();
        super.remove();
    }

    public State getState() {
        return state;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        switch (state){

            case OPENING: opening(delta);
                bgA += delta;
                if (bgA > 0.5f) bgA = 0.5f;
                break;
            case CLOSING:
                closing(delta);
                bgA -= delta * 2f;
                if (bgA <= 0f) bgA = 0f;
                break;
        }
    }

    private void opening(float dt){
        dt *= 60f;

        if (getScaleY() < 1){
            scaleBy(0, 0.15f * dt);
        }else if (getScaleX() < 1){
            setScaleY(1f);
            scaleBy(0.25f * dt, 0);
        }else {
            open();
        }
    }

    private void closing(float dt){
        dt *= 60f;

        if (getScaleX() > 0.1f){
            scaleBy(- 0.25f * dt, 0);
        }else if (getScaleY() > 0.1f){
            setScaleX(0.1f);
            scaleBy(0, - 0.15f * dt);
        }else {
            close();
            super.remove();
        }
    }

    private void open(){
        setScale(1f);
        bgA = 0.5f;
        state = State.OPEN;
    }

    private void close(){
        setScale(0.1f);
        bgA = 0f;
        state = State.CLOSE;
    }
}
