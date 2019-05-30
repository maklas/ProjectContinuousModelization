package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.maklas.model.utils.StringUtils;

/**
 * @author maklas. Created on 27.04.2017.
 */
public class Animation {

    //current time
    public float time = 0;
    //time per frame/duration
    public float tpf = 0.5f;
    public int currentFrame = 0;
    public TextureRegion[] frames;
    public TextureRegion defaultFrame;
    public RenderUnit ru;
    public boolean enabled = true;
    public boolean looped = true;

    public Animation(TextureRegion[] regions, RenderUnit ru, float cycleTime){
        this.defaultFrame = regions[0];
        ru.setRegion(defaultFrame);
        this.frames = regions;
        this.ru = ru;
        tpf = cycleTime/regions.length;
        if (tpf < 0.016666668f){
            tpf = 0.017f;
        }
    }

    public void reset(TextureUnit ru, int frame){
        if (frame >= 0 && frame < frames.length) {
            currentFrame = frame;
            time = frame;
            ru.region = frames[frame];
        }
    }

    public Animation setFrame(int frame){
        currentFrame = frame % frames.length;
        return this;
    }


    public void setCycleTime(float time) {
        tpf = time/frames.length;
    }

    public void update(float dt){
        time += dt;
        if (time >= tpf) {
            time -= tpf;
            currentFrame++;
            if (currentFrame == frames.length){
                if (!looped){
                    enabled = false;
                    time = 0;
                }
                currentFrame = 0;
            }
        }
        ru.setRegion(enabled ? frames[currentFrame] : defaultFrame);
    }

    @Override
    public String toString() {
        return "{" +
                "on=" + enabled +
                ", loop=" + looped +
                ", t=" + StringUtils.ff(tpf - time, 3) +
                ", frms=" + currentFrame + "/" + frames.length +
                '}';
    }

    public Animation loop(boolean enabled) {
        this.looped = enabled;
        return this;
    }

    public Animation defFrame(TextureRegion region) {
        this.defaultFrame = region;
        return this;
    }

    public Animation enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
