package experties.com.handytask.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import experties.com.handytask.R;
import experties.com.handytask.activities.LoginActivity;
import experties.com.handytask.activities.TaskCreatedActivity;
import experties.com.handytask.helpers.FragmentHelpers;

/**
 * Created by hetashah on 3/7/15.
 */
public class LoginFragment extends Fragment {
    private String phoneNumber;

    private EditText edTxtPhone;
    private Button btnLogin;
    private PhoneNumberUtil phoneUtil;

    private ProgressBar pbLogin;

    public interface TabSwitchInterface {
        void onSwitchToSignUpTab();
    }

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
        phoneUtil = PhoneNumberUtil.getInstance();
        pbLogin = (ProgressBar) v.findViewById(R.id.pbLogin);
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
                    edTxtPhone.setText(FragmentHelpers.updateNationalNumber(s.toString()));
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
                        phoneNumber = new String(phoneNumber.replaceAll("(^[1?])|([^\\d.])", ""));
                        PhoneNumber phNumberProto = null;
                        long notFormatted = 0;
                        try {
                            phNumberProto = phoneUtil.parse(phoneNumber, "US");
                            notFormatted = phNumberProto.getNationalNumber();
                        } catch (NumberParseException e) {
                            System.err.println("NumberParseException was thrown: " + e.toString());
                        }

                        boolean isValid = phoneUtil.isPossibleNumber(phNumberProto);

                        if (isValid) {
                            pbLogin.setVisibility(ProgressBar.VISIBLE);
                            String username = String.valueOf(notFormatted);
                            ParseUser.logInInBackground(username, "password", new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    pbLogin.setVisibility(ProgressBar.GONE);
                                    if (user != null) {
                                        Intent taskActivity = new Intent(getActivity(), TaskCreatedActivity.class);
                                        startActivity(taskActivity);
                                    } else {
                                        final TabSwitchInterface switchActivity = (TabSwitchInterface) getActivity();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Invalid Login")
                                                .setMessage("We are not able to find you. Do you want to Sign up?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        switchActivity.onSwitchToSignUpTab();
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Invalid Login")
                                    .setMessage("Phone Number is not valid. Please enter valid one.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
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
}
