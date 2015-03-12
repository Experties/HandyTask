package experties.com.handytask.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseUser;

import experties.com.handytask.R;

public class TaskCreatedActivity extends ActionBarActivity {
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            Intent taskActivity = new Intent(TaskCreatedActivity.this, LoginActivity.class);
            startActivity(taskActivity);
        } else {
            setContentView(R.layout.activity_task_created);
            Typeface fontJamesFajardo = Typeface.createFromAsset(this.getAssets(), "fonts/JamesFajardo.ttf");

            Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrTaskCreation);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mTitle = (TextView) toolbar.findViewById(R.id.task_toolbar_title);

            //toolbar.setLogo(R.drawable.ic_tweets);
            mTitle.setTypeface(fontJamesFajardo);
            mTitle.setText(getResources().getString(R.string.title));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task_created, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.muSignOut) {
            ParseUser.logOut();
            Intent taskActivity = new Intent(TaskCreatedActivity.this, LoginActivity.class);
            startActivity(taskActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
