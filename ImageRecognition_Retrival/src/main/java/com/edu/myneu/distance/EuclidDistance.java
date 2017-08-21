package com.edu.myneu.distance;

public class EuclidDistance extends DistanceFunction {

	static String NAME = "euclidian";

	@Override
	public double calculateDistance(double[] vector1, double[] vector2) {
		double sum = 0;
		for (int i = 0; i < vector1.length; i++) {
			sum += Math.pow(vector1[i] - vector2[i], 2);
		}
		return Math.sqrt(sum);

	}

	@Override
	public String getName() {
		return NAME;
	}

}
