package com.dg.android.lcp.objects;

import java.io.Serializable;

public class ActivityDataObject implements Serializable{
	
	private String ReceiptSubmitID;
    private String chainID;
      private String name;
    private String offerid;
    private String PointsMultiplier;
    private String receiptStatus;
    private String SubmitDate;
    private String offerTitle;
    private String Subtotal;
    private String PointsEarned;
    private String submitted;
    
    
    public String getReceiptSubmitID() {
        return ReceiptSubmitID;
    }


    /**
     * @param ReceiptSubmitID
     *            the restaurantId to set
     */
    public void setReceiptSubmitID(String ReceiptSubmitID) {
        this.ReceiptSubmitID = ReceiptSubmitID;
    }
    
    public String getchainID() {
        return chainID;
    }


    /**
     * @param chainID
     *            the chainID to set
     */
    public void setchainID(String chainID) {
        this.chainID = chainID;
    }
    
    
    public String getname() {
        return name;
    }


    /**
     * @param name
     *            the name to set
     */
    public void setname(String name) {
        this.name = name;
    }
    
    public String getofferid() {
        return offerid;
    }


    /**
     * @param offerid
     *            the offerid to set
     */
    public void setofferid(String offerid) {
        this.offerid = offerid;
    }
    
    public String getPointsMultiplier() {
        return PointsMultiplier;
    }


    /**
     * @param PointsMultiplier
     *            the PointsMultiplier to set
     */
    public void setPointsMultiplier(String PointsMultiplier) {
        this.PointsMultiplier = PointsMultiplier;
    }
    
    
    public String getreceiptStatus() {
        return receiptStatus;
    }


    /**
     * @param receiptStatus
     *            the PointsMultiplier to set
     */
    public void setreceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }
    
    public String getSubmitDate() {
        return SubmitDate;
    }


    /**
     * @param SubmitDate
     *            the SubmitDate to set
     */
    public void setSubmitDate(String SubmitDate) {
        this.SubmitDate = SubmitDate;
    }
    
    public String getofferTitle() {
        return offerTitle;
    }


    /**
     * @param offerTitle
     *            the offerTitle to set
     */
    public void setofferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }
    
    
    public String getSubtotal() {
        return Subtotal;
    }


    /**
     * @param Subtotal
     *            the Subtotal to set
     */
    public void setSubtotal(String Subtotal) {
        this.Subtotal = Subtotal;
    }
    
    
    public String getPointsEarned() {
        return PointsEarned;
    }


    /**
     * @param PointsEarned
     *            the PointsEarned to set
     */
    public void setPointsEarned(String PointsEarned) {
        this.PointsEarned = PointsEarned;
    }
    
    public String getsubmitted() {
        return submitted;
    }


    /**
     * @param submitted
     *            the submitted to set
     */
    public void setsubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    
    

}
