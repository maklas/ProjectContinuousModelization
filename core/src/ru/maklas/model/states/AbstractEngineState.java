package ru.maklas.model.states;

import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.TestEngine;
import ru.maklas.model.utils.Config;
import ru.maklas.model.utils.DebugEventDispatcher;
import ru.maklas.model.utils.gsm_lib.State;

public abstract class AbstractEngineState extends State {

    protected Engine engine;

    @Override
    protected void onCreate() {
        loadAssets();
        engine = Config.DEBUG_ENGINE ? new TestEngine() : new Engine();
        if (Config.DEBUG_ENGINE){
            engine.setDispatcher(new DebugEventDispatcher());
        }
        fillBundler(engine.getBundler());
        addSystems(engine);
        addDefaultEntities(engine);
        start();
    }

    /** Загружаем ассеты и другие игровые объекты, типа TimeSlower **/
    protected abstract void loadAssets();

    /** Заполняем Bundler **/
    protected abstract void fillBundler(Bundler bundler);

    /** Заполняем системы **/
    protected abstract void addSystems(Engine engine);

    /** Добавляем Entity, которые должны быть с самого начала игры **/
    protected abstract void addDefaultEntities(Engine engine);

    /** Движок был установлен, можно делать что хочешь **/
    protected abstract void start();

    @Override
    protected void dispose() {
        engine.dispose();
    }
}
