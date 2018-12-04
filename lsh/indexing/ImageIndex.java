package indexing;

import images.FeatureFactory;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

import com.opencsv.*;

public class ImageIndex implements Serializable {
	
	private static final long serialVersionUID = 1L;
	// Default to the first image file in the provided data directory
	public static final String IMAGE_URLS = "image_data/images/file_1";
	public static final String SAVED_INDEX_FILE = "lsh/test/test_storage.ser";
	
	private int numberOfHashFunctions;
	private int numberOfHashTables;
	private int numberOfImageFeatures;
	private double slotWidth;
	private boolean useEigenVectorsToHash;
	private File imageUrls;
	private Map<URL, List<Double>> imageFeatures;
	private List<SearchableObject> rawFeatureVectors;
	private List<HashTable> hashTables;
	private Random randomNumberGenerator;

	public ImageIndex(int numberOfHashFunctions, int numberOfHashTables, int numberOfImageFeatures, double slotWidth, boolean useEigenVectorsToHash, File imageUrls, boolean test) {
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
		this.imageFeatures = new HashMap<URL, List<Double>>();
		this.rawFeatureVectors = new ArrayList<>();
		this.randomNumberGenerator = new Random();

		if (test) {
			createTestImageIndex();
		}
		else {
			createImageIndex();
		}

		createHashTables();
		for (HashTable hashtable: this.hashTables){
			for (SearchableObject object: this.rawFeatureVectors) {
				hashtable.add(object);
			}
		}

		System.out.println("number of features: "+ rawFeatureVectors.size());
	}
	
	private void createHashTables() {
		if (!useEigenVectorsToHash) {
			for (int hashTableCounter = 0; hashTableCounter < this.numberOfHashTables; ++hashTableCounter) {
				this.hashTables.add(new HashTable(this.numberOfHashFunctions, this.numberOfImageFeatures, this.slotWidth, this.randomNumberGenerator));
			}
		}
		else {
			// Begin construction of matrix M
			double M[][] = new double[this.rawFeatureVectors.size()][this.numberOfImageFeatures];

			for (int point_ndx = 0; point_ndx < this.rawFeatureVectors.size(); point_ndx++) {
				// Set rows of M to be featurevectors
				double featureVector[] = new double[this.numberOfImageFeatures];

				// Perhaps there is a better way to do this?
				for (int i = 0; i < this.numberOfImageFeatures; i++)
					featureVector[i] = this.rawFeatureVectors.get(point_ndx).getObjectFeatures().get(i);

				M[point_ndx] = featureVector;
			}

			// Get Eigendecomposition
			RealMatrix realM = new Array2DRowRealMatrix(M);
			RealMatrix MtM = (realM.transpose()).multiply(realM);
			EigenDecomposition VDV = new EigenDecomposition(MtM);
			RealMatrix V = VDV.getV();

			int ML = this.numberOfHashFunctions * this.numberOfHashTables;
			double eigenvectors[][] = new double[Math.max(this.numberOfImageFeatures, ML)][this.numberOfImageFeatures];

			// We have two cases to consider. 1) More eigenvectors (or dimension) than M*L and less eigenvectors than M*L
			if (ML <= this.numberOfImageFeatures)
				for (int i = 0; i < this.numberOfImageFeatures; i++)
					eigenvectors[i] = V.getColumn(i);

			else
			{
				for (int i = 0; i < this.numberOfImageFeatures; i++)
					eigenvectors[i] = V.getColumn(i);

				for (int i = this.numberOfImageFeatures; i < ML; i++)
				{
					int a = randomNumberGenerator.nextInt(this.numberOfImageFeatures);
					int b = randomNumberGenerator.nextInt(this.numberOfImageFeatures);
					double c1 = randomNumberGenerator.nextGaussian();
					double c2 = randomNumberGenerator.nextGaussian();

					double linearCombination[] = new double[this.numberOfImageFeatures];

					for (int j = 0; j < this.numberOfImageFeatures; j++)
						linearCombination[j] = (c1 * eigenvectors[a][j])+ (c2 * eigenvectors[b][j]);

					eigenvectors[i] = linearCombination;

				}
			}

			// Code from https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
			// Shuffle eigenvectors to remove heavy weight on one hashtable
			for (int i = eigenvectors.length - 1; i > 0; i--)
			{
				int index = this.randomNumberGenerator.nextInt(i + 1);
				// Simple swap
				double a[] = eigenvectors[index];
				eigenvectors[index] = eigenvectors[i];
				eigenvectors[i] = a;
			}

			int eig_ndx = 0;
			for (int i = 0; i < this.numberOfHashTables; i++){
				double vectors[][] = new double[this.numberOfHashFunctions][this.numberOfImageFeatures];

				for (int j = 0; j < this.numberOfHashFunctions; j++)
				{
					vectors[j] = eigenvectors[eig_ndx];
					eig_ndx++;
				}

				this.hashTables.add(new HashTable(this.numberOfHashFunctions, this.numberOfImageFeatures, this.slotWidth, vectors, randomNumberGenerator));
			}
		}
	}
	
