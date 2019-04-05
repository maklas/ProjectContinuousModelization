package ru.maklas.model.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMapFunction;
import com.badlogic.gdx.utils.Pool;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import ru.maklas.model.mnw.MNW;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by maklas on 16.09.2017.
 */

public class Utils {

    public static final Vector2 vec1 = new Vector2(0, 0);
    public static final Vector2 vec2 = new Vector2(0, 0);
    public static final Pool<Vector2> vecPool = new Pool<Vector2>() {
        @Override
        protected Vector2 newObject() {
            return new Vector2();
        }
    };
    public static final Random rand = new Random();
    public static ExecutorService executor = Executors.newCachedThreadPool();
    private static final Vector2 localVec = new Vector2(0, 0);
    private static final Vector3 localVec3 = new Vector3(0, 0, 0);
    private static Calendar localCalendar;

    public static Vector2 obtain(){
        return vecPool.obtain();
    }
    public static Vector2 obtain(float x, float y){
        return vecPool.obtain().set(x, y);
    }
    public static Vector2 obtain(Vector2 v){
        return vecPool.obtain().set(v);
    }

    public static void free(Vector2 v){
        vecPool.free(v);
    }

    public static Vector2 toScreen(float x, float y, PerspectiveCamera cam) {
        Ray ray = cam.getPickRay(x, y);
        float distance = -ray.origin.z / ray.direction.z;
        return localVec.set(ray.direction.x, ray.direction.y).scl(distance).add(ray.origin.x, ray.origin.y);
    }

    public static float distanceToZAxis(PerspectiveCamera cam){
        return distanceToZAxis(Gdx.input.getX(), Gdx.input.getY(), cam);
    }
    public static float distanceToZAxis(float x, float y, PerspectiveCamera cam){
        Ray ray = cam.getPickRay(x, y);
        return -ray.origin.z / ray.direction.z;
    }

    public static Vector2 toScreen(float x, float y, OrthographicCamera cam) {
        return toScreen(localVec.set(x, y), cam);
    }

    public static Vector2 toScreen(float x, float y, Camera cam) {
        if (cam instanceof OrthographicCamera){
            return toScreen(x, y, (OrthographicCamera) cam);
        } else if (cam instanceof PerspectiveCamera){
            return toScreen(x, y, (PerspectiveCamera) cam);
        }
        return localVec.set(x, y);

    }

    public static Vector2 toScreen(Vector2 vec, OrthographicCamera cam){
        float realWidth = Gdx.graphics.getWidth();
        float realHeight = Gdx.graphics.getHeight();
        float targetWidth = cam.viewportWidth;
        float targetHeight = cam.viewportHeight;
        float zoom = cam.zoom;
        float x = vec.x;
        float y = vec.y;
        float widthScale = realWidth/targetWidth;
        float heightScale = realHeight/targetHeight;

        x /= widthScale;
        y = (realHeight - y)/heightScale;

        x -= targetWidth/2;
        y -= targetHeight/2;

        x *= zoom;
        y *= zoom;

        x += cam.position.x;
        y += cam.position.y;

        vec.set(x, y);

        return vec;
    }

    public static Vector2 getMouse(OrthographicCamera cam) {
        return toScreen(Gdx.input.getX(), Gdx.input.getY(), cam);
    }

    public static Vector2 getMouse(PerspectiveCamera cam) {
        return toScreen(Gdx.input.getX(), Gdx.input.getY(), cam);
    }

    public static Vector2 getMouse(Camera cam) {
        if (cam instanceof OrthographicCamera){
            return toScreen(Gdx.input.getX(), Gdx.input.getY(), (OrthographicCamera) cam);
        } else if (cam instanceof PerspectiveCamera){
            return toScreen(Gdx.input.getX(), Gdx.input.getY(), (PerspectiveCamera) cam);
        }
        return localVec.set(Gdx.input.getX(), Gdx.input.getY());
    }

    public static float camRightX (OrthographicCamera cam){
        return cam.position.x + (cam.viewportWidth * 0.5f * cam.zoom);
    }

    public static float camLeftX (OrthographicCamera cam){
        return cam.position.x - cam.viewportWidth * 0.5f * cam.zoom;
    }

    public static float camBotY (OrthographicCamera cam){
        return cam.position.y - cam.viewportHeight * 0.5f * cam.zoom;
    }

