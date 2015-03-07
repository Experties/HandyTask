package experties.com.handytask.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by hetashah on 3/7/15.
 */
@ParseClassName("UserTask")
public class ParseUserTask extends ParseObject {

    public ParseUser getUser() {
        return getParseUser("UserId");
    }

    public void setUser(ParseUser value) {
        put("UserId", value);
    }

    public ParseTask getTask() {
        return (ParseTask) getParseObject("TaskId");
    }

    public void setTask(ParseTask value) {
        put("TaskId", value);
    }
}
