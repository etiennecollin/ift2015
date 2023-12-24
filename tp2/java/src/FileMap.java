/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.util.ArrayList;

public class FileMap extends CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>> {
    public FileMap(int initialCapacity) {
        super(initialCapacity);
    }

    public FileMap() {
    }
}
