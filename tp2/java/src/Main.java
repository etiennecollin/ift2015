/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.util.ArrayList;

public class Main {
    private static final String DATASET_DIR = "dataset_subset";
    private static final String QUERY_FILE = "query.txt";
    private static final String SOLUTION_FILE = "solution.txt";

    public static void main(String[] args) {
        Preprocessor preprocessor = new Preprocessor();
        // Format files in the dataset
        ArrayList<String> processedFiles = preprocessor.processDirectory(DATASET_DIR);

        // Create wordMap and fileMap
        CustomHashMap<String, ArrayList<String>> wordMap = preprocessor.createWordMap(processedFiles);
        CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>> fileMap = preprocessor.createFileMap(processedFiles);

        // Read query file
        QueryHandler queryHandler = new QueryHandler();
        queryHandler.getQueries(QUERY_FILE);
        queryHandler.processQueries(SOLUTION_FILE, wordMap, fileMap);

        // CustomHashMap<String, CustomHashMap<ArrayList<String>, ArrayList<Integer>>> wordMap = new CustomHashMap<>();
    }
}