/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.util.ArrayList;

public class Main {
    private static final String DATASET_DIR = "dataset_subset";
    private static final String QUERY_FILE = "query_test.txt";
    private static final String SOLUTION_FILE = "solution.txt";

    public static void main(String[] args) {
        // Preprocess the dataset
        Preprocessor preprocessor = new Preprocessor();
        preprocessor.processDirectory(DATASET_DIR);

        // Get the processed files, the file names and the wordMap
        ArrayList<String> processedFiles = preprocessor.getProcessedFiles();
        ArrayList<String> fileNames = preprocessor.getFileNames();
        WordMap wordMap = preprocessor.getWordMap();

        // Read query file
        QueryHandler queryHandler = new QueryHandler(QUERY_FILE, SOLUTION_FILE);
        queryHandler.processQueries(wordMap, processedFiles, fileNames);
    }
}
