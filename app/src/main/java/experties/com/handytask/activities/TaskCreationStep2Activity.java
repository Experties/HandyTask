package experties.com.handytask.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseUser;

import experties.com.handytask.R;
import experties.com.handytask.fragments.DetailedTaskViewFragment;
import experties.com.handytask.fragments.TaskCreationFragment;
import experties.com.handytask.fragments.TaskCreationLocationFragment;
import experties.com.handytask.models.TaskItem;

public class TaskCreationStep2Activity extends ActionBarActivity  {
    private String prevQuery;
    private String currentQuery = "";

    private TaskItem item;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            this.finish();
            Intent taskActivity = new Intent(TaskCreationStep2Activity.this, LoginActivity.class);
            startActivity(taskActivity);
        } else {
            setTheme(R.style.AppTheme);
            setContentView(R.layout.activity_task_creation_step2);
            item = (TaskItem) getIntent().getExtras().getParcelable("item");
            final TaskCreationStep2Activity context = this;
            Typeface fontJamesFajardo = Typeface.createFromAsset(this.getAssets(), "fonts/JamesFajardo.ttf");

            Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrTaskCreation2);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            toolbar.setNavigationIcon(R.drawable.ic_back_white);
            mTitle = (TextView) toolbar.findViewById(R.id.task_toolbar_title2);

            toolbar.setLogo(R.mipmap.ic_logo);
            mTitle.setTypeface(fontJamesFajardo);
            mTitle.setText(getResources().getString(R.string.title));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.finish();
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                }
            });
            if(item == null) {
                item = new TaskItem();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.creation_fragment_step2, TaskCreationLocationFragment.newInstance(item));
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_creation_step2, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("You can search places here....");
        /*TextView textView = (TextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        textView.setTextColor(Color.WHITE);*/

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                currentQuery = query;
                prevQuery = query;
                //builder.getImages(query);
                if(currentQuery != null && !"".equals(currentQuery)) {
                    TaskCreationLocationFragment fm = (TaskCreationLocationFragment) getSupportFragmentManager().findFragmentById(R.id.creation_fragment_step2);
                    fm.populateLocation(currentQuery);
                }
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new SearchView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (prevQuery != null) {
                    EditText searchText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                    searchText.setText(prevQuery);
                    searchText.setSelection(searchText.getText().length());
                }
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
        if (id == R.id.muSignOut) {
            ParseUser.logOut();
            this.finish();
            Intent taskActivity = new Intent(TaskCreationStep2Activity.this, LoginActivity.class);
            startActivity(taskActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
