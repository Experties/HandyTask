package experties.com.handytask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import experties.com.handytask.R;
import experties.com.handytask.models.TaskItem;

/**
 * Created by vincetulit on 3/8/15.
 */
public class TaskItemsAdapter extends ArrayAdapter<TaskItem> {

    public TaskItemsAdapter(Context context, List<TaskItem> items) {
        super(context, R.layout.item_task, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        TaskItem taskItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivMainTaskPhoto = (ImageView) convertView.findViewById(R.id.ivMainTaskPhoto);
            viewHolder.tvBriefDescription = (TextView) convertView.findViewById(R.id.tvBriefDescription);
            viewHolder.tvRelativeTime = (TextView) convertView.findViewById(R.id.tvRelativeTime);
            viewHolder.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
            viewHolder.tvRelativeDistance = (TextView) convertView.findViewById(R.id.tvRelativeDistance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvBriefDescription.setText(taskItem.getBriefDescription());
        viewHolder.tvRelativeTime.setText(taskItem.getDate().toString());
        viewHolder.tvLocation.setText(taskItem.getCity() + "," + taskItem.getState());
        // [vince] TODO: populate ivMainTaskPhoto and tvRelativeDistance. Also update ivRelativeTime appropiately.

        return convertView;
    }

    private static class ViewHolder {
        public ImageView ivMainTaskPhoto;
        public TextView tvBriefDescription;
        public TextView tvRelativeTime;
        public TextView tvLocation;
        public TextView tvRelativeDistance;
    }
}
