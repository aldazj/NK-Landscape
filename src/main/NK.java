package main;

import utils.FitnessObject;
import utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;

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
     * We use the deterministic Hill-cLimbing algorithm
     */
    public ArrayList<FitnessObject> deterministic_HC(int timesAlgo){
        //Init solution
        System.out.println("INIT x_max:"+x_max+" Fitness: "+computeFitness(x_max));
        best_fitness = new ArrayList<FitnessObject>();

        //Execute j times the Algorithme
        for (int j = 0; j < timesAlgo; j++) {
            boolean fitnessStable = false;
            int nbVerifications = 0;
            ArrayList<FitnessObject> bestCandidats;
            double fitnessTmp = 0.0;
            FitnessObject best;
            int[] currentSol = Utilities.randomArrayBinaire(N);

            do{
                double highest_fitness = 0.0;
                boolean firstCandidat = true;
                String current_max = "";
                bestCandidats = new ArrayList<FitnessObject>();

                //Generate neighbors and compute bestCandidats
                for (int i = 0; i < currentSol.length; i++) {
                    int[] neighbour = Utilities.neighbour(currentSol, i);
                    String neighbour_s = Arrays.toString(neighbour).replaceAll(", |\\[|\\]", "");
                    double fitness = computeFitness(neighbour_s);

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
                if(bestCandidats.size() > Utilities.MINIMUM_SOLUTION){
                    best = Utilities.randomBestCandidat(bestCandidats);
                }else{
                    if(bestCandidats.size() > 0){
                        best = bestCandidats.get(0);
                    }else{
                        best = new FitnessObject(highest_fitness, current_max);
                    }
                }

                if(fitnessTmp < best.getFitness()){
                    fitnessTmp = best.getFitness();
                }else if(fitnessTmp == best.getFitness()){
                    nbVerifications += 1;
                    if (nbVerifications == 3){
                        fitnessStable = true;
                    }
                }
                //Update best new current solution
                currentSol = Utilities.string_to_intArray(best.getSequence());
            }while(!fitnessStable);

            //Add best solution
            best_fitness.add(best);
        }
        return best_fitness;
    }

    /**
     * We use the probabilistic Hill-cLimbing algorithm
     * @param timesAlgo
     * @return
     */
    private ArrayList<FitnessObject> probabilistic_HC(int timesAlgo){
        System.out.println("INIT x_max:"+x_max+" Fitness: "+computeFitness(x_max));
        best_fitness = new ArrayList<FitnessObject>();
        int iterationProba = 10;

        //Execute j times the Algorithme
        for (int j = 0; j < timesAlgo; j++) {
            //Init solution
            int[] currentSol = Utilities.randomArrayBinaire(N);
            ArrayList<FitnessObject> neighbours = null, bestTmpSolutions = null;
            FitnessObject bestProbabilitic = null;
            double fitness_total = 0.0;
            bestTmpSolutions = new ArrayList<FitnessObject>();
            boolean found = false;

            for (int k = 0; k < iterationProba; k++) {
                neighbours = new ArrayList<FitnessObject>();
                fitness_total = 0.0;

                //Generate neighbors and compute bestCandidats
                for (int i = 0; i < currentSol.length; i++) {
                    int[] neighbour = Utilities.neighbour(currentSol, i);
                    String neighbour_s = Arrays.toString(neighbour).replaceAll(", |\\[|\\]", "");
                    double fitness = computeFitness(neighbour_s);
                    fitness_total += fitness;
                    neighbours.add(new FitnessObject(fitness,neighbour_s));
                }

                //For each neighbour we compute the probability to be selected
                for (int i = 0; i < neighbours.size(); i++) {
                    neighbours.get(i).setFitness(neighbours.get(i).getFitness()/fitness_total);
                }

                //Calculates the accumulative probabilities
                String[] probaAccumulatives = new String[neighbours.size()];
                double probaAccumulValue = 0;
                double delta = 1E-10;
                double tmpAccumulative = 0;
                for (int i = 0; i < neighbours.size(); i++) {
                    if(Double.isNaN(neighbours.get(i).getFitness()) || neighbours.get(i).getFitness() == 0.0){
                        probaAccumulatives[i] = "0.0;0.0";
                    }else{
                        probaAccumulValue += neighbours.get(i).getFitness();
                        probaAccumulatives[i] = tmpAccumulative + ";" + probaAccumulValue;
                        tmpAccumulative = probaAccumulValue + delta;
                    }
                }

                //Verify if we have a solution
                if(Utilities.no_solutionFounded(probaAccumulatives)){
                    currentSol = Utilities.randomArrayBinaire(N);
                }else{
                    double random = Math.random();
                    //Update the new current solution
                    int index_NewSolution = Utilities.recherche_dichotomique(random, probaAccumulatives);
                    bestProbabilitic = neighbours.get(index_NewSolution);
                    bestTmpSolutions.add(bestProbabilitic);
                    currentSol = Utilities.string_to_intArray(bestProbabilitic.getSequence());
                    found = true;
                }
            }

            if(found) {
                //Get the best solution
                bestProbabilitic = Utilities.select_the_best(bestTmpSolutions);
                bestProbabilitic.setFitness(bestProbabilitic.getFitness() * fitness_total);
                //Update best solutions
                best_fitness.add(bestProbabilitic);
            }
        }
        return best_fitness;
    }

    /**
     * Run method to explore
     * @param method : method to explore
     * @param timesAlgo : executions
     * @return
     */
    private ArrayList<FitnessObject> run(String method, int timesAlgo){
        if(method.equals(Utilities.DETERMINISTIC_HC)){
            return deterministic_HC(timesAlgo);
        }else{
            return probabilistic_HC(timesAlgo);
        }
    }

    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int relaunch = -1;
        do{

            System.out.println("---------   NK-Landscape   ---------");
            int k, index_method;
            String[] method = {Utilities.DETERMINISTIC_HC, Utilities.PROBABILISTIC_HC};
            String x_max = "100100100100100100100";
            ArrayList<FitnessObject> bestSolutions;
            int timesAlgo = 50;
            int N = x_max.length();

            System.out.println("Entrez la valeur de K:");
            k = scanner.nextInt();
            System.out.println("Sélectionner la méthode d'exploration: Grimpeur [déterministe/probabiliste]: [0/1]");
            index_method = scanner.nextInt();

            System.out.println("Génération de "+timesAlgo+" solutions");
            NK nkLanscape = new NK(N, k, x_max);
            bestSolutions = nkLanscape.run(method[index_method], timesAlgo);
            double averageFitness = Utilities.averageFitness(bestSolutions);
            Utilities.printBestSolutions(bestSolutions);
            System.out.println("######### "+method[index_method]+" N: "+N+" K: "+k+" Average: "+averageFitness);
            double[] stabilities = Utilities.analyseStability(bestSolutions, x_max);
            System.out.println(" ----> Distance de Hamming pour chaque solution par rapport à \"x_max\":");
            System.out.println(Arrays.toString(stabilities));
            System.out.println(" ");

            System.out.println("#########  Matrix qui montre pour chaque solution de combien elle diffère avec les autres solutions obtenues:  ");
            int[][] hamming_matrix = Utilities.analyseStabilityBestSolutions(bestSolutions);
            for (int i = 0; i < hamming_matrix.length; i++) {
                for (int j = 0; j < hamming_matrix[0].length; j++) {
                    System.out.print(hamming_matrix[i][j] + "\t");
                }
                System.out.println(" ");
            }

//            double[] rugged = Utilities.showRugged(hamming_matrix);
//            WriteDatFile writeDatFile = new WriteDatFile(method[index_method]+"_N_"+N+"_K_"+k+"_graph2");
//            writeDatFile.writeResults(rugged);

            System.out.println("Voulez-vous relancer le programme: Oui:1 \t Non:0");
            relaunch = scanner.nextInt();
        }while(relaunch == 1);
    }
}

