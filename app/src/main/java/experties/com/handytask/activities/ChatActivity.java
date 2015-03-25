package experties.com.handytask.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import experties.com.handytask.R;
import experties.com.handytask.adapters.ChatListAdapter;
import experties.com.handytask.helpers.FragmentHelpers;
import experties.com.handytask.models.ChatMessage;
import experties.com.handytask.models.ParseTask;


public class ChatActivity extends ActionBarActivity {
    public static final String CHAT_DIV = "|||";
    private final ChatActivity context = this;

    private Pubnub pubnub;

    private String otherUserPhoneNumber;
    private String thisUsername; // local user's username
    private String otherUsername; // username of the person you are chatting with
    private String chatChannel; // name of the chat channel, made from the two usernames.
    private String taskId;

    private boolean thisUserIsSeller;

    private ParseTask task;
    private ParseUser requestor;
    private ParseUser responder;
    private ArrayList<ChatMessage> mMessages;

    private ListView lvChat;
    private ChatListAdapter mAdapter;

    private EditText etMessageToSend;
    private TextView mTitle;

    private ImageView btnSend;

    private ProgressBar pbChat;

    private RelativeLayout layoutChat;

    private RoundedImageView ivOtherUserPhoto;

    private FrameLayout layout_call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            // if the user is not logged in, start the Login Activity with the "next" extra set.
            Intent i = new Intent(ChatActivity.this, LoginActivity.class);
            i.putExtra("next", "chat");
            i.putExtra("taskId", taskId);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
            return;
        }

        setContentView(R.layout.activity_chat);

        taskId = getIntent().getStringExtra("taskId");
        pubnub = new Pubnub("pub-c-b0ac15ff-9430-4b40-a2f5-919cf57bf1c4", "sub-c-6b77ceae-c35f-11e4-b54a-0619f8945a4f");

        Typeface fontAngel = Typeface.createFromAsset(this.getAssets(), "fonts/RalewayMedium.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrChat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        mTitle = (TextView) toolbar.findViewById(R.id.chat_toolbar_title);

        toolbar.setLogo(R.mipmap.ic_logo);
        mTitle.setTypeface(fontAngel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });

        thisUsername =  currentUser.getUsername();

        etMessageToSend = (EditText) findViewById(R.id.etMessageToSend);
        etMessageToSend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onSendClick(null);
                    return true;
                }
                return false;
            }
        });
        etMessageToSend.setFocusableInTouchMode(true);
        etMessageToSend.setFocusable(true);
        etMessageToSend.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMessageToSend, InputMethodManager.SHOW_IMPLICIT);
        btnSend = (ImageView) findViewById(R.id.btnSend);

        pbChat = (ProgressBar) findViewById(R.id.pbChat);

        layoutChat = (RelativeLayout) findViewById(R.id.layoutChat);
        layout_call = (FrameLayout) findViewById(R.id.layout_call);

        ivOtherUserPhoto = (RoundedImageView) findViewById(R.id.ivOtherUserPhoto);

        ivOtherUserPhoto.setVisibility(View.GONE);
        layoutChat.setVisibility(View.GONE);
        pbChat.setVisibility(View.VISIBLE);

        ParseQuery<ParseTask> taskQuery = ParseQuery.getQuery(ParseTask.class);
        taskQuery.whereEqualTo("objectId", taskId);
        taskQuery.getInBackground(taskId, new GetCallback<ParseTask>() {
            @Override
            public void done(ParseTask parseTask, ParseException e) {
                String name = null;
                // Got the task object information. Now we need to get the task owner information.
                pbChat.setVisibility(View.GONE);
                layoutChat.setVisibility(View.VISIBLE);
                if (e == null) {
                    task = parseTask; // store the task
                    ParseUser owner = parseTask.getOwner();
                    ParseUser resp = parseTask.getResponder();
                    try {
                        owner.fetchIfNeeded();
                        resp.fetchIfNeeded();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    ParseFile profileImg = null;
                    if(thisUsername.equalsIgnoreCase(owner.getUsername())) {
                        requestor = owner;
                        responder = resp;
                        otherUsername = resp.getUsername();
                        otherUserPhoneNumber = String.valueOf(resp.getLong("Mobile"));
                        name = FragmentHelpers.getUserName(resp);
                        profileImg = resp.getParseFile("ProfilePhoto");

                    } else {
                        requestor = resp;
                        responder = owner;
                        otherUsername = owner.getUsername();
                        otherUserPhoneNumber = String.valueOf(owner.getLong("Mobile"));
                        name = FragmentHelpers.getUserName(owner);
                        profileImg = owner.getParseFile("ProfilePhoto");
                    }

                    ivOtherUserPhoto.setVisibility(View.VISIBLE);
                    if(profileImg != null) {
                        Picasso.with(context)
                                .load(profileImg.getUrl())
                                .fit().centerCrop()
                                .into(ivOtherUserPhoto);
                        //ivOtherUserPhoto.setParseFile(profileImg);
                        //ivOtherUserPhoto.loadInBackground();
                    } else {
                        ivOtherUserPhoto.setImageResource(R.drawable.ic_profilee);
                    }
                    if (thisUsername.compareTo(otherUsername) <= 0) { // chatChannel is derived from alphabetical order of the two usernames
                        chatChannel = thisUsername + "-" + task.getObjectId() + CHAT_DIV + otherUsername + "-" + task.getObjectId();
                    } else {
                        chatChannel = otherUsername + "-" + task.getObjectId() + CHAT_DIV + thisUsername + "-" + task.getObjectId();
                    }

                    if(name != null) {
                        mTitle.setText(name);
                    } else {
                        mTitle.setText(otherUsername);
                    }

                    lvChat = (ListView) findViewById(R.id.lvChat);
                    mMessages = new ArrayList<ChatMessage>();
                    mAdapter = new ChatListAdapter(ChatActivity.this, thisUsername, mMessages, requestor, responder, lvChat);
                    lvChat.setAdapter(mAdapter);
                    subscribeToChannel();
                    refreshAndPopulateFromHistory();
                } else {
                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.Unable_to_get_data_from_the_database_), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void subscribeToChannel() {
        // Subcribe to the channel
        try {
            pubnub.subscribe(chatChannel, new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : CONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                            addChatMessage((String)message);
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            Log.e("XXXXXXXXXXXX", "SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    public void onSendClick(View v) {
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.e("XXXXXXXXXXXX",response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                Log.e("XXXXXXXXXXXX",error.toString());
            }
        };
        pubnub.publish(this.chatChannel, thisUsername + CHAT_DIV + etMessageToSend.getText().toString(), callback);
        etMessageToSend.setText(""); // blank out text field
    }

    public void refreshAndPopulateFromHistory() {
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.e("XXXXXXXXXXXX", "SUCCESS HISTORY " + response.toString());
                try {
                    for (int i = 0; i < ((JSONArray) response).getJSONArray(0).length(); i++) {
                        String message = ((JSONArray) response).getJSONArray(0).getString(i);
                        addChatMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            public void errorCallback(String channel, PubnubError error) {
                Log.e("XXXXXXXXXXXX", "ERROR HISTORY " + error.toString());
            }
        };

        mMessages.clear();
        pubnub.history(chatChannel, 100, callback);
    }

    public void addChatMessage(String message) {
        final String msg = message;

        // the list view can only be updated from the UI thread, so put this code in a Runnable.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String username;
                String text;
                username = msg.substring(0, msg.indexOf(CHAT_DIV));
                text = msg.substring(msg.indexOf(CHAT_DIV) + CHAT_DIV.length());

                mMessages.add(new ChatMessage(username, text));
                mAdapter.notifyDataSetChanged();
                lvChat.invalidate(); // redraw listview
                lvChat.post(new Runnable() {
                    @Override
                    public void run() {
                        // Select the last row so it will scroll into view...
                        lvChat.setSelection(mAdapter.getCount() - 1);
                    }
                });
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case R.id.muAddTask:
                task.setCurrentState("closed");
                task.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        context.finish();
                    }
                });
                break;
            case R.id.muDeclineTask:
                task.setCurrentState("open");
                task.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        context.finish();
                    }
                });
                break;
            case R.id.muSignOut:
                ParseUser.logOut();
                this.finish();
                Intent loginActivity = new Intent(ChatActivity.this, LoginActivity.class);
                startActivity(loginActivity);
                overridePendingTransition(R.anim.left_in, R.anim.slide_down);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onDeclineOfferClick(View v) {
        Intent i = new Intent();
        i.putExtra("offerResponse", "decline");
        setResult(RESULT_OK, i);
        finish();
    }

    public void onAcceptOfferClick(View v) {
        Intent i = new Intent();
        i.putExtra("offerResponse", "accept");
        setResult(RESULT_OK, i);
        finish();
    }

    public void onCallOtherUserClick(View v) {
        if(isCallingSupported(this)) {
            if (otherUserPhoneNumber != null) {
                String uri = "tel:" + otherUserPhoneNumber;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Call Error")
                    .setMessage("Looks like this device does not support calling feature....")
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    // Set the text color on the dialog title and separator
                    setTextColor(dialogInterface, 0xFFE5492A);
                }
            });
            dialog.show();
        }
    }

    private static boolean isCallingSupported(Context context) {
        boolean result = true;
        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() <= 0) {
            result = false;
        }
        return result;
    }

    public void setTextColor(DialogInterface alert, int color) {
        try {
            Class c = alert.getClass();
            Field mAlert = c.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(alert);
            c = alertController.getClass();
            Field mTitleView = c.getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);
            Object dialogTitle = mTitleView.get(alertController);
            TextView dialogTitleView = (TextView)dialogTitle;
            // Set text color on the title
            dialogTitleView.setTextColor(color);
            // To find the horizontal divider, first
            //  get container around the Title
            ViewGroup parent = (ViewGroup)dialogTitleView.getParent();
            // Then get the container around that container
            parent = (ViewGroup)parent.getParent();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View v = parent.getChildAt(i);
                if (v instanceof ImageView) {
                    // We got an ImageView, that should be the separator
                    ImageView im = (ImageView)v;
                    // Set a color filter on the image
                    im.setColorFilter(color);
                }
            }
        } catch (Exception e) {
            // Ignore any exceptions, either it works or it doesn't
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
