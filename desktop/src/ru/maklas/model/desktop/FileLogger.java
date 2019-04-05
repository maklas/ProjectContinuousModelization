package ru.maklas.model.desktop;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.Nullable;
import ru.maklas.model.utils.Log;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ru.maklas.model.utils.Log.*;

@SuppressWarnings("all")
public class FileLogger extends Log.Logger {

    private static final int maxLogFiles = 6;
    private DateFormat format;
    private File folder;
    private File file;
    @Nullable
    private PrintWriter writer;

    public FileLogger() {
        format = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        folder = new File("./../../logs");
        if (!folder.exists()){
            folder.mkdirs();
        }
        removeOldLogs();
        String logFileName = format.format(new Date());
        file = new File(folder, logFileName + ".log");
        try {
            file.createNewFile();
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeOldLogs() {
        try {
            File[] logFiles = folder.listFiles();
            if (logFiles == null || logFiles.length == 0) return;

            Array<File> logs = new Array<>(logFiles);
            logs.filter(f -> getLogFileDate(f) != null);
            if (logs.size < maxLogFiles) return;


            logs.sort((f1, f2) ->{
                Date o1 = getLogFileDate(f1);
                Date o2 = getLogFileDate(f2);
                return o1.compareTo(o2);
            });

            for (int i = 0; i < logs.size - maxLogFiles; i++) {
                System.out.println("Deleting: " + logs.get(i).getName());
                logs.get(i).delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date getLogFileDate(File file){
        String name = file.getName();
        String noExtentionName = name.replace(".log", "");

        try {
            return format.parse(noExtentionName);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public void log(int level, String category, String message, Throwable ex) {
        StringBuilder builder = new StringBuilder(256);

        long time = System.currentTimeMillis();
        long minutes = time / (60_000) % 60;
        long seconds = time / (1000) % 60;
        long millis = time % 1000;
        if (minutes <= 9) builder.append('0');
        builder.append(minutes);
        builder.append(':');
        if (seconds <= 9) builder.append('0');
        builder.append(seconds);
        builder.append(':');
        if (millis <= 9) builder.append('0');
        if (millis <= 99) builder.append('0');
        builder.append(millis);

        switch (level) {
            case LEVEL_ERROR:
                builder.append(" ERROR: ");
                break;
            case LEVEL_DEBUG:
                builder.append(" DEBUG: ");
                break;
            case LEVEL_TRACE:
                builder.append(" TRACE: ");
                break;
        }

        if (category != null) {
            builder.append('[');
            builder.append(category);
            builder.append("] ");
        }

        builder.append(message);

        if (ex != null) {
            StringWriter writer = new StringWriter(256);
            PrintWriter exceptionPrinter = new PrintWriter(writer);
            ex.printStackTrace(exceptionPrinter);
            exceptionPrinter.flush();
            builder.append('\n');
            builder.append(writer);
        }
        String result = builder.toString();
        print(level, result);
    }

    protected void print(int level, String message) {
        PrintWriter writer = this.writer;
        if (writer != null){
            synchronized (writer) {
                writer.println(message);
            }
        }

        if (level == LEVEL_DEBUG) {
            System.out.println(message);
        } else if (level == LEVEL_ERROR) {
            System.err.println(message);
        }
    }

    public void dispose(){
        PrintWriter writer = this.writer;
        if (writer == null) return;
        writer.flush();
        writer.close();
    }
}
