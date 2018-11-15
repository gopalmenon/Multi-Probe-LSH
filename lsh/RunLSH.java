package lsh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import lsh.indexing.ImageIndex;

public class RunLSH {
	
	public static final String USER_QUIT_INPUT = "Quit";
	
	private ImageIndex imageIndex;

	public static void main(String[] parameters) {
		
		RunLSH runLSH = new RunLSH();
		boolean loadIndex = false;
		if (runLSH.indexExists()) {
			loadIndex = true;
		}
			
		runLSH.createImageIndex(loadIndex);
		runLSH.processUserQueries();
		
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
			this.imageIndex = new ImageIndex(10, 2, 5, 1.0, false, null);
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
	
}
