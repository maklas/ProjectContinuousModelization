package ru.maklas.model.engine.input;

import com.badlogic.gdx.math.Vector2;
import ru.maklas.model.engine.other.Event;

/**
 * Нажатие на экран.
 */
public class TouchDownEvent implements Event {

    float x;
    float y;
    int finger;

    public TouchDownEvent(float x, float y, int finger) {
        this.x = x;
        this.y = y;
        this.finger = finger;
    }

    public TouchDownEvent(Vector2 vec, int finger) {
        this.x = vec.x;
        this.y = vec.y;
        this.finger = finger;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getFinger() {
        return finger;
    }

    public void setFinger(int finger) {
        this.finger = finger;
    }

    public Vector2 toVector() {
        return new Vector2(x, y);
    }
}
