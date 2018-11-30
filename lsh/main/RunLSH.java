package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Arrays;


import indexing.ImageIndex;
import indexing.HashTable;
import indexing.SearchableObject;
import querying.Perturbation;
import querying.PerturbationSequenceMapping;
import querying.PerturbationSequences;

public class RunLSH {
	
	public static final String USER_QUIT_INPUT = "Quit";
	
	private ImageIndex imageIndex;
	private int NUMBER_OF_DIMENSIONS = 120;
	private int NUMBER_OF_HASHFUNCTIONS = 10;
	private int NUMBER_OF_HASHTABLES = 10;
	private double SLOT_WIDTH = 15.0;
	private boolean USE_EIGENVECTORS = false;
	private int K = 3;


	public static void main(String[] parameters) {
		boolean test = true;
		RunLSH runLSH = new RunLSH();
		if (test) {
			runLSH.createTestImageIndex();
			runLSH.processUserTestQueries();
		}
		else {
			boolean loadIndex = false;
			if (runLSH.indexExists()) {
				loadIndex = true;
			}

			runLSH.createImageIndex(loadIndex);
			runLSH.processUserQueries();
		}
	}
	
	private void createImageIndex(boolean loadIndex) {
				
		if (loadIndex) {
			try {    
				//Load the saved index 
				FileInputStream savedIndexFile = new FileInputStream(ImageIndex.SAVED_INDEX_FILE); 
				ObjectInputStream in = new ObjectInputStream(savedIndexFile); 
				this.imageIndex = (ImageIndex)in.readObject(); 
				in.close(); 
				savedIndexFile.close(); 
			} catch(IOException e) { 
				System.err.println("IOException is caught. Could not load index.");
				System.exit(0);
			} catch(ClassNotFoundException e) { 
				System.err.println("ClassNotFoundException is caught. Could not load index."); 
			} 
		} else {
			//Create the index and save it
			this.imageIndex = new ImageIndex(this.NUMBER_OF_HASHFUNCTIONS, this.NUMBER_OF_HASHTABLES, this.NUMBER_OF_DIMENSIONS, this.SLOT_WIDTH, this.USE_EIGENVECTORS, null, false);
			try {    
	            FileOutputStream savedIndexFile = new FileOutputStream(ImageIndex.SAVED_INDEX_FILE);
	            ObjectOutputStream out = new ObjectOutputStream(savedIndexFile); 
	            out.writeObject(this.imageIndex); 
	            out.close(); 
	            savedIndexFile.close(); 
	        } catch(IOException e) {
				System.out.println("IOException is caught. Could not save index.");
			}
		}
		
	}
	
