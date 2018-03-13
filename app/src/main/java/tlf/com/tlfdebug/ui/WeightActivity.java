package tlf.com.tlfdebug.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.tlf.keep.callback.BluetoothDataCallback;
import com.tlf.keep.controller.MainBluetoothController;
import com.tlf.keep.value.Actions;
import com.tlf.keep.value.BluetoothDataPackage;
import com.tlf.keep.value.BluetoothDataType;

import java.util.HashMap;

import tlf.com.tlfdebug.R;

public class WeightActivity extends BaseBleActivity {
    private MainBluetoothController mBluetoothController;
    private TextView tv_weight_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        initData();
        tv_weight_content = (TextView) findViewById(R.id.tv_weight_content);
    }

    private BluetoothDataPackage mDataPackage;

    private void initData() {
        mBluetoothController = MainBluetoothController.getInstance(new BluetoothDataCallback() {
            @Override
            public void onReceive(Object data) {
                mDataPackage = (BluetoothDataPackage) data;
                if (mDataPackage.getType() == BluetoothDataType.TYPE_WEIGHT_WEIGHT_RESULT) {
                    HashMap<String, Object> map = (HashMap<String, Object>) mDataPackage.getData();
                    int weight = (int) map.get("weight");
                    String result = String.format("体重为：%.1f", (float) weight / 10);
                    tv_weight_content.setText(result);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBluetoothController.action(Actions.ACTION_WEIGHT_START, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothController.action(Actions.ACTION_WEIGHT_STOP, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothController.action(Actions.ACTION_RELEASE_DATA,null);
    }
}
