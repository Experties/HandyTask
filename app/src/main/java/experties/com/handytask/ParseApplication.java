package experties.com.handytask;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ParseApplication extends Application {
    public static final String PARSE_APP_ID = "ZFUPgMfFlIXDk5Xw7TxVO0fxnHdLMABIgPZS4QrR"; // TODO - move this to MainActivity?
    public static final String PARSE_CLIENT_KEY = "9eURBXZ7cDJHlQQbK34hBsC8rG88YsjE8OdqOkAG"; // TODO - move this to MainActivity?

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
    }
}
