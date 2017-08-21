package com.edu.myneu.kmeansclustering;

import java.util.List;

import com.edu.myneu.distance.DistanceFunction;
import com.edu.myneu.pojo.ImageAttr;

public class ClustersEvaluation {

	public static double calculateSSE(List<ClusterWithMean> clusters,
			DistanceFunction distanceFunction) {
		double sse = 0;
		// for each cluster
		for (ClusterWithMean cluster : clusters) {
			// for each instance in that cluster
			for (ImageAttr vector : cluster.getVectors()) {
				sse += Math.pow(distanceFunction.calculateDistance(
						vector.getFeatures(), cluster.getmean()), 2);
			}
		}
		return sse;
	}

	public static double getSSE(List<FeaturesCluster> clusters,
			DistanceFunction distanceFunction) {
		double sse = 0;
		// for each cluster
		for (FeaturesCluster cluster : clusters) {
			// if the cluster is not empty
			if (cluster.getVectors().size() > 0) {
				// calculate the mean of the cluster
				double[] mean = calculateClusterMeans(cluster);
				// for each instance in that cluster
				for (ImageAttr vector : cluster.getVectors()) {
					sse += Math.pow(
							distanceFunction.calculateDistance(
									vector.getFeatures(), mean), 2);
				}
			}
		}
		return sse;
	}

	public static double[] calculateClusterMeans(FeaturesCluster cluster) {
		int dimensionCount = cluster.getVectors().get(0).getFeatures().length;
		double mean[] = new double[dimensionCount];
		// for each vector
		for (ImageAttr vector : cluster.getVectors()) {
			// for each dimension, we add the value
			for (int i = 0; i < dimensionCount; i++) {
				mean[i] += vector.getFeatures()[i];
			}
		}
		// finally, fo each dimension, we divide by the number of vectors
		for (int i = 0; i < dimensionCount; i++) {
			mean[i] = mean[i] / cluster.getVectors().size();
		}
		return mean;
	}

}
