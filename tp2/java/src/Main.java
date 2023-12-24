/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.util.ArrayList;

/**
 * The Main class serves as the entry point for the application.
 * It demonstrates the usage of the Preprocessor and QueryHandler classes
 * to preprocess a dataset and handle queries based on the processed data.
 */
public class Main {
    /**
     * Directory path for the dataset.
     */
    private static final String DATASET_DIR = "dataset";
    /**
     * Name of the query file.
     */
    private static final String QUERY_FILE = "query.txt";
    /**
     * Name of the solution file.
     */
    private static final String SOLUTION_FILE = "solution.txt";

    /**
     * The main method that initiates the preprocessing of the dataset
     * and handles queries based on the processed data.
     *
     * @param args command-line arguments (not used in this context)
     */
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
