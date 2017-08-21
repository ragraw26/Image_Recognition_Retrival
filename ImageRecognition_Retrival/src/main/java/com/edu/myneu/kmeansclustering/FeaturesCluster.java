package com.edu.myneu.kmeansclustering;

import java.util.ArrayList;
import java.util.List;

import com.edu.myneu.pojo.ImageAttr;

public class FeaturesCluster {

	protected List<ImageAttr> vectors = new ArrayList<ImageAttr>();

	public FeaturesCluster() {
		super();
	}

	public void addVector(ImageAttr vector) {
		vectors.add(vector);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		if (vectors.size() >= 1) {
			for (ImageAttr vector : vectors) {
				buffer.append("[");
				buffer.append(vector.toString());
				buffer.append("]");
			}
		}
		return buffer.toString();
	}

	public List<ImageAttr> getVectors() {
		return vectors;
	}

	public void remove(ImageAttr vector) {
		vectors.remove(vector);
	}

	public void removeVector(ImageAttr vector) {
		vectors.add(vector);
	}

	public boolean contains(ImageAttr vector) {
		return vectors.contains(vector);
	}

}
