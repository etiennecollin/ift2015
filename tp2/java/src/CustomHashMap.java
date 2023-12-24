/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * CustomHashMap is an extension of java.util.AbstractMap, implementing the java.util.Map interface.
 * It provides a custom implementation of a hash map with the ability to resize dynamically based on
 * a custom load factor while maintaining compatibility with java.util.HashMap methods and attributes.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class CustomHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    /**
     * Custom initial capacity for the internal HashMap.
     */
    private final int CUSTOM_INITIAL_CAPACITY = 32;
    /**
     * Custom load factor used for resizing the internal HashMap.
     */
    private final float CUSTOM_LOAD_FACTOR = 0.75f;
    /**
     * Default load factor for the internal HashMap, calculated as twice the custom load factor.
     */
    private final float DEFAULT_LOAD_FACTOR = CUSTOM_LOAD_FACTOR * 2;
    /**
     * Current capacity of the internal HashMap.
     */
    private int capacity = CUSTOM_INITIAL_CAPACITY;
    /**
     * Internal HashMap used for storing key-value pairs.
     */
    private HashMap<K, V> hashMap;
    /**
     * Threshold for resizing the internal HashMap based on the custom load factor.
     */
    private int threshold;

    /**
     * Constructs a CustomHashMap with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the CustomHashMap
     */
    public CustomHashMap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.hashMap = new HashMap<>(initialCapacity, DEFAULT_LOAD_FACTOR);
        this.threshold = (int) (initialCapacity * CUSTOM_LOAD_FACTOR);
    }

    /**
     * Constructs a CustomHashMap with the default initial capacity.
     */
    public CustomHashMap() {
        this.hashMap = new HashMap<>(CUSTOM_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
        this.threshold = (int) (CUSTOM_INITIAL_CAPACITY * CUSTOM_LOAD_FACTOR);
    }

    /**
     * Associates the specified value with the specified key in the CustomHashMap.
     * Resizes the map if necessary to maintain the custom load factor.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     *
     * @return the previous value associated with the specified key, or null if there was no mapping for the key
     */
    public V put(K key, V value) {
        hashMap.put(key, value);
        resizeIfNeeded();
        return value;
    }

    // The resizeIfNeeded method makes sure the CustomHashMap is still able to inherit all the base
    // java.util.HashMap attributes and methods (besides the one's that are overwritten/overridden) while also
    // making sure the custom load factor is respected.

    /**
     * Checks if resizing is needed based on the current size of the CustomHashMap.
     * If the current size exceeds the threshold, the map is resized with a new capacity
     * calculated using the custom resize function and the custom load factor.
     */
    private void resizeIfNeeded() {
        // If the current size exceeds the threshold, the map is resized with a new capacity
        if (hashMap.size() > this.threshold) {
            this.capacity = this.capacity * 2 + 1;
            this.threshold = (int) (capacity * CUSTOM_LOAD_FACTOR);

            // Create a new HashMap with the new capacity and a load factor of twice the custom load factor
            // this ensures that the custom load factor is respected
            HashMap<K, V> newHashMap = new HashMap<>(capacity, DEFAULT_LOAD_FACTOR);

            // Rehash all the elements from the old HashMap into the new one
            newHashMap.putAll(hashMap);
            this.hashMap = newHashMap;
        }
    }

    /**
     * Returns a set view of the mappings contained in the CustomHashMap.
     *
     * @return a set view of the mappings contained in the CustomHashMap
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return hashMap.entrySet();
    }
}
