package com.dg.android.lcp.objects;

import java.io.Serializable;

public class ReceiptsObject implements Serializable{

	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 3717584211126530820L;
	String id;
      String image_url;
      TransactionObject last_transaction;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the image_url
	 */
	public String getImage_url() {
		return image_url;
	}
	/**
	 * @param image_url the image_url to set
	 */
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	/**
	 * @return the last_transaction
	 */
	public TransactionObject getLast_transaction() {
		return last_transaction;
	}
	/**
	 * @param last_transaction the last_transaction to set
	 */
	public void setLast_transaction(TransactionObject last_transaction) {
		this.last_transaction = last_transaction;
	}
      
      
}
