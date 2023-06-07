package task_manager.util;

import java.util.HashMap;

public class Utils {

    public static <K, V> HashMap<K, V> newHashMap(K key1, V value1) {
        HashMap<K, V> ret = new HashMap<>();
        ret.put(key1, value1);
        return ret;
    }

    public static <K, V> HashMap<K, V> newHashMap(K key1, V value1, K key2, V value2) {
        HashMap<K, V> ret = new HashMap<>();
        ret.put(key1, value1);
        ret.put(key2, value2);
        return ret;
    }

    public static <K, V> HashMap<K, V> newHashMap(K key1, V value1, K key2, V value2, K key3, V value3) {
        HashMap<K, V> ret = new HashMap<>();
        ret.put(key1, value1);
        ret.put(key2, value2);
        ret.put(key3, value3);
        return ret;
    }

}
