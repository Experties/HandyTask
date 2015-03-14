package experties.com.handytask.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import experties.com.handytask.R;
import experties.com.handytask.models.TaskItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskCreationLocationFragment extends Fragment {


    public TaskCreationLocationFragment() {
        // Required empty public constructor
    }

    public static TaskCreationLocationFragment newInstance(TaskItem item) {
        TaskCreationLocationFragment frag = new TaskCreationLocationFragment();
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_creation_location, container, false);
    }


}
