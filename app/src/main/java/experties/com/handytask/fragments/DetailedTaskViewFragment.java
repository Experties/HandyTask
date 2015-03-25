package experties.com.handytask.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import experties.com.handytask.R;
import experties.com.handytask.activities.ChatActivity;
import experties.com.handytask.adapters.ImageAdaptor;
import experties.com.handytask.helpers.FragmentHelpers;
import experties.com.handytask.models.ParseTask;


public class DetailedTaskViewFragment extends Fragment {
    ParseTask parseTask;

    TextView tvTitle;
    TextView tvDescription;
    TextView tvLocation;
    TextView tvRelativeTime;

    private ImageView imgVwNoPhoto;
    private LinearLayout layoutImgPager;
    private ViewPager viewPager;

    Button btnChat;

    private int imgCount = 0;
    private ArrayList<String> imgURL = new ArrayList<String>();

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

        imgVwNoPhoto = (ImageView) v.findViewById(R.id.imgVwNoPhoto);
        layoutImgPager = (LinearLayout) v.findViewById(R.id.layoutImgPager);

        btnChat = (Button) v.findViewById(R.id.btnChat);

        try {
            parseTask.getOwner().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnChat.setText(getActivity().getString(R.string.chat_btn_txt, FragmentHelpers.getUserName(parseTask.getOwner())));
        tvTitle.setText(WordUtils.capitalizeFully(parseTask.getTitle()));
        tvDescription.setText(parseTask.getDescription());

        tvLocation.setText(parseTask.getCity() + "," + parseTask.getState());
        tvRelativeTime.setText(FragmentHelpers.getRelativeTime(parseTask.getPostedDate()));

        ParseFile file = parseTask.getPhoto1();
        if (file!=null) {
            imgURL.add(file.getUrl());
            imgCount++;
        }

        file = parseTask.getPhoto2();
        if (file!=null) {
            imgURL.add(file.getUrl());
            imgCount++;
        }

        file = parseTask.getPhoto3();
        if (file!=null) {
            imgURL.add(file.getUrl());
            imgCount++;
        }

        if(imgCount == 0) {
            layoutImgPager.setVisibility(View.GONE);
            imgVwNoPhoto.setVisibility(View.VISIBLE);
        } else {
            imgVwNoPhoto.setVisibility(View.GONE);
            layoutImgPager.setVisibility(View.VISIBLE);
            viewPager = (ViewPager) v.findViewById(R.id.viewPager);
            PagerAdapter adapter = new ImageAdaptor(getActivity(), imgURL);
            viewPager.setAdapter(adapter);
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
                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i("ParseReceiver", "Sending message:");
                    }
                });

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
                            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            //context.dismiss();
                        }
                    }
                });

            }
        });
        return v;

    }


}
