package experties.com.handytask.rest;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import experties.com.handytask.models.ParseTask;

public class HandyTaskApplication extends Application {
    public static final String PARSE_APP_ID = "DLpMhNM3tgZxe1SA97Vq9rBOwVF8ZUtwB1VNHKoE"; // TODO - move this to MainActivity?
    public static final String PARSE_CLIENT_KEY = "VlC5p0X4h8gztmlTBd8rdlJm6yDllTVIm5mCki6L"; // TODO - move this to MainActivity?

    @Override
    public void onCreate() {
        super.onCreate();
        initializeParse();
    }

    private void initializeParse() {
        ParseObject.registerSubclass(ParseTask.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
    }
}
