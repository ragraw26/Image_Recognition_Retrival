package com.edu.myneu.FeatureExtraction;

import ij.process.ColorProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.imageio.ImageIO;

import com.edu.myneu.pojo.ImageAttr;

public class FeatureExtract {

	public static float[][] R;
	public static float[][] G;
	public static float[][] B;
	public static float[][] H;
	public static float[][] S;
	public static float[][] V;

	public static int[][] bin;
	static int height;
	static int width;

	public static Calendar cal = Calendar.getInstance();
	public static PrintStream outFile;

	public static ImageAttr extractSingleImage(String name)
			throws NoSuchAlgorithmException, IOException {
		double[] out = new double[66];
		URL url = Paths.get(name).toUri().toURL();
		System.out.println("url = " + url);
		BufferedImage image = null;
		try {
			image = ImageIO.read(url);
		} catch (IOException e) {
			System.out.println("read error: " + e.getMessage());
		}

		ColorProcessor cp = new ColorProcessor(image);

		// Color Extraction

		ColorExtract ce = new ColorExtract(cp);
		ColorExtract.extract();
		double[] colors = ColorExtract.getColorFeature();

		// Edge Detection

		EdgeDetect ced = new EdgeDetect();
		ced.setSourceImage(image);
		ced.process();
		BufferedImage edge = ced.getEdgesImage();
		rgb2bin(edge);

		// Texture Extraction

		TextureExtraction te = new TextureExtraction(bin, height, width);
		TextureExtraction.extract();
		double[] textures = TextureExtraction.getCoocFeatures();

		// Join
		for (int i = 0; i < 18; i++) {
			out[i] = colors[i];
		}

		for (int i = 0; i < 48; i++) {
			out[i + 18] = textures[i];
		}

		ImageAttr img = new ImageAttr();
		File file = new File(name);
		int imageNumber = Integer.parseInt(file.getName().split("\\.")[0]);
		img.setFeatures(out);
		//img.setImagePath(name);
		img.setImageName("img-" + imageNumber);
		// String md5 = Checksum.checkSumMD5(name);
		img.setHashValue(imageNumber);
		return img;
	}

	public void extractAll() throws MalformedURLException {

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("ImageDB/").getFile());

		long start = cal.getTimeInMillis();
		// File folder = new File(resourcesDirectory);
		File[] listOfFiles = file.listFiles();

		for (int ind = 0; ind < listOfFiles.length; ind++) {
			double[] features = new double[66];
			String filepath = listOfFiles[ind].getPath();

			URL url = Paths.get(filepath).toUri().toURL();
			// where are we looking for this image
			BufferedImage image = null;
			try {
				image = ImageIO.read(url);
			} catch (IOException e) {
				System.out.println("read error: " + e.getMessage());
			}

			ColorProcessor cp = new ColorProcessor(image);

			// Color Extraction
			ColorExtract ce = new ColorExtract(cp);
			ColorExtract.extract();
			double[] colors = ColorExtract.getColorFeature();

			// Edge Detection
			EdgeDetect ced = new EdgeDetect();
			ced.setSourceImage(image);
			ced.process();
			BufferedImage edge = ced.getEdgesImage();
			rgb2bin(edge);

			// //////////// Texture Extraction ///////////////
			TextureExtraction te = new TextureExtraction(bin, height, width);
			TextureExtraction.extract();
			double[] textures = TextureExtraction.getCoocFeatures();

			for (int i = 0; i < 18; i++) {
				features[i] = colors[i];
			}

			for (int i = 0; i < 48; i++) {
				features[i + 18] = textures[i];
			}

			ImageAttr img = new ImageAttr();
			int hashCode = img.hashCode();
			// img.setImagePath();
			img.setFeatures(features);
			img.setImageName("Image" + ind);

			// ImageDB.getImageDB().put(hashCode, img);

		}

		long finish = cal.getTimeInMillis();
		System.out.println("time: " + (finish - start));

	}

	public static void RGB_HSV_Decompose(ColorProcessor cp) {

		R = new float[cp.getHeight()][cp.getWidth()];
		G = new float[cp.getHeight()][cp.getWidth()];
		B = new float[cp.getHeight()][cp.getWidth()];
		H = new float[cp.getHeight()][cp.getWidth()];
		S = new float[cp.getHeight()][cp.getWidth()];
		V = new float[cp.getHeight()][cp.getWidth()];
		int rgb[] = new int[3];
		float hsv[] = new float[3];
		for (int i = 0; i < cp.getHeight(); i++) {
			for (int j = 0; j < cp.getWidth(); j++) {
				rgb = cp.getPixel(i, j, rgb);
				R[i][j] = rgb[0];
				G[i][j] = rgb[1];
				B[i][j] = rgb[2];
				hsv = java.awt.Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsv);
				H[i][j] = hsv[0];
				S[i][j] = hsv[1];
				V[i][j] = hsv[2];
			}
		}
	}

	public static double[] meanStdSkew(float[][] data, int h, int w) {
		double mean = 0;
		double[] out = new double[3];

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				mean += data[i][j];
			}
		}
		mean /= (h * w);
		out[0] = mean;
		double sum = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				final double v = data[i][j] - mean;
				sum += v * v;
			}
		}
		out[1] = Math.sqrt(sum / (h * w - 1));

		sum = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				final double v = (data[i][j] - mean) / out[1];
				sum += v * v * v;
			}
		}

		out[2] = Math.pow(sum / (h * w - 1), 1. / 3);
		return out;
	}

	public static void rgb2bin(BufferedImage in) {
		int h = in.getHeight();
		int w = in.getWidth();
		height = h;
		width = w;
		int[][] out = new int[h][w];
		long rgb;
		int r, g, b;
		ColorProcessor cp = new ColorProcessor(in);
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				rgb = cp.getPixel(i, j);
				r = (int) (rgb % 0x1000000 / 0x10000);
				g = (int) (rgb % 0x10000 / 0x100);
				b = (int) (rgb % 0x100);
				if (r == 0 && g == 0 && b == 0) {
					out[i][j] = 0;
				} else {
					out[i][j] = 1;
				}
			}
		}

		bin = out;

	}
}