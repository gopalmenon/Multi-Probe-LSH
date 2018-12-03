package experiments;

import indexing.ImageIndex;

import java.awt.image.BufferedImage;
import java.io.File;

// orchestrates the completion of various experiments
// on the classes
public class Runner {

    public static int slotWidth = 10000;
    public static int numberOfImagesToQuery = 100; // default provided by the paper, see section 5.1
    public static int kNeighborsCount = 20; // default provided by the paper, see section 5.1

    // values for the paper are
    // width = .7
    // numberHashFunction = 16

    public void searchForXImages(int size){

    }

    public void calculateRecall(){

    }

    public void calculateErrorRatio(){

    }

    public void calculateConfusionMatrixForExperiment() {

    }

    public void writeResultToFile(){

    }

    public void createSimilarIndexOfType(String type) {
        // TODO this will need to take advantage of the indexing properties of the imagenet project to
        // consume and display things
    }

    public void createIndex(int sizeOfIndex, int numberOfQueries, int hashFunctions, int hashTables, boolean eigen, int numberOfProbes) {
        // uses this slotWidth by default to determine slotWidth
//        this.slotWidth;
    }

    public void createIndexOfSize(int size, int hashFunctions, int hashTables, boolean eigen, int slotWidth, int numberOfProbes) {

    }

    public void caclulateBruteForceDistancesForImage(BufferedImage i){

    }

    // R value is the nearest neighbor distance brute forced
    // we're going to try calculating R value for the top 20 items
    // by brute force then return that max number
    // need to try different amount of hash tables to find ideal M and L for dataset -> r, 2r, 4r, etc
    public void calculateRValueForImage(BufferedImage image){

    }

    public void runExperimentsList(File list){

    }

    public void runSimilarImagesExperiments(){

    }
}
