package experties.com.handytask.helpers;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseUser;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import experties.com.handytask.adapters.AddressArrayAdapter;
import experties.com.handytask.models.AddressData;

/**
 * Created by hetashah on 3/14/15.
 */
public class AddressFinderHelper {
    private static final String API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyCeKd_4y7Odo1lEZvZRmQkLMYjkAnD_MY8";
    private StringBuilder url;

    private AsyncHttpClient client;

    private List<AddressData> items;
    private AddressArrayAdapter adapter;
    private int myLocationIndex = -1;
    private int homeIndex = -1;
    private int currentIndex = 0;

    public AddressFinderHelper(List<AddressData> items, AddressArrayAdapter adapter) {
        this.items = items;
        this.adapter = adapter;
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public void getHomeAddress(String address) {
        url = new StringBuilder(API_BASE_URL);
        url.append("&address=").append(address);
        client.get(url.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response != null) {
                    JSONArray results = response.optJSONArray("results");
                    if(results != null && results.length() > 0) {
                        JSONObject result = results.optJSONObject(0);
                        AddressData addr = new AddressData();
                        String address = result.optString("formatted_address");
                        addr.setAddress(address);
                        addr.setAddressName("Home");
                        if (homeIndex == -1) {
                            homeIndex = currentIndex;
                            currentIndex++;
                        }
                        items.add(homeIndex, addr);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void getCurrentLocationAddress(double lat, double lng) {
        url = new StringBuilder(API_BASE_URL);
        url.append("&latlng=").append(lat).append(",").append(lng);
        client.get(url.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response != null) {
                    AddressData addr = new AddressData();
                    String address = response.optString("formatted_address");
                    addr.setAddress(address);
                    addr.setAddressName("My location");
                    if(myLocationIndex == -1) {
                        myLocationIndex = currentIndex;
                        currentIndex++;
                    }
                    items.add(myLocationIndex,addr);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public static String getFullAddress(ParseUser user) {
        String address1 = user.getString("Address1");
        if(address1 != null && !"".equals(address1)) {
            StringBuilder address = new StringBuilder(address1);
            String address2 = user.getString("Address2");
            if(address2 != null && !"".equals(address2)) {
                address.append(",").append(address2);
            }

            String city = user.getString("City");
            if(city != null && !"".equals(city)) {
                address.append(",").append(city);
            }
            String state = user.getString("State");
            if(state != null && !"".equals(state)) {
                address.append(",").append(state);
            }

            String zipCode = user.getString("ZipCode");
            if(zipCode != null && !"".equals(zipCode)) {
                address.append(" ").append(zipCode);
            }

            return address.toString();
        }

        return null;
    }
}
