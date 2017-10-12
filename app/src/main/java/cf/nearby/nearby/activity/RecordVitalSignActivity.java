package cf.nearby.nearby.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

    private final int MEASUREMENT_TIME = 10000;

    private final String BLOOD_PRESSURE = "blood_pressure";
    private final String PULSE = "pulse";
    private final String TEMPERATURE = "temperature";

    private Button saveBtn;
    private RelativeLayout rl_bluetooth;

    private RelativeLayout rl_menu;
    private CardView cv_temperature;
    private CardView cv_pulse;
    private CardView cv_bp;

    private RelativeLayout rl_measurement;
    private TextView tv_time;
    private LinearLayout li_result;
    private ScrollView sc_result;

    private LinearLayout li_resultMsg;
    private TextView tv_resultMsg;
    private CardView cv_delMeasurement;
    private CardView cv_reMeasurement;
    private CardView cv_measurementSave;

    private VitalSign vitalSign;
    private ArrayList<Double> pulseList;
    private ArrayList<Double> tempuratureList;
    private int time;
    private boolean recordEnable;
    private String currentMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_vital_sign);

        vitalSign = (VitalSign)getIntent().getSerializableExtra("vital_sign");
        pulseList = new ArrayList<>();
        tempuratureList = new ArrayList<>();
        setInitMeasurementValue();

        init();

    }

    private void setInitMeasurementValue(){
        if(vitalSign.getPulse() != null){
            pulseList.add(vitalSign.getPulse());
        }
        if(vitalSign.getTemperature() != null){
            tempuratureList.add(vitalSign.getTemperature());
        }
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
            save();
            }
        });
