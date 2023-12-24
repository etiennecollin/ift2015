/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class QueryHandler {
    private final ArrayList<String> queries;

    public QueryHandler() {
        this.queries = new ArrayList<>();
    }

    public void processQueries(String outputPath, CustomHashMap<String, CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>>> wordMap,
                               ArrayList<String> processedFiles, ArrayList<String> fileNames) {
        Utils utils = new Utils();

        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputPath, true)))) {
            for (String query : queries) {
                //TODO: changer pour un bon traitement de queries, mais getBigram fonctionne
                printWriter.println(Utils.getBigram(wordMap, processedFiles, fileNames, query));
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid file output path");
        }
    }

    public void getQueries(String queriesFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(queriesFilePath))) {
            String query;
            while ((query = reader.readLine()) != null) {
                this.queries.add(query);
            }
        } catch (IOException e) {
            throw new RuntimeException("Invalid file path");
        }
    }
}
