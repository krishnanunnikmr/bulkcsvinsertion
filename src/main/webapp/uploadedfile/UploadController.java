package com.bloomberg.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bloomberg.model.CSVRecord;
import com.bloomberg.service.FileUploadService;
import com.bloomberg.util.Logger;
import com.opencsv.CSVReader;

@Controller
public class UploadController {

	private final Logger logger = Logger.getLogger(UploadController.class);


	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	FileUploadService service ;

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/*")
	public String index() {

		logger.logInfo("Stepped into the index() method");


		return "fileUpload";
	}

	@PostMapping(value = "/upload") 
	public String uploadFile(
			ModelMap model,
			@RequestParam("file") MultipartFile file,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {

		
		Date startTime=new Date();
		String METHOD_NAME = "uploadFile";

		Map<String, String> messages = new HashMap<String, String>();
		logger.logInfo(METHOD_NAME + "started");
		if (file.isEmpty()) {
			messages.put("alert-danger", messageSource.getMessage("missing.file",null, Locale.getDefault()));
			model.put("messages", messages);
			logger.logInfo("Validation failed file is empty");
			return "fileUpload";
		}
		else if(service.checkFileExist(file.getOriginalFilename())){
			messages.put("alert-danger", "File already exist");
			model.put("messages", messages);
			logger.logInfo("File already exist");
			return "fileUpload";
		}
		String fileName = file.getOriginalFilename();
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		File dir = new File(rootPath + File.separator + "uploadedfile");
		if (!dir.exists()) {
			dir.mkdirs();
		}


		File serverFile = new File(dir.getAbsolutePath() + File.separator + (fileName));


		try {
			try (InputStream is = file.getInputStream();
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile))) {
				int i;
				//write file to server
				while ((i = is.read()) != -1) {
					stream.write(i);
				}
				stream.flush();
			}
		} catch (IOException ex) {
			messages.put("alert-danger", messageSource.getMessage("failed.msg",new Object [] {ex}, Locale.getDefault()) );
			model.put("messages", messages);
			logger.LogException(METHOD_NAME + " crashed ", ex);
			return "fileUpload";

		}


		try {

			logger.logInfo(" reading CSV file");


			List<Object[]> validDeals = new ArrayList<Object[]>();
			List<Object[]> inValidDeals = new ArrayList<Object[]>();
			boolean invflag=false;
			List<String[]> csvList=readCSVFile(serverFile, fileName);
			int rowlength=0;
			CSVRecord csvtemp=null;
			Object[] obj1 =new   Object[5] ;
			obj1[4]=fileName;
			Object[] obj =null;
			for(String[] line:csvList) {
				invflag=false;
				obj =obj1.clone() ;

				csvtemp = new CSVRecord();

				try {
					rowlength=line.length;

					if(rowlength<4) invflag=true;
					if(rowlength>1) obj[0]=line[0];
					if(rowlength>2) obj[1]=line[1];
					if(rowlength>3) {
						try {
							obj[2]=formatter.parse(line[2]);
						} catch (Exception e) { 
							obj[2]=line[2];
							invflag=true;
						}
					}
					if(rowlength>=4) {

						try {
							obj[3]=new BigInteger(line[3]);
						} catch (Exception e) {  
							invflag=true;
							obj[3]=line[3];
						}
					}


				}catch(Exception e) {
					invflag=true;
				}

				try {

					if( 
							StringUtils.isEmpty(line[0]) ||
							StringUtils.isEmpty(line[1]) ||
							StringUtils.isEmpty(line[2]) ||
							StringUtils.isEmpty(line[3]) 


							) {
						invflag=true;

					}


					if(invflag) inValidDeals.add(obj);

					else validDeals.add(obj);

				}catch(Exception e) {

				}
			}
			if(validDeals.size() > 0)
				service.saveValidData(validDeals);
			if(inValidDeals.size() > 0)
				service.saveInValidData(inValidDeals);
          System.out.println("validDeals="+validDeals.size());
          System.out.println("validDeals="+inValidDeals.size());
		
		} catch (Exception e) {
			//logger.LogException(METHOD_NAME + " crashed ", e);
		} 
		Date endTime=new Date();
		long diff = endTime.getTime() - startTime.getTime();

		long diffSeconds = diff / 1000 % 60;
		System.out.println("diffSeconds==="+diffSeconds);
		
		messages.put("success", messageSource.getMessage("success.msg",new Object [] {file.getOriginalFilename()}, Locale.getDefault()) );
		model.put("messages", messages);
		logger.logInfo(METHOD_NAME + " completed successfully!!!");
		return "fileUpload";
	}


	/**
	 * This method extract the data and return the CVSRecord class 
	 * @param line The line of CSV file.
	 */
	CSVRecord extractData(String[] line){

		CSVRecord dealModel = new CSVRecord();
		dealModel.setToCurrency(line[0]);
		dealModel.setFromCurrency(line[1]);

		try {
			dealModel.setDealDate(formatter.parse(line[2]));
		} catch (ParseException e) {
			logger.LogException("Exception while parsing CSV columns: ", e);
		}

		dealModel.setAmount(new BigInteger(line[3]));
		return dealModel;
	}

	List<String[]> readCSVFile(File serverFile, String fileName){
		String METHOD_NAME = "readCSVFile";
		List<String[]> lines = null;
		try {

			//logger.logInfo(" reading CSV file");
			FileReader fileReader = new FileReader(serverFile);
			CSVReader reader = new CSVReader(fileReader, ',');
			lines = reader.readAll();



		} catch (IOException e) {
			//logger.LogException(METHOD_NAME + " crashed ", e);
		} 

		return lines;
	}


}