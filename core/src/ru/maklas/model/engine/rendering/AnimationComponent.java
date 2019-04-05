package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Optional;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.Component;

/**
 * Добавляет к Entity простые покадровые анимации.
 * Анимации изменяют текстуры в RenderComponent.
 */
public class AnimationComponent implements Component, Pool.Poolable {

    public final Array<Animation> animations = new Array<>(3);

    public AnimationComponent() {

    }

    public AnimationComponent(Animation animation) {
        animations.add(animation);
    }

    public AnimationComponent add(Animation animation) {
        animations.add(animation);
        return this;
    }

    public Animation get(int i) {
        return animations.get(i);
    }

    public Optional<Animation> findByName(String name) {

        for (Animation animation : animations) {
            if (name.equals(animation.ru.name)){
                return Optional.ofNonNull(animation);
            }
        }
        return Optional.empty();
    }

    public Animation getByName(String name) {
        for (Animation animation : animations) {
            if (name.equals(animation.ru.name)){
                return animation;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        animations.clear();
    }

    @Override
    public String toString() {
        return "Anim" + animations;
    }
}
