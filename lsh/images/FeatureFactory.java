package images;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.imageio.*;
import java.io.*;
import javax.media.jai.*;
import java.awt.*;
import java.util.ArrayList;

public class FeatureFactory implements Serializable {

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

    public ArrayList<Integer> imageHistogram(BufferedImage image){
        Histogram hist = new Histogram(60, 0, 60, 4);
        hist.countPixels(image.getRaster(), null, 0, 0, 1, 1);
        int[][] bins = hist.getBins();
        ArrayList<Integer> histList = new ArrayList<Integer>(240);
        for(int j = 0; j < 4; j++) {
            for (int i = 0; i < 60; i++) {
                histList.add(bins[j][i]);
            }
        }
        return histList;
    }
}
