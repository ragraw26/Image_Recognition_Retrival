package com.edu.myneu.KDTress;

import com.edu.myneu.pojo.ImageAttr;

public class NearestNeighbour implements Comparable<NearestNeighbour> {

	public ImageAttr image;
	public double distance;

	public NearestNeighbour(ImageAttr image, double distance) {
		this.image = image;
		this.distance = distance;
	}

	@Override
	public int compareTo(NearestNeighbour point2) {
		return Double.compare(this.distance, point2.distance);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(");
		for (Double element : image.getFeatures()) {
			buffer.append(" " + element);
		}
		buffer.append(")");
		return buffer.toString();
	}

	@Override
	public boolean equals(Object point2) {
		if (point2 == null) {
			return false;
		}
		NearestNeighbour o2 = (NearestNeighbour) point2;
		for (int i = 0; i < image.getFeatures().length; i++) {
			if (o2.image.getFeatures()[i] != image.getFeatures()[i]) {
				return false;
			}
		}
		return true;
	}
}
