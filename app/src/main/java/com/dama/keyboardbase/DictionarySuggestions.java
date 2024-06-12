package com.dama.keyboardbase;

import android.content.res.Resources;
import android.util.Log;

import com.dama.customkeyboardbase.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DictionarySuggestions {
    private List<String> dictionary;
    String[] suggestions;
    public DictionarySuggestions(Resources resources){
        dictionary = Arrays.asList(resources.getStringArray(R.array.dictionary));
    }

    public String[] getSuggestions() {
        return suggestions;
    }

    public void updateSuggestions(String word) {
        // Simple static suggestion example, replace with dynamic logic

        String[] new_suggestions = findSuggestions(word);
        Log.d("DictionarySuggestions", String.valueOf(new_suggestions.length));
        if(new_suggestions.length == 0)
            suggestions = new String[]{"", "", ""};
        else if(new_suggestions.length == 1)
            suggestions = new String[]{new_suggestions[0], "", ""};
        else if(new_suggestions.length == 2)
            suggestions = new String[]{new_suggestions[0], new_suggestions[1], ""};
        else
            suggestions = new_suggestions;
        Log.d("DictionarySuggestions", Arrays.toString(suggestions));
    }

    private String[] findSuggestions(String input) {
        List<String> results = new ArrayList<>();
        int maxDistance = 1; // max Levenshteina

        for (String word : dictionary) {
            if(input.length() > word.length())
                continue;
            if(word.length() - input.length() > 1)
                continue;

            int distance = calculateLevenshteinDistance(input, word);
            if (distance <= maxDistance) {
                results.add(word);
            }
            if (results.size() == 3) {
                break;
            }
        }

        return results.toArray(new String[0]);
    }

    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] distance = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                distance[i][j] = Math.min(Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1), distance[i - 1][j - 1] + cost);
            }
        }

        return distance[s1.length()][s2.length()];
    }
}
