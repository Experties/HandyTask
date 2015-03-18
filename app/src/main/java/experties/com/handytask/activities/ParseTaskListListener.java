package experties.com.handytask.activities;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import experties.com.handytask.models.ParseTask;
import experties.com.handytask.models.TaskItem;

/**
 * Created by vincetulit on 3/8/15.
 */
public interface ParseTaskListListener {
    public ArrayList<ParseTask> getParseTaskList();
    public void populateListTaskList();
    public LatLng getCurrentPosition();
}

