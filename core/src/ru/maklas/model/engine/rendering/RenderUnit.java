package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Нечто, рисуемое на экран.
 * @author maklas. Created on 01.05.2017.
 */
@SuppressWarnings("unchecked")
public abstract class RenderUnit<T extends RenderUnit> {
    
    protected static Vector2 tempVec = new Vector2();

    public float localX;
    public float localY;
    public float width;
    public float height;
    public float pivotX = 0.5f;
    public float pivotY = 0.5f;
    public float angle;
    public float scaleX = 1f;
    public float scaleY = 1;

    public String name;

    public abstract void draw(Batch batch, float x, float y, float angle);

    public abstract TextureRegion getRegion();

    public abstract void setRegion(TextureRegion region);

    public T scale(float scale){
        scaleX = scaleY = scale;
        return (T) this;
    }

    public T flipX(){
        scaleX *= -1;
        return (T) this;
    }

    public T flipY(){
        scaleY *= -1;
        return (T) this;
    }

    public T pos(float locX, float locY){
        this.localX = locX;
        this.localY = locY;
        return (T) this;
    }

    public T size(float width, float height) {
        this.width = width;
        this.height = height;
        return (T) this;
    }

    public T pivot(float pX, float pY) {
        this.pivotX = pX;
        this.pivotY = pY;
        return (T) this;
    }
}
