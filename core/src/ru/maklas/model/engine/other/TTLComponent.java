package ru.maklas.model.engine.other;

import ru.maklas.mengine.Component;

/**
 * <p>
 * <b>TTL - Time To Live</b>
 * </p>
 * <p>
 * Удаляет Entity через определённое время или когда его альфа канал достигает нуля.
 * Управляется {@link TTLSystem}.
 * </p>
 */
public class TTLComponent implements Component {

    public float ttl;
    // Постепенное удаление сущности через альфа-канал
    public boolean isVanish;
    // Время постепенного исчезновения сущности
    public float ttv;

    public TTLComponent(float ttl) {
        this.ttl = ttl;
        isVanish = false;
        ttv = 0f;
    }

    public TTLComponent(float ttl, boolean isVanish, float ttv) {
        this.ttl = ttl;
        this.isVanish = isVanish;
        this.ttv = ttv;
    }
}
