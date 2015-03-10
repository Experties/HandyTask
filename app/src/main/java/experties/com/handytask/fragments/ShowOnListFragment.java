package experties.com.handytask.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import experties.com.handytask.R;
import experties.com.handytask.activities.TaskItemsListListener;
import experties.com.handytask.adapters.TaskItemsAdapter;
import experties.com.handytask.models.TaskItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowOnListFragment extends Fragment {

    private TaskItemsAdapter aTaskItems;
    private ListView lvTaskItems;

    public ShowOnListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_on_list, container, false);

        lvTaskItems = (ListView) v.findViewById(R.id.lvTaskItems);

        // Get the list from the parent activity
        TaskItemsListListener listener = (TaskItemsListListener) getActivity();
        ArrayList<TaskItem> taskItems = listener.getList();

        // Attach list to the adapter
        aTaskItems = new TaskItemsAdapter(getActivity(), taskItems);

        // Link adapter to the listview
        lvTaskItems.setAdapter(aTaskItems);

        return v;
    }
}
