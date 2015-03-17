package experties.com.handytask.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;

import experties.com.handytask.activities.ChatActivity;

/**
 * Created by hetashah on 3/16/15.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver{
    @Override
    public void onPushOpen(Context context, Intent intent) {

        //To track "App Opens"
        ParseAnalytics.trackAppOpenedInBackground(intent);

        //Here is data you sent
        Log.i("ParseReceiver", intent.getExtras().getString("com.parse.Data"));
        String data = intent.getExtras().getString("com.parse.Data");
        if(!"".equals(data)) {
            try {
                JSONObject dataObj = new JSONObject(data);
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("taskId", dataObj.optString("taskId"));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch(Exception e) {}
        }
    }
}
