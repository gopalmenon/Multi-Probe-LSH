package lsh.indexing;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

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
	private boolean secondPass;
	
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
		this.secondPass = false;

		// If we are using eigenvectors, we must sweep through the data once, find the eigenvalues,
		// then make a second pass to begin hashing.
		if (useEigenVectorsToHash) {
			createHashTables(createImageIndex());
			this.secondPass = true;
			createImageIndex();
		}
		else {
			this.secondPass = true;
			createHashTables(null);
			createImageIndex();
		}
	}
	
	private void createHashTables(List<SearchableObject> featureVectors) {
		if (!useEigenVectorsToHash) {
			for (int hashTableCounter = 0; hashTableCounter < this.numberOfHashTables; ++hashTableCounter) {
				this.hashTables.add(new HashTable(this.numberOfHashFunctions, this.numberOfImageFeatures, this.slotWidth, this.randomNumberGenerator));
			}
		}
		else {
			// Begin construction of matrix M
			double M[][] = new double[featureVectors.size()][this.numberOfImageFeatures];

			for (int point_ndx = 0; point_ndx < featureVectors.size(); point_ndx++) {
				// Set rows of M to be featurevectors
				double featureVector[] = new double[this.numberOfImageFeatures];

				// Perhaps there is a better way to do this?
				for (int i = 0; i < this.numberOfImageFeatures; i++)
					featureVector[i] = featureVectors.get(point_ndx).getObjectFeatures().get(i);

				M[point_ndx] = featureVector;
			}

			// Get Eigendecomposition
			RealMatrix realM = new Array2DRowRealMatrix(M);
			RealMatrix MtM = (realM.transpose()).multiply(realM);
			EigenDecomposition VDV = new EigenDecomposition(MtM);
			RealMatrix V = VDV.getV();

			// Construct random indexing array
			int eigenVectorIndex[] = new int[this.numberOfHashFunctions * this.numberOfHashTables];
			for (int i = 0; i < this.numberOfHashFunctions * this.numberOfHashTables; i++)
				eigenVectorIndex[i] = i;

			// Code from https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
			for (int i = eigenVectorIndex.length - 1; i > 0; i--)
			{
				int index = this.randomNumberGenerator.nextInt(i + 1);
				// Simple swap
				int a = eigenVectorIndex[index];
				eigenVectorIndex[index] = eigenVectorIndex[i];
				eigenVectorIndex[i] = a;
			}

			int eig_ndx = 0;
			for (int i = 0; i < this.numberOfHashTables; i++){
				double eigenvectors[][] = new double[this.numberOfHashFunctions][this.numberOfImageFeatures];

				for (int j = 0; j < this.numberOfHashFunctions; j++){
					eigenvectors[j] = V.getColumn(eigenVectorIndex[eig_ndx]);
					eig_ndx++;
				}

				this.hashTables.add(new HashTable(this.numberOfHashFunctions, this.numberOfImageFeatures, this.slotWidth, eigenvectors));
			}
		}
	}
	
	/**
	 * Read image urls, extract image features and put the image in its bucket
	 */
	private List<SearchableObject> createImageIndex() {
		
		URL imageUrl = null;
		List<SearchableObject> featureVectors = new ArrayList<SearchableObject>();

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
				
				if (secondPass) {
					for (HashTable hashtable : this.hashTables) {
						hashtable.add(new SearchableObject(imageFeatures, imageUrl));
					}
				}
				else {
					featureVectors.add(new SearchableObject(imageFeatures, imageUrl));

				}
				fileLine = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return featureVectors;
	}
	
	public List<HashTable> getImageIndex() {
		return this.hashTables;
	}
	
	public Map<URL, List<Double>> getImageFeatures() {
		return this.imageFeatures;
	}
	
}
