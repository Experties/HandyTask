package experties.com.handytask.fragments;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import experties.com.handytask.R;
import experties.com.handytask.activities.ShowTasksActivity;
import experties.com.handytask.activities.TaskCreationStep1Activity;
import experties.com.handytask.adapters.AddressArrayAdapter;
import experties.com.handytask.helpers.AddressFinderHelper;
import experties.com.handytask.models.AddressData;
import experties.com.handytask.models.ParseTask;
import experties.com.handytask.models.TaskItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskCreationLocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private ListView lvAddress;
    private ProgressBar pbSaveTask;
    private RelativeLayout saveTaskStep2;
    private Button postBtn;
    private Button backBtn;

    private GoogleApiClient client;
    private AddressFinderHelper helper;
    private AddressArrayAdapter adapter;

    private double lat;
    private double lng;

    private List<AddressData> items;
    private TaskItem item;

    private AddressData selectedAddress;

    private int selectedIndex = 0;

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
        pbSaveTask = (ProgressBar) v.findViewById(R.id.pbSaveTask);
        saveTaskStep2 = (RelativeLayout) v.findViewById(R.id.saveTaskStep2);
        lvAddress = (ListView) v.findViewById(R.id.lvAddress);
        lvAddress.setAdapter(adapter);
        helper = new AddressFinderHelper(items, adapter, selectedAddress);
        ParseUser currentUser = ParseUser.getCurrentUser();
        String fullHomeAddress = AddressFinderHelper.getFullAddress(currentUser);
        if(fullHomeAddress != null) {
            helper.getHomeAddress(fullHomeAddress);
        }

        lvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddressData prevSelected = items.get(selectedIndex);
                prevSelected.setSelected(false);
                AddressData currentSelected = items.get(position);
                currentSelected.setSelected(true);
                selectedIndex = position;
                selectedAddress = currentSelected;
                adapter.notifyDataSetChanged();
            }
        });

        postBtn = (Button) v.findViewById(R.id.postBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskStep2.setVisibility(View.GONE);
                pbSaveTask.setVisibility(View.VISIBLE);
                if(selectedAddress == null) {
                    selectedAddress = helper.getSelectedAddress();
                }
                if(selectedAddress != null) {
                    item.setAddress(selectedAddress.getAddress());
                    item.setLongitude(selectedAddress.getLongitude());
                    item.setLatitude(selectedAddress.getLatitude());
                }
                saveTask(item);
            }
        });
        backBtn = (Button) v.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void saveTask(TaskItem item) {
        int countImgUpload = 0;
        final ParseTask task = new ParseTask();
        task.setType(item.getType());
        task.setTitle(item.getBriefDescription());
        task.setDescription(item.getDetailedDescription());
        task.setLatitude(item.getLatitude());
        task.setLongitude(item.getLongitude());
        task.setOwner(ParseUser.getCurrentUser());
        task.setCurrentState("open");
        task.setPostedDate(new Date());
        String address1 = item.getAddress1();
        if(address1 != null) {
            task.setAddress1(item.getAddress1());
        }
        String address2 = item.getAddress2();
        if(address2 != null) {
            task.setAddress2(address2);
        }
        String city = item.getCity();
        if(city != null) {
            task.setCity(city);
        }

        String state = item.getState();
        if(state != null) {
            task.setState(state);
        }

        try {
            String zipCode = item.getZipCode();
            if (zipCode != null && !"".equals(zipCode)) {
                task.setZipCode(Integer.parseInt(item.getZipCode()));
            }
        } catch(Exception e) {}

        byte[] selectedImage1 = item.getSelectedImage1();
        byte[] selectedImage2 = item.getSelectedImage2();
        byte[] selectedImage3 = item.getSelectedImage3();
        if(selectedImage1 != null || selectedImage2 != null || selectedImage3 != null) {
            if(selectedImage1 != null && selectedImage2 != null && selectedImage3 != null ) {
                countImgUpload = 3;
            } else if((selectedImage1 != null && selectedImage2 != null) ||
                    (selectedImage1 != null && selectedImage3 != null) ||
                    (selectedImage2 != null && selectedImage3 != null)) {
                countImgUpload = 2;
            } else {
                countImgUpload = 1;
            }
            final int[] saveCount = {0};
            final int finalCountImgUpload = countImgUpload;
            if(selectedImage1 != null) {
                final ParseFile selectedImg1 = new ParseFile("profileImg.jpeg", selectedImage1);
                selectedImg1.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        saveCount[0]++;
                        task.setPhoto1(selectedImg1);
                        if(finalCountImgUpload == saveCount[0]++) {
                            task.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        pbSaveTask.setVisibility(View.GONE);
                                        saveTaskStep2.setVisibility(View.VISIBLE);
                                        getActivity().finish();
                                        Intent intent = new Intent(getActivity(),TaskCreationStep1Activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("EXIT", true);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                });
            }

            if(selectedImage2 != null) {
                final ParseFile selectedImg2 = new ParseFile("profileImg.jpeg", selectedImage2);
                selectedImg2.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            saveCount[0]++;
                            task.setPhoto2(selectedImg2);
                            if (finalCountImgUpload == saveCount[0]++) {
                                task.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            pbSaveTask.setVisibility(View.GONE);
                                            saveTaskStep2.setVisibility(View.VISIBLE);
                                            getActivity().finish();
                                            Intent intent = new Intent(getActivity(),TaskCreationStep1Activity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("EXIT", true);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }

            if(selectedImage3 != null) {
                final ParseFile selectedImg3 = new ParseFile("profileImg.jpeg", selectedImage3);
                selectedImg3.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            saveCount[0]++;
                            task.setPhoto3(selectedImg3);
                            if (finalCountImgUpload == saveCount[0]++) {
                                task.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            pbSaveTask.setVisibility(View.GONE);
                                            saveTaskStep2.setVisibility(View.VISIBLE);
                                            getActivity().finish();
                                            Intent intent = new Intent(getActivity(),TaskCreationStep1Activity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("EXIT", true);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        } else {
            task.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        pbSaveTask.setVisibility(View.GONE);
                        saveTaskStep2.setVisibility(View.VISIBLE);
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(),TaskCreationStep1Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    }
                }
            });
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

    public void populateLocation(String currentQuery) {
        if(items.size() > 1) {
            selectedIndex = 0;
            AddressData addr = items.get(0);
            addr.setSelected(true);
            items.clear();
            items.add(0, addr);
            adapter.notifyDataSetChanged();
        }
        helper.getQueryLocation(currentQuery);
    }

    public void showPreview() {
        /*DetailedTaskViewFragment frag = DetailedTaskViewFragment.newInstance(item);
        frag.show(getFragmentManager(), "fragment_preview_dialog");*/
    }
}
