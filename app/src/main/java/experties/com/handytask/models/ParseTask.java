package experties.com.handytask.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by hetashah on 3/7/15.
 */
@ParseClassName("Task")
public class ParseTask extends ParseObject {

    public ParseTask() {
        super();
    }

    public String getType() {
        return getString("Type");
    }

    public void setType(String value) {
        put("Type", value);
    }

    public String getTitle() {
        return getString("Title");
    }

    public void setTitle(String value) {
        put("Title", value);
    }

    public String getDescription() {
        return getString("Description");
    }

    public void setDescription(String value) {
        put("Description", value);
    }

    public String getAddress1() {
        return getString("Address1");
    }

    public void setAddress1(String value) {
        put("Address1", value);
    }

    public String getAddress2() {
        return getString("Address2");
    }

    public void setAddress2(String value) {
        put("Address2", value);
    }

    public String getCity() {
        return getString("City");
    }

    public void setCity(String value) {
        put("City", value);
    }

    public String getZipCode() {
        return getString("Zipcode");
    }

    public void setZipCode(String value) {
        put("Zipcode", value);
    }

    public Long getLatitude() {
        return getLong("Latitude");
    }

    public void setLatitude(Long value) {
        put("Latitude", value);
    }

    public Long getLongitude() {
        return getLong("Longitude");
    }

    public void setLongitude(Long value) {
        put("Longitude", value);
    }

    public Date getPostedDate() {
        return getDate("Posted");
    }

    public void setPostedDate(Date value) {
        put("Posted", value);
    }

    public ParseFile getPhoto1() {
        return getParseFile("Photo1");
    }

    public void setPhoto1(ParseFile value) {
        put("Photo1", value);
    }

    public ParseFile getPhoto2() {
        return getParseFile("Photo2");
    }

    public void setPhoto2(ParseFile value) {
        put("Photo2", value);
    }

    public ParseFile getPhoto3() {
        return getParseFile("Photo3");
    }

    public void setPhoto3(ParseFile value) {
        put("Photo3", value);
    }
}
