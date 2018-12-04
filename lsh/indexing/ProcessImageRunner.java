package indexing;

import images.FeatureFactory;
import images.SerializableHistogram;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ProcessImageRunner extends Thread {

    String urlString;
    String id;
    ImageIndex parent;
    Semaphore s;

    public ProcessImageRunner(String id, String url, ImageIndex parent, Semaphore s){
        this.urlString = url;
        this.id = id;
        this.parent = parent;
        this.s = s;
    }

    @Override
    public void run() {
        try {
            //Create color histogram for the image
            SerializableHistogram imageFeatures = new FeatureFactory().imageHistogram(urlString, id);
            //Store the image features for later use
            synchronized (parent) {
                parent.putRawFeatureVectors(new SearchableObject(imageFeatures));
            }
        } catch (Exception e) {
            System.err.println("Error processing [id]: url " + "[" + id + "]:" + urlString + ". Exception: " + e.getMessage() + ":" + ExceptionUtils.getRootCause(e) + ". File skipped.");
            String[] stackTrace = ExceptionUtils.getRootCauseStackTrace(e);
            for (String trace : stackTrace) {
                System.err.println(trace);
            }
        }

        s.release();
    }
}
