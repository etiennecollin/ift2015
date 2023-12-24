/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static String getBigram(CustomHashMap<String, CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>>> wordMap,
                                   ArrayList<String> processedFiles, ArrayList<String> fileNames, String word){
        CustomHashMap<ArrayList<String>, ArrayList<ArrayList<Integer>>> fileMap = wordMap.get(word);
        if (fileMap == null) {
            return null;
        }

        Map<String, Integer> bigramCounts = new HashMap<>();
        int totalOccurrences = 0;

        // Iterate over the file names containing the word
        for (ArrayList<String> filesContainingWord : fileMap.keySet()) {
            for (String fileCW : filesContainingWord) {
                int fileIndex = fileNames.indexOf(fileCW);
                if (fileIndex != -1) {
                    String text = processedFiles.get(fileIndex);
                    String[] words = text.split("\\s+");
                    for (int i = 0; i < words.length - 1; i++) {
                        if (word.equals(words[i])) {
                            totalOccurrences++;
                            String nextWord = words[i + 1];
                            bigramCounts.put(nextWord, bigramCounts.getOrDefault(nextWord, 0) + 1);
                        }
                    }
                }
            }
        }

        // Find the most probable bigram

        //If the current bigram's probability (probability) is greater than the maximum probability found so far
        // (maxProbability), the current bigram becomes the new most probable bigram.

        // If the current bigram's probability is equal to the maximum probability (probability == maxProbability),
        // the method checks the lexicographic order of the current bigram's second word (entry.getKey())
        // with the previously identified most probable word (mostProbableWord). The compareTo method returns a
        // negative value if entry.getKey() is lexicographically smaller than mostProbableWord. In such a case,
        // the current bigram's second word replaces the previous one as the most probable word.

        String mostProbableWord = null;
        double maxProbability = 0.0;
        for (Map.Entry<String, Integer> entry : bigramCounts.entrySet()) {
            double probability = (double) entry.getValue() / totalOccurrences;
            if (probability > maxProbability || (probability == maxProbability && (mostProbableWord == null || entry.getKey().compareTo(mostProbableWord) < 0))) {
                maxProbability = probability;
                mostProbableWord = entry.getKey();
            }
        }
        return mostProbableWord;
    }

    // get TF-IDF
    public static double getTFIDF(int termFrequency, int documentFrequency, int totalDocuments) {
        // TODO: implement
        return 0;
    }
}
