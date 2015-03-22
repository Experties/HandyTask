package experties.com.handytask.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;

import experties.com.handytask.R;
import experties.com.handytask.fragments.TaskCreationFragment;
import experties.com.handytask.fragments.TaskCreationLocationFragment;
import experties.com.handytask.models.TaskItem;

public class TaskCreationStep1Activity extends ActionBarActivity implements TaskCreationFragment.TaskCreationNextStep{
    private boolean isMandatoryFilled;

    private TextView mTitle;
    private TaskItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            this.finish();
        }
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            this.finish();
            Intent taskActivity = new Intent(TaskCreationStep1Activity.this, LoginActivity.class);
            startActivity(taskActivity);
        } else {
            setTheme(R.style.AppTheme);
            setContentView(R.layout.activity_task_created);
            final TaskCreationStep1Activity context = this;
            Typeface fontJamesFajardo = Typeface.createFromAsset(this.getAssets(), "fonts/JamesFajardo.ttf");

            Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrTaskCreation);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            toolbar.setNavigationIcon(R.drawable.ic_back_white);
            mTitle = (TextView) toolbar.findViewById(R.id.task_toolbar_title);

            toolbar.setLogo(R.mipmap.ic_logo);
            mTitle.setTypeface(fontJamesFajardo);
            mTitle.setText(getResources().getString(R.string.title));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.finish();
                }
            });
            item = new TaskItem();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.creation_fragment, TaskCreationFragment.newInstance(item));
            ft.commit();
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
            this.finish();
            Intent taskActivity = new Intent(TaskCreationStep1Activity.this, LoginActivity.class);
            startActivity(taskActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNextStep(int stepId, TaskItem item) {
        switch (stepId) {
            case 1:
                break;
            case 2:
                this.item = item;
                Intent step2 = new Intent(TaskCreationStep1Activity.this, TaskCreationStep2Activity.class);
                step2.putExtra("item", this.item);
                startActivity(step2);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case 3:
                break;
        }
    }
}
