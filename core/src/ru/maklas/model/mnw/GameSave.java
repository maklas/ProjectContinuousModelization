package ru.maklas.model.mnw;

import ru.maklas.model.utils.Config;
import ru.maklas.model.utils.persistance.PersistenceManager;

public class GameSave extends PersistenceManager {

    private static final String version = "1.0.0";
    public String name = "PlayerName";
    public boolean firstLanguageSet = false;
    public Language language = Language.ENGLISH;

    public GameSave() {
        super(Config.gameSaveFile);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ACTIONS
    ///////////////////////////////////////////////////////////////////////////

    public void onExit(){
        persist();
    }

    public GameSave toDefaults() {
        name = "PlayerName";
        return this;
    }
}
