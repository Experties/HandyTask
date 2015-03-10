package experties.com.handytask.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
    private Button uploadBtn;

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

        uploadBtn = (Button) v.findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                UploadImageFragment uploadDialog = new UploadImageFragment();
                uploadDialog.show(fm, "fragment_settings_dialog");
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNumber = getArguments().getString("phoneNumber");
    }
}
