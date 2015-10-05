package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by aldazj on 21.09.15.
 */
public class Utilities {

    public static String DETERMINISTIC_HC = "deterministic HC";
    public static String PROBABILISTIC_HC = "probabilistic HC";
    public static int MINIMUM_SOLUTION = 1;

    private Utilities(){ }

    /**
     * Create an array randomly
     * @param size : array size
     * @return: array
     */
    public static int[] randomArrayBinaire(int size){
        int[] my_array = new int[size];
        for (int i = 0; i < size; i++) {
            my_array[i] = (int)(Math.random()*2);
        }
        return  my_array;
    }

    /**
     * Find a neighbour
     * @param current
     * @param pos
     * @return
     */
    public static int[] neighbour(int[] current, int pos){
        int[] neighbour = Arrays.copyOf(current, current.length);
        int value = (neighbour[pos] == 0) ? 1:0;
        neighbour[pos] = value;
        return neighbour;
    }

    /***
     * Convert a string to an array
     * Ex: "1001" -> [1, 0, 0, 1]
     * @param array
     * @return
     */
    public static  int[] string_to_intArray(String array){
        int[] intArray = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            intArray[i] = Character.digit(array.charAt(i), 10);
        }
        return intArray;
    }

    /**
     * Compute Hamming distance -> differences between two arrays
     * @param array1 : array1
     * @param array2 : array2
     * @return
     */
    public static int d_hamming(int[] array1, int[] array2){
        int distance = 0;
        if(array1.length == array2.length){
            for (int i = 0; i < array1.length; i++) {
                if(array1[i] != array2[i]){
                    distance += 1;
                }
            }
        }
        return  distance;
    }

    /**
     * Analyse the statibity about our system.
     * We compute the hamming distance between our solutions and x_max
     * @param bestSolutions : Best solutions
     * @param x_max : pattern to found
     * @return
     */
    public static double[] analyseStability(ArrayList<FitnessObject> bestSolutions, String x_max){
        double[] stabilities = new double[bestSolutions.size()];
        for (int i = 0; i < bestSolutions.size(); i++) {
            stabilities[i] = d_hamming(string_to_intArray(bestSolutions.get(i).getSequence()), string_to_intArray(x_max));
        }
        return stabilities;
    }

    /**
     * Calcule "de combien une solution diffère avec toutes les autres solutions obtenues"
     * @param bestSolutions
     * @return
     */
    public static int[][] analyseStabilityBestSolutions(ArrayList<FitnessObject> bestSolutions){
        int[][] stabilities = new int[bestSolutions.size()][bestSolutions.size()];
        for (int i = 0; i < bestSolutions.size(); i++) {
            for (int j = 0; j < bestSolutions.size(); j++) {
                int[] x = string_to_intArray(bestSolutions.get(i).getSequence());
                int[] y = string_to_intArray(bestSolutions.get(j).getSequence());
                stabilities[i][j] = d_hamming(x, y);
            }
        }
        return  stabilities;
    }

    /**
     * Déssine la rugosité de système
     * @param hamming_matrix
     * @return
     */
    public static double[] showRugged(int[][] hamming_matrix){
        LinkedHashMap<Integer, Integer> hammingRugged = new LinkedHashMap<Integer, Integer>();
        for (int i = 0; i < hamming_matrix.length; i++) {
            for (int j = 0; j < hamming_matrix[0].length; j++) {
                if(i != j){
                    if(hammingRugged.containsKey(hamming_matrix[i][j])){
                        hammingRugged.put(hamming_matrix[i][j], hammingRugged.get(hamming_matrix[i][j])+1);
                    }else{
                        hammingRugged.put(hamming_matrix[i][j], 1);
                    }
                }
            }
        }

        int bigIndex = 0;
        for(Map.Entry<Integer, Integer> entry : hammingRugged.entrySet()){
            if(bigIndex < entry.getKey()){
                bigIndex = entry.getKey();
            }
        }

        double[] rugged = new double[bigIndex+1];
        Arrays.fill(rugged, 0);
        for(Map.Entry<Integer, Integer> entry : hammingRugged.entrySet()){
            rugged[entry.getKey()] = (double)entry.getValue();
//            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        return rugged;
    }

    /**
     *
     * @param F
     * @param K
     * @param x_max
     */
    public static void compute_NBOccurrence(LinkedHashMap<String, Integer> F, int K, String x_max){
        int dimension = (int)Math.pow(2,K+1);
        for (int i = 0; i < dimension; i++) {
            String binary = Integer.toBinaryString(i);
            if(binary.length() < (K+1)){
                binary = String.format("%0"+(K+1)+"d", Integer.parseInt(binary));
            }
            F.put(binary, 0);
        }
        for (int i = 0; i < x_max.length()-K; i++) {
            String key = x_max.substring(i,i+K+1);
            F.put(key, F.get(key)+1);
        }
//        displayHashMap(F);
    }

    /**
     * Return a value between two intervals
     * @param min: minimum
     * @param max: maximum
     * @return : random interval
     */
    public static int randomIndexBetween(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    /***
     * Get a random best candidat if there are many
     * @param bestCandidats
     * @return
     */
    public static FitnessObject randomBestCandidat(ArrayList<FitnessObject> bestCandidats){
        int index = randomIndexBetween(0, bestCandidats.size()-1);
        return bestCandidats.get(index);
    }

    /**
     * Display data saved in a HashMap: nb de repetitions between x_max and a current solution
     * @param F
     */
    public static void displayHashMap(LinkedHashMap<String, Integer> F){
        for(Map.Entry<String, Integer> entry : F.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    /***
     * Print best solutions
     * @param bests
     */
    public static void printBestSolutions(ArrayList<FitnessObject> bests){
        System.out.println("Best Solutions size: "+bests.size());
        for (int i = 0; i < bests.size(); i++) {
            System.out.println(i+")"+bests.get(i).getSequence()+":"+bests.get(i).getFitness());
        }
    }

    /**
     * Détermine si une valeur se trouve dans un interval de deux valeurs
     * Exemple:
     * Intervale sur la forme: "0.25;0.50"
     * @param value : valeur aléatoire
     * @param interval : interval
     * @return
     */
    private static boolean is_in_probaAccumulatives(double value, String interval){
        String[] myInterval = interval.split(";");
        double startInterval = Double.parseDouble(myInterval[0]);
        double endInterval = Double.parseDouble(myInterval[1]);
        return  value >= startInterval && value <= endInterval;
    }

    /**
     * Détermine si une valeur est plus grande que les deux valeurs existantes dans un interval
     * Exemple:
     * Intervale sur la forme: "0.25;0.50"
     * @param value : valeur aléatoire
     * @param interval : interval
     * @return
     */
    private static boolean is_greater_than(double value, String interval){
        String[] myInterval = interval.split(";");
        double startInterval = Double.parseDouble(myInterval[0]);
        double endInterval = Double.parseDouble(myInterval[1]);
        return  value > startInterval && value > endInterval;
    }

    /**
     * Détermine si une valeur est plus petite que les deux valeurs existantes dans un interval
     * Exemple:
     * Intervale sur la forme: "0.25;0.50"
     * @param value : valeur aléatoire
     * @param interval : interval
     * @return
     */
    private static boolean is_less_than(double value, String interval){
        String[] myInterval = interval.split(";");
        double startInterval = Double.parseDouble(myInterval[0]);
        double endInterval = Double.parseDouble(myInterval[1]);
        return  value < startInterval && value < endInterval;
    }

    /**
     * Recherche dichotonique. Nous division l'interval de recherche par deux
     * à chaque étape
     * @param value : valeur à trouver
     * @param probaAccumulatives : probabilités accumulatives
     * @return : l'évènement trouvé ou pas
     */
    public static int recherche_dichotomique(double value, String[] probaAccumulatives){
        LinkedHashMap<String, Integer> probas_init_index = analyseSolutions(probaAccumulatives);
        ArrayList<String> new_probaAccumulatives = deleteZerosProbabilities(probaAccumulatives);
        int start = 0;
        int end = new_probaAccumulatives.size();
        boolean found = false;
        String key = "";
        int pointer;
        do{
            pointer = ((start+end)/2);
            if(is_in_probaAccumulatives(value, new_probaAccumulatives.get(pointer))){
                found = true;
                key = new_probaAccumulatives.get(pointer);
            }else if(is_greater_than(value, new_probaAccumulatives.get(pointer))){
                start = pointer+1;
            }else if(is_less_than(value, new_probaAccumulatives.get(pointer))){
                end = pointer-1;
            }
        }while (found == false && start <= end);

        if(found){
            pointer = probas_init_index.get(key);
            return pointer;
        }else{
            return -1;
        }
    }

    /**
     * Select the the best solutions
     * @param bestTmpSolutions
     * @return
     */
    public static FitnessObject select_the_best(ArrayList<FitnessObject> bestTmpSolutions){
        FitnessObject best = null;
        double fitness = 0.0;
        for (int i = 0; i < bestTmpSolutions.size(); i++) {
            if(fitness < bestTmpSolutions.get(i).getFitness()){
                best = bestTmpSolutions.get(i);
                fitness = bestTmpSolutions.get(i).getFitness();
            }
        }
        return best;
    }

    /**
     * Compute the fitness average after an execution
     * @param bestSolutions
     * @return
     */
    public static double averageFitness(ArrayList<FitnessObject> bestSolutions){
        double average = 0.0;
        for (int i = 0; i < bestSolutions.size(); i++) {
            average += bestSolutions.get(i).getFitness();
        }
        return average/bestSolutions.size();
    }

    /**
     * Analyse solutions
     * @param probaAccumulatives
     * @return
     */
    public static LinkedHashMap<String, Integer> analyseSolutions(String[] probaAccumulatives){
        LinkedHashMap<String, Integer> probaAcc = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < probaAccumulatives.length; i++) {
            probaAcc.put(probaAccumulatives[i], i);
        }
        return probaAcc;
    }

    /**
     * When we dont have solutions we delete solutions where the probabilities is zero
     * @param probaAccumulatives
     * @return
     */
    public static ArrayList<String> deleteZerosProbabilities(String[] probaAccumulatives){
        ArrayList<String> proba_withoutZeros = new ArrayList<String>();
        for (int i = 0; i < probaAccumulatives.length; i++) {
            if(!probaAccumulatives[i].equals("0.0;0.0")){
                proba_withoutZeros.add(probaAccumulatives[i]);
            }
        }
        return proba_withoutZeros;
    }

    /**
     * Verify if a solution was founded
     * @param probaAccumulatives
     * @return
     */
    public static boolean no_solutionFounded(String[] probaAccumulatives){
        int counter = 0;
        for (int i = 0; i < probaAccumulatives.length; i++) {
            if(probaAccumulatives[i].equals("0.0;0.0")){
                counter += 1;
            }
        }
        if(counter == probaAccumulatives.length){
            return true;
        }
        return false;
    }

}
