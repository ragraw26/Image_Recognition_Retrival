package com.edu.myneu.FeatureExtraction;

import ij.process.ColorProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.imageio.ImageIO;

public class FeatureExtract_Text {

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

	public static void extractAll(String path) throws MalformedURLException {
		try {
			outFile = new PrintStream(
					"C:\\Users\\Rajat\\Desktop\\New folder\\Features.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		long start = cal.getTimeInMillis();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int ind = 0; ind < listOfFiles.length; ind++) {
			double[] features = new double[66];
			String filepath = listOfFiles[ind].getPath();
			URL url = Paths.get(filepath).toUri().toURL();

			BufferedImage image = null;
			// where are we looking for this image
			try {
				image = ImageIO.read(url);
			} catch (IOException e) {
				System.out.println("read error: " + e.getMessage());
			}

			ColorProcessor cp = new ColorProcessor(image);

			ColorExtract ce = new ColorExtract(cp);
			ColorExtract.extract();
			double[] colors = ColorExtract.getColorFeature();

			EdgeDetect ced = new EdgeDetect();
			ced.setSourceImage(image);
			ced.process();
			BufferedImage edge = ced.getEdgesImage();
			rgb2bin(edge);

			TextureExtraction te = new TextureExtraction(bin, height, width);
			TextureExtraction.extract();
			double[] textures = TextureExtraction.getCoocFeatures();

			for (int i = 0; i < 18; i++) {
				features[i] = colors[i];
				outFile.print(colors[i] + "\t");
			}

			for (int i = 0; i < 48; i++) {
				features[i + 18] = textures[i];
				outFile.print(textures[i] + "\t");
			}

			outFile.println();

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