package ru.maklas.model.engine.formulas;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.mengine.Component;
import ru.maklas.model.functions.bi_functions.GraphBiFunction;

public class BiFunctionComponent implements Component {

    public String name = "";
    public GraphBiFunction fun;
    public Color color = Color.WHITE.cpy();
    public double thickness = 1; //Чем больше, тем жирнее будут линии, но видно будет лучше
    public double resolutionMultiplier = 1; // Чем больше, тем меньше будет разрешение картинки, но лучше производительность

    public BiFunctionComponent(GraphBiFunction fun) {
        this.fun = fun;
    }

    public BiFunctionComponent setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * @param thickness Чем больше, тем жирнее будут линии, но видно будет лучше
     * @param resolutionMultiplier Чем больше, тем меньше будет разрешение картинки, но лучше производительность
     */
    public BiFunctionComponent setParams(double thickness, double resolutionMultiplier) {
        this.thickness = thickness;
        this.resolutionMultiplier = resolutionMultiplier;
        return this;
    }

    public BiFunctionComponent setName(String name) {
        this.name = name;
        return this;
    }
}
