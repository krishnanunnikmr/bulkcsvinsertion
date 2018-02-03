package com.bulkUpload.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "invalid_deal")
public class InValidDeal  implements java.io.Serializable {


	private static final long serialVersionUID = 1L;
	private Integer id;
	//make all column as String for to handle insert all type of invalid data
	private String fromCurrency;
	private String toCurrency;
	private String dealDate;
	private String amount;
	private String fileName;

	public InValidDeal() {
	}

	public InValidDeal(int id) {
		this.id = id;
	}

	/**
	 * @param id
	 * @param fromCurrency
	 * @param toCurrency
	 * @param dealDate
	 * @param amount
	 * @param fileName
	 */
	public InValidDeal(Integer id, String fromCurrency, String toCurrency, String dealDate, String amount,
			String fileName) {
		super();
		this.id = id;
		this.fromCurrency = fromCurrency;
		this.toCurrency = toCurrency;
		this.dealDate = dealDate;
		this.amount = amount;
		this.fileName = fileName;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}



	/**
	 * @return the fromCurrency
	 */
	@Column(name = "from_currency_iso_code")
	public String getFromCurrency() {
		return fromCurrency;
	}

	/**
	 * @param fromCurrency the fromCurrency to set
	 */
	public void setFromCurrency(String fromCurrency) {
		this.fromCurrency = fromCurrency;
	}

	/**
	 * @return the toCurrency
	 */
	@Column(name = "to_currency_iso_code")
	public String getToCurrency() {
		return toCurrency;
	}

	/**
	 * @param toCurrency the toCurrency to set
	 */
	public void setToCurrency(String toCurrency) {
		this.toCurrency = toCurrency;
	}

	/**
	 * @return the dealDate
	 */
	@Column(name = "date")
	public String getDealDate() {
		return dealDate;
	}

	/**
	 * @param dealDate the dealDate to set
	 */
	public void setDealDate(String dealDate) {
		this.dealDate = dealDate;
	}

	/**
	 * @return the amount
	 */
	@Column(name = "deal_amount")
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the fileName
	 */
	@Column(name = "file_name")
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


}
