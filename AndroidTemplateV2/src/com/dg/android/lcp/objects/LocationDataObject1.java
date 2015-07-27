package com.dg.android.lcp.objects;

public class LocationDataObject1 {


	
    String id;
    String phone_number;
    String address;
    String app_display_text;
    String zipcode;
    String name;
    String longitude;
    String latitude;
    String offerId;

    /**
	 * @return the offerId
	 */
	public String getOfferId() {
		return offerId;
	}

	/**
	 * @param offerId the offerId to set
	 */
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	AvailableOfferObject []available_offers;

 
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
	 * @return the phone_number
	 */
	public String getPhone_number() {
		return phone_number;
	}

	/**
	 * @param phone_number the phone_number to set
	 */
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the app_display_text
	 */
	public String getApp_display_text() {
		return app_display_text;
	}

	/**
	 * @param app_display_text the app_display_text to set
	 */
	public void setApp_display_text(String app_display_text) {
		this.app_display_text = app_display_text;
	}

	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @param zipcode the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	

	
	public AvailableOfferObject[] getAvailable_offers() {
		return available_offers;
	}

		public void setAvailable_offers(AvailableOfferObject[] available_offers) {
		this.available_offers = available_offers;
	}

	
 
    
}
