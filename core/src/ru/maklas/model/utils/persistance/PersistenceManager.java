package ru.maklas.model.utils.persistance;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import ru.maklas.model.utils.Config;
import ru.maklas.model.utils.Log;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.Utils;

import java.io.IOException;

public abstract class PersistenceManager extends Savable implements Json.Serializable{

    /**
     * If true, Enums will be persisted as ordinals. If false, they name() will be used.
     */
    protected final boolean saveEnumsByOrdinal;

    String fileName;
    Json json;
    SaveWriter currentWriter;

    public PersistenceManager(String fileName) {
        this(fileName, true, JsonWriter.OutputType.minimal);
    }

    public PersistenceManager(String fileName, boolean saveEnumsByOrdinal, JsonWriter.OutputType type) {
        this.fileName = fileName;
        this.json = new Json(type);
        this.saveEnumsByOrdinal = saveEnumsByOrdinal;
    }

    protected void failedToLoadFromFile(){

    }

    public final synchronized String toJsonString(){
        String jsonString;
        currentWriter = new SaveWriter(json, saveEnumsByOrdinal);
        if (Config.PRETTY_PRINT_SAVES){
            jsonString = json.prettyPrint(this);
        } else {
            jsonString = json.toJson(this);
        }
        currentWriter = null;
        return jsonString;
    }

    public final synchronized void persist(){
        currentWriter = new SaveWriter(json, saveEnumsByOrdinal);
        String jsonString = toJsonString();
        currentWriter = null;
        FileHandle saveFile = getSaveFile();
        saveFile.writeString(jsonString, false);
    }

    public final synchronized void persist(String fileName){
        currentWriter = new SaveWriter(json, saveEnumsByOrdinal);
        String jsonString = toJsonString();
        currentWriter = null;
        FileHandle saveFile = getSaveFile(fileName);
        saveFile.writeString(jsonString, false);
    }

    public boolean loadFromFile(){
        return loadFromFile(fileName);
    }
    public synchronized boolean loadFromFile(String fileName){
        FileHandle saveFile = getSaveFile(fileName);
        String s;
        try {
            s = saveFile.readString();
            if (s == null) {
                failedToLoadFromFile();
                Log.trace("Save", "Failed to load file " + getLogFileName() + " from save. NULL");
                return false;
            }
            JsonReader reader = new JsonReader();
            JsonValue parse = reader.parse(s);
            if (parse == null){
                Log.trace("Save", "Failed to load file " + getLogFileName() + " from save. Not correct JSON format");
                return false;
            }
            read(json, parse);
        } catch (Exception e) {
            Log.trace("Save", "Failed to load file " + getLogFileName() + " from save", e);
            return false;
        }
        Log.trace("Save", "Successfully read " + getLogFileName() + " from json");
        return true;
    }

    private FileHandle file;
    public final FileHandle getSaveFile(){
        if (file == null){
            file = getSaveFile(fileName);
        }
        return file;
    }

    public final FileHandle getSaveFile(String fileName){
        FileHandle file;
        file = Gdx.app.getType() == Application.ApplicationType.Desktop ? Gdx.files.local("./../../saves/" + fileName) : Gdx.files.local(fileName);
        if (!file.exists()) {
            try {
                file.parent().mkdirs();
                file.file().createNewFile();
                Log.trace("Save", "New save file " + getLogFileName() + " created");
            } catch (IOException e) {
                Log.error("Save", e);
            }
        }
        return file;
    }


    public final void deleteSaveFile(){
        getSaveFile().delete();
    }

    private String getLogFileName(){
        return StringUtils.wrap(fileName, "\"");
    }

    @Override
    public final void write(Json json) {
        SaveWriter writer = this.currentWriter;
        Utils.never(writer == null);
        this.write(writer);
    }

    @Override
    public final void read(Json json, JsonValue jsonData) {
        SaveReader reader = new SaveReader(jsonData, saveEnumsByOrdinal);
        this.read(reader);
    }
}