	/**
	 * Read image urls, extract image features and put the image in its bucket
	 * expected format for the file is a csv of <id>,<url> for each line
	 */
	private void createImageIndex() {
		URL imageUrl = null;
		int availableCoresForThreads = Runtime.getRuntime().availableProcessors() / 2;
		ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(availableCoresForThreads);
		Semaphore s = new Semaphore(0);

		int count = 0;

		try {
			final CSVParser parser =
					new CSVParserBuilder()
							.withSeparator('\t')
							.withIgnoreQuotations(true)
							.build();
			final CSVReader reader =
					new CSVReaderBuilder(new FileReader(this.imageUrls))
							.withCSVParser(parser)
							.build();
			String[] fileLine = reader.readNext();
			while (fileLine != null) {
				count = count + 1;
				System.out.println("Number of processed lines is: " + count);
				//Download the image
				try {
					// first element is the id of the image, second element is the url of the image
					String id = fileLine[0];
					String url = fileLine[1];
					imageUrl = new URL(url);
				} catch (Exception e) {
					System.err.println("Error loading url [id]: url " + "["+ fileLine[0] + "]:" + fileLine[1] + ". Exception: "+e.getMessage()+ ":" + ExceptionUtils.getRootCause(e) + ". File skipped.");
					String[] stackTrace = ExceptionUtils.getRootCauseStackTrace(e);
					for(String trace : stackTrace) {
						System.err.println(trace);
					}
					continue;
				}

					ProcessImageRunner runner = new ProcessImageRunner(fileLine[0], imageUrl, this, s);
					pool.execute(runner);
					fileLine = reader.readNext();
			}

			System.out.println("pool completed task count size: " +pool.getCompletedTaskCount());
			System.out.println("pool task count size: " +pool.getTaskCount());
			System.out.println("pool getPoolSize: " +pool.getPoolSize());
			System.out.println("pool getActiveCount: " +pool.getActiveCount());
			reader.close();
			pool.shutdown();
			s.acquire(count);
		}
		catch (java.lang.InterruptedException e) {
			// If thread is interrupted it should exit the semaphore lock.
			pool.shutdownNow();
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Similar method for testing with simple data
	 */
	private void createTestImageIndex() {
		File file = new File("lsh/test/test_data.txt");

		// Construct BufferedReader from FileReader
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			// Parse and construct file
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] elements = line.split("\\s+");
				List<Double> vector = new ArrayList<Double>();
				for (int i = 0; i < this.numberOfImageFeatures; i++) {
					vector.add(Double.parseDouble(elements[i]));
				}
				this.rawFeatureVectors.add(new SearchableObject(vector, null));
			}
            br.close();
        } catch(IOException e) {
			System.out.println("Error reading data.");
		}
	}

	
	public List<HashTable> getImageIndex() { return this.hashTables; }

	public List<SearchableObject> getRawFeatureVectors() {return this.rawFeatureVectors; }

	public void putImageFeatures(URL imageUrl, List<Double> imageFeatures) {
		this.imageFeatures.put(imageUrl, imageFeatures);
	}

	public void putRawFeatureVectors(SearchableObject obj) {
		this.rawFeatureVectors.add(obj);
	}
}
