package experties.com.handytask.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import experties.com.handytask.R;
import experties.com.handytask.activities.ChatActivity;
import experties.com.handytask.activities.ShowTasksActivity;
import experties.com.handytask.helpers.FragmentHelpers;
import experties.com.handytask.models.ParseTask;
import experties.com.handytask.models.TaskItem;


public class DetailedTaskViewFragment extends DialogFragment {
    ParseTask parseTask;

    TextView tvTitle;
    TextView tvDescription;
    TextView tvLocation;
    TextView tvRelativeTime;

    ParseImageView ivPhoto1;
    ParseImageView ivPhoto2;
    ParseImageView ivPhoto3;

    Button btnChat;
    Button btnBack;

    public static DetailedTaskViewFragment newInstance(ParseTask parseTask) {
        DetailedTaskViewFragment frag = new DetailedTaskViewFragment();
        frag.setTaskItem(parseTask);

        return frag;
    }

    public void setTaskItem(ParseTask parseTask) {
        this.parseTask = parseTask;
    }

    public DetailedTaskViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_detailed_task_view, container, false);

        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        tvRelativeTime = (TextView) v.findViewById(R.id.tvRelativeTime);
        ivPhoto1 = (ParseImageView) v.findViewById(R.id.ivPhoto1);
        ivPhoto2 = (ParseImageView) v.findViewById(R.id.ivPhoto2);
        ivPhoto3 = (ParseImageView) v.findViewById(R.id.ivPhoto3);

        btnChat = (Button) v.findViewById(R.id.btnChat);
        btnBack = (Button) v.findViewById(R.id.btnBack);

        try {
            parseTask.getOwner().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvTitle.setText(parseTask.getTitle());
        tvDescription.setText(parseTask.getDescription());
        LatLng p = new LatLng(parseTask.getLatitude(), parseTask.getLongitude());
        String relativeDistance =
                String.format("%.1f", parseTask.getRelativeDistance());
        tvLocation.setText(relativeDistance + " mi from your current location in " +
                    parseTask.getCity() + "," + parseTask.getState());
        tvRelativeTime.setText(FragmentHelpers.getRelativeTime(parseTask.getPostedDate()));

        ParseFile file = parseTask.getPhoto1();
        if (file!=null) {
            ivPhoto1.setVisibility(View.VISIBLE);
            ivPhoto1.setParseFile(file);
            ivPhoto1.loadInBackground();
        }

        file = parseTask.getPhoto2();
        if (file!=null) {
            ivPhoto2.setVisibility(View.VISIBLE);
            ivPhoto2.setParseFile(file);
            ivPhoto2.loadInBackground();
        }

        file = parseTask.getPhoto3();
        if (file!=null) {
            ivPhoto3.setVisibility(View.VISIBLE);
            ivPhoto3.setParseFile(file);
            ivPhoto3.loadInBackground();
        }
        final DetailedTaskViewFragment context = this;
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String taskId = parseTask.getObjectId();
                JSONObject object = new JSONObject();
                String message = getActivity().getString(R.string.push_message, ParseUser.getCurrentUser().get("FirstName"), parseTask.getTitle());
                try {

                    object.putOpt("taskId", taskId);
                    object.putOpt("alert", message);
                    object.putOpt("badge", "Increment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Create our Installation query
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("taskId", taskId);
                pushQuery.whereEqualTo("username", parseTask.getOwner().getUsername());
                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setMessage(message);
                push.setData(object);
                push.sendInBackground();

                parseTask.setResponder(ParseUser.getCurrentUser());
                parseTask.setCurrentState("pending");
                parseTask.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("taskId", taskId);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(i);
                            context.dismiss();
                        }
                    }
                });

            }
        });
        // [vince] TODO: figure out how to load images from parse
        // [vince] TODO: load all images, figure out a way how to display them

        return v;

    }


}
