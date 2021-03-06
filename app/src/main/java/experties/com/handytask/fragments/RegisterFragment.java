package experties.com.handytask.fragments;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import experties.com.handytask.R;
import experties.com.handytask.activities.ShowTasksActivity;
import experties.com.handytask.helpers.FragmentHelpers;

/**
 * Created by hetashah on 3/7/15.
 */
public class RegisterFragment extends Fragment implements UploadImageFragment.UploadDialogListener{
    private boolean isMandatoryFilled;

    private String phoneNumber;

    private ParseUser userData;
    private PhoneNumberUtil phoneUtil;
    private UploadImageFragment uploadDialog;

    private ImageView imgVwProfile;
    private Spinner sprState;
    private ProgressBar pbSignUP;
    private ScrollView scrVwSignUp;

    private EditText edVwPhoneNo;
    private EditText edTxtFirstName;
    private EditText edTxtLastName;
    private EditText edTxtAddress1;
    private EditText edTxtAddress2;
    private EditText edTxtCity;
    private EditText edTxtZipCode;

    private Button cancelBtn;
    private Button signUpBtn;
    private Button uploadBtn;
    private byte[] selectedImage;

    public static RegisterFragment newInstance(String phoneNumber){
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString("phoneNumber", phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_fragment, parent, false);
        getActivity().setTheme(R.style.AppTheme);
        setupView(v);
        isMandatoryFilled = checkEntries();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(phoneNumber != null) {
            edTxtFirstName.setFocusableInTouchMode(true);
            edTxtFirstName.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edTxtFirstName, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void setupView(View v) {
        final RegisterFragment instance = this;
        //myButton.getBackground().setAlpha(0.5f);
        uploadDialog = new UploadImageFragment();
        userData = new ParseUser();

        pbSignUP = (ProgressBar) v.findViewById(R.id.pbSignUP);
        scrVwSignUp = (ScrollView) v.findViewById(R.id.scrVwSignUp);
        sprState = (Spinner) v.findViewById(R.id.sprState);
        imgVwProfile = (ImageView) v.findViewById(R.id.imgVwProfile);
        edTxtFirstName = (EditText)v.findViewById(R.id.edTxtFirstName);
        edTxtLastName = (EditText)v.findViewById(R.id.edTxtLastName);
        edTxtAddress1 = (EditText)v.findViewById(R.id.edTxtAddress1);
        edTxtAddress2 = (EditText)v.findViewById(R.id.edTxtAddress2);
        edTxtCity = (EditText)v.findViewById(R.id.edTxtCity);
        edTxtZipCode = (EditText)v.findViewById(R.id.edTxtZipCode);
        edVwPhoneNo = (EditText)v.findViewById(R.id.edVwPhoneNo);
        cancelBtn = (Button)v.findViewById(R.id.cancelBtn);
        signUpBtn = (Button)v.findViewById(R.id.signUpBtn);
        uploadBtn = (Button) v.findViewById(R.id.uploadBtn);

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(
                v.getContext(), R.array.state_arrays, R.layout.spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprState.setAdapter(stateAdapter);
        //state.setSelection(spImageSizeAdapter.getPosition(settings.getImgSize()));

        edTxtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isMandatoryFilled = checkEntries();
            }
        });

        edTxtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isMandatoryFilled = checkEntries();
            }
        });

        if(phoneNumber != null) {
            edVwPhoneNo.setText(phoneNumber);
            edVwPhoneNo.setSelection(phoneNumber.length());
        }
        edVwPhoneNo.addTextChangedListener(new TextWatcher() {
            boolean isInAfterTextChanged = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isInAfterTextChanged) {
                    isInAfterTextChanged = true;
                    edVwPhoneNo.setText(FragmentHelpers.updateNationalNumber(s.toString()));
                    edVwPhoneNo.setSelection(edVwPhoneNo.getText().length());
                    isInAfterTextChanged = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isMandatoryFilled = checkEntries();
            }
        });

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

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                uploadDialog.setTargetFragment(RegisterFragment.this, 1337);
                uploadDialog.show(fm, "fragment_settings_dialog");
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNumber = getArguments().getString("phoneNumber");
        phoneUtil = PhoneNumberUtil.getInstance();
    }

    @Override
    public void onSelectImageDialog(byte[] byteArray, int btnId) {
        if(byteArray != null) {
            selectedImage = byteArray;
            uploadDialog.dismiss();
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgVwProfile.setImageBitmap(FragmentHelpers.getResizedBitmap(bmp, 300, 300));
        }
    }

    private boolean checkEntries() {

        String phoneNumber = edVwPhoneNo.getText().toString();
        //phoneNumber = new String(phoneNumber.replaceAll("(^[1?])|([^\\d.])", ""));
        Phonenumber.PhoneNumber phNumberProto = null;
        //long notFormatted = 0;
        try {
            phNumberProto = phoneUtil.parse(phoneNumber, "US");
            //notFormatted = phNumberProto.getNationalNumber();
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        if(phNumberProto != null) {
            boolean isValid = phoneUtil.isPossibleNumber(phNumberProto);
            if (isValid) {
                String firstName = edTxtFirstName.getText().toString();
                if (firstName != null && !"".equals(firstName)) {
                    String lastName = edTxtLastName.getText().toString();
                    if (lastName != null && !"".equals(lastName)) {
                        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(signUpBtn, "alpha", 1.0f);
                        fadeAnim.start();

                        return true;
                    }
                }
            }
        }

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(signUpBtn, "alpha", 0.5f);
        fadeAnim.start();

        return false;
    }

    private long getPhoneNumber() {
        String phoneNumber = edVwPhoneNo.getText().toString();
        Phonenumber.PhoneNumber phNumberProto = null;
        long notFormatted = 0;
        try {
            phNumberProto = phoneUtil.parse(phoneNumber, "US");
            notFormatted = phNumberProto.getNationalNumber();
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return notFormatted;
    }

    private void signUpUser() {
        if(isMandatoryFilled) {
            scrVwSignUp.setVisibility(ScrollView.GONE);
            pbSignUP.setVisibility(ProgressBar.VISIBLE);
            long phone = getPhoneNumber();
            userData = new ParseUser();
            userData.setUsername(String.valueOf(phone));
            userData.setPassword("password");

// other fields can be set just like with ParseObject
            userData.put("Mobile", phone);
            userData.put("FirstName", edTxtFirstName.getText().toString());
            userData.put("LastName", edTxtLastName.getText().toString());
            String zipCode = edTxtZipCode.getText().toString();
            try {
                userData.put("ZipCode", Integer.parseInt(zipCode));
                userData.put("Address1", edTxtAddress1.getText().toString());
                userData.put("Address2", edTxtAddress2.getText().toString());
                userData.put("City", edTxtCity.getText().toString());
            } catch(Exception e) {}

            if(selectedImage != null) {
                final ParseFile profileImg = new ParseFile("profileImg.jpeg", selectedImage);
                profileImg.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            userData.put("ProfilePhoto",profileImg);
                            saveUserOnServer();
                        } else {
                            scrVwSignUp.setVisibility(ScrollView.VISIBLE);
                            pbSignUP.setVisibility(ProgressBar.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Sign up unsuccessful")
                                    .setMessage("We are not able to complete your sign up. Do you want to try again?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            signUpUser();
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
                saveUserOnServer();
            }
        }
    }

    private void saveUserOnServer() {
        userData.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    getActivity().finish();
                    Intent taskActivity = new Intent(getActivity(), ShowTasksActivity.class);
                    startActivity(taskActivity);
                    getActivity().overridePendingTransition(R.anim.slide_up, R.anim.left_out);
                    scrVwSignUp.setVisibility(ScrollView.GONE);
                    pbSignUP.setVisibility(ProgressBar.VISIBLE);
                } else {
                    scrVwSignUp.setVisibility(ScrollView.VISIBLE);
                    pbSignUP.setVisibility(ProgressBar.GONE);

                    if(e.getCode() == ParseException.USERNAME_TAKEN) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Sign up unsuccessful")
                                .setMessage("Phone number is already taken.")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Sign up unsuccessful")
                                .setMessage("We are not able to complete your sign up. Do you want to try again?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        signUpUser();
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
            }
        });
    }
}
