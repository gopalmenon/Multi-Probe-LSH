package lsh.indexing;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageIndex implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String IMAGE_URLS = "data/image_urls.txt";
	public static final String SAVED_INDEX_FILE = "data/saved_index.ser";
	
	private int numberOfHashFunctions;
	private int numberOfHashTables;
	private int numberOfImageFeatures;
	private double slotWidth;
	private boolean useEigenVectorsToHash;
	private File imageUrls;
	private Map<URL, List<Double>> imageFeatures;
	private List<HashTable> hashTables;
	private Random randomNumberGenerator;
	
	public ImageIndex(int numberOfHashFunctions, int numberOfHashTables, int numberOfImageFeatures, double slotWidth, boolean useEigenVectorsToHash, File imageUrls) {
		this.numberOfHashFunctions = numberOfHashFunctions;
		this.numberOfHashTables = numberOfHashTables;
		this.numberOfImageFeatures = numberOfImageFeatures;
		this.slotWidth = slotWidth;
		this.useEigenVectorsToHash = useEigenVectorsToHash;
		if (imageUrls != null && imageUrls.exists() && !imageUrls.isDirectory()) {
			this.imageUrls = imageUrls;
		} else {
			this.imageUrls = new File(IMAGE_URLS);
		}
		this.hashTables = new ArrayList<HashTable>(this.numberOfHashTables);
		imageFeatures = new HashMap<URL, List<Double>>();
		this.randomNumberGenerator = new Random();
		
		createHashTables();
		createImageIndex();
	}
	
	private void createHashTables() {
		
		for (int hashTableCounter = 0; hashTableCounter < this.numberOfHashTables; ++ hashTableCounter) {
			this.hashTables.add(new HashTable(this.numberOfHashFunctions, this.numberOfImageFeatures, this.slotWidth, this.randomNumberGenerator));
		}
		
	}
	
	/**
	 * Read image urls, extract image features and put the image in its bucket
	 */
	private void createImageIndex() {
		
		URL imageUrl = null;
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(this.imageUrls));
			String fileLine = bufferedReader.readLine();
			while (fileLine != null) {

				//Download the image
				try {
					imageUrl = new URL(fileLine.trim());
				} catch (Exception e) {
					System.err.println("Error processing url " + fileLine + ". File skipped.");
					continue;
				}
				try {
					BufferedImage image = ImageIO.read(imageUrl.openStream());
				} catch (Exception e) {
					System.err.println("Error reading " + fileLine + ". File skipped.");
				}
				
				//Create color histogram for the image
				List<Double> imageFeatures = Arrays.asList(Double.valueOf(this.randomNumberGenerator.nextDouble()), Double.valueOf(this.randomNumberGenerator.nextDouble()), Double.valueOf(this.randomNumberGenerator.nextDouble()), Double.valueOf(this.randomNumberGenerator.nextDouble()), Double.valueOf(this.randomNumberGenerator.nextDouble()));
				
				//Store the image features for later use
				this.imageFeatures.put(imageUrl, imageFeatures);
				
				for (HashTable hashtable : this.hashTables) {
					hashtable.add(new SearchableObject(imageFeatures, imageUrl));
				}
				fileLine = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<HashTable> getImageIndex() {
		return this.hashTables;
	}
	
	public Map<URL, List<Double>> getImageFeatures() {
		return this.imageFeatures;
	}
	
}
