package experties.com.handytask.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import experties.com.handytask.R;
import experties.com.handytask.fragments.DetailedTaskViewFragment;
import experties.com.handytask.fragments.TaskCreationFragment;
import experties.com.handytask.models.ParseTask;
import experties.com.handytask.models.TaskItem;

public class DetailedTaskViewActivity extends ActionBarActivity {
    private TextView mTitle;
    private ProgressBar pbDetailTask;
    private FrameLayout details_task_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_task_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            this.finish();
            Intent taskActivity = new Intent(DetailedTaskViewActivity.this, LoginActivity.class);
            startActivity(taskActivity);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            setTheme(R.style.AppTheme);
            setContentView(R.layout.activity_detailed_task_view);

            final DetailedTaskViewActivity context = this;
            Typeface fontJamesFajardo = Typeface.createFromAsset(this.getAssets(), "fonts/RalewayMedium.ttf");

            Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrDetailTask);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            toolbar.setNavigationIcon(R.drawable.ic_back_white);
            mTitle = (TextView) toolbar.findViewById(R.id.detail_task_toolbar_title);

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

            pbDetailTask = (ProgressBar) findViewById(R.id.pbDetailTask);
            details_task_fragment = (FrameLayout) findViewById(R.id.details_task_fragment);

            details_task_fragment.setVisibility(View.GONE);
            pbDetailTask.setVisibility(View.VISIBLE);
            String taskId = (String) getIntent().getExtras().getString("taskId");
            if(taskId != null) {
                ParseQuery<ParseTask> query = ParseQuery.getQuery(ParseTask.class);
                query.getInBackground(taskId, new GetCallback<ParseTask>() {
                    public void done(ParseTask object, ParseException e) {
                        if (e == null) {
                            pbDetailTask.setVisibility(View.GONE);
                            details_task_fragment.setVisibility(View.VISIBLE);
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.details_task_fragment, DetailedTaskViewFragment.newInstance(object));
                            ft.commit();
                        } else {
                            pbDetailTask.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Error occurred while fetching details of task.")
                                    .setMessage("Please check your internet connection and try again later...")
                                    .setCancelable(true)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_task_view, menu);
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
            Intent taskActivity = new Intent(DetailedTaskViewActivity.this, LoginActivity.class);
            startActivity(taskActivity);
            overridePendingTransition(R.anim.left_in, R.anim.slide_down);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
