/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.util.ArrayList;

/**
 * The {@code FileMap} class extends the {@link CustomHashMap} class to represent a mapping of file-related information.
 * Each entry in this map associates an {@link ArrayList} of file names with an {@link ArrayList} of positions.
 * It provides a specialized implementation for handling file associations and their corresponding positions.
 * <p>
 * The class inherits the functionalities of {@link CustomHashMap} and serves as a convenient container for organizing and
 * managing information about file names and their associated positions in the context of a larger dataset.
 */
public class FileMap extends CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>> {
    /**
     * Constructs a FileMap with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the FileMap
     */
    public FileMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a FileMap with the default initial capacity.
     */
    public FileMap() {
    }
}
