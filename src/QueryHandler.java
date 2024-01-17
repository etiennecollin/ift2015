/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.io.*;
import java.util.ArrayList;

/**
 * The QueryHandler class is responsible for processing queries based on a given WordMap and
 * the results of the preprocessing of text files.
 */
public class QueryHandler {
    /**
     * List of queries to be processed.
     */
    private final ArrayList<String> queries;
    /**
     * Path to the file containing queries.
     */
    private final String queriesPath;
    /**
     * Output path for storing the results of query processing.
     */
    private final String outputPath;

    /**
     * Constructs a QueryHandler with the specified paths for queries and output.
     *
     * @param queriesPath the path to the file containing queries
     * @param outputPath  the output path for storing the results of query processing
     */
    public QueryHandler(String queriesPath, String outputPath) {
        this.queriesPath = queriesPath;
        this.outputPath = outputPath;
        this.queries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(queriesPath))) {
            String query;
            while ((query = reader.readLine()) != null) {
                this.queries.add(query);
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid file path");
        }
    }

    /**
     * Processes the queries based on the provided WordMap and the results of preprocessing.
     *
     * @param wordMap        the WordMap containing word associations with FileMaps
     * @param processedFiles the list of processed files containing cleaned and lemmatized content
     * @param fileNames      the list of file names corresponding to the processed files
     */
    public void processQueries(WordMap wordMap, ArrayList<String[]> processedFiles, ArrayList<String> fileNames) {
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputPath, false)))) {
            // Iterate over the queries
            for (String query : queries) {
                // Check what type of query it is and process it accordingly
                QueryType queryType;
                if (query.contains(QueryType.BIGRAM.getQueryPrefix())) {
                    query = query.replace(QueryType.BIGRAM.getQueryPrefix(), "");
                    queryType = QueryType.BIGRAM;
                } else if (query.contains(QueryType.SEARCH.getQueryPrefix())) {
                    query = query.replace(QueryType.SEARCH.getQueryPrefix(), "");
                    queryType = QueryType.SEARCH;
                } else {
                    throw new RuntimeException("Invalid query format: " + query);
                }

                // Split the query words
                String[] queryWords = query.split("\\W+");

                switch (queryType) {
                    case BIGRAM:
                        if (queryWords.length != 1) {
                            throw new RuntimeException("Invalid query format: " + query);
                        }
                        // Use the closest word in the processed files using the edit distance
                        String word = correctWord(queryWords[0], processedFiles);
                        // Get the bigrams of the word
                        CustomHashMap<String, Integer> bigrams = Utils.getBigrams(wordMap, processedFiles, fileNames, word);
                        // Get the most probable bigram
                        String mostProbableBigram = Utils.getMostProbableBigram(bigrams);
                        // Add the most probable bigram to the output file
                        printWriter.println(word + " " + mostProbableBigram);
                        break;
                    case SEARCH:
                        CustomHashMap<String, Double> tfidfsMerged = new CustomHashMap<>();
                        // Process each query word
                        for (String searchWord : queryWords) {
                            searchWord = correctWord(searchWord, processedFiles);
                            // Get the TFIDFs of the searchWord
                            CustomHashMap<String, Double> tfidfs = Utils.getTFIDFs(wordMap, processedFiles, fileNames, searchWord);
                            // Merge to the TFIDFs of the query words
                            tfidfs.forEach((k, v) -> tfidfsMerged.merge(k, v, (v1, v2) -> v1 + v2));
                        }
                        // Get the most relevant file
                        String mostRelevantFile = Utils.getMostRelevantFile(tfidfsMerged);
                        // Add the most relevant file to the output file
                        printWriter.println(mostRelevantFile);
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid file output path");
        }
    }

    /**
     * Corrects the given word by finding the closest match in the list of processed files.
     *
     * @param word           the word to be corrected
     * @param processedFiles the list of processed files containing cleaned and lemmatized content
     *
     * @return the corrected word based on the closest match
     */
    public String correctWord(String word, ArrayList<String[]> processedFiles) {
        String correctedWord = word;
        int minDistance = Integer.MAX_VALUE;

        for (String[] processedFile : processedFiles) {
            for (String processedFileWord : processedFile) {
                int distance = Utils.editDistance(word, processedFileWord);

                if (distance < minDistance) {
                    minDistance = distance;
                    correctedWord = processedFileWord;
                }
            }
        }
        return correctedWord;
    }
}
