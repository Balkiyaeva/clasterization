package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static int idDoc = 0;
    private static Double numOfDoc;

    private static ArrayList<TextDocument> textDocuments;

    private static Map<String, Double> allWords = new LinkedHashMap<>();

    public static List<List<Double>> temporaryVectors = new ArrayList<>();
    public static List<List<Double>> Vectors = new ArrayList<>();
    public static List<List<Double>> randomVectors = new ArrayList<>();

    public static List<List<Double>> distances = new ArrayList<>();

    public static List<List<Double>> centroid1Vectors = new ArrayList<>();
    public static List<List<Double>> centroid2Vectors = new ArrayList<>();
    public static List<List<Double>> centroid3Vectors = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        setTextDocuments(openread());

        setNumOfDoc((double) getTextDocuments().size());


        for (TextDocument text: textDocuments) {
            for (String word: text.getAppearance().keySet()) {
                if(allWords.containsKey(word)){
                    allWords.replace(word, allWords.get(word) + text.getAppearance().get(word));
                } else {
                    allWords.put(word, text.getAppearance().get(word));
                }
            }
        }

        for(Map.Entry<String, Double> word : allWords.entrySet()) {
            List<Double> vector = new ArrayList<>();
            Double IDF_word = IDF(word.getKey());
            for(int i = 1; i <= numOfDoc; i++) {
                Double TF_word = TF(i, word.getKey());
                vector.add(IDF_word * TF_word);
            }
            temporaryVectors.add(vector);
        }

        for (int j = 0; j < numOfDoc; j++) {
            List<Double> vectors = new ArrayList<>();
            for (List<Double> temporaryVector : temporaryVectors) {
                vectors.add(temporaryVector.get(j));
            }
            Vectors.add(vectors);
        }


        createRandomVectors(3);

        calculateDistances();
        centroid1Vectors = new ArrayList<>();
        centroid2Vectors = new ArrayList<>();
        centroid3Vectors = new ArrayList<>();
        attachVectorsToCentroid();

        String output = "Cluster_1:" + centroid1Vectors.size() +
                "\nCluster_2:" + centroid2Vectors.size() +
                "\nCluster_3:" + centroid3Vectors.size();

        System.out.println(output);

        System.out.println("______________________________________________________________");


        while (true) {
            findAverageDistanceForCentroid1();
            findAverageDistanceForCentroid2();
            findAverageDistanceForCentroid3();

            calculateDistances();
            centroid1Vectors = new ArrayList<>();
            centroid2Vectors = new ArrayList<>();
            centroid3Vectors = new ArrayList<>();
            attachVectorsToCentroid();

            String noutput = "Cluster_1:" + centroid1Vectors.size() +
                    "\nCluster_2:" + centroid2Vectors.size() +
                    "\nCluster_3:" + centroid3Vectors.size();

            System.out.println(noutput);

            if (noutput.equals(output)) break;
            output = noutput;
        }

    }

    public static ArrayList<TextDocument> openread() throws IOException {
        Path path = Paths.get("/Users/amira/Downloads/task_1");
        ArrayList<TextDocument> texts = new ArrayList<>();

        Files.walk(path).forEach(path1 -> {
            File file = path1.toFile();
            if (file.isFile()) {

                idDoc++;
                TextDocument textDocument = new TextDocument(idDoc);
                texts.add(textDocument);
                Scanner sc = null;
//                System.out.println(path1);
                try {
                    sc = new Scanner(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (true) {
                    assert sc != null;
                    if (!sc.hasNextLine()){
                        textDocument.apper();
                        break;
                    }
                    else {
                        textDocument.setTextifEx(sc.nextLine().toLowerCase());
                    }
                }
            }
        });
        System.out.println("");
        return texts;
    }

    public static Double IDF(String word){
        Double numOfDoc = getNumOfDoc();
        Double DFI = getNumOfDoc_WordAppearance(word);
        return Math.log(numOfDoc / DFI);
    }

    public static Double TF(int id, String word){

        Double wordInDoc = getWordAppearanceInDoc(id, word);
        Double totalWords = (double) getTotalWordAppearanceInDoc(id);

        return ( wordInDoc / totalWords );
    }

    public static void createRandomVectors(int count) {
        for (int j = 0; j < count; j++) {
            List<Double> randomVector = new ArrayList<>();
            for (int i = 0; i < allWords.size(); i++) {
                List<Double> eachWordValues = temporaryVectors.get(i);

                Double start = 999.0;
                Double end = -999.9;
                for (Double value : eachWordValues) {
                    if (value >= end) {
                        end = value;
                    }
                    if (value <= start) {
                        start = value;
                    }
                }
                Random r = new Random();
                Double randomValue = r.nextDouble();
                Double result = start + (randomValue * (end - start));
                randomVector.add(result);
            }
            randomVectors.add(randomVector);
        }

    }

    public static void calculateDistances() {
        for (List<Double> vector : Vectors) {
            Double distance = 0.0;
            List<Double> tempDistance = new ArrayList<>();

            for (List<Double> randomVector : randomVectors) {
                for (int j = 0; j < vector.size(); j++) {
                    distance += Math.pow(vector.get(j) - randomVector.get(j), 2);
                }
                distance = Math.sqrt(distance);
                tempDistance.add(distance);
                distance = 0.0;
            }

            distances.add(tempDistance);
        }
    }

    public static void attachVectorsToCentroid() {
        for (int i = 0; i < Vectors.size(); i++) {
            if (distances.get(i).get(0) <= distances.get(i).get(1) && (distances.get(i).get(0) <= distances.get(i).get(2))) {
                centroid1Vectors.add(Vectors.get(i));
            } else if (distances.get(i).get(1) <= distances.get(i).get(0) && (distances.get(i).get(1) <= distances.get(i).get(2))) {
                centroid2Vectors.add(Vectors.get(i));
            }
            else {
                centroid3Vectors.add(Vectors.get(i));
            }
        }
    }

    public static void findAverageDistanceForCentroid1(){
        Double avg = 0.0;
        for (int j = 0; j < allWords.size(); j++) {
            for (int i = 0; i < centroid1Vectors.size(); i++) {
                avg += centroid1Vectors.get(i).get(j);
            }
            avg /= centroid1Vectors.size();
            randomVectors.get(0).set(j, avg);
            avg = 0.0;
        }
    }

    public static void findAverageDistanceForCentroid2(){
        Double avg = 0.0;
        for (int j = 0; j < allWords.size(); j++) {
            for (int i = 0; i < centroid2Vectors.size(); i++) {
                avg += centroid2Vectors.get(i).get(j);
            }
            avg /= centroid2Vectors.size();
            randomVectors.get(1).set(j, avg);
            avg = 0.0;
        }
    }

    public static void findAverageDistanceForCentroid3(){
        Double avg = 0.0;
        for (int j = 0; j < allWords.size(); j++) {
            for (int i = 0; i < centroid3Vectors.size(); i++) {
                avg += centroid3Vectors.get(i).get(j);
            }
            avg /= centroid3Vectors.size();
            randomVectors.get(2).set(j, avg);
            avg = 0.0;
        }
    }

    public static Double getNumOfDoc() {
        return numOfDoc;
    }

    public static void setNumOfDoc(Double numOfDoc) {
        Main.numOfDoc = numOfDoc;
    }

    public static ArrayList<TextDocument> getTextDocuments() {
        return textDocuments;
    }

    public static void setTextDocuments(ArrayList<TextDocument> textDocuments) {
        Main.textDocuments = textDocuments;
    }

    public static Double getNumOfDoc_WordAppearance(String word){
        Double totalAppearance = 0.0;
        for (TextDocument text: textDocuments) {
            if (text.getAppearance().containsKey(word)){
                totalAppearance++;
            }
        }
        return totalAppearance;

    }

    public static Double getWordAppearanceInDoc(int id, String word){
        Double totalAppearance = 0.0;
        for (TextDocument text: textDocuments) {
           if (text.getId() == id){
               totalAppearance = text.getAppearance().get(word);
               break;
           }
        }
        if (totalAppearance == null) totalAppearance = 0.0;
        return totalAppearance;
    }

    public static int getTotalWordAppearanceInDoc(int id){
        int totalAppearance = 0;
        for (TextDocument text: textDocuments) {
            if (text.getId() == id){
                totalAppearance = text.getCountWords();
                break;
            }
        }
        return totalAppearance;
    }

}
