package experties.com.handytask.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import experties.com.handytask.R;
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
        // [vince] TODO: figure out how to load images from parse
        // [vince] TODO: load all images, figure out a way how to display them

        return v;

    }


}
