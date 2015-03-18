package experties.com.handytask.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import experties.com.handytask.R;
//import experties.com.handytask.adapters.ShowTasksFragmentPagerAdapter;
import experties.com.handytask.fragments.ShowOnListFragment;
import experties.com.handytask.fragments.ShowOnMapFragment;
import experties.com.handytask.helpers.FragmentHelpers;
import experties.com.handytask.models.ParseTask;
import experties.com.handytask.models.TaskItem;

public class ShowTasksActivity extends ActionBarActivity implements ParseTaskListListener,
         GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private ArrayList<ParseTask> parseTasks;
    private TextView mTitle;

    ShowOnMapFragment showOnMapFragment;
    ShowOnListFragment showOnListFragment;

    private GoogleApiClient mGoogleApiClient;
    private LatLng currentLatLng;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            this.finish();
            Intent taskActivity = new Intent(ShowTasksActivity.this, LoginActivity.class);
            startActivity(taskActivity);
        } else {
            setContentView(R.layout.activity_show_tasks);
            Typeface fontJamesFajardo = Typeface.createFromAsset(this.getAssets(), "fonts/JamesFajardo.ttf");

            Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrShowTask);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mTitle = (TextView) toolbar.findViewById(R.id.show_task_toolbar_title);

            //toolbar.setLogo(R.drawable.ic_tweets);
            mTitle.setTypeface(fontJamesFajardo);
            mTitle.setText(getResources().getString(R.string.title));

            // parseTasks is the same for both fragments, thus they reside on activity
            parseTasks = new ArrayList<>();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            mGoogleApiClient.connect();

            showOnMapFragment = new ShowOnMapFragment();
            showOnListFragment = new ShowOnListFragment();

            //PopulateTaskList with Parse Data
            populateTaskList();


            // Get the ViewPager and set it's PagerAdapter so that it can display items
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new ShowTasksFragmentPagerAdapter(getSupportFragmentManager()));

            // Give the PagerSlidingTabStrip the ViewPager
            PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            // Attach the view pager to the tab strip
            tabsStrip.setViewPager(viewPager);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_tasks, menu);

        MenuItem searchItem = menu.findItem(R.id.muSearchMap);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Geocoder geocoder = new Geocoder(getBaseContext());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(query, 5);
                    if (addresses!=null) {
                        LatLng pos = new LatLng(
                                addresses.get(0).getLatitude(),
                                addresses.get(0).getLongitude());
                        showOnMapFragment.focusMapOn(pos);
                    }
                } catch (IOException e) {

                }


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.muSignOut:
                ParseUser.logOut();
                this.finish();
                Intent taskActivity = new Intent(ShowTasksActivity.this, LoginActivity.class);
                startActivity(taskActivity);
                return true;
            case R.id.muAddTask:
                Intent addTaskActivity = new Intent(ShowTasksActivity.this, TaskCreationStep1Activity.class);
                startActivity(addTaskActivity);
                //overridePendingTransition(R.anim.slide_down, R.id.none);
                return true;
            case R.id.muSearchMap:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void populateTaskList() {
        ParseQuery query = ParseQuery.getQuery(ParseTask.class);
        query.findInBackground(new FindCallback<ParseTask>() {
            @Override
            public void done(List<ParseTask> parseTasksAll, com.parse.ParseException e) {
                parseTasks = (ArrayList) parseTasksAll;
                // calculate relativeDistance for All
                if (currentLatLng!=null) {
                    calculateRelativeDistance();
                    // sort by relativeDistance
                    Collections.sort(parseTasks, ParseTask.relDistComparator);
                    // update Fragments
                    showOnListFragment.updateTasksList();
                    showOnMapFragment.updateTasksList();
                }

            }

        });
    }

    public void populateListTaskList() {
        parseTasks.clear();
        ParseQuery query = ParseQuery.getQuery(ParseTask.class);
        query.findInBackground(new FindCallback<ParseTask>() {
            @Override
            public void done(List<ParseTask> parseTasksAll, com.parse.ParseException e) {
                parseTasks = (ArrayList) parseTasksAll;
                // calculate relativeDistance for All
                if (currentLatLng!=null) {
                    calculateRelativeDistance();
                    // sort by relativeDistance
                    Collections.sort(parseTasks, ParseTask.relDistComparator);
                    // update Fragments
                    showOnListFragment.updateTasksList();
                }

            }

        });
    }

    // Calculate Relative Distance for each ParseTask in the ArrayList
    private boolean calculateRelativeDistance() {
        if ((parseTasks!=null) && (currentLatLng!=null)) {
            for (ParseTask parseTask : parseTasks) {
                LatLng relPos = new LatLng(parseTask.getLatitude(), parseTask.getLongitude());
                double relDist = FragmentHelpers.getDistance(currentLatLng, relPos);
                parseTask.setRelativeDistance(relDist);
            }
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<ParseTask> getParseTaskList() {
        return parseTasks;
    }

    public LatLng getCurrentPosition() {
        return currentLatLng;
    }

    public class ShowTasksFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = {"MAP VIEW", "LIST VIEW"};

        public ShowTasksFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                //return new ShowOnMapFragment();
                return showOnMapFragment;
            } else if (position == 1) {
                //return new ShowOnListFragment();
                return showOnListFragment;
            }
            else {
                return null;
            }
        }

        // Return the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // How many fragments are to swipe between?
        @Override
        public int getCount() {
            return tabTitles.length;
        }

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mCurrentLocation != null) {
            currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            // If Relative Distances were updated successfully update Fragments
            if (calculateRelativeDistance()) {
                // sort by relativeDistance
                Collections.sort(parseTasks, ParseTask.relDistComparator);
                // update Fragments
                showOnListFragment.updateTasksList();
                showOnMapFragment.updateTasksList();
            }
        }
        // [vince] TODO: continuous update of location -> startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // [vince] TODO: continuous update of location -> startLocationUpdates();
    /*protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }*/
}

