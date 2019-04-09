package ru.maklas.model.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ru.maklas.mengine.Entity;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.engine.rendering.CameraComponent;
import ru.maklas.model.engine.rendering.CameraMode;
import ru.maklas.model.functions.GraphFunction;

public class EntityUtils {

    public static Entity function(int id, GraphFunction function, Color color, float precision, boolean trackMouse){
        FunctionComponent fc = new FunctionComponent(function);
        fc.color = color;
        fc.precision = precision;
        fc.trackMouse = trackMouse;
        return new Entity(id).add(fc);
    }

    public static Entity camera(OrthographicCamera cam, CameraMode mode) {
        CameraComponent cc = new CameraComponent(cam);
        cc.mode = mode;
        return new Entity().add(cc);
    }
}
