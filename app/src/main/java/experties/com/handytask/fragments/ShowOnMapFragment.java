package experties.com.handytask.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bolts.Task;
import experties.com.handytask.R;
import experties.com.handytask.activities.TaskItemsListListener;
import experties.com.handytask.models.TaskItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowOnMapFragment extends Fragment {

    // Google Map/GPS related
    private MapView mapView;
    private GoogleMap map;

    // To populate the Brief Item at the bottom of the fragment
    private ImageView ivMainTaskPhoto;
    private TextView tvBriefDescription;
    private TextView tvRelativeTime;
    private TextView tvLocation;
    private TextView tvRelativeDistance;


    public ShowOnMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_on_map, container, false);

        ivMainTaskPhoto = (ImageView) v.findViewById(R.id.ivMainTaskPhoto);
        tvBriefDescription = (TextView) v.findViewById(R.id.tvBriefDescription);
        tvRelativeTime = (TextView) v.findViewById(R.id.tvRelativeTime);
        tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        tvRelativeDistance = (TextView) v.findViewById(R.id.tvRelativeDistance);
        // [vince] TODO: Initialize Brief Item form to closest point on map. Or just make sure you initialize.

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                loadMap(googleMap);
            }
        });

        return v;
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            map.setMyLocationEnabled(true);
            populateMapwithMarkers();
        } else {
            Toast.makeText(getActivity(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateBriefPreview(TaskItem taskItem) {
        // [vince] TODO: populate Brief Preview onStart + onClick

    }

    private void populateMapwithMarkers() {
        int i;
        TaskItemsListListener listener = (TaskItemsListListener) getActivity();
        ArrayList<TaskItem> taskItems = listener.getList();
        if (!taskItems.isEmpty())
            for(i=0;i<taskItems.size();i++) {
                TaskItem taskItem = taskItems.get(i);
                map.addMarker(new MarkerOptions().position(new LatLng(taskItem.getLatitude(), taskItem.getLongitude())));
            }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }


}
