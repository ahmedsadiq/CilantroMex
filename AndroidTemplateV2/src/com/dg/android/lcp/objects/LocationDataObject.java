package com.dg.android.lcp.objects;

public class LocationDataObject {

    private String restaurantId;
    private String name;
    private String offerId;
    private String offerTitle;
    private String PointsMultiplier;
    private String offerStartDate;
    private String offerEndDate;
    private String city;
    private double lattitude;
    private double longitude;
    private String address;
    private String miniDescription;
    private String offerDetails;
    private String restaurantZipcode;
    private String restaurantContact;
    private String zone;
    private String claimId;



    public LocationDataObject() {
    }

    public LocationDataObject(String restaurantId, String name, String offerId, String offerTitle, String PointsMultiplier, String offerStartDate,
            String offerEndDate, String city, double lattitude, double longitude, String address, String miniDescription, String offerDetails,
            String restaurantZipcode, String restaurantContact, String zone, String claimId) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.offerId = offerId;
        this.offerTitle = offerTitle;
        this.PointsMultiplier = PointsMultiplier;
        this.offerStartDate = offerStartDate;
        this.offerEndDate = offerEndDate;
        this.city = city;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.address = address;
        this.miniDescription = miniDescription;
        this.offerDetails = offerDetails;
        this.restaurantZipcode = restaurantZipcode;
        this.restaurantContact = restaurantContact;
        this.zone = zone;
        this.claimId = claimId;

    }


    /**
     * @return the restaurantId
     */
    public String getRestaurantId() {
        return restaurantId;
    }


    /**
     * @param restaurantId
     *            the restaurantId to set
     */
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the offerId
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * @param offerId
     *            the offerId to set
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * @return the offerTitle
     */
    public String getOfferTitle() {
        return offerTitle;
    }

    /**
     * @param offerTitle
     *            the offerTitle to set
     */
    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    /**
     * @return the pointsMultiplier
     */
    public String getPointsMultiplier() {
        return PointsMultiplier;
    }

    /**
     * @param pointsMultiplier
     *            the pointsMultiplier to set
     */
    public void setPointsMultiplier(String pointsMultiplier) {
        PointsMultiplier = pointsMultiplier;
    }

    /**
     * @return the offerStartDate
     */
    public String getOfferStartDate() {
        return offerStartDate;
    }

    /**
     * @param offerStartDate
     *            the offerStartDate to set
     */
    public void setOfferStartDate(String offerStartDate) {
        this.offerStartDate = offerStartDate;
    }

    /**
     * @return the offerEndDate
     */
    public String getOfferEndDate() {
        return offerEndDate;
    }

    /**
     * @param offerEndDate
     *            the offerEndDate to set
     */
    public void setOfferEndDate(String offerEndDate) {
        this.offerEndDate = offerEndDate;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *            the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the lattitude
     */
    public double getLattitude() {
        return lattitude;
    }

    /**
     * @param lattitude
     *            the lattitude to set
     */
    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude
     *            the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }



    /**
     * @return the miniDescription
     */
    public String getMiniDescription() {
        return miniDescription;
    }



    /**
     * @param miniDescription
     *            the miniDescription to set
     */
    public void setMiniDescription(String miniDescription) {
        this.miniDescription = miniDescription;
    }



    /**
     * @return the offerDetails
     */
    public String getOfferDetails() {
        return offerDetails;
    }



    /**
     * @param offerDetails
     *            the offerDetails to set
     */
    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
    }



    /**
     * @return the restaurantZipcode
     */
    public String getRestaurantZipcode() {
        return restaurantZipcode;
    }



    /**
     * @param restaurantZipcode
     *            the restaurantZipcode to set
     */
    public void setRestaurantZipcode(String restaurantZipcode) {
        this.restaurantZipcode = restaurantZipcode;
    }



    /**
     * @return the restaurantContact
     */
    public String getRestaurantContact() {
        return restaurantContact;
    }



    /**
     * @param restaurantContact
     *            the restaurantContact to set
     */
    public void setRestaurantContact(String restaurantContact) {
        this.restaurantContact = restaurantContact;
    }



    /**
     * @return the zone
     */
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }


    /**
     * @param zone
     *            the zone to set
     */
    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getClaimId() {
        return claimId;
    }



    /**
     * @param zone
     *            the zone to set
     */




}