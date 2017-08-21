package com.edu.myneu.Indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import com.edu.myneu.pojo.ImageAttr;
import com.edu.myneu.pojo.ImageDB;

public class HashMapIndexing {

	String path = "C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\Features.txt";

	File hashFile = new File(
			"C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\hashmap.ser");

	File folder = new File(path);
	File[] listOfFiles = folder.listFiles();
	public int noOfFiles;

	public int getNoOfFiles() {
		return noOfFiles;
	}

	public void setNoOfFiles(int noOfFiles) {
		this.noOfFiles = noOfFiles;
	}

	public void createMap() throws IOException, NoSuchAlgorithmException {

		// Go to the Image Database Folder
		if (!folder.exists()) {
			infoBox("The Image Database does not exist. Please create the same !!",
					"Create Image Database");
			return;
		}

		if (hashFile.exists()) {
			infoBox("HashMap has already been created for the Image Database. You might want to Update !!",
					"Illegal Operation");
			return;
		}

		BufferedReader reader;
		// noOfFiles = folder.listFiles().length;
		// System.out.println("Total Files Discovered : " + noOfFiles);
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		int j = 0;
		reader = new BufferedReader(
				new FileReader(
						"C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\Features.txt"));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] strArr = line.split("\\t");

			double[] doubleArray = new double[66];
			for (int i = 0; i < strArr.length; i++) {
				doubleArray[i] = Double.parseDouble(strArr[i]);
			}

			ImageAttr img = new ImageAttr();
			img.setImageName("img-" + j);
			img.setImagePath("C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\image\\"
					+ j + ".jpg");
			img.setFeatures(doubleArray);
			img.setHashValue(j);
			ImageDB.getImageDB().put(j, img);
			System.out.println(j);
			j++;
		}
		reader.close();
		serializeHashMap(ImageDB.getImageDB());
		printMap(ImageDB.getImageDB());
		infoBox("Images have been Indexed Now !", "Indexed");

	}

	public void serializeHashMap(Map NewMap) {
		try {
			try (FileOutputStream fos = new FileOutputStream(
					"C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\hashmap.ser");
					ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				oos.writeObject(NewMap);
			}
			// System.out.println("Serialized HashMap data is saved in hashmap.ser");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void infoBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage,
				"InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void printMap(Map NewMap) {
		Iterator it = NewMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
		}
	}

}
