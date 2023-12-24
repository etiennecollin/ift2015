/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */


/**
 * The {@code WordMap} class extends the {@link CustomHashMap} class to represent a mapping of words to their occurrences
 * in different files, where each word is associated with a {@link FileMap}. It provides a specialized implementation
 * for handling word associations and their corresponding file maps.
 * <p>
 * The class inherits the functionalities of {@link CustomHashMap} and serves as a convenient container for organizing and
 * managing information about the occurrences of words in different files.
 */
public class WordMap extends CustomHashMap<String, FileMap> {
    /**
     * Constructs a WordMap with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the WordMap
     */
    public WordMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a WordMap with the default initial capacity.
     */
    public WordMap() {
    }
}
