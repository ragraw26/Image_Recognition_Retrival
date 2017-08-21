package com.edu.myneu.kmeansclustering;

import com.edu.myneu.pojo.ImageAttr;

public class ClusterWithMean extends FeaturesCluster {

	private double[] mean;

	double[] sum;

	public ClusterWithMean(int vectorsSize) {
		super();
		sum = new double[vectorsSize];
	}

	public void setMean(double[] mean) {
		this.mean = mean;
	}

	@Override
	public void addVector(ImageAttr vector) {
		super.addVector(vector);
		for (int i = 0; i < vector.getFeatures().length; i++) {
			sum[i] += vector.getFeatures()[i];
		}
	}

	public double[] getmean() {
		return mean;
	}

	public void recomputeClusterMean() {
		for (int i = 0; i < sum.length; i++) {
			mean[i] = sum[i] / vectors.size();
		}
	}

	@Override
	public void remove(ImageAttr vector) {
		super.remove(vector);
		for (int i = 0; i < vector.getFeatures().length; i++) {
			sum[i] -= vector.getFeatures()[i];
		}

	}

}
