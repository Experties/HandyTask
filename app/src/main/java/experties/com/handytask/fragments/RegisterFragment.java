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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import experties.com.handytask.R;

/**
 * Created by hetashah on 3/7/15.
 */
public class RegisterFragment extends Fragment {
    private Spinner state;
    private String phoneNumber;
    private EditText edVwPhoneNo;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_fragment, parent, false);
        setupView(v);
        return v;
    }

    private void setupView(View v) {
        state = (Spinner) v.findViewById(R.id.sprState);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(
                v.getContext(), R.array.state_arrays, R.layout.spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(stateAdapter);
        //state.setSelection(spImageSizeAdapter.getPosition(settings.getImgSize()));

        edVwPhoneNo = (EditText)v.findViewById(R.id.edVwPhoneNo);
        edVwPhoneNo.setText(phoneNumber);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager tMgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getLine1Number();
        int apiLevel = android.os.Build.VERSION.SDK_INT;
        if(apiLevel >= 21) {
            phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, "US");
        } else {
            phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber);
        }
    }
}
