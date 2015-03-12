package experties.com.handytask.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import experties.com.handytask.R;
import experties.com.handytask.models.TaskItem;


public class DetailedTaskViewFragment extends DialogFragment {

    TaskItem taskItem;

    TextView tvTitle;
    TextView tvDescription;
    TextView tvLocation;
    TextView tvRelativeTime;
    ImageView ivPhoto;

    public static DetailedTaskViewFragment newInstance(TaskItem taskItem) {
        DetailedTaskViewFragment frag = new DetailedTaskViewFragment();
        frag.setTaskItem(taskItem);

        return frag;
    }

    public void setTaskItem(TaskItem taskItem) {
        this.taskItem = taskItem;
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
        ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);

        tvTitle.setText(taskItem.getBriefDescription());
        tvDescription.setText(taskItem.getDetailedDescription());
        tvLocation.setText("x mi from your current location in " +
                    taskItem.getCity() + "," + taskItem.getState());
        tvRelativeTime.setText(taskItem.getDate().toString());

        // [vince] TODO: relative time
        // [vince] TODO: relative distance
        // [vince] TODO: figure out how to load images from parse
        // [vince] TODO: load all images, figure out a way how to display them

        return v;

    }


}
