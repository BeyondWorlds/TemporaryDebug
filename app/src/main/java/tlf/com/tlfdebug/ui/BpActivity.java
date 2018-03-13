package tlf.com.tlfdebug.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tlf.keep.callback.ISerialportDataCallback;
import com.tlf.keep.controller.MainSerialportController;
import com.tlf.keep.value.Actions;
import com.tlf.keep.value.StateType;

import java.util.Map;

import tlf.com.tlfdebug.R;
import tlf.com.tlfdebug.tool.ShowTool;

public class BpActivity extends BaseActivity implements View.OnClickListener {
    private MainSerialportController mSerialportController;

    private TextView tv_bp_content;
    private Button btn_bp_power_mode;
    private Button btn_bp_start;
    private Button btn_bp_stop;
    private Button btn_bp_status;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp);
        initView();
        initData();
    }

    private void initView() {
        tv_bp_content = (TextView) findViewById(R.id.tv_bp_content);
        btn_bp_power_mode = (Button) findViewById(R.id.btn_bp_power_mode);
        btn_bp_start = (Button) findViewById(R.id.btn_bp_start);
        btn_bp_stop = (Button) findViewById(R.id.btn_bp_stop);
        btn_bp_status = (Button) findViewById(R.id.btn_bp_status);

        btn_bp_power_mode.setOnClickListener(this);
        btn_bp_start.setOnClickListener(this);
        btn_bp_stop.setOnClickListener(this);
        btn_bp_status.setOnClickListener(this);

    }

    private void initData() {
        mSerialportController = MainSerialportController.getInstance(new BpCallback());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSerialportController.action(Actions.ACTION_SERIAL_BP_OPEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShowTool.clear();
        tv_bp_content.setText("");
        mSerialportController.action(Actions.ACTION_SERIAL_BP_CLOSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSerialportController.action(Actions.ACTION_RELEASE_DATA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bp_power_mode:
                mSerialportController.action(Actions.ACTION_SERIAL_BP_RESET);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mSerialportController.action(Actions.ACTION_BP_START);
//                    }
//                }, 500);
                break;
            case R.id.btn_bp_start:
                mSerialportController.action(Actions.ACTION_BP_START);
                break;
            case R.id.btn_bp_stop:
                mSerialportController.action(Actions.ACTION_BP_STOP);
                break;
            case R.id.btn_bp_status:
                mSerialportController.action(Actions.ACTION_BP_STATUS);
                break;
        }

    }

    private String status = "";
    private String result = "";

    private class BpCallback implements ISerialportDataCallback {
        @Override
        public void onReceive(int state) {
            switch (state) {
                //拒绝执行此命令
                case StateType.BP_REFUSE_EXECUTION:
                    status = "拒绝执行此命令";
                    break;
                //复位
                case StateType.BP_POWER_ON:
                    status = "复位";
                    break;
                //进入待机模式
                case StateType.BP_ENTER_STANDBY_MODE:
                    status = "进入待机模式";
                    break;
                //进入测量模式
                case StateType.BP_ENTER_MEASURE_MODE:
                    status = "进入测量模式";
                    break;
                //血压计正常
                case StateType.BP_NORMAL:
                    status = "血压计正常";
                    break;
                //测量完成
                case StateType.BP_MEASURE_FINISHED:
                    status = "测量完成";
                    break;
                //正在测量
                case StateType.BP_MEASURE_RUNNING:
                    status = "正在测量";
                    break;
                //系统异常
                case StateType.BP_SYSTEM_EXCEPTION:
                    status = "系统异常";
                    break;
                //脉搏信号微弱
                case StateType.BP_WEAK_PULSE_SIGNAL:
                    status = "脉搏信号微弱";
                    break;
                //杂讯太大导致脉搏信号弱
                case StateType.BP_WEAK_PULSE_SIGNAL_BY_NOISE:
                    status = "杂讯太大导致脉搏信号弱";
                    break;
                //测量超出正常范围
                case StateType.BP_OUT_OF_RANGE:
                    status = "测量超出正常范围";
                    break;
                case StateType.BP_OUT_OF_PRESSURE:
                    status = "压力过高";
                    break;
                //电压过低
                case StateType.BP_VOLTAGE_LOW:
                    status = "电压过低";
                    break;
                //袖带异常，请佩戴好袖带
                case StateType.BP_CUFF_EXCEPTION:
                    status = "袖带异常，请佩戴好袖带";
                    break;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv_bp_content.setText(ShowTool.getLinkString(status));
                }
            });
        }


        @Override
        public void onReceive(Map map) {
            if (map.get(StateType.DATA_TYPE).toString().equals("" + StateType.TYPE_BP_PRESSURE)) {
                final String pre = map.get(StateType.BP_PRESSURE).toString();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_bp_content.setText(ShowTool.getLinkString(pre));
                    }
                });
                return;
            }

            int type = Integer.valueOf((String) map.get(StateType.BP_IS_IRREGULAR_HEARTBEAT));
            if (type == 0) {
                result = "测量结束，结果为：" + "收缩压：" + map.get(StateType.BP_SYSTOLIC_PRESSURE) + "  舒张压:" + map.get(StateType.BP_DIASTOLIC_PRESSURE) +
                        "   心率：" + map.get(StateType.BP_HEART_RATE) + "   无心率不齐";
            } else {
                result = "测量结束，结果为：" + "收缩压：" + map.get(StateType.BP_SYSTOLIC_PRESSURE) + "  舒张压:" + map.get(StateType.BP_DIASTOLIC_PRESSURE) +
                        "   心率：" + map.get(StateType.BP_HEART_RATE) + "   心率不齐";
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv_bp_content.setText(ShowTool.getLinkString(result));
                }
            });
        }
    }

    ;

}
