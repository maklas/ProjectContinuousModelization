package ru.maklas.model.statics;

import com.badlogic.gdx.math.MathUtils;
import ru.maklas.model.utils.Utils;

/** Created by Danil on 03.11.2017. **/

public class Balance {

    //POSITIONS
    public static final float GRAVITY = -1300;
    public static final float CEIL_Y = 640;
    public static final float FLOOR_Y = -460;
    public static final float CENTER_Y = (CEIL_Y + FLOOR_Y) / 2;

    /** Случайное значение между потолком и полом **/
    public static float randomBetweenFloorAndCeiling(){
       return (Utils.rand.nextFloat() * (CEIL_Y - FLOOR_Y)) + FLOOR_Y;
    }

    /** Случайное значение между потолком - removeFromSides и полом + removeFromSides; **/
    public static float randomBetweenFloorAndCeiling(float removeFromSides){
       return (Utils.rand.nextFloat() * (CEIL_Y - FLOOR_Y - (removeFromSides * 2))) + (FLOOR_Y + removeFromSides);
    }

    /** Случайное значение между потолком - removeFromSides и полом + removeFromSides; **/
    public static float randomBetweenFloorAndCeiling(float alphaMin, float alphaMax){
        float low = betweenFloorAndCeiling(alphaMin);
        float high = betweenFloorAndCeiling(alphaMax);
        return Utils.rand.nextFloat() * (high - low) + low;
    }

    /** @param alpha 0..1 Где 0 - пол, 1 - потолок **/
    public static float betweenFloorAndCeiling(float alpha){
        return (MathUtils.clamp(alpha, 0, 1) * (CEIL_Y - FLOOR_Y)) + FLOOR_Y;
    }

    /** alpha - диапозон между которым обрезаем. Где 0 - пол, 1 - потолок **/
    public static float clampBetweenFloorAndCeiling(float value, float alphaMin, float alphaMax){
        return MathUtils.clamp(value, betweenFloorAndCeiling(alphaMin), betweenFloorAndCeiling(alphaMax));
    }

    /** Обрезаем значение между полом и потолком, с учётом дополнительных шифтов в пикселях. **/
    public static float clampBetweenFloorAndCeilingShift(float value, float shiftBot, float shiftTop){
        return MathUtils.clamp(value, FLOOR_Y + shiftBot, CEIL_Y + shiftTop);
    }


}
