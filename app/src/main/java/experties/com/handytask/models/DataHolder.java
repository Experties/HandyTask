package experties.com.handytask.models;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hetashah on 3/24/15.
 */

public class DataHolder {
    private static DataHolder instance = null;

    Map<String, WeakReference<Object>> data = new HashMap<String, WeakReference<Object>>();
    private DataHolder() {}

    public static DataHolder getInstance() {
        if(instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public void save(String id, Object object) {
        data.put(id, new WeakReference<Object>(object));
    }

    public Object retrieve(String id) {
        WeakReference<Object> objectWeakReference = data.get(id);
        data.remove(id);
        return objectWeakReference.get();
    }
}
