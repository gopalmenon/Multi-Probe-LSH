package images;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.imageio.*;
import java.io.*;
import javax.media.jai.*;
import java.util.List;
import java.awt.*;
import java.util.ArrayList;

public class FeatureFactory implements Serializable {

    public static final int HISTOGRAM_SLOTS_PER_BAND = 60;
    public static final int NUMBER_OF_BANDS_USED = 3;

// Example usage
//    FeatureFactory ff = new FeatureFactory();
//    String userInput = null;
//    Scanner scanner = new Scanner(System.in);
//
//        System.out.print("Enter image url: ");
//    userInput = scanner.next();
//    BufferedImage img = ff.returnImageFromPath(userInput);
//    ff.imageHistogram(img);

    public BufferedImage returnImageFromPath(String filename) {
        try {
            BufferedImage original = ImageIO.read(new File(filename));
            Image image = original.getScaledInstance(60, 60, 3); //Image.SCALE_SMOOTH
            BufferedImage newImage = new BufferedImage(
                    image.getWidth(null), image.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = newImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
           // newImage.getRGB(1,2);
            return newImage;
        } catch (IOException e) {
            System.out.println("The image was not loaded.");
            return null;
            //System.exit(1);
        }
    }

    public List<Double> imageHistogram(BufferedImage image){
        Histogram hist = new Histogram(HISTOGRAM_SLOTS_PER_BAND, 0, HISTOGRAM_SLOTS_PER_BAND, image.getRaster().getNumBands());
        hist.countPixels(image.getRaster(), null, 0, 0, 1, 1);
        int[][] bins = hist.getBins();
        List<Double> histList = new ArrayList<Double>(NUMBER_OF_BANDS_USED * HISTOGRAM_SLOTS_PER_BAND);
        for(int j = 0; j < NUMBER_OF_BANDS_USED; j++) {
            for (int i = 0; i < HISTOGRAM_SLOTS_PER_BAND; i++) {
                histList.add(Double.valueOf(bins[j][i]));
            }
        }
        return histList;
    }
}
