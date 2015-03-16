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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import bolts.Task;
import experties.com.handytask.R;
import experties.com.handytask.activities.ParseTaskListListener;
import experties.com.handytask.activities.ShowTasksActivity;
import experties.com.handytask.helpers.FragmentHelpers;
import experties.com.handytask.models.ParseTask;
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
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowOnMapFragment extends Fragment
        implements GoogleMap.OnMarkerClickListener {

    // Google Map/GPS related
    private MapView mapView;
    private GoogleMap map;

    // To populate the Brief Item at the bottom of the fragment
    private ParseImageView ivMainTaskPhoto;
    private TextView tvBriefDescription;
    private TextView tvRelativeTime;
    private TextView tvLocation;
    private TextView tvRelativeDistance;
    private RelativeLayout rlBriefView;

    //Each fragment holds its own copy
    // [vince] TODO: need a better way for this
    ArrayList<ParseTask> parseTasks;

    ParseTask selectedParseTask;

    ParseTaskListListener listener;

    public ShowOnMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_on_map, container, false);

        ivMainTaskPhoto = (ParseImageView) v.findViewById(R.id.ivMainTaskPhoto);
        tvBriefDescription = (TextView) v.findViewById(R.id.tvBriefDescription);
        tvRelativeTime = (TextView) v.findViewById(R.id.tvRelativeTime);
        tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        tvRelativeDistance = (TextView) v.findViewById(R.id.tvRelativeDistance);
        rlBriefView = (RelativeLayout) v.findViewById(R.id.taskItem);

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                loadMap(googleMap);
            }
        });

        setupOnClickListener();

        listener = (ParseTaskListListener) getActivity();

        return v;
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            map.setMyLocationEnabled(true);

            MapsInitializer.initialize(getActivity());

            populateMapWithMarkers();
            map.setOnMarkerClickListener(this);
        } else {
            Toast.makeText(getActivity(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateBriefPreview(ParseTask parseTask) {
        tvBriefDescription.setText(parseTask.getTitle());
        tvRelativeTime.setText(FragmentHelpers.getRelativeTime(parseTask.getCreatedAt()));
        tvLocation.setText(parseTask.getCity() + "," + parseTask.getState());

        LatLng p = new LatLng(parseTask.getLatitude(), parseTask.getLongitude());
        String relativeDistance =
                String.format("%.1f", parseTask.getRelativeDistance());
        tvRelativeDistance.setText(relativeDistance + " miles");

        ParseFile file = parseTask.getPhoto1();
        if (file!=null) {
            ivMainTaskPhoto.setParseFile(file);
            ivMainTaskPhoto.loadInBackground();
        } else {
            ivMainTaskPhoto.setImageResource(R.drawable.no_image_avail);
        }
    }

    private void populateMapWithMarkers() {
        int i;
        parseTasks = listener.getParseTaskList();

        if (map!=null) {
            map.clear();
            if (!parseTasks.isEmpty()) {
                for (i = 0; i < parseTasks.size(); i++) {
                    ParseTask parseTask = parseTasks.get(i);
                    map.addMarker(new MarkerOptions().title(String.valueOf(i)).
                            position(new LatLng(parseTask.getLatitude(), parseTask.getLongitude())).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.blu_square)));
                }
                selectedParseTask = parseTasks.get(0);
                if (listener.getCurrentPosition()!=null) {
                    map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(listener.getCurrentPosition(), 11));
                }

                populateBriefPreview(selectedParseTask);
            }
        }
    }

    public void updateTasksList() {
        populateMapWithMarkers();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedParseTask = parseTasks.get(Integer.parseInt(marker.getTitle()));
        populateBriefPreview(selectedParseTask);

        return true;
    }


    public void setupOnClickListener() {
        rlBriefView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailedView(selectedParseTask);
            }
        });
    }

    public void showDetailedView(ParseTask parseTask) {
        DetailedTaskViewFragment  frag = DetailedTaskViewFragment.newInstance(parseTask);
        frag.show(getFragmentManager(), "Nothing");
    }

    public void focusMapOn(LatLng pos) {
        if (pos!=null) {
            map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(pos, 11));
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
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
