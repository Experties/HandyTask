package experties.com.handytask.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
public class LoginFragment extends Fragment {
    private EditText edTxtPhone;
    private Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_fragment, parent, false);
        edTxtPhone = (EditText) v.findViewById(R.id.edTxtPhone);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btnLogin:

                        String phoneNumber = edTxtPhone.getText().toString();

                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
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
                            Toast.makeText(
                                    v.getContext(),"Phone number is valid: " + username,
                                    Toast.LENGTH_SHORT).show();
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
    }
}
