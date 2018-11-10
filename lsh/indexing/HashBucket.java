package lsh.indexing;

import java.util.List;

/**
 * This will identify the bucket used to store an object that has been hashed by multiple hash functions
 *
 */
public class HashBucket {
	
	private List<Integer> objectHashBucket;
	private int hashCode;
	
	public HashBucket(List<Integer> objectHashBucket) {
		this.objectHashBucket = objectHashBucket;
		this.hashCode = computeHashCode();
	}
	
	private int computeHashCode() {
		
		int hashCode = 0, binaryMultiplier = 1;
		for (int slotCounter = 0; slotCounter < this.objectHashBucket.size(); ++ slotCounter) {
			hashCode += this.objectHashBucket.get(slotCounter).intValue() * binaryMultiplier;
			binaryMultiplier *= 2;
		}
		
		return hashCode;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * Override equals so that a HashBucket object can be uniquely identified
	 * 
	 */
	@Override
	public boolean equals(Object other) {
		
		if (other instanceof HashBucket) {
			HashBucket otherHashBucket = (HashBucket) other;
			if (this.objectHashBucket.size() == otherHashBucket.objectHashBucket.size()) {
				for (int slotCounter = 0; slotCounter < this.objectHashBucket.size(); ++ slotCounter) {
					if (this.objectHashBucket.get(slotCounter).intValue() != otherHashBucket.objectHashBucket.get(slotCounter).intValue()) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.hashCode;
	}

}
