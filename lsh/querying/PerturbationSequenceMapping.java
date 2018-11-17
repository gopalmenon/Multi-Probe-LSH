package lsh.querying;

import lsh.indexing.HashTable;
import java.util.List;
import java.util.Map;


public class PerturbationSequenceMapping {

    private PerturbationSequences sequence;
    private int numberOfHashFunctions;
    private double slotWidth;
    private int numberOfPerturbations;
    private int numberOfHashTables;
    List<HashTable> hashTables;
    Map<>

    public PerturbationSequenceMapping(int numberOfHashFunctions, double slotWidth, int numberOfPerturbations, int numberOfHashTables, List<HashTable> hashTables)
    {
        this.numberOfHashFunctions = numberOfHashFunctions;
        this.slotWidth = slotWidth;
        this.numberOfPerturbations = numberOfPerturbations;
        this.numberOfHashTables = numberOfHashTables;
        this.hashTables = hashTables;

        this.sequence = new PerturbationSequences(this.numberOfHashFunctions, this.slotWidth, this.numberOfPerturbations);
        GenerateMapping();

    }

    // For each hashtable, we calculate x_i(delta) where x_i(delta) is the distance to the next slot +
    // sort these 2M (= 2 * numberofhashfunctions) values. Create a map between sorted and unsorted. Get the perturbation
    // sequence. Use these as indices for the sorted order of x_i. Put these indices in a new vector. Return vector.
    // Query this vector.


    // First suppose we have one hash table

    public void GenerateMapping()
    {
        for (HashTable hashtable: hashTables)
        {

        }
    }












    private class distanceToNextSlot implements Comparable<distanceToNextSlot> {

        private double distance;
        private int hashFunctionNumber;

        public distanceToNextSlot(double distance, int hashFunctionNumber) {
            this.distance = distance;
            this.hashFunctionNumber = hashFunctionNumber;
        }

        @Override
        public int compareTo(distanceToNextSlot other) {
            return Double.valueOf(this.distance).compareTo(Double.valueOf(other.distance));
        }

        public double getDistance() {
            return distance;
        }

        public int getHashFunctionNumber() {
            return hashFunctionNumber;
        }

    }


}
