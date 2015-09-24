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

    public static  int[] string_to_intArray(String array){
        int[] intArray = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            intArray[i] = Character.digit(array.charAt(i), 10);
        }
        return intArray;
    }

    public static void printMatrix(int[][] distances){
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances[0].length; j++) {
                System.out.print(distances[i][j]+" ");
            }
            System.out.println("");
        }
    }

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

    public static double[] analyseStability(ArrayList<FitnessObject> bestSolutions, String x_max){
        double[] stabilities = new double[bestSolutions.size()];
        for (int i = 0; i < bestSolutions.size(); i++) {
            stabilities[i] = d_hamming(string_to_intArray(bestSolutions.get(i).getSequence()), string_to_intArray(x_max));
        }
        return stabilities;
    }

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
        displayHashMap(F);
    }

    public static int randomIndexBetween(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    public static FitnessObject randomBestCandidat(ArrayList<FitnessObject> bestCandidats){
        int index = randomIndexBetween(0, bestCandidats.size()-1);
        return bestCandidats.get(index);
    }

    public static void displayHashMap(LinkedHashMap<String, Integer> F){
        for(Map.Entry<String, Integer> entry : F.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

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
        int start = 0;
        int end = probaAccumulatives.length;
        boolean found = false;
        int pointer;
        do{
            pointer = ((start+end)/2);
            if(is_in_probaAccumulatives(value, probaAccumulatives[pointer])){
                found = true;
            }else if(is_greater_than(value, probaAccumulatives[pointer])){
                start = pointer+1;
            }else if(is_less_than(value, probaAccumulatives[pointer])){
                end = pointer-1;
            }
        }while (found == false && start <= end);

        if(found){
            return pointer;
        }else{
            return -1;
        }
    }
}
