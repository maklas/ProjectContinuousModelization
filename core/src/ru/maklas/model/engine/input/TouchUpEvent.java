package ru.maklas.model.engine.input;

import com.badlogic.gdx.math.Vector2;
import ru.maklas.model.engine.other.Event;

/**
 * Палец был поднят.
 */
public class TouchUpEvent implements Event {

    float x;
    float y;
    int finger;

    public TouchUpEvent(Vector2 point, int finger) {
        this.x = point.x;
        this.y = point.y;
        this.finger = finger;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getFinger() {
        return finger;
    }
}
