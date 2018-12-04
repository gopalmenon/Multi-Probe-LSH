package images;

import indexing.SearchableObject;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.Semaphore;

public class PreprocessImageFeaturesRunner extends Thread {
    String urlString;
    String id;
    ImageDaemon parent;
    Semaphore s;

    public PreprocessImageFeaturesRunner(String id, String url, ImageDaemon parent, Semaphore s){
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
                parent.putToList(new SearchableObject(imageFeatures));
            }
        } catch (Exception e) {
            synchronized (parent) {
                parent.putMissing();
            }
            System.err.println("Error processing [id]: url " + "[" + id + "]:" + urlString + ". Exception: " + e.getMessage() + ":" + ExceptionUtils.getRootCause(e) + ". File skipped.");
            String[] stackTrace = ExceptionUtils.getRootCauseStackTrace(e);
            for (String trace : stackTrace) {
                System.err.println(trace);
            }
        } finally {
            s.release();
        }
    }
}
