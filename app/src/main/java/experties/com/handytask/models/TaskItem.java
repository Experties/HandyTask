package experties.com.handytask.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by vincetulit on 3/8/15.
 */
public class TaskItem implements Parcelable {

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
    private Date date = new Date();

    // Unique ID
    private long id;

    private byte[] selectedImage1;
    private byte[] selectedImage2;
    private byte[] selectedImage3;


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

    public byte[] getSelectedImage1() {
        return selectedImage1;
    }

    public void setSelectedImage1(byte[] selectedImage1) {
        this.selectedImage1 = selectedImage1;
    }

    public byte[] getSelectedImage2() {
        return selectedImage2;
    }

    public void setSelectedImage2(byte[] selectedImage2) {
        this.selectedImage2 = selectedImage2;
    }

    public byte[] getSelectedImage3() {
        return selectedImage3;
    }

    public void setSelectedImage3(byte[] selectedImage3) {
        this.selectedImage3 = selectedImage3;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(this.id),
                this.type,
                this.briefDescription,
                this.detailedDescription,
                this.address1,
                this.address2,
                this.city,
                this.zipCode,
                this.state,
                String.valueOf(this.longitude),
                String.valueOf(this.latitude)
        });
        if(selectedImage1 != null) {
            dest.writeInt(selectedImage1.length);
            dest.writeByteArray(selectedImage1);
        } else {
            dest.writeInt(0);
        }

        if(selectedImage2 != null) {
            dest.writeInt(selectedImage2.length);
            dest.writeByteArray(selectedImage2);
        } else {
            dest.writeInt(0);
        }

        if(selectedImage3 != null) {
            dest.writeInt(selectedImage3.length);
            dest.writeByteArray(selectedImage3);
        } else {
            dest.writeInt(0);
        }
    }
    public TaskItem() {}
    // Parcelling part
    public TaskItem(Parcel in){
        String[] data = new String[11];

        in.readStringArray(data);
        this.id = Long.parseLong(data[0]);
        this.type = data[1];
        this.briefDescription = data[2];
        this.detailedDescription = data[3];
        this.address1 = data[4];
        this.address2 = data[5];
        this.city = data[6];
        this.zipCode = data[7];
        this.state = data[8];
        this.longitude = Double.parseDouble(data[9]);
        this.latitude = Double.parseDouble(data[10]);
        int length = in.readInt();
        if(length > 0) {
            this.selectedImage1 = new byte[length];
            in.readByteArray(this.selectedImage1);
        }
        length = in.readInt();
        if(length > 0) {
            this.selectedImage2 = new byte[length];
            in.readByteArray(this.selectedImage2);
        }
        length = in.readInt();
        if(length > 0) {
            this.selectedImage3 = new byte[length];
            in.readByteArray(this.selectedImage3);
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TaskItem createFromParcel(Parcel in) {
            return new TaskItem(in);
        }

        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };

    public void setAddress(String address) {
        if(address != null && !"".equals(address)) {
            String[] adresses = address.split(",");
            if(adresses != null && adresses.length > 0) {
                if(adresses.length == 4) {
                    this.address1 = adresses[0];
                    this.city = adresses[1];
                    String[] state = adresses[2].split(" ");
                    if(state != null && state.length > 1) {
                        if(!"".equals(state[0])) {
                            this.state = state[0];
                            this.zipCode = state[1];
                        } else {
                            this.state = state[1];
                            if(state.length > 2) {
                                this.zipCode = state[2];
                            }
                        }
                    }
                } else if(adresses.length == 3) {
                    this.address1 = adresses[0];
                    this.city = adresses[1];
                    String[] state = adresses[2].split(" ");
                    if(state != null && state.length > 1) {
                        this.state = state[0];
                        this.zipCode = state[1];
                    }
                } else if(adresses.length == 2) {
                    this.address1 = adresses[0];
                    this.city = adresses[1];
                } else {
                    this.address1 = adresses[0];
                }
            }
        }
    }
}
