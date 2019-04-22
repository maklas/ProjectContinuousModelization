package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;

public class CrossPointComponent implements Component {

    Entity a; //Entity with FunctionComponent
    Entity b; //Entity with FunctionComponent
    float x;
    float y;
    Color color = Color.BLACK.cpy();

    public CrossPointComponent(Entity a, Entity b, float x, float y) {
        this.a = a;
        this.b = b;
        this.x = x;
        this.y = y;
    }
}
