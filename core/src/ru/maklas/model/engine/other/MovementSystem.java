package ru.maklas.model.engine.other;

import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.model.engine.M;

public class MovementSystem extends EntitySystem {

    private ImmutableArray<Entity> movable;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        movable = entitiesFor(MovementComponent.class);
    }

    @Override
    public void update(float dt) {
        for (Entity entity : movable) {
            MovementComponent mc = entity.get(M.move);
            mc.vX += mc.aX * dt;
            mc.vY += mc.aY * dt;
            entity.x += mc.vX * dt;
            entity.y += mc.vY * dt;
        }
    }
}
