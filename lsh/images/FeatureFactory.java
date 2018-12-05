package images;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.imageio.*;
import java.io.*;
import javax.media.jai.*;
import java.net.URL;
import java.util.List;
import java.awt.*;
import java.util.ArrayList;

public class FeatureFactory implements Serializable {

    // TODO these should be dynamically determined
    public static final int HISTOGRAM_SLOTS_PER_BAND = 60;
    public static final int NUMBER_OF_BANDS_USED = 4;

// Example usage
//    FeatureFactory ff = new FeatureFactory();
//    String userInput = null;
//    Scanner scanner = new Scanner(System.in);
//
//        System.out.print("Enter image url: ");
//    userInput = scanner.next();
//    BufferedImage img = ff.returnImageFromPath(userInput);
//    ff.imageHistogram(img);

    public BufferedImage returnImageFromPath(String url) {
        try {
            BufferedImage original = ImageIO.read(new URL(url).openStream());
            return resizeImage(original);
        } catch (IOException e) {
            System.out.println("The image was not loaded.");
            return null;
        }
    }

    private BufferedImage resizeImage(BufferedImage original){
        Image image = original.getScaledInstance(60, 60, Image.SCALE_SMOOTH); //Image.SCALE_SMOOTH
        BufferedImage newImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public SerializableHistogram imageHistogram(String url, String id){
        BufferedImage image = this.returnImageFromPath(url);
        Histogram hist = new Histogram(HISTOGRAM_SLOTS_PER_BAND, 0, HISTOGRAM_SLOTS_PER_BAND, image.getRaster().getNumBands());
        hist.countPixels(image.getRaster(), null, 0, 0, 1, 1);
        int[][] bins = hist.getBins();
        List<Double> histList = new ArrayList<Double>(NUMBER_OF_BANDS_USED * HISTOGRAM_SLOTS_PER_BAND);
        for(int j = 0; j < NUMBER_OF_BANDS_USED; j++) {
            for (int i = 0; i < HISTOGRAM_SLOTS_PER_BAND; i++) {
                histList.add(Double.valueOf(bins[j][i]));
            }
        }

        return new SerializableHistogram(histList, id, url);
    }

    // for pulling from file system rather than url
    public SerializableHistogram imageHistogram(BufferedImage image, String id){
        image = resizeImage(image);
        Histogram hist = new Histogram(HISTOGRAM_SLOTS_PER_BAND, 0, HISTOGRAM_SLOTS_PER_BAND, image.getRaster().getNumBands());
        hist.countPixels(image.getRaster(), null, 0, 0, 1, 1);
        int[][] bins = hist.getBins();
        List<Double> histList = new ArrayList<Double>(NUMBER_OF_BANDS_USED * HISTOGRAM_SLOTS_PER_BAND);
        for(int j = 0; j < NUMBER_OF_BANDS_USED; j++) {
            for (int i = 0; i < HISTOGRAM_SLOTS_PER_BAND; i++) {
                histList.add(Double.valueOf(bins[j][i]));
            }
        }

        return new SerializableHistogram(histList, id, null);
    }
}
