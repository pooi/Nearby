package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.obj.NearbyLog;
import cf.nearby.nearby.obj.PatientMedicineDetail;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class InquiryLogActivity extends BaseActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_SYMPTOM_LIST = 500;
    private final int MSG_MESSAGE_MAKE_MEDICINE_LIST = 501;
    private final int MSG_MESSAGE_MAKE_RECORD_LIST = 502;
    private final int MSG_MESSAGE_MAKE_WEIGHT_LIST = 503;
    private final int MSG_MESSAGE_MAKE_EDIT_PATIENT_LIST = 504;
    private final int MSG_MESSAGE_MAKE_SUPPORTER_LIST = 505;

    LinearLayout li_list;
    TableLayout tl_symptom;
    TableLayout tl_medicine;
    TableLayout tl_record;
    TableLayout tl_weight;
    TableLayout tl_edit_patient;
    TableLayout tl_supporter;

    Button btn_detailSymptom;
    Button btn_detailMedicine;
    Button btn_detailRecord;
    Button btn_detailWeight;
    Button btn_detailEditPatient;
    Button btn_detailSupporter;

    String[] inquiryList = {
            "symptom",
            "medicine",
            "record",
            "weight",
            "edit_patient",
            "supporter"
    };

    private ArrayList<NearbyLog> symptomList;
    private ArrayList<NearbyLog> medicineList;
    private ArrayList<NearbyLog> recordList;
    private ArrayList<NearbyLog> weightList;
    private ArrayList<NearbyLog> editPatientList;
    private ArrayList<NearbyLog> supporterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_log);

        symptomList = new ArrayList<>();
        medicineList = new ArrayList<>();
        recordList = new ArrayList<>();
        weightList = new ArrayList<>();
        editPatientList = new ArrayList<>();
        supporterList = new ArrayList<>();

        init();

        getLogList();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        li_list = (LinearLayout)findViewById(R.id.li_list);

        tl_symptom = (TableLayout)findViewById(R.id.tl_symptom);
        tl_symptom.setTag(inquiryList[0]);
        tl_medicine = (TableLayout)findViewById(R.id.tl_medicine);
        tl_medicine.setTag(inquiryList[1]);
        tl_record = (TableLayout)findViewById(R.id.tl_record);
        tl_record.setTag(inquiryList[2]);
        tl_weight = (TableLayout)findViewById(R.id.tl_weight);
        tl_weight.setTag(inquiryList[3]);
        tl_edit_patient = (TableLayout)findViewById(R.id.tl_edit_patient);
        tl_edit_patient.setTag(inquiryList[4]);
        tl_supporter = (TableLayout)findViewById(R.id.tl_supporter);
        tl_supporter.setTag(inquiryList[5]);

        btn_detailSymptom = (Button)findViewById(R.id.btn_detail_symptom);
        btn_detailSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InquiryLogActivity.this, LogDetailActivity.class);
                intent.putExtra("type", "symptom");
                startActivity(intent);
            }
        });
        btn_detailMedicine = (Button)findViewById(R.id.btn_detail_medicine);
        btn_detailMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InquiryLogActivity.this, LogDetailActivity.class);
                intent.putExtra("type", "medicine");
                startActivity(intent);
            }
        });
        btn_detailRecord = (Button)findViewById(R.id.btn_detail_record);
        btn_detailRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InquiryLogActivity.this, LogDetailActivity.class);
                intent.putExtra("type", "record");
                startActivity(intent);
            }
        });
        btn_detailWeight = (Button)findViewById(R.id.btn_detail_weight);
        btn_detailWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InquiryLogActivity.this, LogDetailActivity.class);
                intent.putExtra("type", "weight");
                startActivity(intent);
            }
        });
        btn_detailEditPatient = (Button)findViewById(R.id.btn_detail_edit_patient);
        btn_detailEditPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InquiryLogActivity.this, LogDetailActivity.class);
                intent.putExtra("type", "edit_patient");
                startActivity(intent);
            }
        });
        btn_detailSupporter = (Button)findViewById(R.id.btn_detail_supporter);
        btn_detailSupporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InquiryLogActivity.this, LogDetailActivity.class);
                intent.putExtra("type", "supporter");
                startActivity(intent);
            }
        });


    }


    private void getLogList(){

        for(String type : inquiryList){

            final String tag = type;
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "inquiryLog");
//            map.put("isDate", "1");
//            map.put("start_date", Long.toString(date));
//            map.put("finish_date", Long.toString(date+86400000));
            map.put("type", type);
            map.put("location_id", StartActivity.employee.getLocation().getId());
            map.put("page", "0");

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    switch (tag){
                        case "symptom":
                            symptomList = NearbyLog.getLogList(data);
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_SYMPTOM_LIST));
                            break;
                        case "medicine":
                            medicineList = NearbyLog.getLogList(data);
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_MEDICINE_LIST));
                            break;
                        case "record":
                            recordList = NearbyLog.getLogList(data);
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_RECORD_LIST));
                            break;
                        case "weight":
                            weightList = NearbyLog.getLogList(data);
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_WEIGHT_LIST));
                            break;
                        case "edit_patient":
                            editPatientList = NearbyLog.getLogList(data);
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_EDIT_PATIENT_LIST));
                            break;
                        case "supporter":
                            supporterList = NearbyLog.getLogList(data);
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_SUPPORTER_LIST));
                            break;
                    }

                }
            }.start();

        }

    }

    private void makeList(String type){

        ArrayList<NearbyLog> list = new ArrayList<>();
        TableLayout tl = null;

        switch (type){
            case "symptom": {
                list = symptomList;
                tl = tl_symptom;
                break;
            }
            case "medicine": {
                list = medicineList;
                tl = tl_medicine;
                break;
            }
            case "record": {
                list = recordList;
                tl = tl_record;
                break;
            }
            case "weight": {
                list = weightList;
                tl = tl_weight;
                break;
            }
            case "edit_patient": {
                list = editPatientList;
                tl = tl_edit_patient;
                break;
            }
            case "supporter": {
                list = supporterList;
                tl = tl_supporter;
                break;
            }
        }

        if(tl != null){


            for(int i= Math.max(0, list.size()-5); i<list.size(); i++){
                NearbyLog log = list.get(i);

                TableLayout.LayoutParams tlps=new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                TableRow.LayoutParams trps=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView tv_log_msg = new TextView(this);
                tv_log_msg.setText(log.getMsg());
                tv_log_msg.setLayoutParams(trps);
                tv_log_msg.setTextColor(getColorId(R.color.dark_gray));
                tv_log_msg.setGravity(Gravity.CENTER);
                tv_log_msg.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                TextView tv_time = new TextView(this);
                tv_time.setText(AdditionalFunc.getDateTimeSrtString(log.getRegisteredDate()));
                tv_time.setLayoutParams(trps);
                tv_time.setTextColor(getColorId(R.color.dark_gray));
                tv_time.setGravity(Gravity.CENTER);
                tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_log_msg);
                tr.addView(tv_time);

                tl.addView(tr, tlps);

            }

        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_MAKE_SYMPTOM_LIST:
                    makeList("symptom");
                    break;
                case MSG_MESSAGE_MAKE_MEDICINE_LIST:
                    makeList("medicine");
                    break;
                case MSG_MESSAGE_MAKE_RECORD_LIST:
                    makeList("record");
                    break;
                case MSG_MESSAGE_MAKE_WEIGHT_LIST:
                    makeList("weight");
                    break;
                case MSG_MESSAGE_MAKE_EDIT_PATIENT_LIST:
                    makeList("edit_patient");
                    break;
                case MSG_MESSAGE_MAKE_SUPPORTER_LIST:
                    makeList("supporter");
                    break;
//                case MSG_MESSAGE_MARK_FAIL:
//                    new MaterialDialog.Builder(InquiryLogActivity.this)
//                            .title(R.string.fail_srt)
//                            .positiveText(R.string.ok)
//                            .show();
//                    break;
                default:
                    break;
            }
        }
    }

}
