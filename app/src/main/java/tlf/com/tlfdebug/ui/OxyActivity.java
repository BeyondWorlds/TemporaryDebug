package tlf.com.tlfdebug.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tlf.keep.callback.BluetoothDataCallback;
import com.tlf.keep.controller.MainBluetoothController;
import com.tlf.keep.value.Actions;
import com.tlf.keep.value.BluetoothDataPackage;
import com.tlf.keep.value.BluetoothDataType;

import java.util.HashMap;

import tlf.com.tlfdebug.R;


public class OxyActivity extends BaseBleActivity {
    private static final String TAG = "<-OxyActivity->";
    private static final int MONITOR_SCAN = 0;
    private static final int CHECK_TIME = 6000;
    private MainBluetoothController mBluetoothController;
    private boolean isConnected = false;
    private String mAddress;
    private int mTimes = 0;
    private int mLastTimes = 0;
    private HashMap<String, Object> mOMap = new HashMap<String, Object>();

    private LinearLayout ll_ble_state;
    private TextView tv_address;
    private TextView tv_status;
    private TextView tv_time;
    private TextView tv_data;
    private Button btn_conn;
    private Button btn_conn_again;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MONITOR_SCAN) {
                if (mLastTimes != mTimes) {
                    mLastTimes = mTimes;
                    sendEmptyMessageDelayed(MONITOR_SCAN, CHECK_TIME);
                } else {
                    if (!isConnected) {
                        ll_ble_state.setVisibility(View.INVISIBLE);
                        tv_status.setText("未发现设备，请戴上血氧测试器");
                        mTimes = 0;
                        mLastTimes = 0;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxy);
        initView();
        initData();
    }

    private void initView() {
        ll_ble_state = (LinearLayout) findViewById(R.id.ll_ble_state);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_data = (TextView) findViewById(R.id.tv_data);
        btn_conn = (Button) findViewById(R.id.btn_conn);
        btn_conn_again = (Button) findViewById(R.id.btn_conn_again);
        btn_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected) {
                    mBluetoothController.action(Actions.ACTION_OXY_CONN, mAddress);
                } else {
                    mBluetoothController.action(Actions.ACTION_OXY_DISCONN, null);
                }
            }
        });
        btn_conn_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    tv_time.setVisibility(View.VISIBLE);
                    mTimer.start();
                } else {
                    Toast.makeText(OxyActivity.this, "请先连接设备", Toast.LENGTH_LONG).show();
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
                switch (((BluetoothDataPackage) data).getType()) {
                    case BluetoothDataType.TYPE_OXY_DEVICE_FIND:
                        HashMap<String, Object> map = (HashMap<String, Object>) mDataPackage.getData();
                        BluetoothDevice device = (BluetoothDevice) map.get("device");
                        String name = device.getName();
                        if ("POD".equals(name)) {
                            if (mTimes == 0) {
                                mAddress = device.getAddress();
                                tv_address.setText(mAddress);
                                ll_ble_state.setVisibility(View.VISIBLE);
                                tv_status.setText("发现设备，请点击连接设备");
                                mHandler.sendEmptyMessageDelayed(MONITOR_SCAN, CHECK_TIME);
                            }
                            mTimes++;
                        }
                        break;
                    case BluetoothDataType.TYPE_OXY_CONNECTED:
                        mHandler.removeMessages(MONITOR_SCAN);
                        tv_status.setText("设备已连接，正在测量数据");
                        isConnected = true;
                        btn_conn.setText("断开连接");
                        tv_time.setVisibility(View.VISIBLE);
                        mTimer.start();
                        break;
                    case BluetoothDataType.TYPE_OXY_DISCONNECTED:
                        mHandler.sendEmptyMessageDelayed(MONITOR_SCAN, CHECK_TIME);
                        tv_status.setText("设备已断开连接");
                        isConnected = false;
                        btn_conn.setText("连接设备");
                        tv_time.setVisibility(View.INVISIBLE);
                        mTimer.cancel();
                        break;
                    case BluetoothDataType.TYPE_OXY_PARAMS:
                        mOMap = (HashMap<String, Object>) mDataPackage.getData();
                        break;
                }
            }

        });

    }

    CountDownTimer mTimer = new CountDownTimer(21000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv_time.setText(millisUntilFinished / 1000 + "");
        }

        @Override
        public void onFinish() {
            int heart = (int) mOMap.get("nPR");
            int oxy = (int) mOMap.get("nSpO2");
            tv_data.setText("脉率：" + heart + "次/min" + "     血氧: " + oxy + "%");
            tv_time.setVisibility(View.INVISIBLE);
            float fPI = (float) mOMap.get("fPI");
            float nPower = (float) mOMap.get("nPower");
            Log.e(TAG, "血氧数据:" + oxy + "  脉率:" + heart + "  fPI:" + fPI + "  nPower:" + nPower);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mBluetoothController.action(Actions.ACTION_OXY_FIND, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothController.action(Actions.ACTION_OXY_STOP, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MONITOR_SCAN);
        isConnected = false;
        mTimes = 0;
        mLastTimes = 0;
        mBluetoothController.action(Actions.ACTION_RELEASE_DATA, null);
    }

}
