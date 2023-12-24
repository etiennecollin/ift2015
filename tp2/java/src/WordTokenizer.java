/*
 * Copyright (c) 2023. Etienne Collin #2038029, Emiliano Aviles #20178127
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordTokenizer {

    /**
     * Tokenizes the given text and returns a map of words to their positions.
     *
     * @param fileContent The content of the file as a single string.
     * @return A map where each key is a word, and the value is a list of positions of that word.
     */
    public Map<String, ArrayList<Integer>> tokenize(String fileContent) {
        Map<String, ArrayList<Integer>> wordPositions = new HashMap<>();
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
}
