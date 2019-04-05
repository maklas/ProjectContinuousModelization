package ru.maklas.model.engine.other;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.model.engine.M;
import ru.maklas.model.engine.rendering.RenderComponent;

public class TTLSystem extends EntitySystem {

    ImmutableArray<Entity> entities;
    private Array<Entity> removalArray = new Array<>();

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.entitiesFor(TTLComponent.class);
    }

    @Override
    public void update(float dt) {
        ComponentMapper<TTLComponent> ttlM = M.ttl;
        ComponentMapper<RenderComponent> renM = M.render;
        ImmutableArray<Entity> entities = this.entities;

        for (Entity entity : entities) {
            TTLComponent ttlComponent = entity.get(ttlM);
            ttlComponent.ttl -= dt;
            if (ttlComponent.ttl <= 0){

                if (ttlComponent.isVanish){
                    RenderComponent renderC = entity.get(renM);
                    if (renderC != null) {

                        renderC.color.a -= (1f / ttlComponent.ttv) * dt;
                        if (renderC.color.a <= 0) {
                            renderC.color.a = 0;
                            removalArray.add(entity);
                        }

                    } else {
                        removalArray.add(entity);
                    }

                } else {
                    removalArray.add(entity);
                }
            }
        }

        if (removalArray.size > 0){
            removalArray.callAndClear(engine::remove);
        }

    }

    @Override
    public void onRemovedFromEngine(Engine e) {
        super.onRemovedFromEngine(e);
        entities = null;
    }
}
