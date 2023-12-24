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
                // TODO: Correction d'orthographe! Distance de Levenstein
                // Check what type of query it is and process it accordingly
                if (query.contains(BIGRAM_QUERY)) {
                    query = query.replace(BIGRAM_QUERY + " ", "");
                    String[] queryWords = query.split("\\s+");

                    for (String word : queryWords) {
                        HashMap<String, Integer> bigrams = Utils.getBigrams(wordMap, processedFiles, fileNames, word);
                        assert bigrams != null;
                        String mostProbableBigram = Utils.getMostProbableBigram(bigrams);
                        printWriter.println(word + " " + mostProbableBigram);
                    }
                } else if (query.contains(SEARCH_QUERY)) {
                    query = query.replace(SEARCH_QUERY + " ", "");
                    String[] queryWords = query.split("\\s+");

                    for (String word : queryWords) {
                        HashMap<String, Double> tfidfs = Utils.getTFIDFs(wordMap, processedFiles, fileNames, word);
                        String mostRelevantFile = Utils.getMostRelevantFile(tfidfs);
                        printWriter.println(mostRelevantFile);
                    }
                } else {
                    throw new RuntimeException("Invalid query: " + query);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid file output path");
        }
    }
}
