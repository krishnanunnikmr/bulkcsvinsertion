package com.bulkUpload.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
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

import com.bulkUpload.service.FileUploadService;
import com.bulkUpload.util.Logger;
import com.opencsv.CSVReader;

@Controller
public class UploadController {

	private final Logger logger = Logger.getLogger(UploadController.class);


	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	FileUploadService service ;

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/bulkUpload")
	public String index() {

		logger.logInfo("Stepped into the index() method");


		return "bulkUploadJSP";
	}

	@PostMapping(value = "/bulkUpload") 
	public String uploadFile(
			ModelMap model,
			@RequestParam("file") MultipartFile file,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {

     try {
		Date startTime=new Date();
		String METHOD_NAME = "uploadFile";

		Map<String, String> messages = new HashMap<String, String>();
		int validDealCount=0,invalidDealCount=0;
		if (file.isEmpty()) {
			messages.put("alert-danger", messageSource.getMessage("missing.file",null, Locale.getDefault()));
			model.put("messages", messages);
			logger.logInfo("Validation failed file is empty");
			return "bulkUploadJSP";
		}
		else if(!file.getOriginalFilename().toUpperCase().endsWith(".CSV")){
			messages.put("alert-danger", messageSource.getMessage("failed.invalidFormat",new Object [] {file.getOriginalFilename()}, Locale.getDefault()) );
			model.put("messages", messages);
			logger.logInfo("falilded invalid file format");
			return "bulkUploadJSP";
		}
		else if(service.checkFileExist(file.getOriginalFilename())){
			messages.put("alert-danger", "File already exist");
			model.put("messages", messages);
			logger.logInfo("File already exist");
			return "bulkUploadJSP";
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
			return "bulkUploadJSP";
		}
		try {
			logger.logInfo(" reading CSV file");
			List<Object[]> validDeals = new ArrayList<Object[]>();
			List<Object[]> inValidDeals = new ArrayList<Object[]>();
			boolean invflag=false;
			List<String[]> csvList=readCSVFile(serverFile, fileName);
			int rowlength=0;

			Object[] obj1 =new   Object[5] ;
			obj1[4]=fileName;
			Object[] obj =null;
			List<Thread> threadList=new ArrayList();
			Thread thread=null;
			int i=0;
			for(String[] line:csvList) {
				i++;
				invflag=false;
				obj =obj1.clone() ;



				try {
					rowlength=line.length;

					if(rowlength<4) invflag=true;
					if(rowlength>0) obj[0]=line[0];
					if(rowlength>1) obj[1]=line[1];
					if(rowlength>2) 
						try { obj[2]=formatter.parse(line[2]);} catch (Exception e) {invflag=true;}														
					if(rowlength>3) 
						try { obj[3]=new BigInteger(line[3]);} catch (Exception e) { invflag=true;}															

					try {
						if(StringUtils.isEmpty(line[0]) ||
								StringUtils.isEmpty(line[1]) ||
								StringUtils.isEmpty(line[2]) ||
								StringUtils.isEmpty(line[3]) ) {invflag=true;}
					}catch(Exception e) {invflag=true;}					

				}catch(Exception e) {
					invflag=true;
				}

				try {

					if(invflag) {	
						if(rowlength>2) obj[2]=(String)line[2];
						if(rowlength>3) obj[3]=(String)line[3];
					}

					if(invflag) inValidDeals.add(obj);

					else validDeals.add(obj);

					if(i%5000==0){
						validDealCount+=validDeals.size();
						invalidDealCount+=inValidDeals.size();

						thread=startNewThread(validDeals,inValidDeals);							
						threadList.add(thread);
					
						validDeals=new ArrayList();
						inValidDeals=new ArrayList();
						thread.start();	
					}



				}catch(Exception e) {

				}

			}

			joinAllTherads(threadList);

			validDealCount+=validDeals.size();
			invalidDealCount+=inValidDeals.size();

			if(validDeals.size() > 0)
				service.saveValidData(validDeals);
			if(inValidDeals.size() > 0)
				service.saveInValidData(inValidDeals);
			System.out.println("validDeals="+validDeals.size());
			System.out.println("validDeals="+inValidDeals.size());

		} catch (Exception e) {
			logger.LogException(METHOD_NAME + " crashed ", e);
		} 
		Date endTime=new Date();
		long diff = endTime.getTime() - startTime.getTime();

		long diffSeconds = diff / 1000 % 60;
		System.out.println("diffSeconds==="+diffSeconds);

		messages.put("success", messageSource.getMessage("success.msg",new Object [] {file.getOriginalFilename()}, Locale.getDefault()) );

		
		
		
		model.put("duration", messageSource.getMessage("success.duration",new Object [] {diffSeconds+" Seconds"}, Locale.getDefault()));
		model.put("validDeals", messageSource.getMessage("success.validDeals",new Object [] {validDealCount}, Locale.getDefault()));
		model.put("invalidDeals", messageSource.getMessage("success.invalidDeals",new Object [] {invalidDealCount}, Locale.getDefault()));
		
		
		model.put("messages", messages);
		logger.logInfo(METHOD_NAME + " completed successfully!!!");
		return "bulkUploadJSP";	
     }
     catch(Exception ex) {
    	 logger.LogException(this.getClass().toString() + " crashed ", ex);
			return "bulkUploadJSP";
     }
     finally {
    	 System.gc();
     }
	}


	private Thread startNewThread(List validDeals,List inValidDeals) {

		return new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if(validDeals!=null&&validDeals.size()>0) {

					service.saveValidData(validDeals);
				}

				if(inValidDeals!=null&&inValidDeals.size()>0) {

					service.saveInValidData(inValidDeals);
				}


			}
		});
	}
	private void joinAllTherads(List<Thread> threadList) {

		for(Thread thread1:threadList)
			try {
				thread1.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}




	List<String[]> readCSVFile(File serverFile, String fileName){
		String METHOD_NAME = "readCSVFile";
		List<String[]> lines = null;
		try {

			logger.logInfo(" reading CSV file");
			FileReader fileReader = new FileReader(serverFile);
			CSVReader reader = new CSVReader(fileReader, ',');
			lines = reader.readAll();



		} catch (IOException e) {
			logger.LogException(METHOD_NAME + " crashed ", e);
		} 

		return lines;
	}


}