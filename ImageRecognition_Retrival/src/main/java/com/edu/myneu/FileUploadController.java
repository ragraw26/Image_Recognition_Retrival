package com.edu.myneu;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.edu.myneu.pojo.ImageAttr;

@Controller
public class FileUploadController {

	@Autowired
	ServletContext servletContext;

	public int noOfFiles;

	public int getNoOfFiles() {
		return noOfFiles;
	}

	public void setNoOfFiles(int noOfFiles) {
		this.noOfFiles = noOfFiles;
	}

	@RequestMapping(value = "/addImage.htm", method = RequestMethod.GET)
	public String showForm(ModelMap model) {

		ImageAttr img = new ImageAttr();
		// command object
		model.addAttribute("image", img);

		// return form view
		return "addImage";

	}

	@RequestMapping(value = "/addImage.htm", method = RequestMethod.POST)
	public String handleUpload(@ModelAttribute("image") ImageAttr image) {
		try {

			File directory;		

			String path = "C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\image\\";

			File root = new File(path);
			File[] listOfFiles = root.listFiles();
			
			
			directory = new File(
					"C:\\Users\\Rajat\\Desktop\\Final\\sts-bundle\\sts-3.6.4.RELEASE\\image\\");

			boolean temp = directory.exists();
			if (!temp) {
				temp = directory.mkdir();
			}
			if (temp) {

				noOfFiles = root.listFiles().length;
				System.out.println("Total Files Discovered : " + noOfFiles);
				// We need to transfer to a file
				CommonsMultipartFile photoInMemory = image.getImage();

				String fileName = noOfFiles + ".jpg";
				// could generate file names as well

				File localFile = new File(directory.getPath(), fileName);

				// move the file from memory to the file

				photoInMemory.transferTo(localFile);
				image.setImagePath(localFile.getPath());
				System.out.println("File is stored at" + localFile.getPath());

			} else {
				System.out.println("Failed to create directory!");
			}

		} catch (IllegalStateException e) {
			System.out.println("*** IllegalStateException: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("*** IOException: " + e.getMessage());
		}

		return "home";
	}

}