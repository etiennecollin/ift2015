/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */
import java.util.ArrayList;
import java.util.Map;

public class Utils {
    public static CustomHashMap<String, Integer> getBigrams(WordMap wordMap, ArrayList<String> processedFiles, ArrayList<String> fileNames, String word) {
        FileMap fileMap = wordMap.get(word);

        // If the word is not in the wordMap, return null
        if (fileMap == null) {
            return null;
        }

        // Store the possible bigrams and their occurrences
        CustomHashMap<String, Integer> bigrams = new CustomHashMap<>();

        // Iterate over the file names containing the word
        for (Map.Entry<ArrayList<String>, ArrayList<ArrayList<Integer>>> fileMapEntry : fileMap.entrySet()) {
            ArrayList<String> files = fileMapEntry.getKey();
            ArrayList<ArrayList<Integer>> filesPositions = fileMapEntry.getValue();
            for (String file : files) {
                // Get the index of the file in the fileNames list
                int fileIndex = fileNames.indexOf(file);

                // If the file is not in the fileNames list, skip it
                if (fileIndex == -1) {
                    continue;
                }

                // Get the file content
                String[] fileContent = processedFiles.get(fileIndex).split("\\s+");

                // Get the position of the word in the file using the fileMap
                ArrayList<Integer> positions = filesPositions.get(files.indexOf(file));
                for (int position : positions) {
                    // If the word is not the last word in the file, get the next word
                    if (position < fileContent.length - 1) {
                        String nextWord = fileContent[position + 1];
                        bigrams.put(nextWord, bigrams.getOrDefault(nextWord, 0) + 1);
                    }
                }
            }
        }

        // Return the bigrams
        return bigrams;
    }

    public static String getMostProbableBigram(CustomHashMap<String, Integer> bigrams) {
        // If there are no bigrams, return null
        if (bigrams == null) {
            return null;
        }

        // Get the total number of occurrences of the bigrams
        int totalOccurrences = 0;
        for (int occurrence : bigrams.values()) {
            totalOccurrences += occurrence;
        }

        String mostProbableWord = null;
        double maxProbability = 0.0;
        for (Map.Entry<String, Integer> entry : bigrams.entrySet()) {
            double probability = (double) entry.getValue() / totalOccurrences;

            // Get the most probable word. If two words have the same probability, the word that comes first in
            // lexicographic order is chosen.
            if (probability > maxProbability || (probability == maxProbability && (mostProbableWord == null || entry.getKey().compareTo(mostProbableWord) < 0))) {
                maxProbability = probability;
                mostProbableWord = entry.getKey();
            }
        }

        return mostProbableWord;
    }

    // get TF-IDF
    public static CustomHashMap<String, Double> getTFIDFs(WordMap wordMap, ArrayList<String> processedFiles, ArrayList<String> fileNames, String words) {

        CustomHashMap<String, Double> scores = new CustomHashMap<>();
        String[] queryWords = words.split("\\s+");

        for (String word : queryWords) {
            // If the word is not in the wordMap, skip it
            if (!wordMap.containsKey(word)) {
                continue;
            }

            // If the word is in the wordMap, get the TF-IDF score
            FileMap fileMap = wordMap.get(word);

            // Get the file frequency of the word
            int fileFrequency = fileMap.keySet().size();
            double idf = 1 + Math.log((1.0 + processedFiles.size()) / (1.0 + fileFrequency));

            // Get the TF-IDF score for each file
            for (Map.Entry<ArrayList<String>, ArrayList<ArrayList<Integer>>> fileMapEntry : fileMap.entrySet()) {
                ArrayList<String> fileNamesList = fileMapEntry.getKey();
                ArrayList<ArrayList<Integer>> positionsList = fileMapEntry.getValue();

                // Iterate over each file which contains the word
                for (int i = 0; i < fileNamesList.size(); i++) {
                    // Get the file name and its index in the fileNames list
                    String fileName = fileNamesList.get(i);
                    int fileIndex = fileNames.indexOf(fileName);

                    // If the file is not in the fileNames list, skip it
                    if (fileIndex == -1) {
                        continue;
                    }

                    // Get the number of occurrences of the word in the file and the total number of words in the file
                    int occurrences = positionsList.get(i).size();
                    int wordsInFile = processedFiles.get(fileIndex).split("\\s+").length;

                    double tf = occurrences / (double) wordsInFile;

                    double tfidf = tf * idf;
                    scores.put(fileName, scores.getOrDefault(fileName, 0.0) + tfidf);
                }
            }
        }

        // Return the TF-IDF scores
        return scores;
    }

    public static String getMostRelevantFile(CustomHashMap<String, Double> scores) {
        // If there are no scores, return null
        if (scores == null) {
            return null;
        }

        String mostRelevantFile = null;
        double highestScore = 0.0;

        // Get the most relevant file. If two documents have the same TF-IDF, the document with the name
        // that comes first in lexicographic order is chosen.
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            if (entry.getValue() > highestScore || (entry.getValue() == highestScore && (mostRelevantFile == null || entry.getKey().compareTo(mostRelevantFile) < 0))) {
                highestScore = entry.getValue();
                mostRelevantFile = entry.getKey();
            }
        }

        return mostRelevantFile;
    }

    /**
     * Returns a mapping of words to their positions in a string.
     *
     * @param fileContent The content of the file as a single string.
     *
     * @return A map where each key is a word, and the value is a list of positions of that word.
     */
    public static CustomHashMap<String, ArrayList<Integer>> positionalize(String fileContent) {
        CustomHashMap<String, ArrayList<Integer>> wordPositions = new CustomHashMap<>();
        String[] words = fileContent.split("\\W+");

        int position = 0;
        for (String word : words) {
            if (!word.isEmpty()) {
                wordPositions.putIfAbsent(word, new ArrayList<>());
                wordPositions.get(word).add(position);
            }
            position++;
        }
        return wordPositions;
    }

    // This method calculates the edit distance (Levenshtein distance) between two strings.
    // Implementation from https://www.geeksforgeeks.org/edit-distance-dp-5/
    public static int editDistance(String str1, String str2) {
        // Get the lengths of the two input strings.
        int m = str1.length();
        int n = str2.length();

        // Initialize an array to store the current row of edit distances.
        int[] curr = new int[n + 1];

        // Initialize the first row with values 0 to n.
        for (int j = 0; j <= n; j++) {
            curr[j] = j;
        }

        int previous;
        for (int i = 1; i <= m; i++) {
            // Store the value of the previous row's first column.
            previous = curr[0];
            curr[0] = i;

            for (int j = 1; j <= n; j++) {
                // Store the current value before updating it.
                int temp = curr[j];

                // Check if the characters at the current positions in str1 and str2 are the same.
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    // If they are the same, no additional cost is incurred.
                    curr[j] = previous;
                } else {
                    // If the characters are different, calculate the minimum of three operations:
                    // 1. Deletion (previous value)
                    // 2. Insertion (current row's previous value)
                    // 3. Substitution (diagonal previous value)
                    curr[j] = 1 + Math.min(Math.min(previous, curr[j - 1]), curr[j]);
                }
                // Update the previous value to the stored temporary value.
                previous = temp;
            }
        }
        // The value in the last cell of the current row represents the edit distance.
        return curr[n];
    }
}
