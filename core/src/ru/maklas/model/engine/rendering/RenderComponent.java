package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.components.IRenderComponent;

/**
 * Данный компонент позволяет прикреплять картинки к Entity,
 * которыерисуются на экран в {@link RenderingSystem}
 * @author maklas. Created on 26.04.2017.
 */

public class RenderComponent implements IRenderComponent, Pool.Poolable{

    public Array<RenderUnit> renderUnits = new Array<>(2);
    public Color color = Color.WHITE.cpy();
    public boolean visible = true;

    public RenderComponent(TextureRegion textureRegion) {
        renderUnits.add(new TextureUnit(textureRegion));
    }

    public RenderComponent(RenderUnit renderUnit) {
        renderUnits.add(renderUnit);
    }

    public RenderComponent(Array<RenderUnit> renderUnits) {
        this.renderUnits = renderUnits;
    }

    public RenderComponent() {

    }

    public RenderComponent add(RenderUnit renderUnit){
        renderUnits.add(renderUnit);
        return this;
    }

    public RenderUnit getRenderUnitByName(String name){
        for(RenderUnit r:renderUnits){
            if (r.name.equals(name)) return r;
        }
        return null;
    }

    public RenderComponent color(Color color){
        this.color.set(color);
        return this;
    }

    @Override
    public void reset() {
        renderUnits.clear();
        color = Color.WHITE.cpy();
    }

    @Override
    public String toString() {
        return "Render{" +
                "vis=" + visible +
                ", color=" + color +
                ", units=" + renderUnits.size +
                '}';
    }
}
