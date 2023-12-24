/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryHandler {
    private static final String BIGRAM_QUERY = "the most probable bigram of";
    private static final String SEARCH_QUERY = "search";
    private final ArrayList<String> queries;
    private final String queriesPath;
    private final String outputPath;

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

    public void processQueries(WordMap wordMap, ArrayList<String> processedFiles, ArrayList<String> fileNames) {
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputPath, false)))) {
            // Iterate over the queries
            for (String query : queries) {
                // Check what type of query it is and process it accordingly
                QueryType queryType;
                if (query.contains(BIGRAM_QUERY)) {
                    query = query.replace(BIGRAM_QUERY + " ", "");
                    queryType = QueryType.BIGRAM;
                } else if (query.contains(SEARCH_QUERY)) {
                    query = query.replace(SEARCH_QUERY + " ", "");
                    String[] queryWords = query.split("\\s+");
                    queryType = QueryType.SEARCH;
                } else {
                    throw new RuntimeException("Invalid query: " + query);
                }

                // Split the query words
                String[] queryWords = query.split("\\s+");

                // Process each query word
                for (String word : queryWords) {
                    // Use the closest word in the processed files using the edit distance
                    word = correctWord(word, processedFiles);
                    switch (queryType) {
                        case BIGRAM:
                            // Get the bigrams of the word
                            HashMap<String, Integer> bigrams = Utils.getBigrams(wordMap, processedFiles, fileNames, word);
                            // Get the most probable bigram
                            String mostProbableBigram = Utils.getMostProbableBigram(bigrams);
                            // Add the most probable bigram to the output file
                            printWriter.println(word + " " + mostProbableBigram);
                            break;
                        case SEARCH:
                            // Get the TFIDFs of the word
                            HashMap<String, Double> tfidfs = Utils.getTFIDFs(wordMap, processedFiles, fileNames, word);
                            // Get the most relevant file
                            String mostRelevantFile = Utils.getMostRelevantFile(tfidfs);
                            // Add the most relevant file to the output file
                            printWriter.println(mostRelevantFile);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid file output path");
        }
    }

    public String correctWord(String word, ArrayList<String> processedFiles) {
        String correctedWord = word;
        int minDistance = Integer.MAX_VALUE;

        for (String processedFile : processedFiles) {
            String[] processedFileWords = processedFile.split("\\s+");

            for (String processedFileWord : processedFileWords) {
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
