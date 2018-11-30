package indexing;

import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class SearchableObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private List<Integer> objectFeatures;
	private final URL objectUrl;
	
	public SearchableObject(List<Integer> objectFeatures, URL objectUrl) {
		this.objectFeatures = objectFeatures;
		this.objectUrl = objectUrl;
	}
	

	public List<Integer> getObjectFeatures() {
		return Collections.unmodifiableList(objectFeatures);
	}

	public URL getObjectUrl() {
		return objectUrl;
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

	@Override
	public boolean equals(java.lang.Object other)
	{
		if (other == this)
			return true;
		for (int featureCounter = 0; featureCounter < this.objectFeatures.size(); ++featureCounter){
			if (this.getObjectFeatures().get(featureCounter).compareTo( ((SearchableObject) other).getObjectFeatures().get(featureCounter)) != 0)
				return false;
		}
		return true;
	}
}
