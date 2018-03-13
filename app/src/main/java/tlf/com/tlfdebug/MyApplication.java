package tlf.com.tlfdebug;

import android.app.Application;

import com.tlf.keep.Tools;


/**
 * Created by ${wq} on 2017/9/21.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Tools.init(this);
    }
}
