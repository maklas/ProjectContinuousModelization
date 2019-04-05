package ru.maklas.model.engine.input;

import ru.maklas.model.engine.other.Event;

/** Палец был драгнут по экрану. **/
public class TouchDraggedEvent implements Event {

    float x;
    float y;
    float initX;
    float initY;
    int finger;
    float dx;
    float dy;

    public TouchDraggedEvent() {

    }

    public TouchDraggedEvent(float x, float y, float dx, float dy, float initX, float initY, int finger) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.finger = finger;
        this.initX = initX;
        this.initY = initY;
    }

    public TouchDraggedEvent set(float x, float y, float dx, float dy, float initX, float initY, int finger) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.finger = finger;
        this.initX = initX;
        this.initY = initY;
        return this;
    }

    /** Current X position **/
    public float getX() {
        return x;
    }

    /** Current Y position **/
    public float getY() {
        return y;
    }

    /** Initial X position at touch down **/
    public float getInitX() {
        return initX;
    }

    /** Initial Y position at touch down **/
    public float getInitY() {
        return initY;
    }

    public int getFinger() {
        return finger;
    }

    /** Difference between mouse position in X axis relative to last drag event **/
    public float getDx() {
        return dx;
    }

    /** Difference between mouse position in Y axis relative to last drag event **/
    public float getDy() {
        return dy;
    }
}
