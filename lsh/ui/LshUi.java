package ui;

import indexing.ImageIndex;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.swing.*;
import java.net.MalformedURLException;

public class LshUi {

    public static final int DEFAULT_WIDTH = 640, DEFAULT_HEIGHT = 480;

    public static final int DEFAULT_NUMBER_OF_HASHFUNCTIONS = 4;
    public static final int DEFAULT_NUMBER_OF_HASHTABLES = 1;
    public static final int DEFAULT_NUMBER_OF_DIMENSIONS = 180;
    public static final double DEFAULT_SLOT_WIDTH = 0.1;
    public static final boolean DEFAULT_USE_EIGENVECTORS = false;

    private int numberOfHashFunctions, numberOfHashTables, numberOfDimensions;
    private double slotWidth;
    private boolean useEigenVectorsForHashing;
    private ImageIndex imageIndex;
    private JMenu searchMenu, settingsMenu;

    public LshUi() {
        this.numberOfHashFunctions = DEFAULT_NUMBER_OF_HASHFUNCTIONS;
        this.numberOfHashTables = DEFAULT_NUMBER_OF_HASHTABLES;
        this.numberOfDimensions = DEFAULT_NUMBER_OF_DIMENSIONS;
        this.slotWidth = DEFAULT_SLOT_WIDTH;
        this.useEigenVectorsForHashing = DEFAULT_USE_EIGENVECTORS;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Locality Sensitive Hashing - Image Similarity Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        //Create the menu bar.  Make it have a green background.
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setPreferredSize(new Dimension(DEFAULT_WIDTH, 20));

        //Create user menus
        JMenu fileMenu, metricsMenu;
        JMenuItem buildIndexMenuItem, loadIndexMenuItem, exitMenuItem, searchByUrlMenuItem, searchByFileMenuItem, numberOfHashFunctionsMenuItem, numberOfHashTablesMenuItem, numberOfDimensionsMenuItem, slotWidthMenuItem;
        JCheckBoxMenuItem useEigenVectorHashCheckBoxMenuItem;

        //File menu
        fileMenu = new JMenu("File");
        buildIndexMenuItem = new JMenuItem("Build Index");
        buildIndexMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {buildIndex();} });
        fileMenu.add(buildIndexMenuItem);
        loadIndexMenuItem = new JMenuItem("Load Index");
        loadIndexMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {loadIndex();} });
        fileMenu.add(loadIndexMenuItem);
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {System.exit(0);} });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        //Search menu
        searchMenu = new JMenu("Search");
        searchByUrlMenuItem = new JMenuItem("Search  by URL");
        searchByUrlMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {searchByUrl();} });
        searchMenu.add(searchByUrlMenuItem);
        searchByFileMenuItem = new JMenuItem("Search  by Image File");
        searchByFileMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {searchByImageFile();} });
        searchMenu.add(searchByFileMenuItem);
        menuBar.add(searchMenu);
        searchMenu.setEnabled(false);

        //Settings menu
        settingsMenu = new JMenu("Settings");
        numberOfHashFunctionsMenuItem = new JMenuItem("Set Number of Hash Functions");
        numberOfHashFunctionsMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {setNumberOfHashFunctions();} });
        settingsMenu.add(numberOfHashFunctionsMenuItem);
        numberOfHashTablesMenuItem = new JMenuItem("Set Number of Hash Tables");
        numberOfHashTablesMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {setNumberOfHashTables();} });
        settingsMenu.add(numberOfHashTablesMenuItem);
        numberOfDimensionsMenuItem = new JMenuItem("Set Number of Dimensions");
        numberOfDimensionsMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {setNumberOfDimensions();} });
        settingsMenu.add(numberOfDimensionsMenuItem);
        slotWidthMenuItem = new JMenuItem("Set Slot Width");
        slotWidthMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {setSlotWidth();} });
        settingsMenu.add(slotWidthMenuItem);
        useEigenVectorHashCheckBoxMenuItem = new JCheckBoxMenuItem("Hash with Eigen Vectors");
        useEigenVectorHashCheckBoxMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {toggleUseEigenVectors();} });
        settingsMenu.add(useEigenVectorHashCheckBoxMenuItem);
        menuBar.add(settingsMenu);

        //Metrics menu
        metricsMenu = new JMenu("Metrics");
        menuBar.add(metricsMenu);

        //Set the menu bar and add the label to the content pane.
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(this.new ImagePanel());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
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
                }
                searchForImageInIndex(image);
                break;
            } catch (MalformedURLException e) {
                JOptionPane.showMessageDialog(null, "MalformedURLException was caught. Could not obtain image from URL.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "IOException was caught. Could read in the image from URL.");
            }
        }

    }

    private void searchForImageInIndex(BufferedImage imageToSearch) {

        BufferedImage inputImage = imageToSearch;
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

    //Panel to show images
    private class ImagePanel extends JPanel {

        private List<URL> imagesToDisplay;

        public ImagePanel() {
            this.imagesToDisplay = new ArrayList<URL>();
            setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        }

        public void setImagesToDisplay(List<URL> imagesToDisplay) {
            this.imagesToDisplay = imagesToDisplay;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                for (URL imageUrl : imagesToDisplay) {
                    super.paintComponent(g);
                    g.drawImage(ImageIO.read(imageUrl), 0, 0, this);
                }
            } catch (IOException e) {
            }
        }

    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LshUi().createAndShowGUI();
            }
        });
    }


}
