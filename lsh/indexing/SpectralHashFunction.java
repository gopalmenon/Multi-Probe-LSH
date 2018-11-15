package lsh.indexing;


import java.util.ArrayList;
import java.util.List;

public class SpectralHashFunction extends HashFunction {

	private int numberOfDimensions;
	private List<Double> hashFunctionCoefficients;
	private double offset, slotWidthW;
	
	public SpectralHashFunction(int numberOfDimensions, double slotWidthW, double eigenVector[]) {
		
		this.numberOfDimensions = numberOfDimensions;
		this.hashFunctionCoefficients = new ArrayList<Double>(numberOfDimensions);
		
		for (int i = 0; i < numberOfDimensions; i++)
			hashFunctionCoefficients.add(eigenVector[i]);	
				
	    this.offset = 0.0;
		this.slotWidthW = slotWidthW;
	}
}
