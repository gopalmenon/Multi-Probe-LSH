package lsh.querying;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class MultiProbe {
	
	private int numberOfHashFunctions;
	private double slotWidth;
	Random randomNumberGenerator;
	private List<distanceToNextSlot> randomDistancesToNextSlot, sortedRandomDistancesToNextSlot;
	private List <Double> expectedValueNextSlotDistanceSquared;
	private List <Double> expectedValuePreviousSlotDistanceSquared;
	
	public MultiProbe(int numberOfHashFunctions, double slotWidth, Random randomNumberGenerator) {
		this.numberOfHashFunctions = numberOfHashFunctions;
		this.slotWidth = slotWidth;
		this.randomNumberGenerator = randomNumberGenerator;
		this.randomDistancesToNextSlot = new ArrayList<distanceToNextSlot>(this.numberOfHashFunctions);
		generateRandomDistancesToNextSlot();
		this.expectedValueNextSlotDistanceSquared = new ArrayList<Double>(this.numberOfHashFunctions);
		this.expectedValuePreviousSlotDistanceSquared = new ArrayList<Double>(this.numberOfHashFunctions);
		findExpectedDistancesToAdjoiningSlots();
	}
	
	/**
	 * Generate the joint distribution of of the Zj values for j = 1,..., M
	 */
	private void generateRandomDistancesToNextSlot() {
		
		double halfSlotWidth = this.slotWidth/2.0;
		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			this.randomDistancesToNextSlot.add(new distanceToNextSlot(this.randomNumberGenerator.nextDouble() * halfSlotWidth, hashFunctionCounter));
		}
		
		this.sortedRandomDistancesToNextSlot = new ArrayList<distanceToNextSlot>(this.randomDistancesToNextSlot);
		Collections.sort(this.sortedRandomDistancesToNextSlot);

	}
	
	/**
	 * Find sorted distances to next and previous slots
	 */
	private void findExpectedDistancesToAdjoiningSlots() {
		
		double computedValue = 0.0;
		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			
			computedValue = (hashFunctionCounter * (hashFunctionCounter + 1) * slotWidth * slotWidth) / (4.0 * (numberOfHashFunctions + 1) * (numberOfHashFunctions +2));
			expectedValueNextSlotDistanceSquared.add(Double.valueOf(computedValue));
			computedValue = slotWidth * slotWidth * (1.0 - 
					((2.0 * numberOfHashFunctions + 1.0 - hashFunctionCounter) / (numberOfHashFunctions + 1.0)) + 
					(((2.0 * numberOfHashFunctions + 1.0 - hashFunctionCounter) * (2.0 * numberOfHashFunctions + 2.0 - hashFunctionCounter)) / (4.0 * (numberOfHashFunctions + 1.0) * (numberOfHashFunctions + 2.0))));
			expectedValuePreviousSlotDistanceSquared.add(Double.valueOf(computedValue));
		}
		
		
	}
	
	private double getPerturbationScore(List<Integer> perturbedVector) {
		
		double perturbationScore = 0.0;
		int perturbedComponent = 0;
		
		for (int vectorComponentIndex = 0; vectorComponentIndex < perturbedVector.size(); ++vectorComponentIndex) {
			
			perturbedComponent = perturbedVector.get(vectorComponentIndex);
			if (perturbedComponent >= 0 && perturbedComponent < this.numberOfHashFunctions) {
				perturbationScore += this.randomDistancesToNextSlot.get(this.sortedRandomDistancesToNextSlot.get(perturbedComponent).getHashFunctionNumber()).getDistance();
			} if (perturbedComponent >= this.numberOfHashFunctions && perturbedComponent < 2 * this.numberOfHashFunctions) {
				perturbationScore += this.slotWidth - this.randomDistancesToNextSlot.get(this.sortedRandomDistancesToNextSlot.get(2 * this.numberOfHashFunctions - perturbedComponent).getHashFunctionNumber()).getDistance();
			}

		}
		
		return perturbationScore;
	}
	
	private boolean isValidPerturbation(Perturbation candidatePerturbation) {
		return true;
	}
	
	private Perturbation shiftPerturbation(Perturbation inputPerturbation) {
		return null;
	}
	
	private Perturbation expandPerturbation(Perturbation inputPerturbation) {
		return null;
	}
	
	public List<Perturbation> getRandomPerturbations(int numberOfPerturbations) {
		
		PriorityQueue<Perturbation> perturbations = new PriorityQueue<Perturbation>();
		List<Perturbation> randomPerturbations = new ArrayList<Perturbation>(numberOfPerturbations);
		List<Integer> initialPerturbationVector = Arrays.asList(Integer.valueOf(1));
		Perturbation initialPerturbation = new Perturbation(initialPerturbationVector, getPerturbationScore(initialPerturbationVector)), candidatePerturbation = null;
		perturbations.add(initialPerturbation);
		
		for (int perturbationCounter = 0; perturbationCounter < numberOfPerturbations; ++perturbationCounter) {
			
			do {
				
				candidatePerturbation = perturbations.remove();
				perturbations.add(shiftPerturbation(candidatePerturbation));
				perturbations.add(expandPerturbation(candidatePerturbation));
			
			} while(!isValidPerturbation(candidatePerturbation));
			
			randomPerturbations.add(candidatePerturbation);
		}
		
		return randomPerturbations;
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
