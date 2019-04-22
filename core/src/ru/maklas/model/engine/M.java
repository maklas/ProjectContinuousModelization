package ru.maklas.model.engine;


import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.UpdatableEntitySystem;
import ru.maklas.model.engine.formulas.BiFunctionComponent;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.engine.other.*;
import ru.maklas.model.engine.rendering.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author maklas. Created on 05.06.2017.
 */

public class M {

    public static final int totalComponents = 30;

    public static final ComponentMapper<RenderComponent>                render = ComponentMapper.of(RenderComponent.class);
    public static final ComponentMapper<CameraComponent>                camera = ComponentMapper.of(CameraComponent.class); //Движение камеры
    public static final ComponentMapper<AnimationComponent>             anim = ComponentMapper.of(AnimationComponent.class); //Анимации
    public static final ComponentMapper<TTLComponent>                   ttl = ComponentMapper.of(TTLComponent.class); //Удаление Entity из движка через определённое время
    public static final ComponentMapper<MovementComponent>              move = ComponentMapper.of(MovementComponent.class); // Простое движение Entity (vx,vy)

    public static final ComponentMapper<FunctionComponent>              fun = ComponentMapper.of(FunctionComponent.class);
    public static final ComponentMapper<BiFunctionComponent>            biFun = ComponentMapper.of(BiFunctionComponent.class);
    public static final ComponentMapper<CrossPointComponent>            cross = ComponentMapper.of(CrossPointComponent.class);


    /** Сортируем системы в порядке их обновления в Engine.update() **/
    public static void initialize(){
        int mappers = getMappersReflection() + 2;
        Engine.TOTAL_COMPONENTS = Math.max(mappers, totalComponents);
        ObjectMap<Class<? extends EntitySystem>, Integer> map = Engine.systemOrderMap;
        int i = 1;


        map.put(TTLSystem.class, i++);
        map.put(AnimationSystem.class, i++);
        map.put(MovementSystem.class, i++);
        map.put(UpdatableEntitySystem.class, i++);

        map.put(CameraSystem.class, i++);
        map.put(RenderingSystem.class, i++);
        map.put(FunctionRenderSystem.class, i++);
        map.put(BiFunctionRenderSystem.class, i++);
        map.put(CrossPointRenderSystem.class, i++);
        map.put(FunctionTrackingRenderSystem.class, i++);
        map.put(EntityDebugSystem.class, i++);
    }

    /** Устанавливаем длинну массива Entity.components[] в зависимости от количества компонентов. **/
    private static int getMappersReflection(){
        int counter = 0;
        try {
            Field[] fields = M.class.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && ComponentMapper.class.isAssignableFrom(field.getType())){
                    counter++;
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return counter;
    }

}
