package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.Camera;
import ru.maklas.mengine.Component;

/**
 * <p>
 * Прикрепляется к Entity ответственному за движение камеры.
 * Имеет 4 режима:
 *  <li><b>JUST_LOOK</b> - Камеру ничего не двигает</li>
 *  <li><b>FOLLOW_ENTITY</b> - Камера двигается за Entity указанным в followEntityId</li>
 *  <li><b>DRAGGABLE</b> - Камера драгается мышкой на экране</li>
 *  <li><b>BUTTON_CONTROLLED</b> - Камера контролируется кнопками WASD</li>
 *  </p>
 */

public class CameraComponent implements Component {

    public Camera cam;
    public CameraMode mode = CameraMode.JUST_LOOK;

    public int followEntityId;
    public int followOffsetX;
    public int followOffsetY;
    public boolean followX = true;
    public boolean followY = false;
    public float controlSpeed = 10;
    public float lastFrameX;
    public float lastFrameY;
    public float vX;
    public float vY;

    public CameraComponent(Camera cam) {
        this.cam = cam;
    }

    public CameraComponent setFollowEntity(int id){
        this.mode = CameraMode.FOLLOW_ENTITY;
        this.followEntityId = id;
        return this;
    }

    public CameraComponent setFollowOffset(int dx, int dy){
        this.followOffsetX = dx;
        this.followOffsetY = dy;
        return this;
    }

    public CameraComponent setJustLook(){
        this.mode = CameraMode.JUST_LOOK;
        return this;
    }

    public CameraComponent setDraggable(){
        this.mode = CameraMode.DRAGGABLE;
        return this;
    }

    public CameraComponent setControllable(){
        return setControllable(10);
    }
    public CameraComponent setControllable(float speed){
        this.controlSpeed = speed;
        this.mode = CameraMode.BUTTON_CONTROLLED;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "mode=" + mode +
                ", pos=" + cam.position +
                '}';
    }
}
