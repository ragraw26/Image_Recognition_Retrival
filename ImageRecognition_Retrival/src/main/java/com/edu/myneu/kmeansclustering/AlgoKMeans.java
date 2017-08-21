package com.edu.myneu.kmeansclustering;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.edu.myneu.distance.DistanceFunction;
import com.edu.myneu.pojo.ImageAttr;

public class AlgoKMeans {

    // The list of clusters generated
    protected List<ClusterWithMean> clusters = null;

    // A random number generator because K-Means is a randomized algorithm
    protected final static Random random = new Random(System.currentTimeMillis());

    // For statistics
    protected long startTimestamp; // the start time of the latest execution
    protected long endTimestamp;  // the end time of the latest execution
    long iterationCount; // the number of iterations that was performed

    /* The distance function to be used for clustering */
    protected DistanceFunction distanceFunction = null;

    private List<String> attributeNames = null;

    public AlgoKMeans() {

    }

    public List<ClusterWithMean> runAlgorithm(Map<Integer,ImageAttr> map, int k, DistanceFunction distanceFunction) throws NumberFormatException, IOException {
        // record the start time
        startTimestamp = System.currentTimeMillis();
        // reset the number of iterations
        iterationCount = 0;
 
        this.distanceFunction = distanceFunction;

        // Structure to store the vectors from the file
        List<ImageAttr> instances;

        // variables to store the minimum and maximum values in vectors
        double minValue = Integer.MAX_VALUE;
        double maxValue = 0;

        // Read the input file       
        instances = new ArrayList<ImageAttr>(map.values());
        
        // For each instance
        for (ImageAttr instance : instances) {
            for (double value : instance.getFeatures()) {
                if (value < minValue) {
                    minValue = value;
                }
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }

        // Get the size of vectors
        int vectorsSize = instances.get(0).getFeatures().length;

        // if the user ask for only one cluster!
        if (k == 1) {
            // Create a single cluster and return it 
            clusters = new ArrayList<ClusterWithMean>();
            ClusterWithMean cluster = new ClusterWithMean(vectorsSize);
            for (ImageAttr vector : instances) {
                cluster.addVector(vector);
            }
            cluster.setMean(new double[vectorsSize]);
            cluster.recomputeClusterMean();
            clusters.add(cluster);

            // check memory usage
            MemoryLogger.getInstance().checkMemory();

            // record end time
            endTimestamp = System.currentTimeMillis();
            return clusters;
        }

        // SPECIAL CASE: If only one vector
        if (instances.size() == 1) {
            // Create a single cluster and return it 
            clusters = new ArrayList<ClusterWithMean>();
            ImageAttr vector = instances.get(0);
            ClusterWithMean cluster = new ClusterWithMean(vectorsSize);
            cluster.addVector(vector);
            cluster.recomputeClusterMean();
            cluster.setMean(new double[vectorsSize]);
            clusters.add(cluster);

            // check memory usage
            MemoryLogger.getInstance().checkMemory();

            // record end time
            endTimestamp = System.currentTimeMillis();
            return clusters;
        }

        // if the user asks for more cluster then there is data,
        // we set k to the number of data points.
        if (k > instances.size()) {
            k = instances.size();
        }

        applyAlgorithm(k, distanceFunction, instances, minValue, maxValue,
                vectorsSize);

        // check memory usage
        MemoryLogger.getInstance().checkMemory();

        // record end time
        endTimestamp = System.currentTimeMillis();

        for (int i = 0; i < clusters.size(); i++) {
            System.out.println(clusters.get(i).vectors);
        }

        // return the clusters
        return clusters;
    }

    void applyAlgorithm(int k, DistanceFunction distanceFunction,
            List<ImageAttr> vectors, double minValue, double maxValue,
            int vectorsSize) {
        // apply kmeans
        clusters = applyKMeans(k, distanceFunction, vectors, minValue, maxValue, vectorsSize);
    }

   
    List<ClusterWithMean> applyKMeans(int k, DistanceFunction distanceFunction,
            List<ImageAttr> vectors, double minValue, double maxValue,
            int vectorsSize) {
        List<ClusterWithMean> newClusters = new ArrayList<ClusterWithMean>();

        // SPECIAL CASE: If only one vector
        if (vectors.size() == 1) {
            // Create a single cluster and return it 
            ImageAttr vector = vectors.get(0);
            ClusterWithMean cluster = new ClusterWithMean(vectorsSize);
            cluster.addVector(vector);
            newClusters.add(cluster);
            return newClusters;
        }

        // (1) Randomly generate k empty clusters with a random mean (cluster
        // center)
        for (int i = 0; i < k; i++) {
            double[] meanVector = generateRandomVector(minValue, maxValue, vectorsSize);
            ClusterWithMean cluster = new ClusterWithMean(vectorsSize);
            cluster.setMean(meanVector);
            newClusters.add(cluster);
        }

        // (2) Repeat the two next steps until the assignment hasn't changed
        boolean changed;
        while (true) {
            iterationCount++;
            changed = false;
            // (2.1) Assign each point to the nearest cluster center.

            // / for each vector
            for (ImageAttr vector : vectors) {
                // find the nearest cluster and the cluster containing the item
                ClusterWithMean nearestCluster = null;
                ClusterWithMean containingCluster = null;
                double distanceToNearestCluster = Double.MAX_VALUE;

                // for each cluster
                for (ClusterWithMean cluster : newClusters) {
                    // calculate the distance of the cluster mean to the vector
                    double distance = distanceFunction.calculateDistance(cluster.getmean(), vector.getFeatures());
                    // if it is the smallest distance until now, record this cluster
                    // and the distance
                    if (distance < distanceToNearestCluster) {
                        nearestCluster = cluster;
                        distanceToNearestCluster = distance;
                    }
                    // if the cluster contain the vector already,
                    // remember that too!
                    if (cluster.contains(vector)) {
                        containingCluster = cluster;
                    }
                }

                // if the nearest cluster is not the cluster containing
                // the vector
                if (containingCluster != nearestCluster) {
                    // remove the vector from the containing cluster
                    if (containingCluster != null) {
                        containingCluster.remove(vector);
                    }
                    // add the vector to the nearest cluster
                    nearestCluster.addVector(vector);
                    changed = true;
                }
            }

            // check the memory usage
            MemoryLogger.getInstance().checkMemory();

            if (!changed) {     // exit condition for main loop
                break;
            }

            // (2.2) Recompute the new cluster means
            for (ClusterWithMean cluster : newClusters) {
                cluster.recomputeClusterMean();
            }
        }

        return newClusters;
    }

    double[] generateRandomVector(double minValue, double maxValue,
            int vectorsSize
    ) {
        // create a new vector
        double[] vector = new double[vectorsSize];
        // for each position generate a random number
        for (int i = 0; i < vectorsSize; i++) {
            vector[i] = (random.nextDouble() * (maxValue - minValue)) + minValue;
        }
        // return the vector
        return vector;
    }
   
    public void saveToFile(String output) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));

        // First, we will print the attribute names
        for (String attributeName : attributeNames) {
            writer.write("@ATTRIBUTEDEF=" + attributeName);
            writer.newLine();
        }

        // for each cluster
        for (int i = 0; i < clusters.size(); i++) {
            // if the cluster is not empty
            if (clusters.get(i).getVectors().size() >= 1) {
                // write the cluster
                writer.write(clusters.get(i).toString());
                // if not the last cluster, add a line return
                if (i < clusters.size() - 1) {
                    writer.newLine();
                }
            }
        }
        // close the file
        writer.close();
    }

    
    public void printStatistics() {
        System.out.println("========== KMEANS - SPMF 2.09 - STATS ============");
        System.out.println(" Distance function: " + distanceFunction.getName());
        System.out.println(" Total time ~: " + (endTimestamp - startTimestamp)
                + " ms");
        System.out.println(" SSE (Sum of Squared Errors) (lower is better) : " + ClustersEvaluation.calculateSSE(clusters, distanceFunction));
        System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " mb ");
        System.out.println(" Iteration count: " + iterationCount);
        System.out.println("=====================================");
    }

}
