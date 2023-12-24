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
    private final ArrayList<String> fileNames = new ArrayList<>(); // List of file names
    private WordMap wordMap = new WordMap();
    private ArrayList<String> processedFiles;

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

    public void processDirectory(String dir) {
        File folder = new File(dir);
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        ArrayList<String> processedFiles = new ArrayList<>(files.length);
        for (File file : files) {
            processedFiles.add(processFile(file));
            this.fileNames.add(file.getName()); // Add the file name to the fileNames list
        }

        this.processedFiles = processedFiles;
        createWordMap();
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

    public WordMap getWordMap() {
        return wordMap;
    }

    public ArrayList<String> getProcessedFiles() {
        return processedFiles;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    private void createWordMap() {
        this.wordMap = new WordMap();

        // Iterate through each file
        for (int i = 0; i < processedFiles.size(); i++) {
            String fileContent = processedFiles.get(i);
            String fileName = fileNames.get(i);

            // We use the Utils.positionalize method in order to get the list of positions for every word contained
            // in a certain text file. We will be adding the positions that are interesting to us to FileMap.

            CustomHashMap<String, ArrayList<Integer>> positionalizedFileContent = Utils.positionalize(fileContent);

            for (Map.Entry<String, ArrayList<Integer>> entry : positionalizedFileContent.entrySet()) {
                String word = entry.getKey();
                ArrayList<Integer> positions = entry.getValue();

                // Create WordMap and FileMap if the word we encountered in a specific text file hasn't been stored yet

                if (!positions.isEmpty()) {
                    FileMap fileMap = this.wordMap.get(word);
                    if (fileMap == null) {
                        fileMap = new FileMap();

                        ArrayList<String> fileNamesList = new ArrayList<>();
                        fileNamesList.add(fileName);

                        ArrayList<ArrayList<Integer>> positionList = new ArrayList<>();
                        positionList.add(positions);

                        fileMap.put(fileNamesList, positionList);
                        this.wordMap.put(word, fileMap);
                    } else {

                        // Update WordMap and FileMap if the word encountered in a specific text file
                        // has already been stored from another text file. Update the key and value from the instance
                        // of fileMap associated to the word in question, corresponding to the list of texts containing
                        // the specific word, and the list of positions respectively.

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
    }
}