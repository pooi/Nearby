package cf.nearby.nearby.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.obj.Medicine;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientMedicineDetail;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class AddPatientMedicineActivity extends BaseActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SUCCESS = 500;
    private final int MSG_MESSAGE_FAIL = 501;

    private MaterialEditText editTitle;
    private TextView startPeriodBtn;
    private TextView finishPeriodBtn;

    private CardView addMedicineBtn;
    private LinearLayout li_medicineList;

    private Button backBtn;
    private Button saveBtn;

    private long startDate;
    private long finishDate;
    private boolean isSelectStartDate;
    private boolean isSelectFinishDate;

    private Patient selectedPatient;
    private ArrayList<PatientMedicineDetail> list;

    private MaterialDialog progressDialog;

    // Custom Dialog
    private View positiveAction;
    private MaterialEditText form_sd;
    private MaterialEditText form_ndd;
    private MaterialEditText form_tdd;
    private MaterialEditText form_usage;
    private CheckBox cb_morning;
    private CheckBox cb_lunch;
    private CheckBox cb_evening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_medicine);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");

        list = new ArrayList<>();

        init();


    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .build();

        editTitle = (MaterialEditText)findViewById(R.id.edit_title);
        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkSaveBtn();
            }
        });
        startPeriodBtn = (TextView)findViewById(R.id.btn_take_period_start);
        finishPeriodBtn = (TextView)findViewById(R.id.btn_take_period_finish);
        addMedicineBtn = (CardView)findViewById(R.id.cv_add_medicine);
        addMedicineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPatientMedicineActivity.this, SearchMedicineActivity.class);
                startActivityForResult(intent, SearchMedicineActivity.SELECTED_MEDICINE);
            }
        });
        li_medicineList = (LinearLayout)findViewById(R.id.li_medicine_list);
        backBtn = (Button)findViewById(R.id.btn_back);
        saveBtn = (Button)findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        startPeriodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStartPeriod();
            }
        });
        finishPeriodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFinishPeriod();
            }
        });

    }

    private void selectStartPeriod(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        startDate = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                        isSelectStartDate = true;
                        setDateText(startPeriodBtn, AdditionalFunc.getDateString(startDate));
                        checkSaveBtn();
                        selectFinishPeriod();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle(getString(R.string.take_period_start_srt));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void selectFinishPeriod(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        finishDate = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                        isSelectFinishDate = true;
                        setDateText(finishPeriodBtn, AdditionalFunc.getDateString(finishDate));
                        checkSaveBtn();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle(getString(R.string.take_period_finish_srt));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void checkSaveBtn(){

        boolean isTitle = editTitle.isCharactersCountValid();
        boolean isList = list.size() > 0;


        boolean status = isTitle && isSelectStartDate && isSelectFinishDate && isList;

        saveBtn.setEnabled(status);

        if(status){
            saveBtn.setBackgroundColor(getColorId(R.color.colorPrimary));
        }else{
            saveBtn.setBackgroundColor(getColorId(R.color.dark_gray));
        }

    }

    private void save(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "save_patient_medicine");
        map.put("title", AdditionalFunc.replaceNewLineString(editTitle.getText().toString()));
        map.put("start_date", Long.toString(startDate));
        map.put("finish_date", Long.toString(finishDate));
        map.put("patient_id", selectedPatient.getId());
        map.put("count", Integer.toString(list.size()));
        for(int i=0; i<list.size(); i++){
            PatientMedicineDetail detail = list.get(i);
            map.put("sd" + i, Double.toString(detail.getSd()));
            map.put("ndd" + i, Double.toString(detail.getNdd()));
            map.put("tdd" + i, Double.toString(detail.getTdd()));
            map.put("description" + i, detail.getDescription());
            map.put("time"+ i, detail.getTime());
            map.put("medicine_id" + i, detail.getMedicine().getId());
        }

        progressDialog.show();

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {

                try {
                    JSONObject jObj = new JSONObject(data);
                    String status = jObj.getString("status");

                    if("success".equals(status)){
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));
                    }else{
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                }

            }
        }.start();

    }

    private void setDateText(TextView tv, String text){

        tv.setText(text);
        tv.setTextColor(getColorId(R.color.dark_gray));
        tv.setTypeface(tv.getTypeface(), Typeface.NORMAL);

    }

    private void buildMedicineList(){

        li_medicineList.removeAllViews();

        for(final PatientMedicineDetail detail : list){

            View v = getLayoutInflater().inflate(R.layout.add_medicine_list_custom_item, null, false);

            TextView tv_name = (TextView)v.findViewById(R.id.tv_name);
            TextView tv_code = (TextView)v.findViewById(R.id.tv_code);
            TextView tv_info = (TextView)v.findViewById(R.id.tv_info);
            TextView tv_time = (TextView)v.findViewById(R.id.tv_time);
            Button deleteBtn = (Button)v.findViewById(R.id.btn_delete);
            deleteBtn.setTag((PatientMedicineDetail)detail);

            Medicine medicine = detail.getMedicine();
            tv_name.setText(medicine.getNameSrt());
            tv_code.setText(medicine.getCode());
            tv_info.setText(detail.getSd() + "/" + detail.getNdd() + "/" + detail.getTdd() + "/" + detail.getDescription());
            tv_time.setText(detail.getTime());

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDetail(detail);
                }
            });

            li_medicineList.addView(v);

        }

        checkSaveBtn();

    }

    private void deleteDetail(PatientMedicineDetail detail){
        list.remove(detail);
        buildMedicineList();
    }

    private void addMedicine(final Medicine medicine){

        MaterialDialog dialog =
                new MaterialDialog.Builder(this)
                        .title(R.string.enter_more_information)
                        .customView(R.layout.add_patient_medicine_custom_layout, true)
                        .positiveText(R.string.add_srt)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                double sd = Double.parseDouble(form_sd.getText().toString());
                                double ndd = Double.parseDouble(form_ndd.getText().toString());
                                double tdd = Double.parseDouble(form_tdd.getText().toString());
                                String usage = form_usage.getText().toString();
                                String time = "";
                                if(cb_morning.isChecked()){
                                    time += getString(R.string.morning);
                                }
                                if(cb_lunch.isChecked()){
                                    if(!"".equals(time)){
                                        time += "/";
                                    }
                                    time += getString(R.string.lunch);
                                }
                                if(cb_evening.isChecked()){
                                    if(!"".equals(time)){
                                        time += "/";
                                    }
                                    time += getString(R.string.evening);
                                }
                                PatientMedicineDetail detail = new PatientMedicineDetail();
                                detail.setSd(sd);
                                detail.setNdd(ndd);
                                detail.setTdd(tdd);
                                detail.setDescription(usage);
                                detail.setTime(time);
                                detail.setMedicine(medicine);

                                list.add(detail);
                                buildMedicineList();
                            }
                        })
                        .cancelable(false)
                        .build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        form_sd = (MaterialEditText)dialog.getCustomView().findViewById(R.id.form_sd);
        form_ndd = (MaterialEditText)dialog.getCustomView().findViewById(R.id.form_ndd);
        form_tdd = (MaterialEditText)dialog.getCustomView().findViewById(R.id.form_tdd);
        form_usage = (MaterialEditText)dialog.getCustomView().findViewById(R.id.form_usage);
        cb_morning = (CheckBox)dialog.getCustomView().findViewById(R.id.cb_morning);
        cb_lunch = (CheckBox)dialog.getCustomView().findViewById(R.id.cb_lunch);
        cb_evening = (CheckBox)dialog.getCustomView().findViewById(R.id.cb_evening);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkDialog();
            }
        };
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkDialog();
            }
        };
        form_sd.addTextChangedListener(textWatcher);
        form_ndd.addTextChangedListener(textWatcher);
        form_tdd.addTextChangedListener(textWatcher);
        form_usage.addTextChangedListener(textWatcher);
        cb_morning.setOnCheckedChangeListener(onCheckedChangeListener);
        cb_lunch.setOnCheckedChangeListener(onCheckedChangeListener);
        cb_evening.setOnCheckedChangeListener(onCheckedChangeListener);

        dialog.show();
        positiveAction.setEnabled(false);

