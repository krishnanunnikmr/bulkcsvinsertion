package com.bulkUpload.service;

import java.util.List;

public interface IFileUploadService {

	void saveValidData(List<Object[]> dealDetails);

	void saveInValidData(List<Object[]> dealDetails);

	boolean checkFileExist(String fileName);

}
