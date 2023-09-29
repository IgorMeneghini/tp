package DataStructure.Hash;

import java.util.HashMap;
import java.util.Map;

public class Bucket<K, V> {
    public Map<K, V> entries;

    public Bucket() {
        entries = new HashMap<>();
    }

    public V get(K key) {
        return entries.get(key);
    }

    public void put(K key, V value) {
        entries.put(key, value);
    }

    public int size(){
        return entries.size();
    }

    // Other methods like removing entries can be added here.
}
