package experties.com.handytask.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

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

        /*Intent i = new Intent(context, ChatActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
    }
}
