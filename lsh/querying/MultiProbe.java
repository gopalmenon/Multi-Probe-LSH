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
	private List<Perturbation> randomPerturbations;
	
	public MultiProbe(int numberOfHashFunctions, double slotWidth, int numberOfPerturbations, Random randomNumberGenerator) {
		this.numberOfHashFunctions = numberOfHashFunctions;
		this.slotWidth = slotWidth;
		this.randomNumberGenerator = randomNumberGenerator;
		generateRandomDistancesToNextSlot();
		generateRandomPerturbations(numberOfPerturbations);
	}
	
	/**
	 * Generate the joint distribution of of the Zj values for j = 1,..., M
	 */
	private void generateRandomDistancesToNextSlot() {
		
		this.randomDistancesToNextSlot = new ArrayList<distanceToNextSlot>(this.numberOfHashFunctions);
		double halfSlotWidth = this.slotWidth/2.0;
		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			this.randomDistancesToNextSlot.add(new distanceToNextSlot(this.randomNumberGenerator.nextDouble() * halfSlotWidth, hashFunctionCounter));
		}
		
		this.sortedRandomDistancesToNextSlot = new ArrayList<distanceToNextSlot>(this.randomDistancesToNextSlot);
		Collections.sort(this.sortedRandomDistancesToNextSlot);

	}
	
	private int getIndexForDistanceToPreviousSlot(int indexForDistanceToNextSlot) {
		return 2 * this.numberOfHashFunctions - 1 - indexForDistanceToNextSlot;
	}
	
	private double getPerturbationScore(List<Integer> perturbedVector) {
		
		double perturbationScore = 0.0;
		int perturbedComponent = 0;
		
		for (int vectorComponentIndex = 0; vectorComponentIndex < perturbedVector.size(); ++vectorComponentIndex) {
			
			perturbedComponent = perturbedVector.get(vectorComponentIndex);
			if (perturbedComponent >= 0 && perturbedComponent < this.numberOfHashFunctions) {
				perturbationScore += this.randomDistancesToNextSlot.get(this.sortedRandomDistancesToNextSlot.get(perturbedComponent).getHashFunctionNumber()).getDistance();
			} if (perturbedComponent >= this.numberOfHashFunctions && perturbedComponent < 2 * this.numberOfHashFunctions) {
				perturbationScore += this.slotWidth - this.randomDistancesToNextSlot.get(this.sortedRandomDistancesToNextSlot.get(getIndexForDistanceToPreviousSlot(perturbedComponent)).getHashFunctionNumber()).getDistance();
			}

		}
		
		return perturbationScore;
	}
	
	private boolean isValidPerturbation(Perturbation candidatePerturbation) {
		
		int indexForDistanceToPreviousSlot = 0, perturbationComponent = 0;
		List<Integer> candidatePerturbationVector = candidatePerturbation.getPerturbedVector();
		for (int perturbationComponentIndex = 0; perturbationComponentIndex < candidatePerturbationVector.size(); ++perturbationComponentIndex) {
			perturbationComponent = candidatePerturbationVector.get(perturbationComponentIndex);
			if (perturbationComponent < this.numberOfHashFunctions) {
				indexForDistanceToPreviousSlot = getIndexForDistanceToPreviousSlot(perturbationComponent);
				for (int otherPerturbationComponentIndex = 0; otherPerturbationComponentIndex < candidatePerturbationVector.size(); ++otherPerturbationComponentIndex) {
					if (indexForDistanceToPreviousSlot == candidatePerturbationVector.get(otherPerturbationComponentIndex)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private int getMaximumPerturbationComponent(Perturbation inputPerturbation) {
		
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
	
	private Perturbation shiftPerturbation(Perturbation inputPerturbation) {
		
		int maximumPerturbationIndex = getMaximumPerturbationComponent(inputPerturbation);
		List<Integer> inputPerturbationVector = inputPerturbation.getPerturbedVector();
		List<Integer> newPerturbationVector = new ArrayList<Integer>(inputPerturbationVector);
		newPerturbationVector.set(maximumPerturbationIndex, Integer.valueOf(inputPerturbationVector.get(maximumPerturbationIndex).intValue() + 1));
		return new Perturbation(newPerturbationVector, getPerturbationScore(inputPerturbationVector));
		
	}
	
	private Perturbation expandPerturbation(Perturbation inputPerturbation) {
		
		int maximumPerturbationIndex = getMaximumPerturbationComponent(inputPerturbation);
		List<Integer> inputPerturbationVector = inputPerturbation.getPerturbedVector();
		List<Integer> newPerturbationVector = new ArrayList<Integer>(inputPerturbationVector);
		newPerturbationVector.add(Integer.valueOf(inputPerturbationVector.get(maximumPerturbationIndex).intValue() + 1));
		return new Perturbation(newPerturbationVector, getPerturbationScore(inputPerturbationVector));
		
	}
	
	public List<Perturbation> getRandomPerturbations() {
		
		return Collections.unmodifiableList(this.randomPerturbations);
		
	}
	
	private void generateRandomPerturbations(int numberOfPerturbations) {
		
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
		
		this.randomPerturbations = randomPerturbations;
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
