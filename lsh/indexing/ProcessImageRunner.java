package indexing;

import images.FeatureFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

public class ProcessImageRunner extends Thread {

    URL url;
    String id;
    ImageIndex parent;

    public ProcessImageRunner(String id, URL url, ImageIndex parent){
        this.url = url;
        this.id = id;
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            BufferedImage image = ImageIO.read(this.url.openStream());
            //Create color histogram for the image
            List<Double> imageFeatures = new FeatureFactory().imageHistogram(image);
            //Store the image features for later use
            synchronized (parent) {
                parent.putImageFeatures(url, imageFeatures);
                parent.putRawFeatureVectors(new SearchableObject(imageFeatures, url));
            }
        } catch (Exception e) {
            System.err.println("Error processing [id]: url " + "[" + id + "]:" + url.toString() + ". Exception: " + e.getMessage() + ":" + ExceptionUtils.getRootCause(e) + ". File skipped.");
            String[] stackTrace = ExceptionUtils.getRootCauseStackTrace(e);
            for (String trace : stackTrace) {
                System.err.println(trace);
            }
        }
    }
}
