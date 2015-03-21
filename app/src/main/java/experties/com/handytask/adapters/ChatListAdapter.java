package experties.com.handytask.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import experties.com.handytask.R;
import experties.com.handytask.models.ChatMessage;
import experties.com.handytask.models.ParseTask;

public class ChatListAdapter extends ArrayAdapter<ChatMessage> {
    private String thisUser;
    private ParseUser requestor;
    private ParseUser responder;

    public ChatListAdapter(Context context, String thisUser, List<ChatMessage> messages, ParseUser requestor, ParseUser responder) {
        super(context, 0, messages);
        this.thisUser = thisUser;
        this.requestor = requestor;
        this.responder = responder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.imageLeft = (ParseImageView)convertView.findViewById(R.id.ivOtherUserChatPhoto);
            holder.imageRight = (ParseImageView)convertView.findViewById(R.id.ivThisUserChatPhoto);
            holder.text = (TextView)convertView.findViewById(R.id.tvChatText);
            convertView.setTag(holder);
        }
        final ChatMessage message = (ChatMessage)getItem(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag();


        final boolean isMe = message.getUser().equals(thisUser);
        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (isMe) {
            holder.imageRight.setVisibility(View.VISIBLE);
            holder.imageLeft.setVisibility(View.GONE);
            holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            if(requestor != null) {
                ParseFile profileImg = requestor.getParseFile("ProfilePhoto");
                if(profileImg != null) {
                    holder.imageRight.setParseFile(profileImg);
                    holder.imageRight.loadInBackground();
                } else {
                    holder.imageRight.setImageResource(R.drawable.ic_profilee);
                }
            } else {
                holder.imageRight.setImageResource(R.drawable.ic_profilee);
            }
        } else {
            holder.imageLeft.setVisibility(View.VISIBLE);
            holder.imageRight.setVisibility(View.GONE);
            holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            if(responder != null) {
                ParseFile profileImg = responder.getParseFile("ProfilePhoto");
                if(profileImg != null) {
                    holder.imageLeft.setParseFile(profileImg);
                    holder.imageLeft.loadInBackground();
                } else {
                    holder.imageLeft.setImageResource(R.drawable.ic_profilee);
                }
            } else {
                holder.imageLeft.setImageResource(R.drawable.ic_profilee);
            }
        }
        holder.text.setText(message.getMessage());
        return convertView;
    }


    final class ViewHolder {
        public ParseImageView imageLeft;
        public ParseImageView imageRight;
        public TextView text;
    }

}