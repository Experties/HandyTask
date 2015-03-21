package experties.com.handytask.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;

import experties.com.handytask.R;
import experties.com.handytask.adapters.ChatListAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            // if the user is not logged in, start the Login Activity with the "next" extra set.
            Intent i = new Intent(ChatActivity.this, LoginActivity.class);
            i.putExtra("next", "chat");
            i.putExtra("taskId", taskId);
            startActivity(i);
            return;
        }

        setContentView(R.layout.activity_chat);

        taskId = getIntent().getStringExtra("taskId");
        pubnub = new Pubnub("pub-c-b0ac15ff-9430-4b40-a2f5-919cf57bf1c4", "sub-c-6b77ceae-c35f-11e4-b54a-0619f8945a4f");

        Typeface fontAngel = Typeface.createFromAsset(this.getAssets(), "fonts/RINGM.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tolBrChat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        mTitle = (TextView) toolbar.findViewById(R.id.chat_toolbar_title);

        //toolbar.setLogo(R.drawable.ic_tweets);
        mTitle.setTypeface(fontAngel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.finish();
            }
        });

        thisUsername =  currentUser.getUsername();

        etMessageToSend = (EditText) findViewById(R.id.etMessageToSend);

        btnSend = (ImageView) findViewById(R.id.btnSend);

        pbChat = (ProgressBar) findViewById(R.id.pbChat);

        layoutChat = (RelativeLayout) findViewById(R.id.layoutChat);

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
                        otherUserPhoneNumber = resp.getString("Mobile");
                        name = getUserName(resp);
                        profileImg = resp.getParseFile("ProfilePhoto");

                    } else {
                        requestor = resp;
                        responder = owner;
                        otherUsername = owner.getUsername();
                        otherUserPhoneNumber = owner.getString("Mobile");
                        name = getUserName(owner);
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
                    mAdapter = new ChatListAdapter(ChatActivity.this, thisUsername, mMessages, requestor, responder);
                    lvChat.setAdapter(mAdapter);

                    subscribeToChannel();
                    refreshAndPopulateFromHistory();
                } else {
                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.Unable_to_get_data_from_the_database_), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getUserName(ParseUser responder) {
        if(responder != null) {
            StringBuilder name = new StringBuilder(responder.getString("FirstName"));
            name.append(" ").append(responder.getString("LastName").substring(0,1).toUpperCase()).append(".");
            return name.toString();
        }
        return null;
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
        pubnub.publish(this.chatChannel, thisUsername + CHAT_DIV + etMessageToSend.getText().toString() , callback);
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
}
