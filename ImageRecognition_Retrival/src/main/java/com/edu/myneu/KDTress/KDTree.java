package com.edu.myneu.KDTress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.edu.myneu.distance.EuclidDistance;
import com.edu.myneu.distance.DistanceFunction;
import com.edu.myneu.pojo.ImageAttr;

public class KDTree {

	private int nodeCount = 0; // number of nodes in the tree
	private KDNode root = null; // the tree root
	int dimensionCount = 0; // number of dimensions

	// random number generator used by the Randomized-select algorithm
	private static Random random = new Random(System.currentTimeMillis());

	/* The distance function to be used for comparing vectors */
	DistanceFunction distanceFunction = new EuclidDistance();

	class KDNode {

		ImageAttr image; // contains a vector
		int d; // a dimension
		KDNode above; // node above
		KDNode below; // node below

		public KDNode(ImageAttr image, int d) {
			this.image = image;
			this.d = d;
		}

	}

	public KDTree() {

	}

	public int size() {
		return nodeCount;
	}

	public void buildtree(List<ImageAttr> points) {
		if (points.size() == 0) {
			return;
		}
		// pointsDEBUG = points;
		dimensionCount = points.get(0).getFeatures().length;

		root = generateNode(0, points, 0, points.size() - 1);
	}

	private KDNode generateNode(int currentD, List<ImageAttr> points, int left,
			int right) {
		// if there is no point
		if (right < left) {
			return null;
		}

		nodeCount++;
		// if there is only a single point
		if (right == left) {
			return new KDNode(points.get(left), currentD);
		}
		// else if there is more than one point

		// We calculate the desired rank that correspond to the median.
		int m = (right - left) / 2;

		// we select the median point
		ImageAttr medianNode = randomizedSelect(points, m, left, right,
				currentD);

		// we will use this point to separate the two lower branches of the
		// tree.
		KDNode node = new KDNode(medianNode, currentD);
		currentD++;
		if (currentD == dimensionCount) {
			currentD = 0;
		}

		// recursively create subnodes for the two branches of the three
		node.below = generateNode(currentD, points, left, left + m - 1);
		node.above = generateNode(currentD, points, left + m + 1, right);
		return node;
	}

	private ImageAttr randomizedSelect(List<ImageAttr> points, int i, int left,
			int right, int currentD) {
		int p = left;
		int r = right;

		while (true) {
			if (p == r) {
				return points.get(p);
			}
			int q = randomizedPartition(points, p, r, currentD);
			int k = q - p + 1;

			if (i == k - 1) {
				return points.get(q);
			} else if (i < k) {
				r = q - 1;
			} else {
				i = i - k;
				p = q + 1;
			}
		}
	}

	/**
	 * Private method used by the randomized-select method (see the book for
	 * details).
	 */
	private int randomizedPartition(List<ImageAttr> points, int p, int r,
			int currentD) {
		int i = 0;
		if (p < r) {
			i = p + random.nextInt(r - p);
		} else {
			i = r + random.nextInt(p - r);
		}
		swap(points, r, i);
		return partition(points, p, r, currentD); // call the partition method
													// of
		// quicksort.
	}

	/**
	 * Private method used by the randomized-select method (see the book for
	 * details).
	 */
	private int partition(List<ImageAttr> points, int p, int r, int currentD) {
		ImageAttr x = points.get(r);
		int i = p - 1;
		for (int j = p; j <= r - 1; j++) {
			if (points.get(j).getFeatures()[currentD] <= x.getFeatures()[currentD]) {
				i = i + 1;
				swap(points, i, j);
			}
		}
		swap(points, i + 1, r);
		return i + 1;
	}

	private void swap(List<ImageAttr> points, int i, int j) {
		ImageAttr valueI = points.get(i);
		points.set(i, points.get(j));
		points.set(j, valueI);
	}

	// =====================================================================================
	// ======================= To find the first nearest neighbor
	// =========================
	// =====================================================================================
	ImageAttr nearestNeighboor = null; // the current nearest neighboor.
	double minDist = 0; // the distance of the current nearest neighbor with the
						// target point.

	/**
	 * Method to get the nearest neighbor
	 */
	public ImageAttr nearest(ImageAttr targetPoint) {
		if (root == null) {
			return null;
		}

		// Find the node where the point would be inserted and calculate the
		// distance
		findParent(targetPoint, root, 0);

		// After that, start from the root and check all rectangles that overlap
		// the
		// distance with the parent. If a point with a distance smaller than the
		// parent is found,
		// then return it.
		nearest(root, targetPoint);

		return nearestNeighboor;
	}

