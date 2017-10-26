package cf.nearby.nearby.activity;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.view.LineChartView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientMedicine;
import cf.nearby.nearby.obj.PatientWeight;
import cf.nearby.nearby.util.LogManager;
import cf.nearby.nearby.util.ParsePHP;

public class ManageWeightActivity extends BaseActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_SUCCESS = 504;
    private final int MSG_MESSAGE_FAIL = 505;

    private MaterialDialog progressDialog;
    private AVLoadingIndicatorView loading;
    private TextView tv_msg;

    private RelativeLayout rl_graph;
    private MaterialEditText edit_weight;
    private CardView editWeightBtn;

    // Chart
    private LineChartView mChart;

    private ArrayList<PatientWeight> list;
    private Patient selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_weight);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");

        list = new ArrayList<>();

        init();

        getPatientWeightList();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        loading = (AVLoadingIndicatorView)findViewById(R.id.loading);
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

        rl_graph = (RelativeLayout)findViewById(R.id.rl_graph);
        edit_weight = (MaterialEditText)findViewById(R.id.edit_weight);
        editWeightBtn = (CardView)findViewById(R.id.cv_edit_weight);
        editWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });

        edit_weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                changeEditBtn(edit_weight.isCharactersCountValid());
            }
        });

        changeEditBtn(false);

    }

    private void changeEditBtn(boolean status){
        editWeightBtn.setEnabled(status);
        if(status){
            editWeightBtn.setCardBackgroundColor(getColorId(R.color.pastel_green));
        }else{
            editWeightBtn.setCardBackgroundColor(getColorId(R.color.dark_gray));
        }
    }

    private void edit(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "savePatientWeight");
        map.put("patient_id", selectedPatient.getId());
        map.put("weight", edit_weight.getText().toString());

        progressDialog.show();

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {

                try {
                    JSONObject jObj = new JSONObject(data);
                    String status = jObj.getString("status");

                    if("success".equals(status)){
                        new LogManager(ManageWeightActivity.this)
                                .buildWeightMsg(selectedPatient, edit_weight.getText().toString())
                                .record();

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));
                    }else{
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                }

            }
        }.start();

    }

    private void getPatientWeightList(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "getPatientWeightList");
        map.put("patient_id", selectedPatient.getId());

        loading.show();

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {

                list.clear();

                list = PatientWeight.getPatientWeightList(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));

            }
        }.start();

    }


    private void makeList(){

        if(list.size() > 0){
            tv_msg.setVisibility(View.GONE);

            mChart = (LineChartView)findViewById(R.id.chart_weight);
            mChart.reset();

            LineSet dataset = new LineSet();
            for(int i=Math.max(0, list.size()-6); i<list.size(); i++){
                PatientWeight pw = list.get(i);
                dataset.addPoint(pw.getRegisteredDateStringSrt(), (float)pw.getWeight());
            }
            dataset.setColor(Color.parseColor("#53c1bd"))
                    .setFill(Color.parseColor("#3d6c73"))
                    .setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")},
                            null);
            mChart.addData(dataset);

            dataset = new LineSet();
            for(int i=Math.max(0, list.size()-6); i<list.size(); i++){
                PatientWeight pw = list.get(i);
                dataset.addPoint(pw.getRegisteredDateStringSrt(), (float)pw.getWeight());
            }
            dataset.setColor(Color.parseColor("#b3b5bb"))
                    .setFill(Color.parseColor("#2d374c"))
                    .setDotsColor(Color.parseColor("#ffc755"))
                    .setThickness(4);
            mChart.addData(dataset);

            mChart.setAxisBorderValues(PatientWeight.getListMinValue(list) - 5, PatientWeight.getListMaxValue(list) + 5);

            mChart.show();

        }else{
            tv_msg.setVisibility(View.VISIBLE);
            setFadeInAnimation(tv_msg);
        }


//        mChart.show(new Animation().withEndAction(action));

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_MAKE_LIST:
                    progressDialog.hide();
                    loading.hide();
                    makeList();
                    break;
                case MSG_MESSAGE_PROGRESS_HIDE:
                    progressDialog.hide();
                    loading.hide();
                    break;
                case MSG_MESSAGE_SUCCESS:
                    loading.hide();
                    progressDialog.hide();
                    getPatientWeightList();
                    edit_weight.setText("");
                    editWeightBtn.setEnabled(false);
                    break;
                case MSG_MESSAGE_FAIL:
                    loading.hide();
                    progressDialog.hide();
                    new MaterialDialog.Builder(ManageWeightActivity.this)
                            .title(R.string.fail_srt)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
