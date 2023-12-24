/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

// The CustomHashMap class extends from AbstractMap (implements Map), making sure the modified
// HashMap resizes and rehashes if the load factor is > 0.75. We use java.util.HashMap as a base
// to construct the new one.

public class CustomHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    private final int DEFAULT_INITIAL_CAPACITY = 32;
    private final float CUSTOM_LOAD_FACTOR = 0.75f;
    private final float DEFAULT_LOAD_FACTOR = CUSTOM_LOAD_FACTOR * 2;
    private final Function<Integer, Integer> RESIZE_FUNCTION = (currentSize) -> currentSize * 2 + 1;
    private HashMap<K, V> hashMap;
    private int threshold;

    public CustomHashMap(int initialCapacity) {
        this.hashMap = new HashMap<>(initialCapacity, DEFAULT_LOAD_FACTOR);
        this.threshold = (int) (initialCapacity * CUSTOM_LOAD_FACTOR);
    }

    public CustomHashMap() {
        this.hashMap = new HashMap<>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
        this.threshold = (int) (DEFAULT_INITIAL_CAPACITY * CUSTOM_LOAD_FACTOR);
    }

    public V get(Object key) {
        return hashMap.get(key);
    }

    public V put(K key, V value) {
        hashMap.put(key, value);
        resizeIfNeeded();
        return value;
    }

    // The resizeIfNeeded method makes sure the CustomHashMap is still able to inherit all the base
    // java.util.HashMap attributes and methods (besides the one's that are overwritten/overridden) while also
    // making sure the custom load factor is respected.

    private void resizeIfNeeded() {
        int currentSize = hashMap.size();
        if (currentSize > this.threshold) {
            int newCapacity = RESIZE_FUNCTION.apply(currentSize);
            this.threshold = (int) (newCapacity * CUSTOM_LOAD_FACTOR);

            HashMap<K, V> newHashMap = new HashMap<>(newCapacity, DEFAULT_LOAD_FACTOR);
            newHashMap.putAll(hashMap);
            hashMap = newHashMap;
        }
    }

    public V remove(Object key) {
        return hashMap.remove(key);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return hashMap.entrySet();
    }
}
