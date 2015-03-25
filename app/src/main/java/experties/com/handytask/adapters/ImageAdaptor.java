package experties.com.handytask.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import experties.com.handytask.R;

/**
 * Created by hetashah on 3/23/15.
 */
public class ImageAdaptor extends PagerAdapter {

    Context context;
    ArrayList<String> imageURL = new ArrayList<String>();

    public ImageAdaptor(Context context, ArrayList<String> imageURL){
        this.context = context;
        if(imageURL != null) {
            this.imageURL = imageURL;
        }
    }

    @Override
    public int getCount() {
        return imageURL.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        View viewItem = inflater.inflate(R.layout.image_item, container, false);
        final ImageView imageView = (ImageView) viewItem.findViewById(R.id.imageView);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                // TODO Fill profile image with the bitmap
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch mutedDarkSwatch = palette.getDarkMutedSwatch();
                        if(mutedDarkSwatch != null) {
                            int rgbColor = mutedDarkSwatch.getRgb();
                            imageView.setBackgroundColor(rgbColor);
                        } else {
                            int vibrant = palette.getVibrantColor(0xFFFFFF);
                            imageView.setBackgroundColor(vibrant);
                        }
                        imageView.setImageBitmap(bitmap);
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
        imageView.setTag(target);
        //holder.imageRight.setTag(target);
        Picasso.with(context)
                .load(imageURL.get(position))
                .placeholder(R.mipmap.ic_loading)
                //.fit().centerInside()
                .into(target);

        TextView textView1 = (TextView) viewItem.findViewById(R.id.txtVwImgInx);
        textView1.setText((position+1) + " / " + getCount());

        ((ViewPager)container).addView(viewItem);

        return viewItem;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }
}
