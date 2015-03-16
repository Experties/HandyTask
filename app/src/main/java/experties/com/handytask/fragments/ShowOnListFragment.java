package experties.com.handytask.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import bolts.Task;
import experties.com.handytask.R;
import experties.com.handytask.activities.ParseTaskListListener;
import experties.com.handytask.activities.ShowTasksActivity;
import experties.com.handytask.adapters.ParseTasksAdapter;
import experties.com.handytask.models.ParseTask;
import experties.com.handytask.models.TaskItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowOnListFragment extends Fragment {

    ArrayList<ParseTask> parseTasks;
    private ParseTasksAdapter aParseTasks;
    private ListView lvParseTasks;


    public ShowOnListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_on_list, container, false);

        lvParseTasks = (ListView) v.findViewById(R.id.lvParseTasks);

        // Get the list from the parent activity
        ParseTaskListListener listener = (ParseTaskListListener) getActivity();
        parseTasks = listener.getParseTaskList();

        // Attach list to the adapter
        aParseTasks = new ParseTasksAdapter(getActivity(), parseTasks);

        // Link adapter to the listview
        lvParseTasks.setAdapter(aParseTasks);

        setupOnClickListener();

        return v;
    }

    public void setupOnClickListener() {
        lvParseTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetailedView(parseTasks.get(position));
            }
        });
    }

    public void showDetailedView(ParseTask parseTask) {
        DetailedTaskViewFragment  frag = DetailedTaskViewFragment.newInstance(parseTask);
        frag.show(getFragmentManager(), "fragment_reply_dialog");
    }

    public void updateTasksList() {
        ParseTaskListListener listener = (ParseTaskListListener) getActivity();
        parseTasks = listener.getParseTaskList();
        // [vince] TODO: Ugly solution. Because I completely overwrite parseTask instead remove/add
        // I need to do the adapter setup and attach again.
        aParseTasks = new ParseTasksAdapter(getActivity(), parseTasks);
        lvParseTasks.setAdapter(aParseTasks);
        aParseTasks.notifyDataSetChanged();
    }



}
