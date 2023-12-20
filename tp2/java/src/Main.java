/*
 * Copyright (c) 2023. Etienne Collin #2038029,
 */
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    private static final String DATASET_DIR = "dataset_subset";
    private static final String QUERY_FILE = "query.txt";
    private static final String SOLUTION_FILE = "solution.txt";

    public static void main(String[] args) {
        // Preprocessor preprocessor = new Preprocessor();
        // // Format files in the dataset
        // ArrayList<String> processedFiles = preprocessor.processDirectory(DATASET_DIR);
        //
        // // Read query file
        // QueryHandler queryHandler = new QueryHandler();
        // queryHandler.getQueries(QUERY_FILE);
        // queryHandler.processQueries(SOLUTION_FILE);

        CustomHashMap<String, CustomHashMap<ArrayList<String>, ArrayList<Integer>>> wordMap = new CustomHashMap<>();
    }
}