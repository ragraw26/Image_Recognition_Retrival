package com.edu.myneu.pojo;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Rajat
 *
 */
public class ImageAttr implements Serializable {
	private double[] features;
	private String imageName;
	private String imagePath;
	private int HashValue;
	private CommonsMultipartFile image;

	public void setHashValue(int j) {
		HashValue = j;
	}

	public double[] getFeatures() {
		return features;
	}

	public void setFeatures(double[] features) {
		this.features = features;
	}

	public CommonsMultipartFile getImage() {
		return image;
	}

	public void setImage(CommonsMultipartFile image) {
		this.image = image;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getHashValue() {
		return HashValue;
	}

}
