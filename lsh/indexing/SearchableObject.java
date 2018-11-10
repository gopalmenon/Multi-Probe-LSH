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
	
}
