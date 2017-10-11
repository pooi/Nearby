package cf.nearby.nearby.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.VitalSign;

public class RecordVitalSignActivity extends BaseActivity {

    private Button saveBtn;
    private TextView tv_time;
    private LinearLayout li_result;
    private ScrollView sc_result;
    private RelativeLayout rl_bluetooth;

    private LinearLayout li_resultMsg;
    private TextView tv_resultMsg;

    private ArrayList<VitalSign> vitalSigns;
    private ArrayList<Double> list;
    private int time;
    private boolean recordEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_vital_sign);

        vitalSigns = (ArrayList<VitalSign>)getIntent().getSerializableExtra("vital_signs");
        list = new ArrayList<>();
        time = 0;
        recordEnable = true;

        init();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveBtn = (Button)findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VitalSign sign = new VitalSign();
                sign.setPulse(getAverage());
                vitalSigns.add(sign);
                Intent intent = new Intent();
                intent.putExtra("vital_signs", vitalSigns);
                setResult(NurseRecordActivity.UPDATE_VITAL, intent);
                finish();
            }
        });
        saveBtn.setVisibility(View.GONE);

        tv_time = (TextView)findViewById(R.id.tv_time);
        li_result = (LinearLayout)findViewById(R.id.li_result);
        rl_bluetooth = (RelativeLayout)findViewById(R.id.rl_bluetooth);

        sc_result = (ScrollView)findViewById(R.id.sc_result);
        li_resultMsg = (LinearLayout)findViewById(R.id.li_result_msg);
        tv_resultMsg = (TextView)findViewById(R.id.tv_result_msg);

        sc_result.setVisibility(View.VISIBLE);
        li_resultMsg.setVisibility(View.GONE);

        new Thread(){
            @Override
            public void run(){
                try{
                    Thread.sleep(30000);
                    recordEnable = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double avg = getAverage();
                            tv_resultMsg.setText(avg + "");

                            saveBtn.setVisibility(View.VISIBLE);
                            sc_result.setVisibility(View.GONE);
                            li_resultMsg.setVisibility(View.VISIBLE);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run(){
                while(recordEnable){
                    try{
                        Thread.sleep(1000);
                        time += 1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createNewData();
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private double getAverage(){
        double avg = 0.0;
        for(double d : list){
            avg += d;
        }
        avg /= list.size();
        return avg;
    }

    private void createNewData(){

        Random rand = new Random();
        double temp = rand.nextDouble();
        list.add(temp);

        TextView msg = new TextView(this);
        msg.setText(temp + "");
        msg.setTextColor(getColorId(R.color.dark_gray));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 0);
        params.gravity = Gravity.CENTER;
        msg.setLayoutParams(params);
        msg.setGravity(Gravity.CENTER);
        li_result.addView(msg, 0);

        tv_time.setText("경과시간 : " + time + "s");

    }
}
