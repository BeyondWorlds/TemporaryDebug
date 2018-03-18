package tlf.com.tlfdebug;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
