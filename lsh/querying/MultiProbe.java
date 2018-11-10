package lsh.querying;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MultiProbe {
	
	private int numberOfHashFunctions;
	private double slotWidth;
	Random randomNumberGenerator;
	List<Double> randomPerturbations;
	List <Double> expectedValueNextSlotDistanceSquared;
	List <Double> expectedValuePreviousSlotDistanceSquared;
	
	public MultiProbe(int numberOfHashFunctions, double slotWidth, Random randomNumberGenerator) {
		this.numberOfHashFunctions = numberOfHashFunctions;
		this.slotWidth = slotWidth;
		this.randomNumberGenerator = randomNumberGenerator;
		this.randomPerturbations = new ArrayList<Double>(this.numberOfHashFunctions);
		generateRandomPerturbations();
		findExpectedDistancesToAdjoiningSlots();
	}
	
	private void generateRandomPerturbations() {
		
		double halfSlotWidth = this.slotWidth/2.0;
		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			this.randomPerturbations.add(Double.valueOf(this.randomNumberGenerator.nextDouble() * halfSlotWidth));
		}

	}
	
	public List<Double> getRandomPerturbations() {
		return Collections.unmodifiableList(this.randomPerturbations);
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
					((2.0 * numberOfHashFunctions + 1.0 - hashFunctionCounter) / (numberOfHashFunctions + 1.0)) - 
					(((2.0 * numberOfHashFunctions + 1.0 - hashFunctionCounter) * (2.0 * numberOfHashFunctions + 2.0 - hashFunctionCounter)) / (4.0 * (numberOfHashFunctions + 1.0) * (numberOfHashFunctions + 2.0))));
			expectedValuePreviousSlotDistanceSquared.add(Double.valueOf(computedValue));
		}
		
		
	}

}
