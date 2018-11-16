package lsh.indexing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3;

public class SpectralHashTable extends HashTable{

	private List<HashFunction> hashFunctionTable;
	private int numberOfHashFunctions;
	private Map<HashBucket, SearchableObject> objectIndex;
	

	public SpectralHashTable(int numberOfHashFunctions, int numberOfDimensions, double slotWidthW,  List<SearchableObject> featureVectors) {
		
		this.hashFunctionTable = new ArrayList<HashFunction>(numberOfHashFunctions);

		// Restrict the number of hash functions to be min(numberOfDimensions, numberOfHashFunctions) to avoid breaking
		this.numberOfHashFunctions = (numberOfHashFunctions <= numberOfDimensions) ? numberOfHashFunctions : numberOfDimensions;

		// Begin construction of matrix M
		double M[][] = new double[featureVectors.size()][numberOfDimensions];
		
		for (int point_ndx = 0; point_ndx < featureVectors.size(); point_ndx++) {
			// Set rows of M to be featurevectors 
			double featureVector[] = new double[numberOfDimensions];
			featureVector = featureVectors[point_ndx].toArray(featureVector);
			M[point_ndx] = featureVector;	
		}
		
		// Get Eigendecomposition
	    RealMatrix realM = new Array2DRowRealMatrix(M);
		RealMatrix MtM = (realM.transpose()).multiply(realM);
		EigenDecomposition VDV = new EigenDecomposition(MtM);
		RealMatrix V = VDV.getV();
		
		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			double eigenVector[] = V.getColumn(hashFunctionCounter);
			
			this.hashFunctionTable.add(new HashFunction(numberOfDimensions, slotWidthW, eigenVector));
		}
		
		this.objectIndex = new HashMap<HashBucket, SearchableObject>();
	}

}
