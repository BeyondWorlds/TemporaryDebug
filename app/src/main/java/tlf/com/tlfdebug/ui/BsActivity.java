package tlf.com.tlfdebug.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.tlf.keep.callback.ISerialportDataCallback;
import com.tlf.keep.controller.MainSerialportController;
import com.tlf.keep.value.Actions;
import com.tlf.keep.value.StateType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import tlf.com.tlfdebug.R;
import tlf.com.tlfdebug.tool.ShowTool;

public class BsActivity extends BaseActivity {
    private MainSerialportController mSerialportController;
    private TextView tv_bs_content;
    private TextView tv_temp_humidity;
    private TextView tv_date;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs);
        initView();
        initData();
    }

    private void initView() {
        tv_bs_content = (TextView) findViewById(R.id.tv_bs_content);
        tv_temp_humidity = findViewById(R.id.tv_temp_humidity);
        tv_date = findViewById(R.id.tv_date);
    }

    private void initData() {
        mSerialportController = MainSerialportController.getInstance(new BsCallback());
        tv_bs_content.setText(ShowTool.getLinkString("设备正在初始化，请勿插入试纸"));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_bs_content.setText(ShowTool.getLinkString("设备初始化完成，请插入试纸"));
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSerialportController.action(Actions.ACTION_SERIAL_BS_OPEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShowTool.clear();
        tv_bs_content.setText("");
        tv_temp_humidity.setText("");
        mSerialportController.action(Actions.ACTION_SERIAL_BS_CLOSE);
    }

    private String data = "";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSerialportController.action(Actions.ACTION_RELEASE_DATA);
    }

    class BsCallback implements ISerialportDataCallback {
        @Override
        public void onReceive(Map map) {
            if (StateType.TYPE_TEMP_HUMIDITY_RESULT.equals(map.get(StateType.DATA_TYPE))) {
                final String temp = (String) map.get(StateType.BS_TEMP__RESULT);
                final String humidity = (String) map.get(StateType.BS_HUMIDITY__RESULT);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        tv_date.setText(date.format(new Date(System.currentTimeMillis())));
                        tv_temp_humidity.setText(ShowTool.getLinkTempString("温度：" + temp + "℃" + "  湿度" + humidity + "%"));
                    }
                });
                return;
            }
            data = "测量完成，结果为：" + map.get(StateType.BS_RESULT) + "mmol/l";
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv_bs_content.setText(ShowTool.getLinkString(data));
                }
            });
        }

        @Override
        public void onReceive(int state) {
            switch (state) {
                //进入待机模式
                case StateType.BS_ENTER_STANDBY_STATE:
                    data = "进入待机模式";
                    break;
                //开机自检异常
                case StateType.BS_BOOT_EXCEPTION:
                    data = "开机自检异常";
                    break;
                //试纸已插入，请滴血
                case StateType.BS_PAPER_INSERTED:
                    data = "试纸已插入，请滴血";
                    break;
                //试纸已过期
                case StateType.BS_PAPER_OVERDUE:
                    data = "试纸已过期，请更换试纸";
                    break;
                //正在测量
                case StateType.BS_MEASURING:
                    data = "正在测量中...";
                    break;
                //测量操作有误
                case StateType.BS_WRONG_OPERATION:
                    data = "滴血测量操作有误";
                    break;
                case 107:
                    data = "系统错误";
                    break;
                //试纸已拔出
                case StateType.BS_PAPER_PULLOUT:
                    data = "试纸已拔出";
                    break;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv_bs_content.setText(ShowTool.getLinkString(data));
                }
            });
        }
    }
}
