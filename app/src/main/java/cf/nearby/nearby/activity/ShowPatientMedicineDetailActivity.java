package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ppamorim.dragger.DraggerPosition;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.obj.Medicine;
import cf.nearby.nearby.obj.MedicineDetail;
import cf.nearby.nearby.obj.PatientMedicine;
import cf.nearby.nearby.obj.PatientMedicineDetail;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class ShowPatientMedicineDetailActivity extends BaseActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_FILL_BASIC_FORM = 503;

    private AVLoadingIndicatorView loading;


    private TextView tv_title;
    private TextView tv_startDate;
    private TextView tv_finishDate;
    private LinearLayout li_medicineList;

    private PatientMedicine patientMedicine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient_medicine_detail);

        boolean flag;

        patientMedicine = (PatientMedicine)getIntent().getSerializableExtra("patient_medicine");
        flag = patientMedicine == null;
        if(flag){
            patientMedicine = new PatientMedicine();
            patientMedicine.setId(getIntent().getStringExtra("patient_medicine_id"));
            getPatientMedicineList();
        }

        init();

        if(!flag)
            getDetailList();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_startDate = (TextView)findViewById(R.id.tv_take_period_start);
        tv_finishDate = (TextView)findViewById(R.id.tv_take_period_finish);

        li_medicineList = (LinearLayout)findViewById(R.id.li_medicine_list);

        loading = (AVLoadingIndicatorView)findViewById(R.id.loading);

        fillBasicInfo();

    }

    private void fillBasicInfo(){

        tv_title.setText(patientMedicine.getTitle());
        tv_startDate.setText(AdditionalFunc.getDateString(patientMedicine.getStartDate()));
        tv_finishDate.setText(AdditionalFunc.getDateString(patientMedicine.getFinishDate()));

    }

    private void getPatientMedicineList(){
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getPatientMedicineListWithID");
            map.put("patient_medicine_id", patientMedicine.getId());

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    patientMedicine = PatientMedicine.getPatientMedicineList(data).get(0);
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FILL_BASIC_FORM));

                }
            }.start();
    }

    private void getDetailList(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "getPatientMedicineDetailList");
        map.put("patient_medicine_id", patientMedicine.getId());

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                patientMedicine.setDetails(PatientMedicineDetail.getPatientMedicineDetailList(data));

                if(patientMedicine.getDetails().size() > 0){
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                }else{
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_PROGRESS_HIDE));
                }

            }
        }.start();

    }

    private void buildMedicineList(){

        li_medicineList.removeAllViews();

        for(final PatientMedicineDetail detail : patientMedicine.getDetails()){

            View v = getLayoutInflater().inflate(R.layout.add_medicine_list_custom_item, null, false);

            TextView tv_name = (TextView)v.findViewById(R.id.tv_name);
            TextView tv_code = (TextView)v.findViewById(R.id.tv_code);
            TextView tv_info = (TextView)v.findViewById(R.id.tv_info);
            TextView tv_time = (TextView)v.findViewById(R.id.tv_time);
            ImageView img = (ImageView)v.findViewById(R.id.img);
            LinearLayout li_btn = (LinearLayout)v.findViewById(R.id.li_btn);
            li_btn.setVisibility(View.GONE);

            final Medicine medicine = detail.getMedicine();
            tv_name.setText(medicine.getNameSrt(15));
            tv_code.setText(medicine.getCode());
            tv_info.setText(detail.getSd() + "/" + detail.getNdd() + "/" + detail.getTdd() + "/" + detail.getDescription());
            tv_time.setText(detail.getTime());
            Picasso.with(getApplicationContext())
                    .load("http://nearby.cf/medicine/" + medicine.getCode() + ".jpg")
                    .into(img);

            setCardButtonOnTouchAnimation(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShowPatientMedicineDetailActivity.this, MedicineDetailActivity.class);
                    intent.putExtra("drag_position", DraggerPosition.TOP);
                    intent.putExtra("detail", new MedicineDetail(medicine.getCode(), medicine.getName(), medicine.getCompany(), "http://nearby.cf/medicine/" + medicine.getCode() + ".jpg"));
                    startActivity(intent);
                }
            });

            li_medicineList.addView(v);

        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MESSAGE_MAKE_LIST:
                    loading.hide();
                    loading.setVisibility(View.GONE);
                    buildMedicineList();
                    break;
                case MSG_MESSAGE_PROGRESS_HIDE:
                    loading.hide();
                    break;
                case MSG_MESSAGE_FILL_BASIC_FORM:
                    fillBasicInfo();
                    getDetailList();
                    break;
                default:
                    break;
            }
        }
    }



}
