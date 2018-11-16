package lsh.indexing;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HashTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private List<HashFunction> hashFunctionTable;
	private int numberOfHashFunctions;
	private Map<HashBucket, SearchableObject> objectIndex;
	
	public HashTable(int numberOfHashFunctions, int numberOfDimensions, double slotWidthW, Random randomNumberGenerator) {
		
		this.hashFunctionTable = new ArrayList<HashFunction>(numberOfHashFunctions);
		for (int hashFunctionCounter = 0; hashFunctionCounter < numberOfHashFunctions; ++hashFunctionCounter) {
			this.hashFunctionTable.add(new HashFunction(numberOfDimensions, slotWidthW, randomNumberGenerator));
		}
		
		this.numberOfHashFunctions = numberOfHashFunctions;
		this.objectIndex = new HashMap<HashBucket, SearchableObject>();
	}

	public HashTable(int numberOfHashFunctions, int numberOfDimensions, List<SearchableObject> featureVectors) {
		this.hashFunctionTable = new ArrayList<HashFunction>(numberOfHashFunctions);

		// Restrict the number of hash functions to be min(numberOfDimensions, numberOfHashFunctions)
		this.numberOfHashFunctions = (numberOfHashFunctions <= numberOfDimensions) ? numberOfHashFunctions : numberOfDimensions;

		// Begin construction of matrix M
		double M[][] = new double[featureVectors.size()][numberOfDimensions];

		for (int point_ndx = 0; point_ndx < featureVectors.size(); point_ndx++) {
			// Set rows of M to be featurevectors
			double featureVector[] = new double[numberOfDimensions];

            // Perhaps there is a better way to do this?
			for (int i = 0; i < numberOfDimensions; i++)
				featureVector[i] = featureVectors.get(point_ndx).getObjectFeatures().get(i);

			M[point_ndx] = featureVector;
		}

		// Get Eigendecomposition
		RealMatrix realM = new Array2DRowRealMatrix(M);
		RealMatrix MtM = (realM.transpose()).multiply(realM);
		EigenDecomposition VDV = new EigenDecomposition(MtM);
		RealMatrix V = VDV.getV();

		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			double eigenVector[] = V.getColumn(hashFunctionCounter);

			this.hashFunctionTable.add(new HashFunction(numberOfDimensions, 0.0, eigenVector));
		}

		this.objectIndex = new HashMap<HashBucket, SearchableObject>();
	}

	/**
	 * @param searchableObject
	 * @return the bucket corresponding to the object
	 */
	public HashBucket getHashBucket(SearchableObject searchableObject) {
		
		List<Double> objectFeatures = searchableObject.getObjectFeatures();
		
		List<Integer> objectHashBucket = new ArrayList<Integer>(this.numberOfHashFunctions);
		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			objectHashBucket.add(this.hashFunctionTable.get(hashFunctionCounter).getSlotNumber(objectFeatures));
		}
		
		return new HashBucket(objectHashBucket);
		
	}
	
	/**
	 * @param searchableObject
	 * @return true if object is added to the index
	 */
	public boolean add(SearchableObject searchableObject) {
		
		HashBucket hashBucket = getHashBucket(searchableObject);
		if (this.objectIndex.containsKey(hashBucket)) {
			return false;
		}
		
		this.objectIndex.put(hashBucket, searchableObject);
		return true;
		
	}
	
	/**
	 * @param hashBucket
	 * @return a list of objects in the bucket
	 */
	public List<SearchableObject> getObjects(HashBucket hashBucket) {
		Set<Map.Entry<HashBucket, SearchableObject>> entrySet = this.objectIndex.entrySet();
		
		List<SearchableObject> objectsInBucket = new ArrayList<SearchableObject>();
		for (Map.Entry<HashBucket, SearchableObject> entry : entrySet) {
			if (entry.getKey().equals(hashBucket)) {
				objectsInBucket.add(entry.getValue());
			}
		}
		
		return objectsInBucket;
		
	}
	
}