    public static float camTopY (OrthographicCamera cam){
        return cam.position.y + cam.viewportHeight * 0.5f * cam.zoom;
    }

    public static long daysSince(long timeMillis){
        long timeDifference = System.currentTimeMillis() - timeMillis;
        if (timeDifference < 0) return 0;
        long days = TimeUnit.DAYS.convert(timeDifference, TimeUnit.MILLISECONDS);
        return days;
    }

    /**
     * Возвращает через сколько миллисекунд наступит определённый час.
     * hour = 3 -> вернёт через сколько миллисекунд наступит 3 часа ночи.
     */
    public static long nextXHour(int hour){
        if (hour < 0) throw new IllegalArgumentException("Hour arg must be > 0");
        hour %= 24;

        if (localCalendar == null) {
            localCalendar = Calendar.getInstance();
        }
        Calendar cal = localCalendar;
        cal.setTimeInMillis(System.currentTimeMillis());
        if (!(cal.get(Calendar.HOUR_OF_DAY) < hour)) {
            cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * Возвращает сколько миллисекунд назад наступил определённый час.
     * hour = 3 -> вернёт сколько милисекунд назад было 3 часа ночи.
     */
    public static long prevXHour(int hour){
        return nextXHour(hour) - (1000 * 60 * 60 * 24);
    }

    @Nullable
    public static InetAddress getByNameForAndroid(String name){
        FutureTask<InetAddress> addressTask = new FutureTask<>(() -> InetAddress.getByName(name));

        executor.execute(addressTask);
        try {
            return addressTask.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public static <T> T rand(T... arr){
        return arr[rand.nextInt(arr.length)];
    }

    /**
     * returns true if value was found among array of items
     * @param identity true -> .equals(), false -> ==
     */
    public static <T> boolean oneOf(boolean identity, T value, T... arr){
        if (identity){
            for (T t : arr) {
                if (t.equals(value)){
                    return true;
                }
            }
        } else {
            for (T t : arr) {
                if (t == value){
                    return true;
                }
            }
        }
        return false;
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> e){
        T[] enumConstants = e.getEnumConstants();
        return enumConstants[rand.nextInt(enumConstants.length)];
    }

    public static long ceil(long a, long b){
        long l = a / b;
        return l * b;
    }

    /**
     * Пытается найти локальный адрес в сети, который не 127.0.0.1
     * Возвращает null, если не был найден
     */
    @Nullable
    public static String getLocalInetAddress(){

        try {
            InetAddress address = InetAddress.getLocalHost();
            String localHost = address.getHostAddress();
            if (!localHost.contains("127.0.0.1") && !localHost.contains("::1")){
                return localHost;
            }

                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()){
                NetworkInterface intef = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = intef.getInetAddresses();

                while (inetAddresses.hasMoreElements()){
                    InetAddress a = inetAddresses.nextElement();
                    String addr = a.getHostAddress();
                    boolean isIPv6 = addr.split(":").length > 1;
                    if (!addr.contains("127.0.0.1") && !isIPv6){
                        return addr;
                    }
                }
            }

        } catch (Exception ignored) {}

        return null;
    }

    @Contract("null -> fail")
    public static Object assertNotNull(Object o){
        if (o == null){
            throw new RuntimeException("Object can't be null");
        }
        return o;
    }

    private static void assertNotEmpty(Object[] arr) {
        if (arr == null || arr.length == 0){
            throw new RuntimeException("Array must not be null or empty");
        }
    }

    @Contract("true -> fail")
    public static void never(boolean condition){
        if (condition) throw new RuntimeException("Never!");
    }

    @Contract("-> fail")
    public static void never() throws RuntimeException{
        throw new RuntimeException("Never!");
    }

    public static float getDeltaTime() {
        float dt = Gdx.graphics.getDeltaTime();
        if (dt > 0.05f){
            dt = 0.016666667f;
        } else if (dt == 0){
            dt = 0.001f;
        }
        return dt;
    }


    @SuppressWarnings("all")
    public static <T> Comparator<T> comparingInt(LongMapFunction<? super T> keyExtractor) {
        if (keyExtractor == null) throw new NullPointerException();
        return (Comparator<T>) (c1, c2) -> compare((int)keyExtractor.map(c1), (int)keyExtractor.map(c2));
    }

    @SuppressWarnings("all")
    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static <T> Comparator<T> reverseComparator(Comparator<T> c){
        return Collections.reverseOrder(c);
    }

    @SuppressWarnings("all")
    public static <E extends Enum<E>> E next(E e) {
        E[] enumConstants = (E[]) e.getClass().getEnumConstants();
        int next = (e.ordinal() + 1) % enumConstants.length;
        return enumConstants[next];
    }
    @SuppressWarnings("all")
    public static <E extends Enum<E>> E previous(E e) {
        E[] enumConstants = (E[]) e.getClass().getEnumConstants();
        int next = (e.ordinal() - 1);
        if (next < 0){
            next = enumConstants.length - 1;
        }
        return enumConstants[next];
    }

    public static void onOutsideClick(Actor actor, Runnable r){
        actor.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > actor.getWidth() || y < 0 || y > actor.getHeight()){
                    r.run();
                    return true;
                }
                return false;
            }
        });
    }

    public static boolean isCompatibleVersion(String version){
        return versionsAreCompatible(version, MNW.VERSION);
    }

    public static boolean versionsAreCompatible(String clientVersion, String serverVersion) {
        if (clientVersion == null || serverVersion == null) return false;

        try {
            int[] client = toVersion(clientVersion);
            int[] server = toVersion(serverVersion);
            return client[0] == server[0] && client[1] == server[1];
        } catch (Exception ignore) { return false; }
    }

    public static boolean versionsAreExactlyTheSame(String clientVersion, String serverVersion){
        if (clientVersion == null || serverVersion == null) return false;

        try {
            int[] client = toVersion(clientVersion);
            int[] server = toVersion(serverVersion);
            return client[0] == server[0] && client[1] == server[1] && client[2] == server[2];
        } catch (Exception ignore) { return false; }
    }

    private static int[] toVersion(String v) throws Exception {
        int[] ret = new int[3];
        String[] split = v.split("\\.");
        for (int i = 0; i < split.length; i++) {
            ret[i] = Integer.parseInt(split[i]);
        }
        return ret;
    }

    public static float getLabelScale(String s, int maxLength){
        return s.length() > maxLength ? (((float) maxLength) / s.length()) : 1;
    }

    public static Array<Window> getWindows(Object scene2dObject){
        return getWindows(scene2dObject, Window.class);
    }
    @SuppressWarnings("all")
    public static <T extends Window> Array<T> getWindows(Object scene2dObject, Class<T> windowClazz){
        Array<T> array = new Array<>();
        if (scene2dObject instanceof Stage){
            for (Actor actor : ((Stage) scene2dObject).getActors()) {
                if (windowClazz.isAssignableFrom(actor.getClass())){
                    array.addAll((T) actor);
                }
            }
        } else if (scene2dObject instanceof Actor){
            Stage actorsStage = ((Actor) scene2dObject).getStage();
            if (actorsStage != null){

                for (Actor actor : actorsStage.getActors()) {
                    if (windowClazz.isAssignableFrom(actor.getClass())){
                        array.addAll((T) actor);
                    }
                }
            }
        }

        return array;
    }

    public static String getStackTrace(){
        return stackTraceToString(Thread.currentThread().getStackTrace());
    }

    public static String stackTraceToString(StackTraceElement[] stackTrace){
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : stackTrace) {
            sb.append(e).append("\n");
        }

        return sb.toString();
    }

    /**
     * @param pos 0 - position in the method of codePoint. 1 - caller of codePoint() and so on
     * @return class name + line number + method name
     */
    public static String codePoint(int pos) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        pos = Math.min(pos + 1, stackTrace.length - 1);
        String className = stackTrace[pos].getClassName();
        return className.substring(className.lastIndexOf('.') + 1) + ":" + stackTrace[pos].getLineNumber() + " - " + stackTrace[pos].getMethodName() + "()";
    }

    /**
     *
     * @return class name + line number + method name of the caller method
     */
    public static String codePoint() {
        return Gdx.app.getType() == Application.ApplicationType.Desktop ? codePoint(2) : codePoint(3);
    }

    public static boolean contains(int[] integers, int i) {
        for (int integer : integers) {
            if(integer == i) return true;
        }
        return false;
    }
}
