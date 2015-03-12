package experties.com.handytask.helpers;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

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
}
