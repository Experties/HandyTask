package experties.com.handytask.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by hetashah on 3/7/15.
 */
@ParseClassName("Task")
public class ParseTask extends ParseObject {

    private double relativeDistance;

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

    public String getState() {
        return getString("State");
    }

    public void setState(String value) {
        put("State", value);
    }

    public Number getZipCode() {
        return getNumber("ZipCode");
    }

    public void setZipCode(Number value) { put("ZipCode", value); }

    public Double getLatitude() {
        return getDouble("Latitude");
    }

    public void setLatitude(Double value) {
        put("Latitude", value);
    }

    public Double getLongitude() {
        return getDouble("Longitude");
    }

    public void setLongitude(Double value) {
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

    public ParseUser getOwner() {
        return getParseUser("Owner");
    }

    public void setOwner(ParseUser value) {
        put("Owner", value);
    }

    public ParseUser getResponder() {
        return getParseUser("Responder");
    }

    public void setResponder(ParseUser value) {
        put("Responder", value);
    }

    public String getCurrentState() {
        return getString("Responder");
    }

    public void setCurrentState(String value) {
        put("CurrentState", value);
    }

    public double getRelativeDistance() { return relativeDistance; }

    public void setRelativeDistance(double relativeDistance) { this.relativeDistance = relativeDistance; }

    public static Comparator<ParseTask> relDistComparator = new Comparator<ParseTask>() {
        @Override
        public int compare(ParseTask lhs, ParseTask rhs) {
            return (int)(lhs.getRelativeDistance()*10 - rhs.getRelativeDistance()*10);
        }
    };
}
