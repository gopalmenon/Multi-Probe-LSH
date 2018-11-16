package indexing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This will simulate a hash function which consists of a vector and an offset
 *
 */
public class HashFunction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	private int numberOfDimensions;
	private List<Double> hashFunctionCoefficients;
	private double offset, slotWidthW;
	
	public HashFunction(int numberOfDimensions, double slotWidthW, Random randomNumberGenerator) {
		
		this.numberOfDimensions = numberOfDimensions;
		this.hashFunctionCoefficients = new ArrayList<Double>(numberOfDimensions);
		
		for (int dimensionCounter = 0; dimensionCounter < numberOfDimensions; ++dimensionCounter) {
			this.hashFunctionCoefficients.add(Double.valueOf(randomNumberGenerator.nextGaussian()));
		}
		
		this.offset = randomNumberGenerator.nextDouble() * slotWidthW;
		this.slotWidthW = slotWidthW;
		
	}
	
	/**
	 * @param objectFeatures
	 * @return slot number for locality sensitive hashing
	 */
	public int getSlotNumber(List<Double> objectFeatures) {
		
		assert objectFeatures.size() == this.hashFunctionCoefficients.size() : 
			"Size mismatch between object to be hashed and hash function";
		
		double innerProduct = 0.0;
		for (int dimensionCounter = 0; dimensionCounter < numberOfDimensions; ++dimensionCounter) {
			innerProduct += objectFeatures.get(dimensionCounter) * this.hashFunctionCoefficients.get(dimensionCounter);
		}
	
		innerProduct += this.offset;
		
		return Double.valueOf(Math.floor(innerProduct / this.slotWidthW)).intValue();
		
	}
	
}
