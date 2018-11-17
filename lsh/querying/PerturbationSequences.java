package lsh.querying;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class PerturbationSequences {
    private int numberOfHashFunctions;
    private double slotWidth;
    private int numberOfPermutations;
    private List<Perturbation2> randomPerturbations;

    public PerturbationSequences(int numberOfHashFunctions, double slotWidth, int numberOfPerturbations)
    {
        this.numberOfHashFunctions = numberOfHashFunctions;
        this.slotWidth = slotWidth;
        this.numberOfPermutations = numberOfPerturbations;

        generateRandomPerturbations(this.numberOfPermutations);
    }

    private double getPerturbationScore(List<Integer> perturbedVector) {

        double perturbationScore = 0.0;
        int perturbedComponent = 0;

        for (int vectorComponentIndex = 0; vectorComponentIndex < perturbedVector.size(); ++vectorComponentIndex) {

            perturbedComponent = perturbedVector.get(vectorComponentIndex);
            if (perturbedComponent >= 0 && perturbedComponent <= this.numberOfHashFunctions) {

                perturbationScore += (perturbedComponent* (perturbedComponent + 1))*(this.slotWidth * this.slotWidth) /
                        (4 * ((double)this.numberOfHashFunctions + 1) * ((double)this.numberOfHashFunctions + 2));

            } else if (perturbedComponent > this.numberOfHashFunctions && perturbedComponent <= 2 * this.numberOfHashFunctions) {

                perturbationScore += (double)(this.slotWidth * this.slotWidth) * (1 - ((2 * (double)this.numberOfHashFunctions + 1 - perturbedComponent)/
                        (this.numberOfHashFunctions + 1)) + ((2 * (double)this.numberOfHashFunctions + 1 - perturbedComponent) *
                        (2 * this.numberOfHashFunctions + 2 - perturbedComponent))/(4 * (this.numberOfHashFunctions + 1)*(this.numberOfHashFunctions + 2)));
            }
        }

        return perturbationScore;
    }

    private int getIndexForDistanceToPreviousSlot(int indexForDistanceToNextSlot) {
        return 2 * this.numberOfHashFunctions - 1 - indexForDistanceToNextSlot;
    }

    private boolean isValidPerturbation(Perturbation2 candidatePerturbation) {

        int indexForDistanceToPreviousSlot = 0, perturbationComponent = 0;
        List<Integer> candidatePerturbationVector = candidatePerturbation.getPerturbedVector();
        for (int perturbationComponentIndex = 0; perturbationComponentIndex < candidatePerturbationVector.size(); ++perturbationComponentIndex) {
            perturbationComponent = candidatePerturbationVector.get(perturbationComponentIndex);
            if (perturbationComponent <= 2 * this.numberOfHashFunctions) {
                indexForDistanceToPreviousSlot = getIndexForDistanceToPreviousSlot(perturbationComponent);
                for (int otherPerturbationComponentIndex = 0; otherPerturbationComponentIndex < candidatePerturbationVector.size(); ++otherPerturbationComponentIndex) {
                    if (indexForDistanceToPreviousSlot == candidatePerturbationVector.get(otherPerturbationComponentIndex)) {
                        return false;
                    }
                }
            }
            else return false;
        }
        return true;
    }

    private int getMaximumPerturbationComponent(Perturbation2 inputPerturbation) {

        List<Integer> inputPerturbationVector = inputPerturbation.getPerturbedVector();
        int maximumPerturbation = Integer.MIN_VALUE, maximumPerturbationIndex = 0;
        for (int perturbationComponentIndex = 0; perturbationComponentIndex < inputPerturbationVector.size(); ++perturbationComponentIndex) {
            if (inputPerturbationVector.get(perturbationComponentIndex) > maximumPerturbation) {
                maximumPerturbation = inputPerturbationVector.get(perturbationComponentIndex);
                maximumPerturbationIndex = perturbationComponentIndex;
            }
        }

        return maximumPerturbationIndex;

    }

    private Perturbation2 shiftPerturbation(Perturbation2 inputPerturbation) {

        int maximumPerturbationIndex = getMaximumPerturbationComponent(inputPerturbation);
        List<Integer> inputPerturbationVector = inputPerturbation.getPerturbedVector();
        List<Integer> newPerturbationVector = new ArrayList<Integer>(inputPerturbationVector);
        newPerturbationVector.set(maximumPerturbationIndex, Integer.valueOf(inputPerturbationVector.get(maximumPerturbationIndex).intValue() + 1));
        return new Perturbation2(newPerturbationVector, getPerturbationScore(newPerturbationVector));

    }

    private Perturbation2 expandPerturbation(Perturbation2 inputPerturbation) {

        int maximumPerturbationIndex = getMaximumPerturbationComponent(inputPerturbation);
        List<Integer> inputPerturbationVector = inputPerturbation.getPerturbedVector();
        List<Integer> newPerturbationVector = new ArrayList<Integer>(inputPerturbationVector);
        newPerturbationVector.add(Integer.valueOf(inputPerturbationVector.get(maximumPerturbationIndex).intValue() + 1));
        return new Perturbation2(newPerturbationVector, getPerturbationScore(newPerturbationVector));

    }

    private void generateRandomPerturbations(int numberOfPerturbations) {

        PriorityQueue<Perturbation2> perturbations = new PriorityQueue<Perturbation2>();
        List<Perturbation2> randomPerturbations = new ArrayList<Perturbation2>(numberOfPerturbations);
        List<Integer> initialPerturbationVector = Arrays.asList(Integer.valueOf(1));
        Perturbation2 initialPerturbation = new Perturbation2(initialPerturbationVector, getPerturbationScore(initialPerturbationVector)), candidatePerturbation = null;
        perturbations.add(initialPerturbation);

        for (int perturbationCounter = 0; perturbationCounter < numberOfPerturbations; ++perturbationCounter) {

            do {

                candidatePerturbation = perturbations.remove();
                perturbations.add(shiftPerturbation(candidatePerturbation));
                perturbations.add(expandPerturbation(candidatePerturbation));

            } while(!isValidPerturbation(candidatePerturbation));

            randomPerturbations.add(candidatePerturbation);
        }

        this.randomPerturbations = randomPerturbations;
    }

    public List<Perturbation2> getPerturbations() { return this.randomPerturbations; }
}