//        saveBtn.setVisibility(View.GONE);

        rl_menu = (RelativeLayout)findViewById(R.id.rl_menu);
        cv_temperature = (CardView)findViewById(R.id.cv_temperature);
        cv_temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurementTemperature();
            }
        });
        cv_pulse = (CardView)findViewById(R.id.cv_pulse);
        cv_pulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurementPulse();
            }
        });
        cv_bp = (CardView)findViewById(R.id.cv_bp);

        rl_measurement = (RelativeLayout)findViewById(R.id.rl_measurement);
        tv_time = (TextView)findViewById(R.id.tv_time);
        li_result = (LinearLayout)findViewById(R.id.li_result);
        rl_bluetooth = (RelativeLayout)findViewById(R.id.rl_bluetooth);

        sc_result = (ScrollView)findViewById(R.id.sc_result);
        li_resultMsg = (LinearLayout)findViewById(R.id.li_result_msg);
        tv_resultMsg = (TextView)findViewById(R.id.tv_result_msg);
        cv_delMeasurement = (CardView)findViewById(R.id.cv_del_measurement);
        cv_delMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentMeasurement){
                    case TEMPERATURE:
                        tempuratureList.clear();
                        setScreen(false);
                        break;
                    case PULSE:
                        pulseList.clear();
                        setScreen(false);
                        break;
                    case BLOOD_PRESSURE:
                        break;
                }
            }
        });
        cv_reMeasurement = (CardView)findViewById(R.id.cv_re_measurement);
        cv_reMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentMeasurement){
                    case TEMPERATURE:
                        tempuratureList.clear();
                        measurementTemperature();
                        break;
                    case PULSE:
                        pulseList.clear();
                        measurementPulse();
                        break;
                    case BLOOD_PRESSURE:
                        break;
                }
            }
        });
        cv_measurementSave = (CardView)findViewById(R.id.cv_measurement_save);
        cv_measurementSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMeasurement = null;
                setScreen(false);
            }
        });

        setScreen(false);

    }

    private void initMeasurementData(){

        time = 0;
        recordEnable = true;

    }

    private void setScreen(boolean isMeasurement){

        if(isMeasurement){
            rl_menu.setVisibility(View.GONE);
            rl_measurement.setVisibility(View.VISIBLE);

            initMeasurementData();
            tv_time.setText("");
            li_result.removeAllViews();
            sc_result.setVisibility(View.VISIBLE);
            li_resultMsg.setVisibility(View.GONE);

//            saveBtn.setVisibility(View.GONE);
        }else{
            rl_menu.setVisibility(View.VISIBLE);
            rl_measurement.setVisibility(View.GONE);

            boolean isPulse = pulseList.size() > 0;
            changeBtnColor(cv_pulse, isPulse);

            boolean isTemper = tempuratureList.size() > 0;
            changeBtnColor(cv_temperature, isTemper);

//            boolean status = isPulse || isTemper;
//            if(status){
//                saveBtn.setVisibility(View.VISIBLE);
//            }else{
//                saveBtn.setVisibility(View.GONE);
//            }
        }

    }

    private void showMeasurementResult(String value){
        tv_time.setText("");
        tv_resultMsg.setText(value);
        sc_result.setVisibility(View.GONE);
        li_resultMsg.setVisibility(View.VISIBLE);
    }

    private void measurementPulse(){

        setScreen(true);
        currentMeasurement = PULSE;

        if(pulseList.isEmpty()){
            new Thread(){
                @Override
                public void run(){
                    try{
                        Thread.sleep(MEASUREMENT_TIME);
                        recordEnable = false;
                        Thread.sleep(1100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                double avg = getAverage(pulseList);
                                showMeasurementResult(avg+"");
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
                                    createNewData(pulseList);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }else{
            showMeasurementResult(getAverage(pulseList)+"");
        }

    }

    private void measurementTemperature(){

        setScreen(true);
        currentMeasurement = TEMPERATURE;

        if(tempuratureList.isEmpty()){
            new Thread(){
                @Override
                public void run(){
                    try{
                        Thread.sleep(MEASUREMENT_TIME);
                        recordEnable = false;
                        Thread.sleep(1100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                double avg = getAverage(tempuratureList);
                                showMeasurementResult(avg+"");
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
                                    createNewData(tempuratureList);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }else{
            showMeasurementResult(getAverage(tempuratureList)+"");
        }

    }

    private double getAverage(ArrayList<Double> list){
        double avg = 0.0;
        for(double d : list){
            avg += d;
        }
        avg /= list.size();
        return avg;
    }

    private void createNewData(ArrayList<Double> list){

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

    private void save(){

        if(pulseList.size() > 0){
            vitalSign.setPulse(getAverage(pulseList));
        }else{
            vitalSign.setPulse(null);
        }
        if(tempuratureList.size() > 0){
            vitalSign.setTemperature(getAverage(tempuratureList));
        }else{
            vitalSign.setTemperature(null);
        }
        Intent intent = new Intent();
        intent.putExtra("vital_sign", vitalSign);
        setResult(NurseRecordActivity.UPDATE_VITAL, intent);
        finish();

    }

    private void changeBtnColor(CardView cv, boolean check){

        if(check){
            cv.setCardBackgroundColor(getColorId(R.color.pastel_green));
            for(int i=0; i<cv.getChildCount(); i++){
                ViewGroup vg = (ViewGroup)cv.getChildAt(i);
                for(int j=0; j<vg.getChildCount(); j++){
                    View v = vg.getChildAt(j);
                    if(v instanceof TextView){
                        ((TextView) v).setTextColor(getColorId(R.color.white));
                    }
                }
            }
        }else{
            cv.setCardBackgroundColor(getColorId(R.color.white));
            for(int i=0; i<cv.getChildCount(); i++){
                ViewGroup vg = (ViewGroup)cv.getChildAt(i);
                for(int j=0; j<vg.getChildCount(); j++){
                    View v = vg.getChildAt(j);
                    if(v instanceof TextView){
                        ((TextView) v).setTextColor(getColorId(R.color.dark_gray));
                    }
                }
            }
        }

    }
}
