package tlf.com.tlfdebug.ui;

import android.os.Bundle;

import com.tlf.keep.PermissionManager;


public class BaseBleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionManager.checkBLE(this);
    }
}
