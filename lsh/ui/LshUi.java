package ui;

import images.FeatureFactory;
import indexing.ImageIndex;
import indexing.SearchableObject;
import querying.SearchableObjectComparator;
import querying.Perturbation;
import querying.PerturbationSequenceMapping;
import querying.PerturbationSequences;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

public class LshUi {

    public static final int DEFAULT_WIDTH = 640, DEFAULT_HEIGHT = 480;

    public static final int DEFAULT_NUMBER_OF_HASHFUNCTIONS = 4;
    public static final int DEFAULT_NUMBER_OF_HASHTABLES = 1;
    public static final int DEFAULT_NEAREST_NEIGHBORS_TO_SEARCH = 5;
    public static final int DEFAULT_MAX_SEARCH_RESULTS_TO_DISPLAY = 25;
    public static final int DEFAULT_NUMBER_OF_DIMENSIONS = 180;
    public static final double DEFAULT_SLOT_WIDTH = 1000000.0;
    public static final boolean DEFAULT_USE_EIGENVECTORS = false;

    private int numberOfHashFunctions, numberOfHashTables, numberOfDimensions, nearestNeighborsToSearch;
    private double slotWidth;
    private boolean useEigenVectorsForHashing;
    private ImageIndex imageIndex;
    private JMenu searchMenu, settingsMenu;
    private JPanel gui;
    private JFileChooser fileChooser;
    FilenameFilter fileNameFilter;
    private JMenuBar menuBar;
    private DefaultListModel model;