// Exemple d'execution pour comparaître les résultats
//        int [] K = {0,1,2,3,4, 5, 10};
//        String[] method = {Utilities.DETERMINISTIC_HC, Utilities.PROBABILISTIC_HC};
//        String x_max = "100100100100100100100";
//        ArrayList<FitnessObject> bestSolutions;
//        int timesAlgo = 50;
//        int N = x_max.length();
//
//        for (int i = 0; i < method.length; i++) {
//            for (int j = 0; j < K.length; j++) {
//                double averageFitness = 0.0;
//                WriteDatFile writeDatFile = new WriteDatFile(method[i]+"_N_"+N+"_K_"+K[j]);
//                System.out.println("######### "+method[i]+" N: "+N+" K: "+K[j]);
//                NK nkLanscape = new NK(N, K[j], x_max);
//                bestSolutions = nkLanscape.run(method[i], timesAlgo);
//                averageFitness = Utilities.averageFitness(bestSolutions);
//                System.out.println("######### "+method[i]+" N: "+N+" K: "+K[j]+" Average: "+averageFitness);
//                Utilities.printBestSolutions(bestSolutions);
//                double[] stabilities = Utilities.analyseStability(bestSolutions, x_max);
//                System.out.println(Arrays.toString(stabilities));
//                writeDatFile.writeResults(stabilities);
//            }
//        }