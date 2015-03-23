package experties.com.handytask.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
    private SwipeRefreshLayout swipeContainer;

    public ShowOnListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_show_on_list, container, false);

        lvParseTasks = (ListView) v.findViewById(R.id.lvParseTasks);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        parseTasks = new ArrayList<ParseTask>();
        // Get the list from the parent activity
        final ParseTaskListListener listener = (ParseTaskListListener) getActivity();
        parseTasks.addAll(listener.getParseTaskList());
        aParseTasks = new ParseTasksAdapter(getActivity(), parseTasks);
        lvParseTasks.setAdapter(aParseTasks);
        // Attach list to the adapter
        aParseTasks = new ParseTasksAdapter(getActivity(), parseTasks);

        // Link adapter to the listview
        lvParseTasks.setAdapter(aParseTasks);

        setupOnClickListener();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listener.populateListTaskList();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
        if(parseTasks != null) {
            parseTasks.clear();
        } else {
            parseTasks = new ArrayList<ParseTask>();
        }
        ParseTaskListListener listener = (ParseTaskListListener) getActivity();
        if(listener != null) {
            List<ParseTask> tasks = listener.getParseTaskList();
            if (tasks != null) {
                parseTasks.addAll(tasks);
                // [vince] TODO: Ugly solution. Because I completely overwrite parseTask instead remove/add
                // I need to do the adapter setup and attach again.
                aParseTasks.notifyDataSetChanged();
            }
            if (swipeContainer != null) {
                swipeContainer.setRefreshing(false);
            }
        }
    }
}
