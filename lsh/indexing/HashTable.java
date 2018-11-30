package indexing;

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
	private Map<HashBucket, List<SearchableObject>> objectIndex;
	
	public HashTable(int numberOfHashFunctions, int numberOfDimensions, double slotWidthW, Random randomNumberGenerator) {
		
		this.hashFunctionTable = new ArrayList<HashFunction>(numberOfHashFunctions);
		for (int hashFunctionCounter = 0; hashFunctionCounter < numberOfHashFunctions; ++hashFunctionCounter) {
			this.hashFunctionTable.add(new HashFunction(numberOfDimensions, slotWidthW, randomNumberGenerator));
		}
		
		this.numberOfHashFunctions = numberOfHashFunctions;
		this.objectIndex = new HashMap<HashBucket, List<SearchableObject>>();
	}

	public HashTable(int numberOfHashFunctions, int numberOfDimensions, double slotWidthW, double[][] eigenVectors, Random randomNumberGenerator) {
		this.hashFunctionTable = new ArrayList<HashFunction>(numberOfHashFunctions);

		this.numberOfHashFunctions = numberOfHashFunctions;

        for (int hashFunctionCounter = 0; hashFunctionCounter < numberOfHashFunctions; ++hashFunctionCounter) {
            this.hashFunctionTable.add(new HashFunction(numberOfDimensions, slotWidthW, eigenVectors[hashFunctionCounter], randomNumberGenerator));
        }

		this.objectIndex = new HashMap<HashBucket, List<SearchableObject>>();
	}

	/**
	 * @param searchableObject
	 * @return the bucket corresponding to the object
	 */
	public HashBucket getHashBucket(SearchableObject searchableObject) {
		
		List<Integer> objectFeatures = searchableObject.getObjectFeatures();
		
		List<Integer> objectHashBucket = new ArrayList<Integer>();
		for (int hashFunctionCounter = 0; hashFunctionCounter < this.numberOfHashFunctions; ++hashFunctionCounter) {
			objectHashBucket.add(this.hashFunctionTable.get(hashFunctionCounter).getSlotNumber(objectFeatures));
		}
		
		return new HashBucket(objectHashBucket);
		
	}
	
	/**
	 * @param searchableObject
	 * @return true if object is added to the index
	 */
	public void add(SearchableObject searchableObject) {
		
		HashBucket hashBucket = getHashBucket(searchableObject);
		if (!this.objectIndex.containsKey(hashBucket)) {
			this.objectIndex.put(hashBucket, new ArrayList<SearchableObject>());
		}
		
		this.objectIndex.get(hashBucket).add(searchableObject);
	}
	
	/**
	 * @param hashBucket
	 * @return a list of objects in the bucket
	 */
	public List<SearchableObject> getObjects(HashBucket hashBucket) {
		List<SearchableObject> objectsInBucket = new ArrayList<>();
		if (this.objectIndex.get(hashBucket) != null)
			objectsInBucket.addAll(this.objectIndex.get(hashBucket));

		return objectsInBucket;
		
	}

	public List<SearchableObject> getAllObjects()
	{
		List<SearchableObject> retList = new ArrayList<>();
		for (HashBucket bucket : this.objectIndex.keySet())
		{
			retList.addAll(this.getObjects(bucket));
		}
		return retList;
	}

	public List<HashFunction> getHashFunctions() { return this.hashFunctionTable; }

}
