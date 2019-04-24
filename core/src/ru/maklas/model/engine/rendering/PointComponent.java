package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.mengine.Component;

public class PointComponent implements Component {

    public float x;
    public float y;
    public Color color = Color.BLACK.cpy();
    public String name;

    public PointComponent(float x, float y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public PointComponent(float x, float y, String name, Color color) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.color = color.cpy();
    }
}
