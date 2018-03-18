package tlf.com.tlfdebug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.tlf.keep.PermissionManager;

import tlf.com.tlfdebug.ui.BpActivity;
import tlf.com.tlfdebug.ui.BsActivity;
import tlf.com.tlfdebug.ui.LightActivity;
import tlf.com.tlfdebug.ui.OxyActivity;
import tlf.com.tlfdebug.ui.TempActivity;
import tlf.com.tlfdebug.ui.WeightActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private Button btn_bp, btn_bs, btn_oxy, btn_weight, btn_temp, btn_other;
    private Window mWindow;
    private View mDecorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        mWindow = getWindow();
//        mDecorView = mWindow.getDecorView();
//        setHideVirtualKey();
//        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                setHideVirtualKey();
//            }
//        });
    }

    private void setHideVirtualKey() {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        mDecorView.setSystemUiVisibility(uiOptions);
    }


    private void initView() {
        btn_bp = (Button) findViewById(R.id.btn_bp);
        btn_bs = (Button) findViewById(R.id.btn_bs);
        btn_oxy = (Button) findViewById(R.id.btn_oxy);
        btn_weight = (Button) findViewById(R.id.btn_weight);
        btn_temp = (Button) findViewById(R.id.btn_temp);
        btn_other = (Button) findViewById(R.id.btn_other);
        btn_bp.setOnClickListener(this);
        btn_bs.setOnClickListener(this);
        btn_oxy.setOnClickListener(this);
        btn_weight.setOnClickListener(this);
        btn_temp.setOnClickListener(this);
        btn_other.setOnClickListener(this);
        PermissionManager.setPermission(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bp:
                Intent bpIntent = new Intent(MainActivity.this, BpActivity.class);
                startActivity(bpIntent);
                break;
            case R.id.btn_bs:
                Intent bsIntent = new Intent(MainActivity.this, BsActivity.class);
                startActivity(bsIntent);
                break;
            case R.id.btn_oxy:
                Intent oxyIntent = new Intent(MainActivity.this, OxyActivity.class);
                startActivity(oxyIntent);
                break;
            case R.id.btn_weight:
                Intent weightIntent = new Intent(MainActivity.this, WeightActivity.class);
                startActivity(weightIntent);
                break;
            case R.id.btn_temp:
                Intent tempIntent = new Intent(MainActivity.this, TempActivity.class);
                startActivity(tempIntent);
                break;
            case R.id.btn_other:
                Intent lightIntent = new Intent(MainActivity.this, LightActivity.class);
                startActivity(lightIntent);
                break;

        }
    }
}
