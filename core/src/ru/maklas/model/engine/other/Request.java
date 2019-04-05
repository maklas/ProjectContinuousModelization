package ru.maklas.model.engine.other;

/**
 * <p>
 * Представляет собой запрос. Побуждение к действию.
 * <b>Важно! Не использовать реквесты для чего-то кроме исполнения заявленного действия,
 * запрос не является приказом и может быть отклонён системой.</b>
 *</p>
 * <p>
 *     После отправки запроса в систему обычно следует ожидать какого-то ивента о совершении данного действия,
 *     Так, например, после HealRequest можно ожидать HealEvent в котором будет указано кто, кого и на сколько
 *     хильнул. При этом очевидно, что HealRequest может быть отклонён, по причине смерти персонажа, тогда
 *     HealEvent не выстрелит. В случае, если известно что Request всегда будет исполнен,
 *     рекомендуется заменить его на {@link Command}, дабы отметить этот факт.
 * </p>
 * <p>
 * Пример реквестов:
 * <li>DamageRequest{target, source, damage}</li>
 * <li>HealRequest{target, healer, healingSpell}</li>
 * <li>OpenDoorRequest{source, door}</li>
 * <li>CreateEntityRequest{param1, param2, param3...}</li>
 * </p>
 */
public interface Request extends Dispatchable{
}
