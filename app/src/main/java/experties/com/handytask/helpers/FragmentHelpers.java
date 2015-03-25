package experties.com.handytask.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.format.DateUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.ParseUser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import experties.com.handytask.R;

/**
 * Created by hetashah on 3/11/15.
 */
public class FragmentHelpers {

    public static String updateNationalNumber(String s){

        String fNationalNumber = null;
        if(s != null && s.length() > 0){
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            AsYouTypeFormatter aytf = phoneUtil.getAsYouTypeFormatter("US");
            String digitString = new String(s.replaceAll("(^[1?])|([^\\d.])", ""));
            if(digitString != null){
                for(int i = 0; i < digitString.length(); i++){
                    fNationalNumber = aytf.inputDigit(digitString.charAt(i));
                }
            }

            aytf.clear();
            try {
                Phonenumber.PhoneNumber phNumberProto = phoneUtil.parse(fNationalNumber, "US");
                fNationalNumber = phoneUtil.format(phNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
            }
        }
        //Return the formatted phone number
        return fNationalNumber;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) throws IOException {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = new ExifInterface(photoFilePath);
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    private static Double EARTH_RADIUS = 6371.00; // Radius in Kilometers default
    private static Double KM_TO_MI = 0.621371192;

    public static Double getDistance(LatLng p1, LatLng p2){
        Double Radius = EARTH_RADIUS; //6371.00;
        Double dLat = toRadians(p1.latitude-p2.latitude);
        Double dLon = toRadians(p1.longitude-p2.longitude);
        Double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(toRadians(p1.latitude)) *   Math.cos(toRadians(p2.latitude)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        Double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c * KM_TO_MI;
    }

    public static Double toRadians(Double degree){
        // Value degree * Pi/180
        Double res = degree * 3.1415926 / 180;
        return res;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTime(Date date) {
        String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(date.toString()).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getDateDifferenceForDisplay(Date inputdate) {
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();

        now.setTime(new Date());
        then.setTime(inputdate);

        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = then.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = nowMs - thenMs;

        // Calculate difference in seconds
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffSeconds < 60) {
            return diffSeconds + "s";
        }
        if (diffMinutes < 60) {
            return diffMinutes + "m";

        } else if (diffHours < 24) {
            return diffHours + "h";

        } else if (diffDays < 7) {
            return diffDays + "d";

        } else {

            SimpleDateFormat todate = new SimpleDateFormat("MMM dd",
                    Locale.ENGLISH);

            return todate.format(inputdate);
        }
    }

    public static String getUserName(ParseUser responder) {
        if(responder != null) {
            StringBuilder name = new StringBuilder(responder.getString("FirstName"));
            name.append(" ").append(responder.getString("LastName").substring(0,1).toUpperCase()).append(".");
            return name.toString();
        }
        return null;
    }
}
