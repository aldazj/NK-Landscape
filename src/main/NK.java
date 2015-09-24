package main;

import utils.FitnessObject;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Created by aldazj on 21.09.15.
 */
public class NK {

    private int N, K;
    private String x_max;
    private LinkedHashMap<String, Integer> F;
    private ArrayList<FitnessObject> best_fitness;

    public NK(int N, int K, String x_max) {
        this.N = N;
        this.K = K;
        this.x_max = x_max;
        F = new LinkedHashMap<String, Integer>();
        Utilities.compute_NBOccurrence(F, K, x_max);
    }

    /**
     * Fitness fonction
     * Use the occurence frequencies of the corresponding sub-sequences in x_max
     */
    private double computeFitness(String neighbour_s){
        double fitness = 0;
        for (int i = 0; i < neighbour_s.length()-K; i++) {
            String key = neighbour_s.substring(i, i+K+1);
            fitness += F.get(key);
        }
        return fitness;
    }

    /**
     * Fitness fonction 2
     * Use the occurence frequencies of the corresponding sub-sequences in x_max
     */
    private double computeFitness2(String neighbour_s){
        double fitness = 0.0;
        for (int i = 0; i < neighbour_s.length()-K; i++) {
            String key = neighbour_s.substring(i, i+K+1);
            fitness += F.get(key);
        }
        return fitness;
    }

    /**
     * Deterministic Hill-Climbing
     */
    public ArrayList<FitnessObject> deterministic_HC(int timesAlgo){
        //Init solution
        System.out.println("INIT x_max:"+x_max);
        int[] currentSol = Utilities.randomArrayBinaire(N);
        best_fitness = new ArrayList<FitnessObject>();

        //Execute j times the Algorithme
        for (int j = 0; j < timesAlgo; j++) {
            double highest_fitness = 0.0;
            boolean firstCandidat = true;
            String current_max = "";
            ArrayList<FitnessObject> bestCandidats = new ArrayList<FitnessObject>();

            //Generate neighbors and compute bestCandidats
            for (int i = 0; i < currentSol.length; i++) {
                int[] neighbour = Utilities.neighbour(currentSol, i);
                String neighbour_s = Arrays.toString(neighbour).replaceAll(", |\\[|\\]", "");
//                double fitness = computeFitness(neighbour_s);
                double fitness = computeFitness2(neighbour_s);

                if(highest_fitness < fitness){
                    highest_fitness = fitness;
                    current_max = neighbour_s;
                    if(firstCandidat){
                        firstCandidat = false;
                    }else{
                        bestCandidats.clear();
                    }
                    bestCandidats.add(new FitnessObject(highest_fitness, current_max));
                }else if(highest_fitness == fitness){
                    if(!firstCandidat){
                        bestCandidats.add(new FitnessObject(fitness, neighbour_s));
                    }
                }
            }

            //Get the best solution
            FitnessObject best;
            if(bestCandidats.size() > Utilities.MINIMUM_SOLUTION){
                best = Utilities.randomBestCandidat(bestCandidats);
            }else{
                best = bestCandidats.get(0);
            }
            //Add new best Solutions
            best_fitness.add(best);
            currentSol = Utilities.string_to_intArray(best.getSequence());
        }
        return best_fitness;
    }

    private ArrayList<FitnessObject> probabilistic_HC(int timesAlgo){
        //Init solution
        System.out.println("INIT x_max:"+x_max);
        int[] currentSol = Utilities.randomArrayBinaire(N);
        best_fitness = new ArrayList<FitnessObject>();

        //Execute j times the Algorithme
        for (int j = 0; j < timesAlgo; j++) {

            ArrayList<FitnessObject> neighbours = new ArrayList<FitnessObject>();
//            System.out.println("current:"+Arrays.toString(currentSol));
            double fitness_total = 0.0;
            //Generate neighbors and compute bestCandidats
            for (int i = 0; i < currentSol.length; i++) {
                int[] neighbour = Utilities.neighbour(currentSol, i);
                String neighbour_s = Arrays.toString(neighbour).replaceAll(", |\\[|\\]", "");
//                double fitness = computeFitness(neighbour_s);
                double fitness = computeFitness2(neighbour_s);
                fitness_total += fitness;
                neighbours.add(new FitnessObject(fitness,neighbour_s));
    //            System.out.println(neighbour_s+":"+fitness);
            }

//            Utilities.printBestSolutions(neighbours);
            //For each neighbour we compute the probability to be selected
            for (int i = 0; i < neighbours.size(); i++) {
                neighbours.get(i).setFitness(neighbours.get(i).getFitness()/fitness_total);
            }
//            System.out.println("Apres");
//            Utilities.printBestSolutions(neighbours);
//            double tmp = 0;
//            for (int i = 0; i < neighbours.size(); i++) {
//                tmp += neighbours.get(i).getFitness();
//            }
//            System.out.println("verif: "+tmp);

            //Calcule les probabilités accumulatives
            String[] probaAccumulatives = new String[neighbours.size()];
            double probaAccumulValue = 0;
            double delta = 1E-10;
            double tmpAccumulative = 0;
            for (int i = 0; i < neighbours.size(); i++) {
                probaAccumulValue += neighbours.get(i).getFitness();
                probaAccumulatives[i] = tmpAccumulative+";"+probaAccumulValue;
                tmpAccumulative = probaAccumulValue+delta;
            }
//            System.out.println("Proba Accumulative");
//            System.out.println(Arrays.toString(probaAccumulatives));
            double rand = Math.random();
//            System.out.println("rand: "+rand);
            int index_NewSolution = Utilities.recherche_dichotomique(rand, probaAccumulatives);
//            System.out.println("index next:"+index_NewSolution);
            FitnessObject bestProbabilitic = neighbours.get(index_NewSolution);
//            System.out.println("----- NEW SOlution -------");
//            System.out.println(bestProbabilitic.getSequence()+":"+bestProbabilitic.getFitness());
            best_fitness.add(bestProbabilitic);
            currentSol = Utilities.string_to_intArray(bestProbabilitic.getSequence());
        }
        return best_fitness;
    }

    private ArrayList<FitnessObject> run(String method, int timesAlgo){
        if(method.equals(Utilities.DETERMINISTIC_HC)){
            return deterministic_HC(timesAlgo);
        }else{
            return probabilistic_HC(timesAlgo);
        }
    }

    public static void main(String[] args) {
        int [] K = {0,1,2,3,4,5};
        String[] method = {Utilities.DETERMINISTIC_HC, Utilities.PROBABILISTIC_HC};
        String x_max = "100100100100100100100";
        ArrayList<FitnessObject> bestSolutions;
        int timesAlgo = 50;
//        String x_max = "1001001";
        int N = x_max.length();
        NK nkLanscape = new NK(N, K[3], x_max);
        bestSolutions = nkLanscape.run(method[1], timesAlgo);
        Utilities.printBestSolutions(bestSolutions);
        double[] stabilities = Utilities.analyseStability(bestSolutions, x_max);
        System.out.println(Arrays.toString(stabilities));

//        System.out.println("---- Stability ----");
//        int[][] stability = Utilities.analyseStabilityBestSolutions(bestSolutions);
//        Utilities.printMatrix(stability);
    }
}
