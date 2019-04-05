package ru.maklas.model.utils.gsm_lib;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

/** @author maklas. Created on 11.05.2017. **/
public interface GameStateManager {


    /**
     * Launches first state for this manager.
     * Must be called before first update() and never be called twice!
     * state.onCreate() will be called.
     */
    void launch(State firstState, Batch batch);

    /**
     * Обновляет все состояния которые требуют обновления.
     * Затем рисует их
     * @param dt Время за которое протекает кадр
     */
    void update(float dt);

    /** Указывает всем состояниям что приложение ушло на задний план и не видимо больше **/
    void toBackground();

    /** Указывает всем состояниям что приложение ушло на передний план и снова видимо **/
    void toForeground();

    /** Вызывается при изменении размеров окна **/
    void resize(int width, int height);

    /** @return Текущий State (State находящийся на самом верху Стака) **/
    State getCurrentState();

    /** делает sout всех State в стаке **/
    void printStackTrace();

    /** Избавляется от всех стейтов и завершает работу. **/
    void dispose();

    /** @param command Комманда которая исполнится сразу после текущего кадра **/
    void setCommand(GSMCommand command);

    /**
     * @param number номер State (снизу вверх). Самый нижний State имеет индекс 0.
     * @return <b>null</b> если State не найден
     */
    State getState(int number);

    void print(Object msg);

    void print(Object msg, float seconds);

    void print(Object msg, float seconds, Color color);

    void printAsync(Object msg, float seconds);

    /** Возвращает сколько миллисекунд занял предыдущий кадр **/
    float getLastFrameMillis();

    /** Clears all messages that were printed **/
    void clearPrints();

    /** Сколько всего состояний в стаке **/
    int stackSize();

    Batch getBatch();
}
