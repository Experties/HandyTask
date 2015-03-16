package experties.com.handytask.helpers;

import com.loopj.android.http.AsyncHttpClient;
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
    private static final String API_PLACE_AUTO_COMPLETE_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyCeKd_4y7Odo1lEZvZRmQkLMYjkAnD_MY8&types=geocode";
    private static final String API_PLACE_DETAILS_BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyCeKd_4y7Odo1lEZvZRmQkLMYjkAnD_MY8";
    private StringBuilder url;

    private AsyncHttpClient client;

    private List<AddressData> items;
    private AddressArrayAdapter adapter;
    private int myLocationIndex = -1;
    private int homeIndex = -1;
    private int currentIndex = 0;

    private AddressData selectedAddress;

    public AddressFinderHelper(List<AddressData> items, AddressArrayAdapter adapter, AddressData item) {
        this.items = items;
        this.adapter = adapter;
        this.selectedAddress = item;
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public AddressData getSelectedAddress() {
        return selectedAddress;
    }

    public int getCurrentIndex() {
        return currentIndex;
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
                        JSONObject geometry = result.optJSONObject("geometry");
                        if(geometry != null) {
                            JSONObject location = geometry.optJSONObject("location");
                            if(location != null) {
                                String lat = location.optString("lat");
                                if(lat != null) {
                                    String lng = location.optString("lng");
                                    if(lng != null) {
                                        addr.setLatitude(Double.parseDouble(lat));
                                        addr.setLongitude(Double.parseDouble(lng));
                                    }
                                }
                            }
                        }
                        if (homeIndex == -1) {
                            homeIndex = currentIndex;
                            currentIndex++;
                        }
                        if(homeIndex == 0) {
                            addr.setSelected(true);
                            selectedAddress = addr;
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

    public void getCurrentLocationAddress(final double lat, final double lng) {
        url = new StringBuilder(API_BASE_URL);
        url.append("&latlng=").append(lat).append(",").append(lng);
        client.get(url.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray results = response.optJSONArray("results");
                if(results != null && results.length() > 0) {
                    JSONObject result = results.optJSONObject(0);
                    AddressData addr = new AddressData();
                    String address = result.optString("formatted_address");
                    addr.setAddress(address);
                    addr.setAddressName("My location");
                    addr.setLatitude(lat);
                    addr.setLongitude(lng);
                    if(myLocationIndex == -1) {
                        myLocationIndex = currentIndex;
                        currentIndex++;
                    }
                    if(myLocationIndex == 0) {
                        addr.setSelected(true);
                        selectedAddress = addr;
                    }
                    items.add(myLocationIndex, addr);
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

    public void getQueryLocation(String query){
        url = new StringBuilder(API_PLACE_AUTO_COMPLETE_BASE_URL);
        url.append("&input=").append(query);
        client.get(url.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response != null) {
                    JSONArray results = response.optJSONArray("predictions");
                    if(results != null) {
                        for (int i = 0, length = results.length(); i < length; i++) {
                            JSONObject result = results.optJSONObject(i);
                            AddressData addr = new AddressData();
                            String address = result.optString("description");
                            addr.setAddressName(address);
                            String placeId = result.optString("place_id");
                            addr.setPlaceId(placeId);
                            items.add(addr);
                            getPlaceDetails(placeId, items.size() - 1);
                        }
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

    public void getPlaceDetails(String placeId, final int index) {
        url = new StringBuilder(API_PLACE_DETAILS_BASE_URL);
        url.append("&placeid=").append(placeId);
        client.get(url.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response != null) {
                    JSONObject result = response.optJSONObject("result");
                    if(result != null) {
                        String address = result.optString("formatted_address");
                        AddressData addr = adapter.getItem(index);
                        addr.setAddress(address);
                        JSONObject geometry = result.optJSONObject("geometry");
                        if(geometry != null) {
                            JSONObject location = geometry.optJSONObject("location");
                            if(location != null) {
                                String lat = location.optString("lat");
                                if(lat != null) {
                                    String lng = location.optString("lng");
                                    if(lng != null) {
                                        addr.setLatitude(Double.parseDouble(lat));
                                        addr.setLongitude(Double.parseDouble(lng));
                                    }
                                }
                            }
                        }
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
}
