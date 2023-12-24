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

/**
 * The Preprocessor class is responsible for processing a directory of text files,
 * extracting lemmatized words from each file, and creating a WordMap that associates
 * each word with a FileMap containing file names and positions.
 */
public class Preprocessor {
    /**
     * StanfordCoreNLP pipeline for natural language processing.
     */
    private final StanfordCoreNLP pipeline;
    /**
     * List of file names processed by the preprocessor.
     */
    private final ArrayList<String> fileNames = new ArrayList<>(); // List of file names
    /**
     * WordMap associating each word with a FileMap containing file names and positions.
     */
    private WordMap wordMap = new WordMap();
    /**
     * List of processed files containing lemmatized and cleaned content.
     */
    private ArrayList<String[]> processedFiles;

    /**
     * Constructs a Preprocessor with the necessary setup for natural language processing.
     */
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

    /**
     * Processes all text files in the specified directory, extracting lemmatized words,
     * and creating a WordMap associating each word with a FileMap containing file names and positions.
     *
     * @param dir the directory path containing text files to be processed
     */
    public void processDirectory(String dir) {
        File folder = new File(dir);
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        ArrayList<String[]> processedFiles = new ArrayList<>(files.length);
        for (File file : files) {
            processedFiles.add(processFile(file).split("\\W+"));
            this.fileNames.add(file.getName()); // Add the file name to the fileNames list
        }

        this.processedFiles = processedFiles;
        createWordMap();
    }

    /**
     * Processes a single text file, extracting lemmatized words and returning the cleaned content.
     *
     * @param file the text file to be processed
     *
     * @return the cleaned and lemmatized content of the file
     *
     * @throws RuntimeException if the file is not valid or if there is an issue reading the file
     */
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

    /**
     * Creates a WordMap associating each word with a FileMap containing file names and positions.
     */
    private void createWordMap() {
        this.wordMap = new WordMap();

        // Iterate through each file
        for (int i = 0; i < processedFiles.size(); i++) {
            String[] fileContent = processedFiles.get(i);
            String fileName = fileNames.get(i);

            // Get the list of positions for every word of the processed file
            CustomHashMap<String, ArrayList<Integer>> positionalizedFileContent = Utils.positionalize(fileContent);

            // Add relevant positions to the wordMap
            for (Map.Entry<String, ArrayList<Integer>> entry : positionalizedFileContent.entrySet()) {
                String word = entry.getKey();
                ArrayList<Integer> positions = entry.getValue();

                if (positions.isEmpty()) {
                    continue;
                }

                // Try to get the fileMap associated to the word in question
                FileMap fileMap = this.wordMap.get(word);

                // If the word has not been encountered yet, create a new fileMap
                if (fileMap == null) {
                    fileMap = new FileMap();

                    // Update the key of th fileMap with the new file name
                    ArrayList<String> fileNamesList = new ArrayList<>();
                    fileNamesList.add(fileName);

                    // Update the value of the fileMap with the new list of positions
                    ArrayList<ArrayList<Integer>> positionList = new ArrayList<>();
                    positionList.add(positions);

                    // Update the wordMap with the new fileMap
                    fileMap.put(fileNamesList, positionList);
                    this.wordMap.put(word, fileMap);
                } else {
                    // Update the existing fileMap with the new file name and the new list of positions
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

    /**
     * Performs natural language processing on a given line of text, returning a CoreDocument.
     *
     * @param line the input line of text to be processed
     *
     * @return the CoreDocument obtained after natural language processing
     */
    private CoreDocument getCoreDocument(String line) {
        String formattedLine = line.replaceAll("[^’'a-zA-Z0-9]", " ").replaceAll("\\s+", " ").trim();

        // Create a document object
        CoreDocument document = new CoreDocument(formattedLine);
        // Annotate the document
        this.pipeline.annotate(document);
        return document;
    }

    /**
     * Retrieves the WordMap created during the preprocessing.
     *
     * @return the WordMap associating each word with a FileMap containing file names and positions
     */
    public WordMap getWordMap() {
        return wordMap;
    }

    /**
     * Retrieves the list of processed files.
     *
     * @return the list of processed files containing cleaned and lemmatized content
     */
    public ArrayList<String[]> getProcessedFiles() {
        return processedFiles;
    }

    /**
     * Retrieves the list of file names processed during preprocessing.
     *
     * @return the list of file names corresponding to the processed files
     */
    public ArrayList<String> getFileNames() {
        return fileNames;
    }
}