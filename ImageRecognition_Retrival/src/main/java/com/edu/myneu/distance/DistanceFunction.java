package com.edu.myneu.distance;

public abstract class DistanceFunction {

	public abstract double calculateDistance(double[] ds, double[] targetPoint);

	public abstract String getName();

	public static EuclidDistance getDistanceFunctionByName(String name) {
		if (EuclidDistance.NAME.equals(name)) {
			return new EuclidDistance();
		}
		return null;
	}
}
