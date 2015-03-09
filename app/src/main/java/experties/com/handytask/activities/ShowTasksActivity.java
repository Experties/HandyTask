package experties.com.handytask.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import java.util.Date;
import java.util.ArrayList;

import experties.com.handytask.R;
import experties.com.handytask.adapters.ShowTasksFragmentPagerAdapter;
import experties.com.handytask.models.TaskItem;

public class ShowTasksActivity extends ActionBarActivity implements TaskItemsListListener{
    private ArrayList<TaskItem> taskItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tasks);

        // taskItems is the same for both fragments, thus they reside on activity
        taskItems = new ArrayList<TaskItem>();

        // [vince] TODO: temporary solution
        populateDummyList();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ShowTasksFragmentPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

    public ArrayList<TaskItem> getList() {
        return taskItems;
    }
}
