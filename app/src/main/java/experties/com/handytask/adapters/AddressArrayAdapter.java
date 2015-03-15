package experties.com.handytask.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import experties.com.handytask.R;
import experties.com.handytask.models.AddressData;

/**
 * Created by hetashah on 3/14/15.
 */
public class AddressArrayAdapter extends ArrayAdapter<AddressData> {
    private Context context;

    public AddressArrayAdapter(Context context, List<AddressData> data) {
        super(context, android.R.layout.simple_list_item_1, data);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AddressData item = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            Typeface fontRingm = Typeface.createFromAsset(getContext().getAssets(), "fonts/RINGM.ttf");
            Typeface fontJamesFajardo = Typeface.createFromAsset(getContext().getAssets(), "fonts/JamesFajardo.ttf");

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.address_item,parent, false);
            viewHolder.txtAddress = (TextView) convertView.findViewById(R.id.txtVwAddress);
            viewHolder.txtAddress.setTypeface(fontRingm);
            viewHolder.txtAddressName = (TextView) convertView.findViewById(R.id.txtVwAddressName);
            viewHolder.txtAddress.setTypeface(fontJamesFajardo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtAddress.setText(Html.fromHtml(item.getAddress()));
        viewHolder.txtAddressName.setText(Html.fromHtml(item.getAddressName()));
        return convertView;
    }

    private static class ViewHolder {
        TextView txtAddress;
        TextView txtAddressName;
    }
}
