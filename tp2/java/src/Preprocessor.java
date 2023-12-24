/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
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
import java.util.Map;
import java.util.Properties;

public class Preprocessor {
    private final StanfordCoreNLP pipeline;
    private final CustomHashMap<String, CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>>> wordMap = new CustomHashMap<>();
    private final WordTokenizer tokenizer = new WordTokenizer();
    private final ArrayList<String> fileNames = new ArrayList<>(); // List of file names

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
            this.fileNames.add(file.getName()); // Add the file name to the fileNames list
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

    public CustomHashMap<String, CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>>> getWordMap(){
        return this.wordMap;
    }

    // TODO: Explain how we add words to wordMap, into adding the stuff into fileMap afterwards
    public CustomHashMap<String, CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>>> createWordMap(ArrayList<String> processedFiles) {

        // Iterate through each file
        for (int i = 0; i < processedFiles.size(); i++) {
            String text = processedFiles.get(i);
            String fileName = fileNames.get(i);

            Map<String, ArrayList<Integer>> tokenized = tokenizer.tokenize(text);
            for (Map.Entry<String, ArrayList<Integer>> entry : tokenized.entrySet()) {
                String word = entry.getKey();
                ArrayList<Integer> positions = entry.getValue();

                if (!positions.isEmpty()) {
                    CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>> fileMap = this.getWordMap().get(word);
                    if (fileMap == null) {
                        fileMap = new CustomHashMap<>();

                        ArrayList<String> fileNamesList = new ArrayList<>();
                        fileNamesList.add(fileName);

                        ArrayList<ArrayList<Integer>> positionList = new ArrayList<>();
                        positionList.add(positions);

                        fileMap.put(fileNamesList, positionList);
                        this.getWordMap().put(word, fileMap);

                    } else {
                        for (Map.Entry<ArrayList<String>, ArrayList<ArrayList<Integer>>> fileEntry : fileMap.entrySet()) {
                            ArrayList<String> existingFileNames = fileEntry.getKey();
                            ArrayList<ArrayList<Integer>> existingPositions = fileEntry.getValue();

                            if (!existingFileNames.contains(fileName)) {
                                existingFileNames.add(fileName);
                                existingPositions.add(positions);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return wordMap;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }
}
