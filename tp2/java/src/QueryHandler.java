/*
 * Copyright (c) 2023. Etienne Collin #2038029,
 */
import java.io.*;
import java.util.ArrayList;

public class QueryHandler {
    private final ArrayList<String> queries;

    public QueryHandler() {
        this.queries = new ArrayList<>();
    }

    public void processQueries(String outputPath) {
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputPath, true)))) {
            for (String query : queries) {
                // TODO: implement
                printWriter.println(query);
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
