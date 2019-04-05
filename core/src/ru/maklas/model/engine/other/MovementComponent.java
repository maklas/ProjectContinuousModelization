package ru.maklas.model.engine.other;

import ru.maklas.mengine.Component;

/**
 * Компонент который добавляет Entity скорость и ускорение в двух осях.
 * Внимание! при наличии MovementComponent и PhysicsComponent у одного Entity одновременно,
 * MovementComponent работать не будет.
 */
public class MovementComponent implements Component {

    public float vX;
    public float vY;

    public float aX;
    public float aY;

    public MovementComponent() {}

    public MovementComponent(float vX, float vY) {
        this.vX = vX;
        this.vY = vY;
    }

    public MovementComponent(float vX, float vY, float aX, float aY) {
        this.vX = vX;
        this.vY = vY;
        this.aX = aX;
        this.aY = aY;
    }

}