    LshUi() {

        this.numberOfHashFunctions = DEFAULT_NUMBER_OF_HASHFUNCTIONS;
        this.numberOfHashTables = DEFAULT_NUMBER_OF_HASHTABLES;
        this.numberOfDimensions = DEFAULT_NUMBER_OF_DIMENSIONS;
        this.slotWidth = DEFAULT_SLOT_WIDTH;
        this.useEigenVectorsForHashing = DEFAULT_USE_EIGENVECTORS;
        this.nearestNeighborsToSearch = DEFAULT_NEAREST_NEIGHBORS_TO_SEARCH;

        gui = new JPanel(new GridLayout());

        JPanel imageViewContainer = new JPanel(new GridBagLayout());
        final JLabel imageView = new JLabel();
        imageViewContainer.add(imageView);

        model = new DefaultListModel();
        final JList imageList = new JList(model);
        imageList.setCellRenderer(new IconCellRenderer());
        ListSelectionListener listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                Object o = imageList.getSelectedValue();
                if (o instanceof BufferedImage) {
                    imageView.setIcon(new ImageIcon((BufferedImage)o));
                }
            }

        };
        imageList.addListSelectionListener(listener);

        fileChooser = new JFileChooser();
        String[] imageTypes = ImageIO.getReaderFileSuffixes();
        FileNameExtensionFilter fnf = new FileNameExtensionFilter("Images", imageTypes);
        fileChooser.setFileFilter(fnf);
        File userHome = new File(System.getProperty("user.home"));
        fileChooser.setSelectedFile(userHome);



        fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return true;
            }
        };

        //Create user menus starting with File menu
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem buildIndexMenuItem = new JMenuItem("Build Index");
        buildIndexMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {buildIndex();} });
        fileMenu.add(buildIndexMenuItem);
        JMenuItem loadIndexMenuItem = new JMenuItem("Load Index");
        loadIndexMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {loadIndex();} });
        fileMenu.add(loadIndexMenuItem);
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {System.exit(0);} });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        //Search menu
        searchMenu = new JMenu("Search");
        JMenuItem searchByUrlMenuItem = new JMenuItem("Search  by URL");
        searchByUrlMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {searchByUrl();} });
        searchMenu.add(searchByUrlMenuItem);
        JMenuItem searchByFileMenuItem = new JMenuItem("Search  by Image File");
        searchByFileMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {searchByImageFile();} });
        searchMenu.add(searchByFileMenuItem);
        JMenuItem bruteForceSearchByUrlMenuItem = new JMenuItem("Brute Force Search  by URL");
        bruteForceSearchByUrlMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {bruteForceSearchByUrl();} });
        searchMenu.add(bruteForceSearchByUrlMenuItem);
        menuBar.add(searchMenu);
        searchMenu.setEnabled(false);

        //Settings menu
        settingsMenu = new JMenu("Settings");
        JMenuItem numberOfHashFunctionsMenuItem = new JMenuItem("Set Number of Hash Functions");
        numberOfHashFunctionsMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {setNumberOfHashFunctions();} });
        settingsMenu.add(numberOfHashFunctionsMenuItem);
        JMenuItem numberOfHashTablesMenuItem = new JMenuItem("Set Number of Hash Tables");
        numberOfHashTablesMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {setNumberOfHashTables();} });
        settingsMenu.add(numberOfHashTablesMenuItem);
        JMenuItem numberOfDimensionsMenuItem = new JMenuItem("Set Number of Dimensions");
        numberOfDimensionsMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {setNumberOfDimensions();} });
        settingsMenu.add(numberOfDimensionsMenuItem);
        JMenuItem slotWidthMenuItem = new JMenuItem("Set Slot Width");
        slotWidthMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {setSlotWidth();} });
        settingsMenu.add(slotWidthMenuItem);
        JMenuItem useEigenVectorHashCheckBoxMenuItem = new JCheckBoxMenuItem("Hash with Eigen Vectors");
        useEigenVectorHashCheckBoxMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {toggleUseEigenVectors();} });
        settingsMenu.add(useEigenVectorHashCheckBoxMenuItem);
        menuBar.add(settingsMenu);

        //Metrics menu
        JMenu metricsMenu = new JMenu("Metrics");
        menuBar.add(metricsMenu);


        gui.add(new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(
                        imageList,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
                new JScrollPane(imageViewContainer)));
    }

    private void loadImages(List<SearchableObject> similarImages, List<Double> imageToSearchFeatures)  {

        int imageCounter = 0, numberOfImagesToShow = Math.min(DEFAULT_MAX_SEARCH_RESULTS_TO_DISPLAY, similarImages.size());
        BufferedImage[] images = new BufferedImage[numberOfImagesToShow];
        model.removeAllElements();
        BufferedImage image = null;

        List<SearchableObject> sortedImages = getSortedResults(similarImages, imageToSearchFeatures);

        for (SearchableObject searchableObject : sortedImages) {
            if (imageCounter++ < numberOfImagesToShow) {
                try {
                    image = ImageIO.read(searchableObject.getObjectUrl());
                    model.addElement(image);
                } catch (Exception e) {}
            } else {
                break;
            }
        }
    }

    /**
     * Sort the query result by increasing distance from query object
     * @param similarImages
     * @return
     */
    private List<SearchableObject> getSortedResults(List<SearchableObject> similarImages, List<Double> imageToSearchFeatures) {

        SearchableObjectComparator resultsComparator = new SearchableObjectComparator(new SearchableObject(imageToSearchFeatures, null));
        Collections.sort(similarImages, resultsComparator);
        return similarImages;

    }

    public Container getGui() {
        return gui;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Select image urls from a file and build an index. Prompt the user to save the index.
     */
    private void buildIndex() {

        File imageUrlsFile = null;

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select file containing image urls:");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            imageUrlsFile = jfc.getSelectedFile();
        } else {
            return;
        }

        //Create the index and save it
        this.imageIndex = new ImageIndex(this.numberOfHashFunctions, this.numberOfHashTables, this.numberOfDimensions, this.slotWidth, this.useEigenVectorsForHashing, imageUrlsFile, false);
        this.searchMenu.setEnabled(true);
        this.settingsMenu.setEnabled(false);
        try {

            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setDialogTitle("Specify file to save index in:");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (!fileChooser.getSelectedFile().isDirectory()) {
                    FileOutputStream savedIndexFile = new FileOutputStream(fileChooser.getSelectedFile());
                    ObjectOutputStream out = new ObjectOutputStream(savedIndexFile);
                    out.writeObject(this.imageIndex);
                    out.close();
                    savedIndexFile.close();
                }
            }


        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "IOException was caught. Could not save index.");
            System.out.println("IOException was caught. Could not save index.");
        }

    }

    private void loadIndex() {


        File imageIndexFile = null;

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select index file:");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            imageIndexFile = jfc.getSelectedFile();
        } else {
            return;
        }

        try {
            //Load the saved index
            FileInputStream savedIndexFile = new FileInputStream(imageIndexFile);
            ObjectInputStream in = new ObjectInputStream(savedIndexFile);
            this.imageIndex = (ImageIndex)in.readObject();
            in.close();
            savedIndexFile.close();
            this.searchMenu.setEnabled(true);
            this.settingsMenu.setEnabled(false);
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "IOException was caught. Could not load index.");
            System.err.println("IOException was caught. Could not load index.");
        } catch(ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Selected file does not contain an index.");
            System.err.println("ClassNotFoundException is caught. Could not load index.");
        }
    }

    private void searchByUrl() {

        String urlStringSuppliedByUser = null;
        while(true) {
            urlStringSuppliedByUser = JOptionPane.showInputDialog("ImageURL: ");
            if (urlStringSuppliedByUser == null) {
                break;
            }
            try {
                BufferedImage image = ImageIO.read(new URL(urlStringSuppliedByUser).openStream());
                if (image == null) {
                    JOptionPane.showMessageDialog(null, "Could not obtain image from URL.");
                } else {
                    searchForImageInIndex(image);
                    break;
                }
            } catch (MalformedURLException e) {
                JOptionPane.showMessageDialog(null, "MalformedURLException was caught. Could not obtain image from URL.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "IOException was caught. Could read in the image from URL.");
            }
        }

    }

    private void bruteForceSearchByUrl() {

        String urlStringSuppliedByUser = null;
        while(true) {
            urlStringSuppliedByUser = JOptionPane.showInputDialog("ImageURL: ");
            if (urlStringSuppliedByUser == null) {
                break;
            }
            try {
                BufferedImage image = ImageIO.read(new URL(urlStringSuppliedByUser).openStream());
                if (image == null) {
                    JOptionPane.showMessageDialog(null, "Could not obtain image from URL.");
                } else {
                    List<Double> imageFeatures = new FeatureFactory().imageHistogram(image);
                    List<SearchableObject>  searchResults = getRawKNeaerestNeighbors(imageFeatures);
                    if (searchResults.size() == 0) {
                        JOptionPane.showMessageDialog(null, "No matches found.");
                    } else {
                        loadImages(searchResults, imageFeatures);
                    }
                    break;
                }
            } catch (MalformedURLException e) {
                JOptionPane.showMessageDialog(null, "MalformedURLException was caught. Could not obtain image from URL.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "IOException was caught. Could read in the image from URL.");
            }
        }


    }

    private void searchForImageInIndex(BufferedImage imageToSearch) {

        List<Double> imageFeatures = new FeatureFactory().imageHistogram(imageToSearch);
        PerturbationSequences perturbationSequences = new PerturbationSequences(this.numberOfHashFunctions, this.slotWidth, this.nearestNeighborsToSearch);
        List<SearchableObject> kNearestNeighbors = getMultiProbeNearestNeighbors(imageFeatures, perturbationSequences.getPerturbations());
        if (kNearestNeighbors.size() == 0) {
            JOptionPane.showMessageDialog(null, "No matches found.");
        } else {
            loadImages(kNearestNeighbors, imageFeatures);
        }

    }


    private java.util.List<SearchableObject> getMultiProbeNearestNeighbors(java.util.List<Double> queryVector, java.util.List<Perturbation> perturbations)
    {
        PerturbationSequenceMapping runMultiProbe = new PerturbationSequenceMapping(this.numberOfHashFunctions, this.slotWidth, this.nearestNeighborsToSearch, this.numberOfHashTables, this.imageIndex.getImageIndex(), queryVector, perturbations);
        return removeDuplicates(runMultiProbe.getQueryResults());
    }


    // Due to the existence of multiple hashtables, we originally get many duplicates.
    // This removes them.
    private ArrayList<SearchableObject> removeDuplicates(List<SearchableObject> vector)
    {
        Set<SearchableObject> hs = new HashSet<>();
        hs.addAll(vector);
        vector.clear();
        vector.addAll(hs);
        return new ArrayList(vector);
    }

    private void searchByImageFile() {

        File imageFileSelected = null;
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select image to search for similar images:");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            imageFileSelected = jfc.getSelectedFile();
        } else {
            return;
        }

        try {
            BufferedImage imageSelected = ImageIO.read(imageFileSelected);
            if (imageSelected == null) {
                JOptionPane.showMessageDialog(null, "Could not process selected file as image.");
            } else {
                searchForImageInIndex(imageSelected);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IOException was caught. Could not process selected file as image.");
        }


    }


    // Return raw nearest neighbors without yet hashing
    private List<SearchableObject> getRawKNeaerestNeighbors(List<Double> vector)
    {
        List<SearchableObject> data = this.imageIndex.getRawFeatureVectors();

        List<SearchableObject> KNearestNeighbors = new ArrayList<SearchableObject>();
        List<Double> MAX_VECTOR = new ArrayList<Double>();
        for (int i = 0; i < this.numberOfDimensions; i++)
            MAX_VECTOR.add(Double.MAX_VALUE);

        // Find K-Nearest Neighbors
        for (int i = 0; i < this.nearestNeighborsToSearch; i++) {
            SearchableObject min_vector = new SearchableObject(MAX_VECTOR, null);

            SearchableObject queryObject = new SearchableObject(vector, null);
            for (SearchableObject object : data) {
                if ((object.distanceTo(queryObject) < min_vector.distanceTo(queryObject)) &&
                        (!KNearestNeighbors.contains(object)))  {
                    min_vector = new SearchableObject(object.getObjectFeatures(), object.getObjectUrl());
                }
            }

            KNearestNeighbors.add(min_vector);
        }
        return removeDuplicates(KNearestNeighbors);

    }

    /**
     * Ask user for number of hash functions setting
     */
    private void setNumberOfHashFunctions() {

        int numberOfHashFunctions = 0;
        String hashFunctionsCount = null;
        while(true) {
            hashFunctionsCount = JOptionPane.showInputDialog("Number of Hash Functions: ", this.numberOfHashFunctions);
            if (hashFunctionsCount == null) {
                break;
            }
            try {
                numberOfHashFunctions = Integer.parseInt(hashFunctionsCount);
                this.numberOfHashFunctions = numberOfHashFunctions;
                break;
            } catch (NumberFormatException e) {

            }

        }

    }

    /**
     * Ask user for number oif hash tables setting
     */
    private void setNumberOfHashTables() {

        int numberOfHashTables = 0;
        String hashTablesCount = null;
        while(true) {
            hashTablesCount = JOptionPane.showInputDialog("Number of Hash Tables: ", this.numberOfHashTables);
            if (hashTablesCount == null) {
                break;
            }
            try {
                numberOfHashTables = Integer.parseInt(hashTablesCount);
                this.numberOfHashTables = numberOfHashTables;
                break;
            } catch (NumberFormatException e) {

            }

        }
    }

    /**
     * Ask user for number of dimensions for image features
     */
    private void setNumberOfDimensions() {

        int numberOfDimensions = 0;
        String dimensionsCount = null;
        while(true) {
            dimensionsCount = JOptionPane.showInputDialog("Number of Dimensions: ", this.numberOfDimensions);
            if (dimensionsCount == null) {
                break;
            }
            try {
                numberOfDimensions = Integer.parseInt(dimensionsCount);
                this.numberOfDimensions = numberOfDimensions;
                break;
            } catch (NumberFormatException e) {

            }

        }

    }

    /**
     * Ask user for slot width setting
     */
    private void setSlotWidth() {

        double slotWidth = 0.0;
        String slotWidthString = null;
        while(true) {
            slotWidthString = JOptionPane.showInputDialog("Slot Width: ", this.slotWidth);
            if (slotWidthString == null) {
                break;
            }
            try {
                slotWidth = Double.parseDouble(slotWidthString);
                this.slotWidth = slotWidth;
                break;
            } catch (NumberFormatException e) {

            }

        }

    }

    /**
     * Say whether to use Eigen vectors as hash functions or not
     */
    private void toggleUseEigenVectors() {

        if (this.useEigenVectorsForHashing) {
            this.useEigenVectorsForHashing = false;
        } else {
            this.useEigenVectorsForHashing = true;
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                LshUi imageList = new LshUi();

                JFrame f = new JFrame("Locality Sensitive Hashing - Image Similarity Search");
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.add(imageList.getGui());
                f.setJMenuBar(imageList.getMenuBar());
                f.setLocationByPlatform(true);
                f.pack();
                f.setSize(800,600);
                f.setVisible(true);
            }
        });
    }
}

class IconCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private int size;
    private BufferedImage icon;

    IconCellRenderer() {
        this(100);
    }

    IconCellRenderer(int size) {
        this.size = size;
        icon = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel && value instanceof BufferedImage) {
            JLabel l = (JLabel)c;
            l.setText("");
            BufferedImage i = (BufferedImage)value;
            l.setIcon(new ImageIcon(icon));

            Graphics2D g = icon.createGraphics();
            g.setColor(new Color(0,0,0,0));
            g.clearRect(0, 0, size, size);
            g.drawImage(i,0,0,size,size,this);

            g.dispose();
        }
        return c;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }

}