	/**
	 * @return true if index file exists on disk
	 */
	private boolean indexExists() {
		
		File indexFile = new File(ImageIndex.SAVED_INDEX_FILE);
		if (indexFile.exists() && !indexFile.isDirectory()) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private void processUserQueries() {
		
		String userInput = null;
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Enter image url: ");
		userInput = scanner.next();
		
		//Repeat till user quits
		while (!USER_QUIT_INPUT.equalsIgnoreCase(userInput)) {
			
			//Query hash tables and return exact matches
			
			
			//Run multi-probe and return neighboring bucket contents
			
			System.out.print("Enter image url: ");
			userInput = scanner.next();

		}
		
	}

	private void createTestImageIndex() {
		this.imageIndex = new ImageIndex(this.NUMBER_OF_HASHFUNCTIONS, this.NUMBER_OF_HASHTABLES, this.NUMBER_OF_DIMENSIONS, this.SLOT_WIDTH, this.USE_EIGENVECTORS, null, true);
		try {
			FileOutputStream savedIndexFile = new FileOutputStream(ImageIndex.SAVED_INDEX_FILE);
			ObjectOutputStream out = new ObjectOutputStream(savedIndexFile);
			out.writeObject(this.imageIndex);
			out.close();
			savedIndexFile.close();
		} catch(IOException e) {
			System.out.println("IOException is caught. Could not save test index.");
		}
	}

	private void processUserTestQueries() {
		System.out.print("Running with vector v = (0.5, ..., 0.5): \n");

		// Make testing vector v = (0.5, ..., 0.5)
		List<Integer> vector = new ArrayList<Integer>();
		for (int i = 0; i < this.NUMBER_OF_DIMENSIONS; i++)
			vector.add(Integer.valueOf(1));

		// Find absolute K - Nearest Neighbors using raw data before hashing
		System.out.println("Begin finding raw K nearest neighbors");
		List<SearchableObject> KRawNearestNeighbors = getRawKNeaerestNeighbors(vector);
		System.out.println("Found " + this.K + " nearest neighbors");
		for (SearchableObject object : KRawNearestNeighbors)
			System.out.println("Distance is " + object.distanceTo(new SearchableObject(vector, null)));

		// Get perturbation vectors to use in query
		PerturbationSequences sequence = new PerturbationSequences(this.NUMBER_OF_HASHFUNCTIONS, this.SLOT_WIDTH, this.K);

		//Query hash tables and return exact matches after hashing. Note this should return the same as
		// using the raw data.
		List<SearchableObject> KNearestNeighbors = getExactKNearestNeighbors(vector);
		System.out.println("Found " + this.K + " nearest neighbors");
		for (SearchableObject object : KNearestNeighbors)
		{
////				for (Double element : object.getObjectFeatures()) {
////					System.out.printf("%f ", element.doubleValue());
////				}
//				//System.out.println();
			System.out.println("Distance is " + object.distanceTo(new SearchableObject(vector, null)));
		}

		// Now use multiprobe
		System.out.println("\nBEGIN RUNNING MULTIPROBE\n");

		//Run multi-probe and return neighboring bucket contents
		KNearestNeighbors = getMultiProbeNearestNeighbors(vector, sequence.getPerturbations());

		// Construct primitive array for sorting
		double distances[] = new double[KNearestNeighbors.size()];
		int dist_ndx = 0;
		for (SearchableObject object : KNearestNeighbors)
		{
			distances[dist_ndx] = object.distanceTo(new SearchableObject(vector, null));
			dist_ndx++;
		}

//		for (SearchableObject object : KNearestNeighbors)
//		{
//			System.out.println("Distance is " + object.distanceTo(new SearchableObject(vector, null)));
//		}

		// Sort and print results
		Arrays.sort(distances);

		System.out.println("Sorted K distances is:");
		for (dist_ndx = 0; dist_ndx < this.K; dist_ndx++)
			System.out.println(distances[dist_ndx]);
	}

	// Return exact nearest neighbors using hashing
	private List<SearchableObject> getExactKNearestNeighbors(List<Integer> vector)
	{
		List<SearchableObject> KNearestNeighbors = new ArrayList<SearchableObject>();
		List<Integer> MAX_VECTOR = new ArrayList<Integer>();
		for (int i = 0; i < this.NUMBER_OF_DIMENSIONS; i++)
			MAX_VECTOR.add(Integer.MAX_VALUE);

		// Find K-Nearest Neighbors
		for (int i = 0; i < this.K; i++) {
			SearchableObject min_vector = new SearchableObject(MAX_VECTOR, null);

			SearchableObject queryObject = new SearchableObject(vector, null);
			for (HashTable hashTable : this.imageIndex.getImageIndex()) {
				for (SearchableObject object : hashTable.getAllObjects()) {
					if ((object.distanceTo(queryObject) < min_vector.distanceTo(queryObject)) &&
							(!KNearestNeighbors.contains(object)))  {
						min_vector = new SearchableObject(object.getObjectFeatures(), null);
					}
				}
			}
			KNearestNeighbors.add(min_vector);
		}
		return removeDuplicates(KNearestNeighbors);
	}

	private List<SearchableObject> getMultiProbeNearestNeighbors(List<Integer> queryVector, List<Perturbation> perturbations)
	{
		PerturbationSequenceMapping runMultiProbe = new PerturbationSequenceMapping(this.NUMBER_OF_HASHFUNCTIONS, this.SLOT_WIDTH, this.K, this.NUMBER_OF_HASHTABLES, this.imageIndex.getImageIndex(), queryVector, perturbations);
		return removeDuplicates(runMultiProbe.getQueryResults());
	}

	// Return raw nearest neighbors without yet hashing
	private List<SearchableObject> getRawKNeaerestNeighbors(List<Integer> vector)
	{
		List<SearchableObject> data = this.imageIndex.getRawFeatureVectors();

		List<SearchableObject> KNearestNeighbors = new ArrayList<SearchableObject>();
		List<Integer> MAX_VECTOR = new ArrayList<Integer>();
		for (int i = 0; i < this.NUMBER_OF_DIMENSIONS; i++)
			MAX_VECTOR.add(Integer.MAX_VALUE);

		// Find K-Nearest Neighbors
		for (int i = 0; i < this.K; i++) {
			SearchableObject min_vector = new SearchableObject(MAX_VECTOR, null);

			SearchableObject queryObject = new SearchableObject(vector, null);
			for (SearchableObject object : data) {
				if ((object.distanceTo(queryObject) < min_vector.distanceTo(queryObject)) &&
						(!KNearestNeighbors.contains(object)))  {
					min_vector = new SearchableObject(object.getObjectFeatures(), null);
				}
			}

			KNearestNeighbors.add(min_vector);
		}
		return removeDuplicates(KNearestNeighbors);

	}

	// Due to the existence of multiple hashtables, we originally get many duplicates.
	// This removes them.
	private ArrayList<SearchableObject> removeDuplicates(List<SearchableObject> vector)
	{
		Set<SearchableObject> hs = new HashSet<>();
		hs.addAll(vector);
		vector.clear();
		vector.addAll(hs);
		return new ArrayList(vector);
	}


}