//        new MaterialDialog.Builder(this)
//                .title(R.string.title_srt)
//                .content(R.string.completed_take_medicine_srt)
//                .positiveText(R.string.add_medicine)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        PatientMedicineDetail detail = new PatientMedicineDetail();
//                        detail.setMedicine(medicine);
//                        list.add(detail);
//                        buildMedicineList();
//                    }
//                })
//                .show();

    }

    private class MyHandler extends Handler implements Serializable {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SUCCESS:
                    progressDialog.hide();
                    AddPatientMedicineActivity.this.setResult(ManageMedicineActivity.UPDATE_LIST);
                    finish();
                    break;
                case MSG_MESSAGE_FAIL:
                    progressDialog.hide();
                    new MaterialDialog.Builder(AddPatientMedicineActivity.this)
                            .title(R.string.fail_srt)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                default:
                    break;
            }
        }
    }

    private void checkDialog(){

        boolean isSd = form_sd.isCharactersCountValid();
        boolean isNdd = form_ndd.isCharactersCountValid();
        boolean isTdd = form_tdd.isCharactersCountValid();
        boolean isUsage = form_usage.isCharactersCountValid();
        boolean isMorning = cb_morning.isChecked();
        boolean isLunch = cb_lunch.isChecked();
        boolean isEvening = cb_evening.isChecked();

        boolean status = isSd && isNdd && isTdd && isUsage && (isMorning || isLunch || isEvening);

        positiveAction.setEnabled(status);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case SearchMedicineActivity.SELECTED_MEDICINE:
                Medicine medicine = (Medicine)data.getSerializableExtra("medicine");
                addMedicine(medicine);
                break;
            default:
                break;
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
