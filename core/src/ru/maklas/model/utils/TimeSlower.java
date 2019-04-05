package ru.maklas.model.utils;

import com.badlogic.gdx.math.MathUtils;

public class TimeSlower {

    private float currentScale = 1;
    private float targetScale = 1f;
    private final float changeVelocity = 1f;
    private float startScale = 0;
    private float alpha = 0;
    private boolean changing = false;

    public float convert(float dt){
        if (changing){
            alpha += dt * changeVelocity;
            if (alpha > 1) {
                alpha = 1;
                changing = false;
            }
            this.currentScale = MathUtils.lerp(startScale, targetScale, alpha);
        }
        return dt * currentScale;
    }

    public void setCurrentScale(float currentScale){
        this.currentScale = currentScale;
        this.targetScale = currentScale;
        this.changing = false;
    }

    public void setTargetScale(float targetScale){
        if (targetScale == this.targetScale) return;
        this.targetScale = targetScale;
        this.startScale = currentScale;
        this.changing = true;
        this.alpha = 0;
    }

    public float getTargetScale() {
        return targetScale;
    }
}
