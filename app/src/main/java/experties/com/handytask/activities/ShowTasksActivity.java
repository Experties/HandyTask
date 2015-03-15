package experties.com.handytask.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseUser;

import java.util.Date;
import java.util.ArrayList;

import experties.com.handytask.R;
import experties.com.handytask.adapters.ShowTasksFragmentPagerAdapter;
import experties.com.handytask.models.TaskItem;

public class ShowTasksActivity extends ActionBarActivity implements TaskItemsListListener{
    private ArrayList<TaskItem> taskItems;
    private TextView mTitle;

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

            // taskItems is the same for both fragments, thus they reside on activity
            taskItems = new ArrayList<TaskItem>();

            // [vince] TODO: temporary solution
            populateDummyList();
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
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // [vince] TODO: temporary solution
    public void populateDummyList() {
        int i;
        Date d = new Date();
        for(i=0; i<10; i++) {
            TaskItem taskItem = new TaskItem();
            taskItem.setBriefDescription("Brief Description" + i);
            taskItem.setDate(d);
            taskItem.setCity("City Name" + i);
            taskItem.setState("State" + i);
            taskItems.add(taskItem);
            taskItem.setLatitude(37.734654 + i * 0.001756);
            taskItem.setLongitude(-122.425290 + i * 0.008787);
        }

    }

    public void populateTaskList() {

    }

    public ArrayList<TaskItem> getList() {
        return taskItems;
    }
}
