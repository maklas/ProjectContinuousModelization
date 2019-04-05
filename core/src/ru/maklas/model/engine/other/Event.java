package ru.maklas.model.engine.other;

/**
 * <p>
 * Представляет собой результат какого-то действия.
 * Используется дли привязки к определённым событиям происходящим в движке.
 * Например когда нужно проиграть звук при сборе монеты.
 * Для этого мы создадим соответствующий ивент и при сборе монеты будем его посылать в движок.
 * А в системе звуков подпишемся на этот ивент и будем издавать звук при его получении.
 * </p>
 *
 * <p>
 * Пример ивентов:
 * <li>GamePauseEvent{}</li>
 * <li>CoinCollectedEvent{coin}</li>
 * <li>TouchEvent{x, y}</li>
 * <li>DamageEvent{target, source, damage}</li>
 * <li>HealEvent{target, source, healingSpell, damageHealed}</li>
 * </p>
 */
public interface Event extends Dispatchable{
}
