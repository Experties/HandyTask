package experties.com.handytask.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseUser;

import experties.com.handytask.R;
import experties.com.handytask.models.TaskItem;

public class TaskCreatedActivity extends ActionBarActivity {
    private TextView mTitle;

    private EditText edTxtTitle;
    private EditText edTxtComment;

    private Button taskUploadBtn1;
    private Button taskUploadBtn2;
    private Button taskUploadBtn3;
    private Button cancelTaskBtn;
    private Button nextTaskBtn;

    private ImageView imgVwTask1;
    private ImageView imgVwTask2;
    private ImageView imgVwTask3;

    private Spinner sprTaskType;

    private TaskItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            this.finish();
            Intent taskActivity = new Intent(TaskCreatedActivity.this, LoginActivity.class);
            startActivity(taskActivity);
        } else {
            setContentView(R.layout.activity_task_created);
            setupView();
        }
    }

    private void setupView() {
        final TaskCreatedActivity context = this;
        sprTaskType = (Spinner) findViewById(R.id.sprTaskType);

        edTxtTitle = (EditText) findViewById(R.id.edTxtTitle);
        edTxtComment = (EditText) findViewById(R.id.edTxtComment);

        taskUploadBtn1 = (Button) findViewById(R.id.taskUploadBtn1);
        taskUploadBtn2 = (Button) findViewById(R.id.taskUploadBtn2);
        taskUploadBtn3 = (Button) findViewById(R.id.taskUploadBtn3);
        cancelTaskBtn = (Button) findViewById(R.id.cancelTaskBtn);
        cancelTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
        nextTaskBtn = (Button) findViewById(R.id.nextTaskBtn);
        nextTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imgVwTask1 = (ImageView) findViewById(R.id.imgVwTask1);
        imgVwTask2 = (ImageView) findViewById(R.id.imgVwTask2);
        imgVwTask3 = (ImageView) findViewById(R.id.imgVwTask3);

        Typeface fontJamesFajardo = Typeface.createFromAsset(this.getAssets(), "fonts/JamesFajardo.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrTaskCreation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        mTitle = (TextView) toolbar.findViewById(R.id.task_toolbar_title);

        //toolbar.setLogo(R.drawable.ic_tweets);
        mTitle.setTypeface(fontJamesFajardo);
        mTitle.setText(getResources().getString(R.string.title));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.finish();
            }
        });
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
