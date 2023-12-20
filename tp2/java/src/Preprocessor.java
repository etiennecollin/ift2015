/*
 * Copyright (c) 2023. Etienne Collin #2038029,
 */
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Preprocessor {
    private final StanfordCoreNLP pipeline;

    public Preprocessor() {
        // Set up pipeline properties
        Properties properties = new Properties();
        // Set the list of annotators to run
        properties.setProperty("annotators", "tokenize,pos,lemma");
        // Set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        properties.setProperty("coref.algorithm", "neural");
        // Remove the annoying initialization messages
        RedwoodConfiguration.current().clear().apply();

        // Build pipeline
        this.pipeline = new StanfordCoreNLP(properties);
    }

    public ArrayList<String> processDirectory(String dir) {
        File folder = new File(dir);
        File[] files = folder.listFiles();

        if (files == null) {
            return new ArrayList<>();
        }

        ArrayList<String> processedFiles = new ArrayList<>(files.length);
        for (File file : files) {
            processedFiles.add(processFile(file));
        }
        return processedFiles;
    }

    public String processFile(File file) {
        if (!file.isFile()) {
            throw new RuntimeException("Invalid file");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file.getPath()))) {
            StringBuilder word = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                CoreDocument document = getCoreDocument(line);
                // System.out.println(document.tokens());
                for (CoreLabel token : document.tokens()) {
                    // System.out.println(String.format("%s\t%s", token.word(), token.lemma()));
                    String lemma = String.valueOf(token.lemma());
                    if (!(lemma.contains("'s") || lemma.contains("’s"))) {
                        word.append(lemma).append(" ");
                    }
                }
            }
            // Return a string which has the content of the read file, but it is processed and their
            // words are space-separated.
            return String.valueOf(word).replaceAll("[^a-zA-Z0-9]", " ").replaceAll("\\s+", " ").trim();
        } catch (IOException e) {
            throw new RuntimeException("Invalid file path");
        }
    }

    private CoreDocument getCoreDocument(String line) {
        String formattedLine = line.replaceAll("[^’'a-zA-Z0-9]", " ").replaceAll("\\s+", " ").trim();

        // Create a document object
        CoreDocument document = new CoreDocument(formattedLine);
        // Annotate the document
        this.pipeline.annotate(document);
        return document;
    }
}
