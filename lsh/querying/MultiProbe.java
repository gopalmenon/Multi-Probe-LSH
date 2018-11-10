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
	
	public MultiProbe(int numberOfHashFunctions, double slotWidth, Random randomNumberGenerator) {
		this.numberOfHashFunctions = numberOfHashFunctions;
		this.slotWidth = slotWidth;
		this.randomNumberGenerator = randomNumberGenerator;
		this.randomPerturbations = new ArrayList<Double>(this.numberOfHashFunctions);
		generateRandomPerturbations();
	}
	
	private void generateRandomPerturbations() {
		
		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			this.randomPerturbations.add(Double.valueOf(this.randomNumberGenerator.nextDouble()*this.slotWidth/2.0));
		}

	}
	
	public List<Double> getRandomPerturbations() {
		return Collections.unmodifiableList(this.randomPerturbations);
	}

}
