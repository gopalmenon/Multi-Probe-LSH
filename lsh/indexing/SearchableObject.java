package lsh.indexing;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SearchableObject {
	
	private List<Double> objectFeatures;
	private final File objectFile;
	
	public SearchableObject(List<Double> objectFeatures, File objectFile) {
		this.objectFeatures = objectFeatures;
		this.objectFile = objectFile;
	}
	

	public List<Double> getObjectFeatures() {
		return Collections.unmodifiableList(objectFeatures);
	}

	public File getObjectFile() {
		return objectFile;
	}
	
	/**
	 * @param other
	 * @return square of euclidian distance to another object. 
	 * Used for brute force similar search.
	 */
	public double distanceTo(SearchableObject other) {
		
		double featureCount = this.objectFeatures.size();
		assert featureCount == other.objectFeatures.size() :
			"Both objects should have same dimensions to compute distance";
		
		double distanceToOther = 0.0;
		
		for (int featureCounter = 0; featureCounter < featureCount; ++featureCounter) {
			distanceToOther += Math.pow(this.objectFeatures.get(featureCounter) - other.objectFeatures.get(featureCounter), 2.0);
		}
		
		return distanceToOther;
		
	}
	
}
