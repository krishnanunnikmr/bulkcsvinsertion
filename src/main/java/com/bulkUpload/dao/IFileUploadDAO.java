package com.bulkUpload.dao;

import java.util.Collection;
import java.util.List;

import com.bulkUpload.model.AccumulativeDeal;
import com.bulkUpload.model.InValidDeal;
import com.bulkUpload.model.ValidDeal;

public interface IFileUploadDAO {

	boolean fileExists(String fileName);

	public <T extends ValidDeal> Collection<T> bulkValidSave(Collection<T> entities, List<AccumulativeDeal> accumulativeDeals);

	public <T extends InValidDeal> Collection<T> bulkInvalidSave(Collection<T> entities);

}
