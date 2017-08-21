package com.edu.myneu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.edu.myneu.FeatureExtraction.FeatureExtract;
import com.edu.myneu.KDTress.KDTree;
import com.edu.myneu.KDTress.NearestNeighbour;
import com.edu.myneu.KDTress.RBTree;
import com.edu.myneu.distance.EuclidDistance;
import com.edu.myneu.distance.DistanceFunction;
import com.edu.myneu.kmeansclustering.BisectingKMeans;
import com.edu.myneu.kmeansclustering.ClusterWithMean;
import com.edu.myneu.kmeansclustering.ClustersEvaluation;
import com.edu.myneu.pojo.ImageAttr;
import com.edu.myneu.pojo.ImageDB;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	@Autowired
	ServletContext servletContext;

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model)
			throws NumberFormatException, IOException {
		logger.info("Welcome home! The client locale is {}.", locale);
		ImageAttr image = new ImageAttr();
		model.addAttribute("image", image);
		return "home";
	}

	@RequestMapping(value = "/upload.htm", method = RequestMethod.POST)
	public ModelAndView handleUpload(@ModelAttribute("image") ImageAttr image,
			@RequestParam("check") String checkedValue, Model model)
			throws NumberFormatException, IOException, NoSuchAlgorithmException {

		if (image.getImage() != null && checkedValue != null) {

			List<ImageAttr> actual = new ArrayList<>();
			List<ImageAttr> similar = new ArrayList<>();
			actual.clear();
			similar.clear();

			double[][] distances = new double[1000][2];
			double dis;
			double accuracy = 76;
			for (int i = 0; i < 1000; i++) {
				distances[i][0] = -1;
				distances[i][1] = 100;
			}

			int k;
			double D;
			BufferedReader reader;
			String filepath = "C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\image\\";
			File folder = new File(filepath);
			File[] listOfFiles = folder.listFiles();

			try {
				CommonsMultipartFile photoInMemory = image.getImage();
				String path = photoInMemory.getFileItem().getName();
				String fileName = photoInMemory.getOriginalFilename();
				System.out.println("File is stored at" + fileName);
				FeatureExtract featureExtraction = new FeatureExtract();
				File file = new File(path);
				int inputkey = Integer.parseInt(file.getName().split("\\.")[0]);
				ImageAttr inputImage = FeatureExtract.extractSingleImage(path);

				try {
					int j = 0;
					reader = new BufferedReader(
							new FileReader(
									"C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\Features.txt"));
					String line;
					while ((line = reader.readLine()) != null) {

						String filpath = listOfFiles[j].getPath();
						String[] strArr = line.split("\\t");

						double[] doubleArray = new double[66];
						for (int i = 0; i < strArr.length; i++) {
							doubleArray[i] = Double.parseDouble(strArr[i]);
						}
						k = 0;
						DistanceFunction distanceFunction = new EuclidDistance();
						D = distanceFunction.calculateDistance(
								inputImage.getFeatures(), doubleArray);
						while (distances[k][1] < D) {
							k = k + 1;
						}
						for (int n = 999; n >= k + 1; n--) {
							distances[n][0] = distances[n - 1][0];
							distances[n][1] = distances[n - 1][1];
						}
						distances[k][0] = j;
						distances[k][1] = D;

						ImageAttr img = new ImageAttr();
						img.setImageName("img-" + j);
						img.setImagePath("C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\image\\"
								+ j + ".jpg");
						img.setFeatures(doubleArray);
						img.setHashValue(j);
						ImageDB.getImageDB().put(j, img);
						System.out.println(j);
						j++;
					}
					reader.close();

					Map<Integer, ImageAttr> ht = ImageDB.getImageDB();
					if (ht.get(inputkey) != null
							&& ht.get(inputkey).getFeatures()[0] == inputImage
									.getFeatures()[0]) {
						actual.add(ht.get(inputImage.getHashValue()));
					} else {
						ImageDB.getImageDB().put(inputImage.getHashValue(),
								inputImage);

					}

					if (checkedValue.equalsIgnoreCase("KNearset")) {
						KDTree tree = new KDTree();
						List<ImageAttr> images = new ArrayList<ImageAttr>(
								ImageDB.getImageDB().values());
						tree.buildtree(images);
						System.out.println("\nTREE: \n" + tree.toString()
								+ "  \n\n Number of elements in tree: "
								+ tree.size());

						int KNN = 10;
						RBTree<NearestNeighbour> result = tree.knearest(
								inputImage, KNN);
						System.out.println(result.toString());
						Iterator<NearestNeighbour> iterator = result.iterator();
						while (iterator.hasNext()) {
							NearestNeighbour point = iterator.next();
							ImageAttr img = point.image;
							similar.add(img);
						}
					}
					if (checkedValue.equalsIgnoreCase("KMeans")) {
						int kM = 100;
						DistanceFunction distanceFunction = new EuclidDistance();
						BisectingKMeans algoKMeans = new BisectingKMeans();
						List<ClusterWithMean> clusters = algoKMeans
								.runAlgorithm(ImageDB.getImageDB(), kM,
										distanceFunction, 100);
						algoKMeans.printStatistics();
						double error = ClustersEvaluation.calculateSSE(
								clusters, distanceFunction);
						accuracy = 100 - error;
						System.out.println(accuracy);
						for (int i = 0; i < clusters.size(); i++) {
							for (ImageAttr img : clusters.get(i).getVectors()) {
								if (img.getImageName().contains(
										inputImage.getImageName())) {
									for (ImageAttr images : clusters.get(i)
											.getVectors()) {
										similar.add(images);
									}

								}
							}
						}
					}

					if (checkedValue.equalsIgnoreCase("Normal")) {
						for (int i = 0; i < 10; i++) {
							String r = "C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\image\\"
									+ (int) distances[i][0] + ".jpg";
							System.out.println(r);
							int key = (int) distances[i][0];
							if (ht.get(key) != null) {
								similar.add(ht.get(key));
							}
						}
					}
					model.addAttribute("acc", accuracy);
					model.addAttribute("path", path);
					model.addAttribute("actual", path);

				} catch (IllegalStateException e) {
					System.out.println("*** IllegalStateException: "
							+ e.getMessage());
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}

			return new ModelAndView("home", "userList", similar);
		}
		return new ModelAndView("home");
	}
}
