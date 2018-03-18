package tlf.com.tlfdebug.ui;

import android.os.Handler;
import android.os.Vehicle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tlf.keep.callback.ISerialportDataCallback;
import com.tlf.keep.controller.MainSerialportController;

import java.util.Map;

import tlf.com.tlfdebug.R;
import tlf.com.tlfdebug.tool.ShowTool;

public class LightActivity extends AppCompatActivity implements View.OnClickListener {
    private MainSerialportController mSerialportController;
    private Vehicle mVehicle;
    private Button btn_infrared, btn_env, btn_key_on, btn_key_off, btn_orange_on, btn_orange_off,
            btn_green_on, btn_green_off, btn_blue_on, btn_blue_off, btn_infrared_loop,
            btn_infrared_loop_cancel;
    private TextView tv_state_content;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
//        mSerialportController = MainSerialportController.getInstance(new ISerialportDataCallback() {
//            @Override
//            public void onReceive(Map map) {
//
//            }
//
//            @Override
//            public void onReceive(int i) {
//
//            }
//        });
//        mVehicle = mSerialportController.getVehicle();
        mVehicle = new Vehicle();
        initView();
    }

    private void initView() {
        btn_infrared = findViewById(R.id.btn_infrared);
        btn_infrared_loop = findViewById(R.id.btn_infrared_loop);
        btn_infrared_loop_cancel = findViewById(R.id.btn_infrared_loop_cancel);
        btn_env = findViewById(R.id.btn_env);
        btn_key_on = findViewById(R.id.btn_key_on);
        btn_key_off = findViewById(R.id.btn_key_off);
        btn_orange_on = findViewById(R.id.btn_orange_on);
        btn_orange_off = findViewById(R.id.btn_orange_off);
        btn_green_on = findViewById(R.id.btn_green_on);
        btn_green_off = findViewById(R.id.btn_green_off);
        btn_blue_on = findViewById(R.id.btn_blue_on);
        btn_blue_off = findViewById(R.id.btn_blue_off);
        btn_infrared.setOnClickListener(this);
        btn_infrared_loop.setOnClickListener(this);
        btn_infrared_loop_cancel.setOnClickListener(this);
        btn_env.setOnClickListener(this);
        btn_key_on.setOnClickListener(this);
        btn_key_off.setOnClickListener(this);
        btn_orange_on.setOnClickListener(this);
        btn_orange_off.setOnClickListener(this);
        btn_green_on.setOnClickListener(this);
        btn_green_off.setOnClickListener(this);
        btn_blue_on.setOnClickListener(this);
        btn_blue_off.setOnClickListener(this);
        tv_state_content = findViewById(R.id.tv_state_content);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_infrared:
                getInfraredState();
                break;
            case R.id.btn_infrared_loop:
                startCheckInfrared();
                break;
            case R.id.btn_infrared_loop_cancel:
                mHandler.removeMessages(0);
                break;
            case R.id.btn_env:
                tv_state_content.setText(ShowTool.getLinkString("环境状态1= " + mVehicle.getAir1() +
                        "  环境状态2=" + mVehicle.getAir2()));
                break;
            case R.id.btn_key_on:
                mVehicle.KeyLedOn();
                break;
            case R.id.btn_key_off:
                mVehicle.KeyLedOff();
                break;
            case R.id.btn_orange_on:
                mVehicle.OrangeLedOn();
                break;
            case R.id.btn_orange_off:
                mVehicle.OrangeLedOff();
                break;
            case R.id.btn_green_on:
                mVehicle.GreenLedOn();
                break;
            case R.id.btn_green_off:
                mVehicle.GreenLedOff();
                break;
            case R.id.btn_blue_on:
                mVehicle.BlueLedOn();
                break;
            case R.id.btn_blue_off:
                mVehicle.BlueLedOff();
                break;
        }
    }

    private void startCheckInfrared() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getInfraredState();
                startCheckInfrared();
            }
        }, 3000);
    }

    private void getInfraredState() {
        int state = mVehicle.getHuman();
        if (state == 0) {
            mVehicle.KeyLedOff();
        } else {
            mVehicle.KeyLedOn();
        }
        tv_state_content.setText(ShowTool.getLinkString("红外状态" + state));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeMessages(0);
    }
}
