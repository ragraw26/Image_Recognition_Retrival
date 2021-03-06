package com.edu.myneu;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.edu.myneu.FeatureExtraction.FeatureExtract_Text;
import com.edu.myneu.Indexing.HashMapIndexing;

@Controller
public class FeaturesController {

	@Autowired
	ServletContext servletContext;

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);

	@RequestMapping(value = "/features", method = RequestMethod.GET)
	public String getFeatures(Locale locale, Model model)
			throws NumberFormatException, IOException, NoSuchAlgorithmException {
		FeatureExtract_Text featureExtraction_Text = new FeatureExtract_Text();
		String path = "C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\image\\";
		FeatureExtract_Text.extractAll(path);
		HashMapIndexing hashMapIndexing = new HashMapIndexing();
		hashMapIndexing.createMap();
		System.out.println("Features file created");
		return "home";
	}

}
