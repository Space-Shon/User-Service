package ru.headsandhands.userservice.Thread;

public class ThreadLocalPayload {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setId(String id){
        threadLocal.set(String.valueOf(id));
    }

    public static String getId(){
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }

}
