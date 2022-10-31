package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class TextDocument {
    public TextDocument(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private int id;

    private String text = "";
    private int countWords;

    public String getText() {
        return text;
    }

    public void setTextifEx(String text) {
        String st = this.text;
        this.text = st + text;
    }

    public int getCountWords() {
        return countWords;
    }

    public Map<String, Double> getAppearance() {
        return appearance;
    }

    private Map<String, Double> appearance = new LinkedHashMap<>();


    public void apper(){
        //Map<Character, Integer> appearance = new LinkedHashMap<>();

        String[] wtspace = text.split(" ");
        String[] stopwords = {"and", "for", "from", "are", "was", "were", "its", "the", "not"};

        for (String word: wtspace) {
            word = word.replaceAll("[^a-zA-Z]","");

            for (String el: stopwords) {
                if (el.equals(word)){
                    word = "";
                    break;
                }
            }

            if (word.length() >= 3) {
                countWords++;
                if(appearance.containsKey(word)){
                    appearance.replace(word, appearance.get(word) + 1.0);
                } else {
                    appearance.put(word, 1.0);
                }
            }

        }

    }



}
