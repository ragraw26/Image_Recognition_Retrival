/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edu.myneu.kmeansclustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.edu.myneu.distance.DistanceFunction;
import com.edu.myneu.pojo.ImageAttr;

public class BisectingKMeans extends AlgoKMeans {

    int iter = -1;

    public BisectingKMeans() {

    }
    
    public List<ClusterWithMean> runAlgorithm(Map<Integer, ImageAttr> map, int k,
            DistanceFunction distanceFunction, int iter) throws NumberFormatException, IOException {
        this.iter = iter;

        return runAlgorithm(map, k, distanceFunction);
    }

    @Override
	void applyAlgorithm(int k, DistanceFunction distanceFunction,
            List<ImageAttr> vectors, double minValue, double maxValue,
            int vectorsSize) {

        clusters = new ArrayList<ClusterWithMean>();

        List<ImageAttr> currentVectors = vectors;

        while (true) {
            // apply kmeans iter times and keep the best clusters
            List<ClusterWithMean> bestClustersUntilNow = null;
            double smallestSSE = Double.MAX_VALUE;

            for (int i = 0; i < iter; i++) {
                List<ClusterWithMean> newClusters = applyKMeans(2, distanceFunction, currentVectors, minValue, maxValue, vectorsSize);
                double sse = ClustersEvaluation.calculateSSE(newClusters, distanceFunction);
                if (sse < smallestSSE) {
                    bestClustersUntilNow = newClusters;
                    smallestSSE = sse;
                }
            }
           
            clusters.addAll(bestClustersUntilNow);

            // if we have enough clusters, we stop
            if (clusters.size() == k) {
                break;
            }

            // otherwise, we choose the next cluster to be bisected.
            int biggestClusterSize = -1;
            int biggestClusterIndex = -1;
            for (int i = 0; i < clusters.size(); i++) {
                ClusterWithMean cluster = clusters.get(i);
                // if the biggest cluster until now, we remember it
                if (cluster.getVectors().size() > biggestClusterSize) {
                    biggestClusterIndex = i;
                    biggestClusterSize = cluster.getVectors().size();
                    currentVectors = cluster.getVectors();
                }
            }
            // remove the cluster from the list of clusters because we will split it
            clusters.remove(biggestClusterIndex);
        }
    }

    @Override
	public void printStatistics() {
        System.out.println("========== BISECTING KMEANS - SPMF 2.09 - STATS ============");
        System.out.println(" Distance function: " + distanceFunction.getName());
        System.out.println(" Total time ~: " + (endTimestamp - startTimestamp)
                + " ms");
        System.out.println(" SSE (Sum of Squared Errors) (lower is better) : " + ClustersEvaluation.calculateSSE(clusters, distanceFunction));
        System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " mb ");
        System.out.println("=====================================");
    }

}
