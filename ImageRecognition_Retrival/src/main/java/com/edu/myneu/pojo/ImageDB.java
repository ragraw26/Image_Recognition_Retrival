package com.edu.myneu.pojo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ImageDB {

	private static final ImageDB imageInstance = new ImageDB();
	private static Map<Integer, ImageAttr> imageDB;

	private ImageDB() {
		imageDB = Collections.synchronizedMap(new HashMap<Integer, ImageAttr>());
	}

	public static Map<Integer, ImageAttr> getImageDB() {
		return imageDB;
	}

	public static ImageDB getImageinstance() {
		return imageInstance;
	}

	public void deserializeHashMap(String sername) {

		try {
			FileInputStream fis = new FileInputStream(sername);
			ObjectInputStream ois = new ObjectInputStream(fis);
			imageDB = (Map) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}

		Set set = imageDB.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();

			ImageAttr localObj = (ImageAttr) mentry.getValue();

		}
	}

}
