package ru.maklas.model.engine.rendering;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.model.engine.M;

/**
 * Производит обновления всех анимаций.
 * Created by Danil on 19.08.2017.
 */
public class AnimationSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.entitiesFor(AnimationComponent.class);
    }

    @Override
    public void update(float deltaTime) {

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            final AnimationComponent animationComponent = entity.get(M.anim);
            final Array<Animation> animations = animationComponent.animations;
            int size = animations.size;
            Object[] items = animations.items;
            for (int j = 0; j < size; j++) {
                Animation animation = (Animation) items[j];

                if (animation.enabled) {
                    doAnimation(animation, deltaTime);
                } else {
                    animation.ru.setRegion(animation.defaultFrame);
                }

            }
        }
    }

    public static void doAnimation(Animation animation, float dt) {
        final float tpf = animation.tpf;
        animation.time += dt;
        if (animation.time >= tpf) {
            animation.time -= tpf;
            animation.currentFrame++;
            checkFrameOverflow(animation);
        }
        animation.ru.setRegion(animation.enabled ? animation.frames[animation.currentFrame] : animation.defaultFrame);
    }

    private static void checkFrameOverflow(Animation animation) {
        if (animation.currentFrame == animation.frames.length){
            if (!animation.looped){
                animation.enabled = false;
                animation.time = 0;
            }
            animation.currentFrame = 0;
        }
    }

    @Override
    public void onRemovedFromEngine(Engine e) {
        super.onRemovedFromEngine(e);
        entities = null;
    }
}