	private void findParent(ImageAttr target, KDNode node, int d) {
		// IF the node would be inserted in the branch "below" this node.
		if (target.getFeatures()[d] < node.image.getFeatures()[d]) {
			if (++d == dimensionCount) {
				d = 0;
			}
			if (node.below == null) {
				nearestNeighboor = node.image;
				minDist = distanceFunction.calculateDistance(
						node.image.getFeatures(), target.getFeatures());
				return;
			}
			findParent(target, node.below, d);
		}

		// IF the node would be inserted in the branch "above" this node.
		if (++d == dimensionCount) {
			d = 0;
		}

		if (node.above == null) {
			nearestNeighboor = node.image;
			minDist = distanceFunction.calculateDistance(
					node.image.getFeatures(), target.getFeatures());
			return;
		}
		findParent(target, node.above, d);
	}

	private void nearest(KDNode node, ImageAttr targetPoint) {
		// If shorter, update minimum
		double d = distanceFunction.calculateDistance(node.image.getFeatures(),
				targetPoint.getFeatures());
		if (d < minDist) {
			minDist = d;
			nearestNeighboor = node.image;
		}

		int dMinus1 = node.d - 1;
		if (dMinus1 < 0) {
			dMinus1 = dimensionCount - 1;
		}

		// calculate perpendiculary distance with preceding dimensions.
		double perpendicularyDistance = Math
				.abs(node.image.getFeatures()[node.d]
						- targetPoint.getFeatures()[node.d]);
		if (perpendicularyDistance < minDist) {
			// explore both side of the tree
			if (node.above != null) {
				nearest(node.above, targetPoint);
			}
			if (node.below != null) {
				nearest(node.below, targetPoint);
			}
		} else // only explore one side of the three
		{
			if (targetPoint.getFeatures()[dMinus1] < node.image.getFeatures()[dMinus1]) {
				if (node.below != null) {
					nearest(node.below, targetPoint);
				}
			} else if (node.above != null) {
				nearest(node.above, targetPoint);
			}
		}
	}

	// =====================================================================================
	// ======================= Method to find the k nearest neighboor
	// =========================
	// =====================================================================================
	RBTree<NearestNeighbour> resultKNN = null;
	int k = 0; // the parameter k.

	/**
	 * Method to get the k nearest neighboors
	 */
	public RBTree<NearestNeighbour> knearest(ImageAttr targetPoint, int k) {
		this.k = k;
		this.resultKNN = new RBTree<NearestNeighbour>();

		if (root == null) {
			return null;
		}

		findParent_knn(targetPoint, root, 0);

		nearest_knn(root, targetPoint);

		return resultKNN;
	}

	private void findParent_knn(ImageAttr target, KDNode node, int d) {
		// If the node would be inserted in the branch "below"
		if (target.getFeatures()[d] < node.image.getFeatures()[d]) {
			if (++d == dimensionCount) {
				d = 0;
			}
			if (node.below == null) {
				tryToSave(node, target);
				return;
			}
			tryToSave(node.below, target);
			findParent_knn(target, node.below, d);
		}

		// If the node would be inserted in the branch "above".
		if (++d == dimensionCount) {
			d = 0;
		}

		if (node.above == null) {
			tryToSave(node, target);
			return;
		}
		tryToSave(node.above, target);
		findParent_knn(target, node.above, d);
	}

	private void tryToSave(KDNode node, ImageAttr target) {
		if (node == null) {
			return;
		}
		double distance = distanceFunction.calculateDistance(
				target.getFeatures(), node.image.getFeatures());
		if (resultKNN.size() == k && resultKNN.maximum().distance < distance) {
			return;
		}
		NearestNeighbour point = new NearestNeighbour(node.image, distance);

		if (resultKNN.contains(point)) {
			return;
		}

		resultKNN.add(point);

		if (resultKNN.size() > k) {
			resultKNN.popMaximum();
		}
	}

	private void nearest_knn(KDNode node, ImageAttr targetPoint) {
		tryToSave(node, targetPoint);

		double perpendicularDistance = Math
				.abs(node.image.getFeatures()[node.d]
						- targetPoint.getFeatures()[node.d]);
		if (perpendicularDistance < resultKNN.maximum().distance) {
			// explore the "above" and "below" branches.
			if (node.above != null) {
				nearest_knn(node.above, targetPoint);
			}
			if (node.below != null) {
				nearest_knn(node.below, targetPoint);
			}
		} else {
			if (targetPoint.getFeatures()[node.d] < node.image.getFeatures()[node.d]) {
				if (node.below != null) {
					nearest_knn(node.below, targetPoint);
				}
			} else if (node.above != null) {
				nearest_knn(node.above, targetPoint);
			}
		}
	}

