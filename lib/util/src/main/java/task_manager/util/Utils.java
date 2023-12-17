package task_manager.util;

import java.util.*;

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

    public static <K> LinkedHashSet<K> newLinkedHashSet() {
        return new LinkedHashSet<>();
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

    public static <K> LinkedHashSet<K> newLinkedHashSet(K key1, K key2, K key3, K key4) {
        LinkedHashSet<K> ret = new LinkedHashSet<>();
        ret.add(key1);
        ret.add(key2);
        ret.add(key3);
        ret.add(key4);
        return ret;
    }

    public static <K> ArrayList<K> newArrayList() {
        return new ArrayList<>();
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

    public static <K> ArrayList<K> newArrayList(K value1, K value2, K value3, K value4) {
        ArrayList<K> ret = new ArrayList<>();
        ret.add(value1);
        ret.add(value2);
        ret.add(value3);
        ret.add(value4);
        return ret;
    }

    public static <K> ArrayList<K> newArrayList(K value1, K value2, K value3, K value4, K value5) {
        ArrayList<K> ret = new ArrayList<>();
        ret.add(value1);
        ret.add(value2);
        ret.add(value3);
        ret.add(value4);
        ret.add(value5);
        return ret;
    }

    public static <K> ArrayList<K> newArrayList(K value1, K value2, K value3, K value4, K value5, K value6, K value7, K value8, K value9, K value10) {
        ArrayList<K> ret = new ArrayList<>();
        ret.add(value1);
        ret.add(value2);
        ret.add(value3);
        ret.add(value4);
        ret.add(value5);
        ret.add(value6);
        ret.add(value7);
        ret.add(value8);
        ret.add(value9);
        ret.add(value10);
        return ret;
    }

    public static boolean yesNo(String prompt) {
        Scanner scanner = new Scanner(System.in);

        System.out.print(prompt + " (y/n)");
        String answer = scanner.nextLine();

        return answer.equalsIgnoreCase("y");
    }

}
