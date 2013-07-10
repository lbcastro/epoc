
package com.castro.epoc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Environment;

public class Files {

    /**
     * Adds multiple arrays of data to a file.
     * 
     * @param f The file where the data is to be stored.
     * @param values ArrayList where the multiple arrays were collected.
     */
    public static void addValues(File f, ArrayList<double[]> values) {
        // Initiates the file to be written.
        final Document doc = getDoc(f);
        final Node root = doc.getFirstChild();
        // For each array, prepares data and appends data to a new node. Appends
        // the new node to the file.
        for (int x = 0; x < values.size(); x++) {
            final Element recording = doc.createElement("recording");
            final String valuesString = Arrays.toString(values.get(x)).replaceAll("\\[|\\]", "");
            root.appendChild(recording);
            recording.setTextContent(valuesString);
        }
        // Saves changes.
        saveChanges(doc, f);
    }

    /**
     * Creates a new file to store events data. Only a root node is built.
     * 
     * @param f The location of the file to be created
     * @param s The name of the root element
     */
    public static void createFile(File f, String s) {
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = fac.newDocumentBuilder();
            Document doc = b.newDocument();
            Element root = doc.createElement(s);
            doc.appendChild(root);
            saveChanges(doc, f);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the file structure of an existing xml file.
     * 
     * @param f The existing xml file
     * @return The current file structure
     */
    public static Document getDoc(File f) {
        Document doc = null;
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = fac.newDocumentBuilder();
            doc = b.parse(f);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static double[] getDoubleArray(Node n) {
        String[] s = n.getTextContent().split(",");
        return getDoubleArray(s);
    }

    public static double[] getDoubleArray(String[] s) {
        double[] d = new double[s.length];
        for (int x = 0; x < s.length; x++) {
            d[x] = Double.parseDouble(s[x]);
        }
        return d;
    }

    /**
     * Reads channels data from the specified file.
     * 
     * @param f The file to read from
     * @return Double array with all stored channels' readings
     */
    public static double[][] getRecordings(File f) {
        double[][] recTotal = null;
        // Initiates and normalizes the file to be read.
        final Document doc = getDoc(f);
        doc.getDocumentElement().normalize();
        // Gets all recordings in a NodeList.
        final NodeList recordings = doc.getElementsByTagName("recording");
        final int recSize = recordings.getLength();
        recTotal = new double[recSize][14];
        // Inserts all recordings in a double array.
        for (int x = 0; x < recSize; x++) {
            final double[] recDouble = getDoubleArray(recordings.item(x));
            recTotal[x] = recDouble;
        }
        return recTotal;
    }

    /** Resets all recorded data. */
    public static void resetData() {
        for (Events e : Events.values()) {
            final File f = e.getFile();
            if (f.exists()) {
                f.delete();
            }
        }
    }

    /**
     * Resets the data of a specific file.
     * 
     * @param f The file to be reseted.
     */
    public static void resetData(File f) {
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * Saves changes made to a specified file structure, on an existing file.
     * 
     * @param doc The modified file structure
     * @param f The file to be saved
     */
    public static void saveChanges(Document doc, File f) {
        try {
            File dir = new File(f.getParentFile().getAbsolutePath());
            if (!dir.exists())
                dir.mkdir();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            t.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static File sdCard(String location) {
        if (Profiles.getInstance().getActiveUser() == null) {
            return null;
        }
        String s = "/EPOC/" + Profiles.getInstance().getActiveUser() + "/" + location + ".xml";
        File f = new File(Environment.getExternalStorageDirectory() + s);
        return f;
    }

    private Files() {
    }
}