	public List<ImageAttr> pointsWithinRadiusOf(ImageAttr targetPoint,
			double radius) {
		List<ImageAttr> result = new ArrayList<>();

		// // return the points within the radius
		return pointsWithinRadiusOf(targetPoint, radius, result);
	}

	public List<ImageAttr> pointsWithinRadiusOf(ImageAttr targetPoint,
			double radius, List<ImageAttr> result) {

		if (root == null) {
			return null;
		}

		findPointsWithinRadius(root, targetPoint, result, radius);

		// return the points within the radius
		return result;
	}

	private void findPointsWithinRadius(KDNode node, ImageAttr targetPoint,
			List<ImageAttr> result, double radius) {
		if (node.image != targetPoint) {
			tryToSaveRadius(node, targetPoint, result, radius);
		}

		double perpendicularDistance = Math
				.abs(node.image.getFeatures()[node.d]
						- targetPoint.getFeatures()[node.d]);
		if (perpendicularDistance < radius) {
			// explore the "above" and "below" branches.
			if (node.above != null) {
				findPointsWithinRadius(node.above, targetPoint, result, radius);
			}
			if (node.below != null) {
				findPointsWithinRadius(node.below, targetPoint, result, radius);
			}
		} else // explore one side of the tree.
		{
			if (targetPoint.getFeatures()[node.d] < node.image.getFeatures()[node.d]) {
				if (node.below != null) {
					findPointsWithinRadius(node.below, targetPoint, result,
							radius);
				}
			} else if (node.above != null) {
				findPointsWithinRadius(node.above, targetPoint, result, radius);
			}
		}
	}

	private void tryToSaveRadius(KDNode node, ImageAttr target,
			List<ImageAttr> result, double radius) {
		if (node == null) {
			return;
		}
		double distance = distanceFunction.calculateDistance(
				target.getFeatures(), node.image.getFeatures());
		if (distance <= radius) {
			result.add(node.image);
		}
	}

	public List<NearestNeighbour> pointsWithinRadiusOfWithDistance(
			ImageAttr targetPoint, double radius) {
		if (root == null) {
			return null;
		}
		List<NearestNeighbour> result = new ArrayList<NearestNeighbour>();

		// return the points within the radius
		return pointsWithinRadiusOfWithDistance(targetPoint, radius, result);
	}

	public List<NearestNeighbour> pointsWithinRadiusOfWithDistance(
			ImageAttr targetPoint, double radius, List<NearestNeighbour> result) {
		if (root == null) {
			return null;
		}
		findPointsWithinRadiusWithDistance(root, targetPoint, result, radius);

		// return the points within the radius
		return result;
	}

	private void findPointsWithinRadiusWithDistance(KDNode node,
			ImageAttr targetPoint, List<NearestNeighbour> result, double radius) {
		if (node.image != targetPoint) {
			tryToSaveRadiusWithDistance(node, targetPoint, result, radius);
		}

		double perpendicularDistance = Math
				.abs(node.image.getFeatures()[node.d]
						- targetPoint.getFeatures()[node.d]);
		if (perpendicularDistance < radius) {
			// explore the "above" and "below" branches.
			if (node.above != null) {
				findPointsWithinRadiusWithDistance(node.above, targetPoint,
						result, radius);
			}
			if (node.below != null) {
				findPointsWithinRadiusWithDistance(node.below, targetPoint,
						result, radius);
			}
		} else // explore one side of the tree.
		{
			if (targetPoint.getFeatures()[node.d] < node.image.getFeatures()[node.d]) {
				if (node.below != null) {
					findPointsWithinRadiusWithDistance(node.below, targetPoint,
							result, radius);
				}
			} else if (node.above != null) {
				findPointsWithinRadiusWithDistance(node.above, targetPoint,
						result, radius);
			}
		}
	}

	private void tryToSaveRadiusWithDistance(KDNode node, ImageAttr target,
			List<NearestNeighbour> result, double radius) {
		if (node != null) {
			double distance = distanceFunction.calculateDistance(
					target.getFeatures(), node.image.getFeatures());
			if (distance <= radius) {
				result.add(new NearestNeighbour(node.image, distance));
			}
		}
	}

	private String toString(double[] values) {
		StringBuilder buffer = new StringBuilder();
		for (Double element : values) {
			buffer.append(" " + element);
		}
		return buffer.toString();
	}

	@Override
	public String toString() {
		return toString(root, " ");
	}

	private String toString(KDNode node, String indent) {
		if (node == null) {
			return "";
		}
		String newIndent1 = indent + "   |";
		String newIndent2 = indent + "   |";
		return node.image + " (" + node.d + ") \n" + indent
				+ toString(node.above, newIndent1) + "\n" + indent
				+ toString(node.below, newIndent2);
	}
}
