package com.bulkUpload.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.bulkUpload.controller.UploadController;
import com.bulkUpload.service.FileUploadService;
import com.bulkUpload.util.Logger;

public class FileUploadControllerTest extends Mockito{

	private final Logger logger = Logger.getLogger(FileUploadControllerTest.class);

	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Mock
	FileUploadService service;

	@Mock
	MessageSource message;

	@InjectMocks
	UploadController fileUploadController;

	@Spy
	List<Object[]> validDeals = new ArrayList<Object[]>();

	@Spy
	List<Object[]> inValidDeals = new ArrayList<Object[]>();

	@Spy
	ModelMap model;

	@Mock
	BindingResult result;

	String fileName = "sample.csv";

	File file;

	HttpServletRequest request = mock(HttpServletRequest.class);       
	HttpServletResponse response = mock(HttpServletResponse.class);   

	@BeforeClass
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		readCSVFile(getFileList());
	}


	@Test
	public void newFileUpload() throws FileNotFoundException, IOException{
		String METHOD_NAME = "newStorage";
		logger.logInfo(METHOD_NAME+ " started..");

		MockMultipartFile multipartFile = new MockMultipartFile(fileName, 
				org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(file)));


		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		try {
			mockMvc.perform(MockMvcRequestBuilders.fileUpload("/bulkUpload")
					.file(multipartFile))
			.andExpect(MockMvcResultMatchers.status().is(200))
			.andReturn();
		} catch (Exception e) {
			e.printStackTrace();
		}		

		Assert.assertNotNull(model.get("documents"));
	}


	public List<String[]> getFileList() {

		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource(fileName).getFile());
		System.out.println(file.getAbsolutePath());
		return fileUploadController.readCSVFile(file, fileName);

	}

	public void readCSVFile(List<String[]> lines){

		int rowlength=0;

		Object[] obj1 =new   Object[5] ;
		obj1[4]=fileName;
		Object[] obj =null;
		List<Thread> threadList=new ArrayList();
		Thread thread=null;
		int i=0;
		boolean invflag=false;

		for(String[] line:lines) {

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

				if(invflag) this.inValidDeals.add(obj);

				else this.validDeals.add(obj);




			}catch(Exception e) {

			}









		}
	}
}
