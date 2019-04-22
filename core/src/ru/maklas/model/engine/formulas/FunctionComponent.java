package ru.maklas.model.engine.formulas;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.mengine.Component;
import ru.maklas.model.functions.GraphFunction;

public class FunctionComponent implements Component {

    public GraphFunction graphFunction;
    public Color color = Color.WHITE.cpy();
    public boolean trackMouse = true;
    public double precision = 1d;
    public float lineWidth = 1f; //1..2
    public String name = "";

    public FunctionComponent(GraphFunction graphFunction) {
        this.graphFunction = graphFunction;
    }


}
