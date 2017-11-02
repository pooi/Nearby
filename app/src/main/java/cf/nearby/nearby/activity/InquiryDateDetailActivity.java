package cf.nearby.nearby.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.obj.HaveMeal;
import cf.nearby.nearby.obj.MainRecord;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.obj.VitalSign;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class InquiryDateDetailActivity extends BaseActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_VITAL_LIST = 500;
    private final int MSG_MESSAGE_MAKE_MEDICINE_LIST = 501;
    private final int MSG_MESSAGE_MAKE_MEAL_LIST = 502;
    private final int MSG_MESSAGE_MAKE_REMARK_LIST = 503;
    private final int MSG_MESSAGE_HIDE_VITAL = 504;
    private final int MSG_MESSAGE_HIDE_MEDICINE = 505;
    private final int MSG_MESSAGE_HIDE_MEAL = 506;
    private final int MSG_MESSAGE_HIDE_REMARK = 507;

    private TextView toolbarTitle;

    private CardView cv_vital;
    private CardView cv_medicine;
    private CardView cv_meal;
    private CardView cv_remark;

    private TableLayout tl_vital;
    private TableLayout tl_medicine;
    private TableLayout tl_meal;
    private TableLayout tl_remark;

    private TextView tv_msg_vital;
    private TextView tv_msg_medicine;
    private TextView tv_msg_meal;
    private TextView tv_msg_remark;

    private AVLoadingIndicatorView loading_vital;
    private AVLoadingIndicatorView loading_medicine;
    private AVLoadingIndicatorView loading_meal;
    private AVLoadingIndicatorView loading_remark;

    private ArrayList<VitalSign> vitalSigns;
    private ArrayList<TakeMedicine> takeMedicines;
    private ArrayList<HaveMeal> haveMeals;
    private ArrayList<PatientRemark> remarks;

    private Patient selectedPatient;
    long date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_date_detail);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        date = getIntent().getLongExtra("date", 0);
        vitalSigns = new ArrayList<>();
        takeMedicines = new ArrayList<>();
        haveMeals = new ArrayList<>();
        remarks = new ArrayList<>();

        init();

        getVitalSignList();
        getTakeMedicineList();
        getHaveMealList();
        getRemarkList();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText(AdditionalFunc.getDateString(date));

        cv_vital = (CardView)findViewById(R.id.cv_vital);
        cv_medicine = (CardView)findViewById(R.id.cv_medicine);
        cv_meal = (CardView)findViewById(R.id.cv_meal);
        cv_remark = (CardView)findViewById(R.id.cv_remark);

        tl_vital = (TableLayout)findViewById(R.id.tl_vital);
        tl_medicine = (TableLayout)findViewById(R.id.tl_medicine);
        tl_meal = (TableLayout)findViewById(R.id.tl_meal);
        tl_remark = (TableLayout)findViewById(R.id.tl_remark);

        tv_msg_vital = (TextView)findViewById(R.id.tv_msg_vital);
        tv_msg_medicine = (TextView)findViewById(R.id.tv_msg_medicine);
        tv_msg_meal = (TextView)findViewById(R.id.tv_msg_meal);
        tv_msg_remark = (TextView)findViewById(R.id.tv_msg_remark);

        loading_vital = (AVLoadingIndicatorView)findViewById(R.id.loading_vital);
        loading_medicine = (AVLoadingIndicatorView)findViewById(R.id.loading_medicine);
        loading_meal = (AVLoadingIndicatorView)findViewById(R.id.loading_meal);
        loading_remark = (AVLoadingIndicatorView)findViewById(R.id.loading_remark);

    }

    private void getVitalSignList(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "inquiryPatientVitalSign");
        map.put("isDate", "1");
        map.put("start_date", Long.toString(date));
        map.put("finish_date", Long.toString(date+86400000));
        map.put("patient_id", selectedPatient.getId());

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                vitalSigns = VitalSign.getVitalSignList(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_VITAL_LIST));

            }
        }.start();

    }

    private void getTakeMedicineList(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "inquiryPatientMedicine");
        map.put("isDate", "1");
        map.put("start_date", Long.toString(date));
        map.put("finish_date", Long.toString(date+86400000));
        map.put("patient_id", selectedPatient.getId());

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                takeMedicines = TakeMedicine.getTakeMedicineList(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_MEDICINE_LIST));

            }
        }.start();

    }

    private void getHaveMealList(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "inquiryPatientMeal");
        map.put("isDate", "1");
        map.put("start_date", Long.toString(date));
        map.put("finish_date", Long.toString(date+86400000));
        map.put("patient_id", selectedPatient.getId());

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                haveMeals = HaveMeal.getHaveMealList(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_MEAL_LIST));

            }
        }.start();

    }

    private void getRemarkList(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "inquiryPatientRemarks");
        map.put("isDate", "1");
        map.put("start_date", Long.toString(date));
        map.put("finish_date", Long.toString(date+86400000));
        map.put("patient_id", selectedPatient.getId());

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                remarks = PatientRemark.getPatientRemarkList(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_REMARK_LIST));

            }
        }.start();

    }

    private void makeVitalList(){

        if(vitalSigns.size() > 0){
            tv_msg_vital.setVisibility(View.GONE);
        }else{
            tv_msg_vital.setVisibility(View.VISIBLE);
        }

        for(VitalSign vs : vitalSigns){

            TableLayout.LayoutParams tlps=new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams trps=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

            if(vs.getPulse() != 0){

                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView tv_type = new TextView(this);
                tv_type.setText(R.string.pulse_srt);
                tv_type.setLayoutParams(trps);
                tv_type.setTextColor(getColorId(R.color.dark_gray));
                tv_type.setGravity(Gravity.CENTER);
                tv_type.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_type);

                TextView tv_title = new TextView(this);
                tv_title.setText(vs.getPulse() + "");
                tv_title.setLayoutParams(trps);
                tv_title.setTextColor(getColorId(R.color.dark_gray));
                tv_title.setGravity(Gravity.CENTER);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_title);


                TextView tv_time = new TextView(this);
                tv_time.setText(AdditionalFunc.getTimeString(vs.getRegisteredDate()));
                tv_time.setLayoutParams(trps);
                tv_time.setTextColor(getColorId(R.color.dark_gray));
                tv_time.setGravity(Gravity.CENTER);
                tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_time);

                tl_vital.addView(tr, tlps);
            }

            if(vs.getTemperature() != 0){

                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView tv_type = new TextView(this);
                tv_type.setText(R.string.temperature_srt);
                tv_type.setLayoutParams(trps);
                tv_type.setTextColor(getColorId(R.color.dark_gray));
                tv_type.setGravity(Gravity.CENTER);
                tv_type.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_type);

                TextView tv_title = new TextView(this);
                tv_title.setText(vs.getTemperature() + "");
                tv_title.setLayoutParams(trps);
                tv_title.setTextColor(getColorId(R.color.dark_gray));
                tv_title.setGravity(Gravity.CENTER);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_title);


                TextView tv_time = new TextView(this);
                tv_time.setText(AdditionalFunc.getTimeString(vs.getRegisteredDate()));
                tv_time.setLayoutParams(trps);
                tv_time.setTextColor(getColorId(R.color.dark_gray));
                tv_time.setGravity(Gravity.CENTER);
                tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_time);

                tl_vital.addView(tr, tlps);
            }

            if(vs.getBpMax() != 0){

                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView tv_type = new TextView(this);
                tv_type.setText(R.string.blood_pressure_srt);
                tv_type.setLayoutParams(trps);
                tv_type.setTextColor(getColorId(R.color.dark_gray));
                tv_type.setGravity(Gravity.CENTER);
                tv_type.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_type);

                TextView tv_title = new TextView(this);
                tv_title.setText(vs.getBpMin() + " / " + vs.getBpMax());
                tv_title.setLayoutParams(trps);
                tv_title.setTextColor(getColorId(R.color.dark_gray));
                tv_title.setGravity(Gravity.CENTER);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_title);


                TextView tv_time = new TextView(this);
                tv_time.setText(AdditionalFunc.getTimeString(vs.getRegisteredDate()));
                tv_time.setLayoutParams(trps);
                tv_time.setTextColor(getColorId(R.color.dark_gray));
                tv_time.setGravity(Gravity.CENTER);
                tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_time);

                tl_vital.addView(tr, tlps);
            }


        }

    }
    private void makeMedicineList(){

        if(takeMedicines.size() > 0){
            tv_msg_medicine.setVisibility(View.GONE);
        }else{
            tv_msg_medicine.setVisibility(View.VISIBLE);
        }

        for(TakeMedicine tm : takeMedicines){
            TableLayout.LayoutParams tlps=new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams trps=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView tv_title = new TextView(this);
            tv_title.setText(tm.getTitle());
            tv_title.setLayoutParams(trps);
            tv_title.setTextColor(getColorId(R.color.dark_gray));
            tv_title.setGravity(Gravity.CENTER);
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.default_font_small_size));

            TextView tv_time = new TextView(this);
            tv_time.setText(AdditionalFunc.getTimeString(tm.getRegisteredDate()));
            tv_time.setLayoutParams(trps);
            tv_time.setTextColor(getColorId(R.color.dark_gray));
            tv_time.setGravity(Gravity.CENTER);
            tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.default_font_small_size));

            tr.addView(tv_title);
            tr.addView(tv_time);

            tl_medicine.addView(tr, tlps);
        }

    }
    private void makeMealList(){

        if(haveMeals.size() > 0){
            tv_msg_meal.setVisibility(View.GONE);
        }else{
            tv_msg_meal.setVisibility(View.VISIBLE);
        }

        for(HaveMeal hm : haveMeals){

            TableLayout.LayoutParams tlps=new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams trps=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView tv_title = new TextView(this);
            if("nursing_home".equals(hm.getType())){
                tv_title.setText(getString(R.string.nursing_home_meal_srt));
            }else if("water_gruel".equals(hm.getType())){
                tv_title.setText(getString(R.string.water_gruel));
            }else{
                tv_title.setText(hm.getType());
            }
            tv_title.setLayoutParams(trps);
            tv_title.setTextColor(getColorId(R.color.dark_gray));
            tv_title.setGravity(Gravity.CENTER);
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.default_font_small_size));

            TextView tv_time = new TextView(this);
            tv_time.setText(AdditionalFunc.getTimeString(hm.getRegisteredDate()));
            tv_time.setLayoutParams(trps);
            tv_time.setTextColor(getColorId(R.color.dark_gray));
            tv_time.setGravity(Gravity.CENTER);
            tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.default_font_small_size));

            tr.addView(tv_title);
            tr.addView(tv_time);

            tl_meal.addView(tr, tlps);
        }

    }
    private void makeRemarkList(){

        if(remarks.size() > 0){
            tv_msg_remark.setVisibility(View.GONE);
        }else{
            tv_msg_remark.setVisibility(View.VISIBLE);
        }

        for(PatientRemark pr : remarks){

            TableLayout.LayoutParams tlps=new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams trps=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView tv_title = new TextView(this);
            tv_title.setText(pr.getDescription());
            tv_title.setLayoutParams(trps);
            tv_title.setTextColor(getColorId(R.color.dark_gray));
            tv_title.setGravity(Gravity.CENTER);
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.default_font_small_size));

            TextView tv_time = new TextView(this);
            tv_time.setText(AdditionalFunc.getTimeString(pr.getRegisteredDate()));
            tv_time.setLayoutParams(trps);
            tv_time.setTextColor(getColorId(R.color.dark_gray));
            tv_time.setGravity(Gravity.CENTER);
            tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.default_font_small_size));

            tr.addView(tv_title);
            tr.addView(tv_time);

            tl_remark.addView(tr, tlps);
        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_MAKE_VITAL_LIST:
                    loading_vital.hide();
                    makeVitalList();
                    break;
                case MSG_MESSAGE_MAKE_MEDICINE_LIST:
                    loading_medicine.hide();
                    makeMedicineList();
                    break;
                case MSG_MESSAGE_MAKE_MEAL_LIST:
                    loading_meal.hide();
                    makeMealList();
                    break;
                case MSG_MESSAGE_MAKE_REMARK_LIST:
                    loading_remark.hide();
                    makeRemarkList();
                    break;
                default:
                    break;
            }
        }
    }

}
