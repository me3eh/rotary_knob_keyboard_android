package com.dama.keyboardbase;

import android.content.res.Resources;

import com.dama.customkeyboardbase.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionarySuggestions {
    private final TrieNode root;
    private final Map<Character, String> t9Map_3Row;
    private final Map<Character, String> t9Map_4Row;
    private List<String> dictionary;
    // Constructor
    public DictionarySuggestions(Resources resources) {

        root = new TrieNode();
        dictionary = Arrays.asList(resources.getStringArray(R.array.dictionary));
        for(String s : dictionary){
            addWord(s);
        }
        t9Map_3Row = new HashMap<>();
        t9Map_3Row.put('0', "qaz");
        t9Map_3Row.put('1', "wsx");
        t9Map_3Row.put('2', "edc");
        t9Map_3Row.put('3', "rfv");
        t9Map_3Row.put('4', "tgb");
        t9Map_3Row.put('5', "yhn");
        t9Map_3Row.put('6', "ujm");
        t9Map_3Row.put('7', "ik");
        t9Map_3Row.put('8', "olp");
        t9Map_4Row = new HashMap<>();
        t9Map_4Row.put('0', "qazw");
        t9Map_4Row.put('1', "sxed");
        t9Map_4Row.put('2', "crfv");
        t9Map_4Row.put('3', "tgby");
        t9Map_4Row.put('4', "hnuj");
        t9Map_4Row.put('5', "miko");
        t9Map_4Row.put('6', "pl");
    }

    // TrieNode class
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }

    // Add a word to the dictionary
    public void addWord(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    // Get words based on T9 key sequence
    public List<String> getWords(String digits, int version) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.isEmpty() || digits.equals("")) return result;

        findWords(root, digits, 0, new StringBuilder(), result, version);
        return result;
    }

    // Helper method to find words recursively
    private void findWords(TrieNode node, String digits, int index, StringBuilder currentWord, List<String> result, int version) {
        Map<Character, String> t9 = t9Map_4Row;
        if(version == 3)
            t9 = t9Map_3Row;
//        if(/version == 3) {
//        }
        if (index == digits.length()) {
            if (node.isEndOfWord) {
                result.add(currentWord.toString());
            }
            return;
        }

        char digit = digits.charAt(index);
        if (!t9.containsKey(digit)) return;

        for (char ch : t9.get(digit).toCharArray()) {
            TrieNode child = node.children.get(ch);
            if (child != null) {
                currentWord.append(ch);
                findWords(child, digits, index + 1, currentWord, result, version);
                currentWord.deleteCharAt(currentWord.length() - 1); // Backtrack
            }
        }
    }

    // Main method for testing
//    public static void main(String[] args) {
//        T9Dictionary t9Dictionary = new T9Dictionary();
//        t9Dictionary.addWord("hello");
//        t9Dictionary.addWord("hi");
//        t9Dictionary.addWord("tree");
//        t9Dictionary.addWord("used");
//
//        List<String> words = t9Dictionary.getWords("8733");
//        for (String word : words) {
//            System.out.println(word); // Output: tree, used
//        }
//    }
}