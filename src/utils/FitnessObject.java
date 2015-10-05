package utils;

/**
 * Created by aldazj on 21.09.15.
 */
public class FitnessObject {

    private double fitness;
    private String sequence;

    public FitnessObject(double fitness, String sequence) {
        this.fitness = fitness;
        this.sequence = sequence;
    }

    /**
     * Get a fitness
     * @return
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * To get a sequence
     * @return
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * To edit the fitness
     * @param fitness
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
