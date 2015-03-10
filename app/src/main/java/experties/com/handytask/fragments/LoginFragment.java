package experties.com.handytask.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import experties.com.handytask.R;

/**
 * Created by hetashah on 3/7/15.
 */
public class LoginFragment extends Fragment {
    private String phoneNumber;

    private EditText edTxtPhone;
    private Button btnLogin;
    private PhoneNumberUtil phoneUtil;

    public static LoginFragment newInstance(String phoneNumber){
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString("phoneNumber", phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_fragment, parent, false);
        final LoginFragment instance = this;
        phoneUtil = PhoneNumberUtil.getInstance();
        edTxtPhone = (EditText) v.findViewById(R.id.edTxtPhone);
        if(phoneNumber != null) {
            edTxtPhone.setText(phoneNumber);
            edTxtPhone.setSelection(phoneNumber.length());
        }

        edTxtPhone.addTextChangedListener(new TextWatcher() {
            boolean isInAfterTextChanged = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isInAfterTextChanged) {
                    isInAfterTextChanged = true;
                    edTxtPhone.setText(instance.updateNationalNumber(s.toString()));
                    edTxtPhone.setSelection(edTxtPhone  .getText().length());
                    isInAfterTextChanged = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btnLogin:

                        String phoneNumber = edTxtPhone.getText().toString();

                        PhoneNumber phNumberProto = null;
                        long notFormatted = 0;
                        try {
                            phNumberProto = phoneUtil.parse(phoneNumber, "US");
                            notFormatted = phNumberProto.getNationalNumber();
                        } catch (NumberParseException e) {
                            System.err.println("NumberParseException was thrown: " + e.toString());
                        }

                        boolean isValid = phoneUtil.isValidNumber(phNumberProto);

                        if (isValid) {
                            String username = String.valueOf(notFormatted);
                            final View viewInstance = v;
                            ParseUser.logInInBackground(username, "password", new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null) {
                                        Toast.makeText(viewInstance.getContext(),"User is successfully login",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(viewInstance.getContext(),"Needs to go to sign up screen, User not found.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(
                                    v.getContext(),"Phone number is not valid: " + phoneNumber,
                                    Toast.LENGTH_SHORT).show();

                        }

                        break;

                }
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNumber = getArguments().getString("phoneNumber");
    }

    public String updateNationalNumber(String s){
        AsYouTypeFormatter aytf = phoneUtil.getAsYouTypeFormatter("US");
        String fNationalNumber = null;

        if(s.length() > 0){
            String digitString = new String(s.replaceAll("(^[1?])|([^\\d.])", ""));
            if(digitString != null){
                for(int i = 0; i < digitString.length(); i++){
                    fNationalNumber = aytf.inputDigit(digitString.charAt(i));
                }
            }

            aytf.clear();
            try {
                PhoneNumber phNumberProto = phoneUtil.parse(fNationalNumber, "US");
                fNationalNumber = phoneUtil.format(phNumberProto, PhoneNumberFormat.NATIONAL);
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
            }
        }
        //Return the formatted phone number
        return fNationalNumber;
    }
}
