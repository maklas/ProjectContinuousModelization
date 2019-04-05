package ru.maklas.model.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class LaserUtils {


    private static final Vector2 tempVec = new Vector2();
    
    public static void drawLaser(Batch batch,
                                 TextureRegion start, TextureRegion middle, TextureRegion end, TextureRegion effect,
                                 Vector2 p1, Vector2 p2,
                                 float startLocX, float startLocY,
                                 float endLocX, float endLocY,
                                 float middleLocStart, float middleLocEnd,
                                 float time,
                                 Color beamColor, Color glowColor){

        float angle = Vector2.angle(p2.x - p1.x, p2.y - p1.y) - 90; //Angle
        float length = p1.dst(p2); //Distance between points
        Vector2 middleStartPoint = tempVec.set(0, middleLocStart).rotate(angle).add(p1).sub(middle.getRegionWidth()/2, 0);

        batch.setColor(glowColor);

        batch.draw(middle, middleStartPoint.x, middleStartPoint.y, middle.getRegionWidth()/2, 0, middle.getRegionWidth(), length - middleLocEnd - middleLocStart, 1, 1, angle);

        batch.draw(start, p1.x - startLocX, p1.y - startLocY, startLocX, startLocY, start.getRegionWidth(), start.getRegionHeight(), 1, 1, angle);
        batch.draw(end, p2.x - endLocX, p2.y - endLocY, endLocX, endLocY, end.getRegionWidth(), end.getRegionHeight(), 1, 1, angle);

        effect.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        effect.setV2(time + length / effect.getTexture().getHeight());
        effect.setV(time);
        batch.setColor(beamColor);
        batch.draw(effect, p1.x - effect.getRegionWidth()/2, p1.y, effect.getRegionWidth()/2, 0, effect.getRegionWidth(), effect.getRegionHeight(), 1, 1, angle);
    }
    
    
}
