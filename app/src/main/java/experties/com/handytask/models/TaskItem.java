package experties.com.handytask.models;

import java.util.Date;

/**
 * Created by vincetulit on 3/8/15.
 */
public class TaskItem {

    // Job Related
    private String type;
    private String briefDescription;
    private String detailedDescription;

    // Address Related
    private String address1;
    private String address2;
    private String city;
    private String zipCode;
    private String state;

    // GPS coordinate
    private double longitude;
    private double latitude;

    // DateTime
    private Date date;

    // [vince] TODO: How to handle photos?


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
