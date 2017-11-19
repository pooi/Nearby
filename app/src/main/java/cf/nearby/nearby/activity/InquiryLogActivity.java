package cf.nearby.nearby.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.obj.NearbyLog;
import cf.nearby.nearby.util.ParsePHP;

public class InquiryLogActivity extends AppCompatActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_SYMPTOM_LIST = 500;
    private final int MSG_MESSAGE_MAKE_MEDICINE_LIST = 501;
    private final int MSG_MESSAGE_MAKE_RECORD_LIST = 502;
    private final int MSG_MESSAGE_MAKE_WEIGHT_LIST = 503;

    LinearLayout li_list;
    TableLayout tl_symptom;
    TableLayout tl_medicine;
    TableLayout tl_record;
    TableLayout tl_weight;

    String[] inquiryList = {
            "symptom",
            "medicine",
            "record",
            "weight"
    };

    private ArrayList<NearbyLog> symptomList;
    private ArrayList<NearbyLog> medicineList;
    private ArrayList<NearbyLog> recordList;
    private ArrayList<NearbyLog> weightList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_log);

        symptomList = new ArrayList<>();
        medicineList = new ArrayList<>();
        recordList = new ArrayList<>();
        weightList = new ArrayList<>();

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
                    }

                }
            }.start();

        }

    }

    private void makeList(String type){

        ArrayList<NearbyLog> list = new ArrayList<>();

        switch (type){
            case "symptom": {
                list = symptomList;
                break;
            }
            case "medicine": {
                list = medicineList;
                break;
            }
            case "record": {
                list = recordList;
                break;
            }
            case "weight": {
                list = weightList;
                break;
            }
        }

        for(NearbyLog log : list){

            System.out.println(log.getMsg());

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
