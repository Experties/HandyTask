package experties.com.handytask.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import experties.com.handytask.R;
import experties.com.handytask.models.ChatMessage;
import experties.com.handytask.models.ParseTask;

public class ChatListAdapter extends ArrayAdapter<ChatMessage> {
    private Context context;
    private String thisUser;
    private ParseUser requestor;
    private ParseUser responder;

    private LinearLayout layoutChatItem;

    private int primaryBGColor = 0xFF0288D1;
    private int primaryTxtColor = 0xFFFFFFFF;
    private int secondaryBGColor = 0xFFB3E5FC;
    private int secondaryTxtColor = 0xFF212121;

    private Target primaryTarget = null;
    private Target secondaryTarget = null;
    public ChatListAdapter(Context context, String thisUser, List<ChatMessage> messages, ParseUser requestor, ParseUser responder, final ListView lvChat) {
        super(context, 0, messages);
        this.context = context;
        this.thisUser = thisUser;
        this.requestor = requestor;
        this.responder = responder;
        final ArrayAdapter adapter = this;
        if(requestor != null) {
            ParseFile profileImg = requestor.getParseFile("ProfilePhoto");
            if(profileImg != null) {
                primaryTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // TODO Fill profile image with the bitmap
                        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch vibrant = palette.getVibrantSwatch();
                                if (vibrant != null) {
                                    // If we have a vibrant color, update the title TextView
                                    primaryBGColor = vibrant.getRgb();
                                    primaryTxtColor = vibrant.getTitleTextColor();
                                    adapter.notifyDataSetChanged();
                                    lvChat.invalidate();
                                }
                            }
                        });
                        //holder.imageRight.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                //holder.imageRight.setTag(target);
                Picasso.with(context).load(profileImg.getUrl()).into(primaryTarget);
            }
        }

        if(responder != null) {
            ParseFile profileImg = responder.getParseFile("ProfilePhoto");
            if(profileImg != null) {
                secondaryTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch vibrant = palette.getVibrantSwatch();
                                if (vibrant != null) {
                                    // If we have a vibrant color, update the title TextView
                                    secondaryBGColor = vibrant.getRgb();
                                    secondaryTxtColor = vibrant.getTitleTextColor();
                                    adapter.notifyDataSetChanged();
                                    lvChat.invalidate();
                                }
                            }
                        });
                        //holder.imageLeft.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                //holder.imageLeft.setTag(target);
                Picasso.with(context).load(profileImg.getUrl()).into(secondaryTarget);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.layoutChatItem = (LinearLayout) convertView.findViewById(R.id.layoutChatItem);
            holder.imageLeft = (ImageView)convertView.findViewById(R.id.ivOtherUserChatPhoto);
            holder.imageRight = (ImageView)convertView.findViewById(R.id.ivThisUserChatPhoto);
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
                    Picasso.with(context).load(profileImg.getUrl()).into(holder.imageRight);
                } else {
                    holder.imageRight.setImageResource(R.drawable.ic_profilee);
                }
            } else {
                holder.imageRight.setImageResource(R.drawable.ic_profilee);
            }
            holder.layoutChatItem.setBackgroundColor(primaryBGColor);
            holder.text.setTextColor(primaryTxtColor);
        } else {
            holder.imageLeft.setVisibility(View.VISIBLE);
            holder.imageRight.setVisibility(View.GONE);
            holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            if(responder != null) {
                ParseFile profileImg = responder.getParseFile("ProfilePhoto");
                if(profileImg != null) {
                    Picasso.with(context).load(profileImg.getUrl()).into(holder.imageLeft);
                } else {
                    holder.imageLeft.setImageResource(R.drawable.ic_profilee);
                }
            } else {
                holder.imageLeft.setImageResource(R.drawable.ic_profilee);
            }
            holder.layoutChatItem.setBackgroundColor(secondaryBGColor);
            holder.text.setTextColor(secondaryTxtColor);
        }
        holder.text.setText(message.getMessage());
        return convertView;
    }


    final class ViewHolder {
        public LinearLayout layoutChatItem;
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView text;
    }

}