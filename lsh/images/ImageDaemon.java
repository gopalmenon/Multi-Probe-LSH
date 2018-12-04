package images;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import indexing.HashTable;
import indexing.ProcessImageRunner;
import indexing.SearchableObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

// collects images from the underworld
// Used like so
//        ImageDaemon i = new ImageDaemon();
//        i.collectImages();
//        System.out.println("****** Daemon finished!");
public class ImageDaemon {
    File imageUrls;
    private List<SearchableObject> objects;
    public int missingCount;

    public ImageDaemon(){
        System.setProperty("sun.net.client.defaultReadTimeout", "500");
        System.setProperty("sun.net.client.defaultConnectTimeout", "500");
        this.objects = new ArrayList<>();
        this.imageUrls = new File("image_data/images/test");
    }

    public void collectImages(){
        URL imageUrl = null;
        int availableCoresForThreads = Runtime.getRuntime().availableProcessors() / 2;
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(availableCoresForThreads);
        Semaphore s = new Semaphore(0);

        int count = 0;

        try {
            final CSVParser parser =
                    new CSVParserBuilder()
                            .withSeparator('\t')
                            .withIgnoreQuotations(true)
                            .build();
            final CSVReader reader =
                    new CSVReaderBuilder(new FileReader(this.imageUrls))
                            .withCSVParser(parser)
                            .build();
            String[] fileLine = reader.readNext();
            while (fileLine != null) {
                count = count + 1;
                System.out.println("Number of processed lines is: " + count);
                //Download the image
                String id = fileLine[0];
                String url = fileLine[1];

                PreprocessImageFeaturesRunner runner = new PreprocessImageFeaturesRunner(id, url, this, s);
                pool.execute(runner);
                fileLine = reader.readNext();
            }

            reader.close();
            pool.shutdown();
            s.acquire(count);
            System.out.println("Semaphore complete. *********");
            // TODO individual writes might be useful / quicker
            this.putToFile();
        }
        catch (java.lang.InterruptedException e) {
            // If thread is interrupted it should exit the semaphore lock.
            pool.shutdownNow();
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putToFile() {
        System.out.println("*********");
        System.out.println("Writing "+ this.objects.size() + " features to disk.");
        System.out.println("Missing "+ this.missingCount + " features.");
        try {
            File file = new File("featuresFromDaemon.set");
            FileOutputStream savedIndexFile = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(savedIndexFile);
            out.writeObject(this.objects);
            out.close();
            savedIndexFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putToList(SearchableObject searchableObject) {
        this.objects.add(searchableObject);
    }

    public void putMissing(){
        missingCount = missingCount + 1;
    }
}
