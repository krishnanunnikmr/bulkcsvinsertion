package com.bulkUpload.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bulkUpload.dao.IFileUploadDAO;
import com.bulkUpload.model.AccumulativeDeal;
import com.bulkUpload.model.InValidDeal;
import com.bulkUpload.model.ValidDeal;
import com.bulkUpload.util.Logger;



@Service
public class FileUploadService implements IFileUploadService {

	private static final Logger logger = Logger.getLogger(FileUploadService.class);

	@Autowired
	private IFileUploadDAO dao;


	 @Transactional
	 @Override
	public void saveValidData(List<Object[]> dealDetails) {
		//logger.logInfo("Stepped into the saveValidData() method CSV record size: "+ dealDetails.size());
		List<ValidDeal> validDeals = new ArrayList<>();
		Map accumulativeValues = new HashMap<>(); 
		ValidDeal validDeal =null;
		for(Object[] deal:dealDetails){

			validDeal = new ValidDeal();
			validDeal.setFromCurrency((String)deal[0]);
			validDeal.setToCurrency((String)deal[1]);
			validDeal.setDealDate((Date)deal[2]);
			validDeal.setAmount((BigInteger)deal[3]);			
			validDeal.setFileName((String)deal[4]);;


			if(accumulativeValues.containsKey(validDeal.getFromCurrency())){
				int value = Integer.parseInt(String.valueOf((accumulativeValues.get(validDeal.getFromCurrency()))));
				accumulativeValues.put(validDeal.getFromCurrency(), ++value);
			}
			else{
				accumulativeValues.put(validDeal.getFromCurrency(), 1);
			}

			validDeals.add(validDeal);
		}
		List<AccumulativeDeal> accumulativeDeals = new ArrayList<>();
		for (Object key : accumulativeValues.keySet()) {
			AccumulativeDeal accumulativeDeal = new AccumulativeDeal();
			accumulativeDeal.setCount(new BigInteger(String.valueOf(accumulativeValues.get(key))));
			accumulativeDeal.setOderingCurrency(key.toString());
			accumulativeDeals.add(accumulativeDeal);
			
		}
		dao.bulkValidSave(validDeals, accumulativeDeals);
	}

    @Transactional
	@Override
	public void saveInValidData(List<Object[]> dealDetails) {

		logger.logInfo("Stepped into the saveInValidData() method CSV record size: "+ dealDetails.size());
		List<InValidDeal> inValidDeals = new ArrayList<>();
		InValidDeal invalideal = null;
		for(Object[] deal:dealDetails){

			invalideal = new InValidDeal();

			invalideal.setFromCurrency((String)deal[0]);
			invalideal.setToCurrency((String)deal[1]);
			invalideal.setDealDate((String)deal[2]);
			invalideal.setAmount((String)deal[3]);			
			invalideal.setFileName((String)deal[4]);;

			inValidDeals.add(invalideal);
		}
		dao.bulkInvalidSave(inValidDeals);
	}


	@Override
	public boolean checkFileExist(String fileName) {
		logger.logInfo("Stepped into the checkFileExist() method file name: "+ fileName);

		return dao.fileExists(fileName);
	}

}
