package tlf.com.tlfdebug.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tlf.keep.callback.BluetoothDataCallback;
import com.tlf.keep.controller.MainBluetoothController;
import com.tlf.keep.value.Actions;
import com.tlf.keep.value.BluetoothDataPackage;
import com.tlf.keep.value.BluetoothDataType;

import java.util.HashMap;

import tlf.com.tlfdebug.R;

public class TempActivity extends BaseBleActivity {
    private boolean isConnected = false;
    private MainBluetoothController mBluetoothController;
    private String mAddress;

    private LinearLayout ll_temp_state;
    private TextView tv_temp_address;
    private TextView tv_temp_status;
    private TextView tv_temp_data;
    private Button btn_temp_conn;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        initView();
        initData();
    }

    private void initView() {
        ll_temp_state = (LinearLayout) findViewById(R.id.ll_temp_state);
        tv_temp_address = (TextView) findViewById(R.id.tv_temp_address);
        tv_temp_status = (TextView) findViewById(R.id.tv_temp_status);
        tv_temp_data = (TextView) findViewById(R.id.tv_temp_data);
        btn_temp_conn = (Button) findViewById(R.id.btn_temp_conn);
        btn_temp_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected) {
                    mBluetoothController.action(Actions.ACTION_TEMP_CONN, mAddress);
                    tv_temp_status.setText("正在连接设备...");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isConnected) {
                                tv_temp_status.setText("设备连接失败");
                            }
                        }
                    }, 10000);
                } else {
                    mBluetoothController.action(Actions.ACTION_TEMP_DISCONN, mAddress);
                }
            }
        });
    }

    private BluetoothDataPackage mDataPackage;

    private void initData() {
        mBluetoothController = MainBluetoothController.getInstance(new BluetoothDataCallback() {
            @Override
            public void onReceive(Object data) {
                if (data == null || !(data instanceof BluetoothDataPackage)) return;
                mDataPackage = (BluetoothDataPackage) data;
                switch (mDataPackage.getType()) {
                    case BluetoothDataType.TYPE_TEMP_FIND_DEVICE:
                        HashMap<String, Object> map = (HashMap<String, Object>) mDataPackage.getData();
                        BluetoothDevice device = (BluetoothDevice) map.get("device");
                        String name = device.getName();
                        Log.e("test", "name=" + name + "   address=" + device.getAddress());
//                        if ("JK_FR".equals(name)) {
                        if (!isConnected) {
                            mAddress = device.getAddress();
                            tv_temp_address.setText(mAddress);
                            tv_temp_status.setText("发现设备，请连接设备");
                            ll_temp_state.setVisibility(View.VISIBLE);
                        }
//                        }
                        break;
                    case BluetoothDataType.TYPE_TEMP_CONN_DATA:
                        break;
                    //物体温度
                    case BluetoothDataType.TYPE_TEMP_OBJECT_DATA:
                        int tempObject = (int) (mDataPackage.getData());
                        tv_temp_data.setText(String.format("物体温度为： %.1f", (float) tempObject / 100));
                        break;
                    //体温
                    case BluetoothDataType.TYPE_TEMP_MAN_DATA:
                        int tempMan = (int) (mDataPackage.getData());
                        tv_temp_data.setText(String.format("体温为： %.1f", (float) tempMan / 100));
                        break;
                    //环境温度
                    case BluetoothDataType.TYPE_TEMP_ENV_DATA:
                        tv_temp_data.setText("");
                        break;
                    //温度过低
                    case BluetoothDataType.TYPE_TEMP_LOW_DATA:
                        tv_temp_data.setText("测量错误，温度过低");//测量结果异常
                        break;
                    //温度过高
                    case BluetoothDataType.TYPE_TEMP_HIGH_DATA:
                        tv_temp_data.setText("测量错误，温度过高");
                        break;
                    case BluetoothDataType.TYPE_TEMP_CONNECTED:
                        isConnected = true;
                        ll_temp_state.setVisibility(View.VISIBLE);
                        tv_temp_status.setText("设备已连接，请使用体温设备");
                        btn_temp_conn.setText("断开连接");
                        break;
                    case BluetoothDataType.TYPE_TEMP_DISCONNECTED:
                        isConnected = false;
                        ll_temp_state.setVisibility(View.INVISIBLE);
                        tv_temp_status.setText("设备已断开连接");
                        btn_temp_conn.setText("连接设备");
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBluetoothController.action(Actions.ACTION_TEMP_FIND, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothController.action(Actions.ACTION_TEMP_STOP, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothController.action(Actions.ACTION_RELEASE_DATA, null);
    }
}

