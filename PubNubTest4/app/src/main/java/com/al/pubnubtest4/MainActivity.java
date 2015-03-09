package com.al.pubnubtest4;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pubnub.api.*;
import org.json.*;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public static final String CHAT_DIV = "|||";

    private Pubnub pubnub;
    private String otherUserPhoneNumber;
    private String thisUsername; // local user's username
    private String otherUsername; // username of the person you are chatting with
    private String chatChannel; // name of the chat channel, made from the two usernames.

    private ListView lvChat;
    private ArrayList<ChatMessage> mMessages;
    private ChatListAdapter mAdapter;
    private EditText etMessageToSend;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pubnub = new Pubnub("pub-c-b0ac15ff-9430-4b40-a2f5-919cf57bf1c4", "sub-c-6b77ceae-c35f-11e4-b54a-0619f8945a4f");
        otherUserPhoneNumber = null; // TODO - temporary until this value can be populated from the server
        thisUsername = "Alice"; // TODO - temp value
        otherUsername = "Bob"; // TODO - temp value
        if (thisUsername.compareTo(otherUsername) <= 0) { // chatChannel is derived from alphabetical order of the two usernames
            chatChannel = thisUsername + CHAT_DIV + otherUsername;
        } else {
            chatChannel = otherUsername + CHAT_DIV + thisUsername;
        }

        etMessageToSend = (EditText) findViewById(R.id.etMessageToSend);
        btnSend = (Button) findViewById(R.id.btnSend);

        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<ChatMessage>();
        mAdapter = new ChatListAdapter(MainActivity.this, thisUsername, mMessages);
        lvChat.setAdapter(mAdapter);

        subscribeToChannel();
        populateFromHistory();
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

    public void populateFromHistory() {
        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                final Object resp = response; // local variable response must be final if accessed from within inner class
                Log.e("XXXXXXXXXXXX", "SUCCESS HISTORY " + response.toString());

                // the list view can only be updated from the UI thread, so put this code in a Runnable.

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message;
                        String username;
                        String text;
                        try {
                            for (int i = 0; i < ((JSONArray) resp).getJSONArray(0).length(); i++) {
                                message = ((JSONArray) resp).getJSONArray(0).getString(i);
                                username = message.substring(0, message.indexOf(CHAT_DIV));
                                text = message.substring(message.indexOf(CHAT_DIV) + CHAT_DIV.length());
                                mMessages.add(new ChatMessage(username, text));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                        lvChat.invalidate(); // redraw listview
                    }
                });
            }
            public void errorCallback(String channel, PubnubError error) {
                Log.e("XXXXXXXXXXXX", "ERROR HISTORY " + error.toString());
            }
        };
        pubnub.history(chatChannel, 100, callback);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
