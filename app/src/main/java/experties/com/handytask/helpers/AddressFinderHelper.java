package experties.com.handytask.helpers;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hetashah on 3/14/15.
 */
public class AddressFinderHelper {
    private static final String API_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyCeKd_4y7Odo1lEZvZRmQkLMYjkAnD_MY8";
    private StringBuilder url;

    private AsyncHttpClient client;

    private AddressFinderHelper() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public void getAddress(String address) {
        url = new StringBuilder(API_BASE_URL);
        url.append("&address=").append(address);
        client.get(url.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response != null) {
                    String address = response.optString("formatted_address");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void getAddress(double lat, double lng) {
        url = new StringBuilder(API_BASE_URL);
        url.append("&latlng=").append(lat).append(",").append(lng);
        client.get(url.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response != null) {
                    String address = response.optString("formatted_address");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
