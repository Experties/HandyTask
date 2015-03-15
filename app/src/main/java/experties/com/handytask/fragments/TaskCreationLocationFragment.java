package experties.com.handytask.fragments;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import experties.com.handytask.R;
import experties.com.handytask.adapters.AddressArrayAdapter;
import experties.com.handytask.helpers.AddressFinderHelper;
import experties.com.handytask.models.AddressData;
import experties.com.handytask.models.TaskItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskCreationLocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private ListView lvAddress;
    private MapView mapView;

    private GoogleApiClient client;
    private AddressFinderHelper helper;
    private AddressArrayAdapter adapter;

    private double lat;
    private double lng;

    private List<AddressData> items;
    private TaskItem item;

    public TaskCreationLocationFragment() {
        // Required empty public constructor
    }

    public static TaskCreationLocationFragment newInstance(TaskItem item) {
        TaskCreationLocationFragment frag = new TaskCreationLocationFragment();
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_task_creation_location, container, false);
        if(getArguments() != null) {
            item = (TaskItem) getArguments().getParcelable("item");
        } else {
            item = new TaskItem();
        }
        items = new ArrayList<AddressData>();
        adapter = new AddressArrayAdapter(getActivity(), items);
        setupView(v);
        return v;
    }

    private void setupView(View v) {
        lvAddress = (ListView) v.findViewById(R.id.lvAddress);
        lvAddress.setAdapter(adapter);
        mapView = (MapView) v.findViewById(R.id.addressMap);
        helper = new AddressFinderHelper(items, adapter);
        ParseUser currentUser = ParseUser.getCurrentUser();
        String fullHomeAddress = AddressFinderHelper.getFullAddress(currentUser);
        if(fullHomeAddress != null) {
            helper.getHomeAddress(fullHomeAddress);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
            helper.getCurrentLocationAddress(lat, lng);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }
}
