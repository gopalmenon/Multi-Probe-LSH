package lsh;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lsh.indexing.ImageIndex;

public class RunLSH {

	public static void main(String[] parameters) {
		
		RunLSH runLSH = new RunLSH();
		boolean loadIndex = true;
		if (parameters.length > 0) {
			if (!"Y".equals(parameters[0])) {
				loadIndex = false;
			}
			
		}
			
		runLSH.createImageIndex(loadIndex);
		
	}
	
	private void createImageIndex(boolean loadIndex) {
		
		ImageIndex imageIndex = null;
		
		if (loadIndex) {
			try {    
				//Load the saved index 
				FileInputStream savedIndexFile = new FileInputStream(ImageIndex.SAVED_INDEX_FILE); 
				ObjectInputStream in = new ObjectInputStream(savedIndexFile); 
				imageIndex = (ImageIndex)in.readObject(); 
				in.close(); 
				savedIndexFile.close(); 
			} catch(IOException e) { 
				System.err.println("IOException is caught. Could not load index.");
				System.exit(0);
			} catch(ClassNotFoundException ex) { 
				System.err.println("ClassNotFoundException is caught. Could not load index."); 
} 
		} else {
			//Create the index and save it
			imageIndex = new ImageIndex(10, 2, 5, 1.0, false, null);
			try {    
	            FileOutputStream savedIndexFile = new FileOutputStream(ImageIndex.SAVED_INDEX_FILE); 
	            ObjectOutputStream out = new ObjectOutputStream(savedIndexFile); 
	            out.writeObject(imageIndex); 
	            out.close(); 
	            savedIndexFile.close(); 
	        } catch(IOException ex) { 
	            System.out.println("IOException is caught. Could not save index."); 
	        } 
		}
		
	}
	
}
