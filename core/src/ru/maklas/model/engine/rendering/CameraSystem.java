package ru.maklas.model.engine.rendering;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.model.engine.M;
import ru.maklas.model.utils.NoCaseException;
import ru.maklas.model.utils.Utils;

/**
 * Created by Danil on 27.10.2017.
 */

public class CameraSystem extends EntitySystem {

    private ComponentMapper<CameraComponent> cameraM = M.camera;
    private ImmutableArray<Entity> cameras;
    private static final long DOUBLE_PRESS_TIME = 300;
    private long preLastShiftPress;
    private long lastShiftPress;
    private boolean shifting = false;
    private boolean doubleShifting = false;
    private long lastCtrlPress;
    private boolean ctrling = false;

    @Override
    public void onAddedToEngine(Engine engine) {
        cameras = engine.entitiesFor(CameraComponent.class);
    }

    @Override
    public void update(float dt) {

        for (Entity e : cameras) {
            CameraComponent cc = e.get(cameraM);
            cc.lastFrameX = cc.cam.position.x;
            cc.lastFrameY = cc.cam.position.y;

            Camera cam = cc.cam;
            switch (cc.mode){
                case JUST_LOOK:
                    break;
                case FOLLOW_ENTITY:
                    Entity byId = getEngine().findById(cc.followEntityId);
                    if (byId == null){
                        continue;
                    }
                    if (cc.followX) e.x = byId.x - cc.followOffsetX;
                    if (cc.followY) e.y = byId.y - cc.followOffsetY;
                    break;
                case DRAGGABLE:
                    if ((Gdx.app.getType() == Application.ApplicationType.Android && Gdx.input.isTouched()) || Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {

                        if (cam instanceof OrthographicCamera) {
                            float widthMultiplier = (cam.viewportWidth * ((OrthographicCamera) cam).zoom) / Gdx.graphics.getWidth();
                            float heightMultiplier = (cam.viewportHeight * ((OrthographicCamera) cam).zoom) / Gdx.graphics.getHeight();
                            e.x -= Gdx.input.getDeltaX() * widthMultiplier;
                            e.y += Gdx.input.getDeltaY() * heightMultiplier;
                        } else if (cam instanceof PerspectiveCamera){
                            Vector2 currMousePos = new Vector2(Utils.getMouse(((PerspectiveCamera) cam)));
                            Vector2 prevMousePos = new Vector2(Utils.toScreen(Gdx.input.getX() - Gdx.input.getDeltaX(), Gdx.input.getY() - Gdx.input.getDeltaY(), ((PerspectiveCamera) cam)));
                            e.x += currMousePos.x - prevMousePos.x;
                            e.y += currMousePos.y - prevMousePos.y;
                        }
                    }
                    break;
                case BUTTON_CONTROLLED:
                    float speed;
                    if (cam instanceof OrthographicCamera) {
                        speed = cc.controlSpeed * ((OrthographicCamera)cam).zoom;
                    } else if (cam instanceof PerspectiveCamera){
                        speed = cc.controlSpeed * cam.position.z / 100;
                    } else {
                        speed = cc.controlSpeed;
                    }

                    if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
                        long now = System.currentTimeMillis();
                        if (now - lastShiftPress < DOUBLE_PRESS_TIME){
                            shifting = true;
                            if (lastShiftPress - preLastShiftPress < DOUBLE_PRESS_TIME){
                                doubleShifting = true;
                            }
                        }
                        preLastShiftPress = lastShiftPress;
                        lastShiftPress = now;
                    }
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        speed *= doubleShifting ? 64 : shifting ? 16 : 4;
                    } else {
                        shifting = false;
                        doubleShifting = false;
                    }

                    if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)){
                        long now = System.currentTimeMillis();
                        if (now - lastCtrlPress < DOUBLE_PRESS_TIME){
                            ctrling = true;
                        }
                        lastCtrlPress = now;
                    }
                    if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                        speed /= ctrling ? 16 : 4;
                    } else {
                        ctrling = false;
                    }

                    if (Gdx.input.isKeyPressed(Input.Keys.W)){
                        e.y += speed;
                    }
                    if (Gdx.input.isKeyPressed(Input.Keys.A)){
                        e.x -= speed;
                    }
                    if (Gdx.input.isKeyPressed(Input.Keys.S)){
                        e.y -= speed;
                    }
                    if (Gdx.input.isKeyPressed(Input.Keys.D)){
                        e.x += speed;
                    }
                    break;
                default: throw new NoCaseException(cc.mode);
            }

            cam.position.set(e.x, e.y, cam.position.z);
            cc.vX = (cam.position.x - cc.lastFrameX) / dt;
            cc.vY = (cam.position.y - cc.lastFrameY) / dt;
        }
    }

    @Override
    public void onRemovedFromEngine(Engine e) {
        super.onRemovedFromEngine(e);
        cameras = null;
    }
}
