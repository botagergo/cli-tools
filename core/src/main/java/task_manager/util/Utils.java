package task_manager.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

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

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(K key1, V value1, K key2, V value2) {
        LinkedHashMap<K, V> ret = new LinkedHashMap<>();
        ret.put(key1, value1);
        ret.put(key2, value2);
        return ret;
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3) {
        LinkedHashMap<K, V> ret = new LinkedHashMap<>();
        ret.put(key1, value1);
        ret.put(key2, value2);
        ret.put(key3, value3);
        return ret;
    }

    public static <K> LinkedHashSet<K> newLinkedHashSet(K key1) {
        LinkedHashSet<K> ret = new LinkedHashSet<>();
        ret.add(key1);
        return ret;
    }

    public static <K> LinkedHashSet<K> newLinkedHashSet(K key1, K key2) {
        LinkedHashSet<K> ret = new LinkedHashSet<>();
        ret.add(key1);
        ret.add(key2);
        return ret;
    }

    public static <K> LinkedHashSet<K> newLinkedHashSet(K key1, K key2, K key3) {
        LinkedHashSet<K> ret = new LinkedHashSet<>();
        ret.add(key1);
        ret.add(key2);
        ret.add(key3);
        return ret;
    }

    public static <K> ArrayList<K> newArrayList(K value1) {
        ArrayList<K> ret = new ArrayList<>();
        ret.add(value1);
        return ret;
    }

    public static <K> ArrayList<K> newArrayList(K value1, K value2) {
        ArrayList<K> ret = new ArrayList<>();
        ret.add(value1);
        ret.add(value2);
        return ret;
    }

    public static <K> ArrayList<K> newArrayList(K value1, K value2, K value3) {
        ArrayList<K> ret = new ArrayList<>();
        ret.add(value1);
        ret.add(value2);
        ret.add(value3);
        return ret;
    }

}
