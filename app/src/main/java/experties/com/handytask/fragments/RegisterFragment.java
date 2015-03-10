package experties.com.handytask.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import experties.com.handytask.R;

/**
 * Created by hetashah on 3/7/15.
 */
public class RegisterFragment extends Fragment {
    private String phoneNumber;

    private ImageView imgVwProfile;

    private Spinner sprState;

    private EditText edVwPhoneNo;
    private EditText edTxtFirstName;
    private EditText edTxtLastName;
    private EditText edTxtAddress1;
    private EditText edTxtAddress2;
    private EditText edTxtCity;
    private EditText edTxtZipCode;

    private Button cancelBtn;
    private Button signUpBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_fragment, parent, false);
        setupView(v);
        return v;
    }

    private void setupView(View v) {
        //myButton.getBackground().setAlpha(0.5f);

        sprState = (Spinner) v.findViewById(R.id.sprState);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(
                v.getContext(), R.array.state_arrays, R.layout.spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprState.setAdapter(stateAdapter);
        //state.setSelection(spImageSizeAdapter.getPosition(settings.getImgSize()));
        imgVwProfile = (ImageView) v.findViewById(R.id.imgVwProfile);
        edTxtFirstName = (EditText)v.findViewById(R.id.edTxtFirstName);
        edTxtLastName = (EditText)v.findViewById(R.id.edTxtLastName);
        edTxtAddress1 = (EditText)v.findViewById(R.id.edTxtAddress1);
        edTxtAddress2 = (EditText)v.findViewById(R.id.edTxtAddress2);
        edTxtCity = (EditText)v.findViewById(R.id.edTxtCity);
        edTxtZipCode = (EditText)v.findViewById(R.id.edTxtZipCode);
        edVwPhoneNo = (EditText)v.findViewById(R.id.edVwPhoneNo);
        if(phoneNumber != null) {
            edVwPhoneNo.setText(phoneNumber);
            edVwPhoneNo.setSelection(phoneNumber.length());
            edTxtFirstName.setFocusableInTouchMode(true);
            edTxtFirstName.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edTxtFirstName, InputMethodManager.SHOW_IMPLICIT);
        }



        cancelBtn = (Button)v.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edTxtFirstName.setText("");
                edTxtLastName.setText("");
                edTxtAddress1.setText("");
                edTxtAddress2.setText("");
                edTxtCity.setText("");
                edTxtZipCode.setText("");
                imgVwProfile.setImageResource(R.drawable.ic_profilee);
                Toast.makeText(v.getContext(),"Entries got cleared",Toast.LENGTH_LONG).show();
            }
        });
        signUpBtn = (Button)v.findViewById(R.id.signUpBtn);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getLine1Number();
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            PhoneNumber usPhoneNumber = phoneUtil.parse(phoneNumber, "US");
            phoneNumber = phoneUtil.format(usPhoneNumber, PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {}
    }
}
