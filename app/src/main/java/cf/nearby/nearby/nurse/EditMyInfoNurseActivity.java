package cf.nearby.nearby.nurse;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.obj.Employee;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class EditMyInfoNurseActivity extends AppCompatActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SUCCESS = 500;
    private final int MSG_MESSAGE_FAIL = 501;

    private MaterialEditText nurse_ln;
    private MaterialEditText nurse_fn;
    private MaterialEditText nurse_address;
    private MaterialEditText nurse_zip;
    private MaterialEditText nurse_phone;
    private MaterialEditText nurse_email;
    private MaterialEditText nurse_major;
    private MaterialEditText nurse_license;
    private MaterialEditText nurse_description;

    private Button back_btn;
    private Button register_btn;

    private MaterialDialog progressDialog;

    private CheckBox bla;

    private TextView startdate;
    private TextView dob;

    private RadioGroup selgender;

    private RadioButton male;
    private RadioButton female;

    private long start_Date;
    private long date_of_Birth;

    private Employee em;

    private boolean isSelectStartDate;
    private boolean isSelectDob;

    private String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_info_nurse);
        em = StartActivity.employee;
        init();
    }

    public void init(){
        nurse_ln = (MaterialEditText)findViewById(R.id.nurse_ln);
        nurse_fn = (MaterialEditText)findViewById(R.id.nurse_fn);
        nurse_email = (MaterialEditText)findViewById(R.id.nurse_email);
        nurse_license = (MaterialEditText)findViewById(R.id.nurse_license);
        nurse_major = (MaterialEditText)findViewById(R.id.nurse_major);
        nurse_address = (MaterialEditText)findViewById(R.id.nurse_address);
        nurse_zip = (MaterialEditText)findViewById(R.id.nurse_zip);
        nurse_phone = (MaterialEditText)findViewById(R.id.nurse_phone);
        nurse_description = (MaterialEditText)findViewById(R.id.nurse_description);

        selgender = (RadioGroup)findViewById(R.id.selectgender);
        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);

        back_btn = (Button)findViewById(R.id.btn_back);
        register_btn = (Button)findViewById(R.id.register_btn);


        startdate = (TextView)findViewById(R.id.startdate);
        dob = (TextView)findViewById(R.id.dob);

        nurse_fn.setText(em.getFn());
        nurse_ln.setText(em.getLn());
        nurse_email.setText(em.getEmail());
        nurse_address.setText(em.getAddress());
        nurse_zip.setText(em.getZip());
        nurse_license.setText(em.getLicense());
        nurse_phone.setText(em.getPhone());
        nurse_major.setText(em.getMajor());

        if("female".equals(em.getGender()))
            selgender.check(R.id.female);
        else
            selgender.check(R.id.male);

        start_Date = em.getStartDate();
        date_of_Birth = em.getDob();
        String Dob = setCalender(Long.toString(em.getDob()));
        String Sd = setCalender(Long.toString(em.getStartDate()));
        setDateText(dob, AdditionalFunc.getDateString(date_of_Birth));
        setDateText(startdate, AdditionalFunc.getDateString(start_Date));
//        dob.setText(Dob);
//        registerdate.setText(Rd);
//        startdate.setText(Sd);

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.back)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();


        startdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startDatePick();
            }
        });
        dob.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dobDatePick();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressDialog.show();
                finish();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean a=CheckInfo();
                if(a==true){
                    modify_nurse();
//                    Intent intent = new Intent(EditMyInfoNurseActivity.this, NurseManageActivity.class);
//                    startActivity(intent);
//                    finish();
                }
                else{
                    progressDialog.hide();
                }
            }
        });
    }

    private void modify_start_em(){
        StartActivity.employee.setFn(nurse_fn.getText().toString());
        StartActivity.employee.setLn(nurse_ln.getText().toString());
        StartActivity.employee.setAddress(nurse_address.getText().toString());
        StartActivity.employee.setZip(nurse_zip.getText().toString());
        StartActivity.employee.setGender(textradio());
        StartActivity.employee.setStartDate(start_Date);
        StartActivity.employee.setDob(date_of_Birth);
        StartActivity.employee.setPhone(nurse_phone.getText().toString());
        StartActivity.employee.setMajor(nurse_major.getText().toString());
        StartActivity.employee.setEmail(nurse_email.getText().toString());
        StartActivity.employee.setLicense(nurse_license.getText().toString());
    }

    private int modify_nurse() {
        if (true) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("service", "ModifyNurseInfo");
            map.put("nurse_id",em.getId());
            map.put("nurse_ln", nurse_ln.getText().toString());
            map.put("nurse_fn", nurse_fn.getText().toString());
            map.put("nurse_address", nurse_address.getText().toString());
            map.put("nurse_zip", nurse_zip.getText().toString());
            map.put("nurse_gender", textradio());
            map.put("nurse_pic","");
            map.put("start_date", Long.toString(start_Date));
            map.put("dob", Long.toString(date_of_Birth));
            map.put("location_id", em.getLocation().getId());
            map.put("nurse_phone", nurse_phone.getText().toString());
            map.put("nurse_email", nurse_email.getText().toString());
            map.put("nurse_license", nurse_license.getText().toString());
            map.put("nurse_major", nurse_major.getText().toString());
            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {
                @Override
                protected void afterThreadFinish(String data) {
                    try {
                        JSONObject jObj = new JSONObject(data);
                        String status = jObj.getString("status");
                        if("success".equals(status)){
                            modify_start_em();
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));
//                            finish();
                        }else{
                            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                        }

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            }.start();
            return 1;
        }
        else
            return 0;
    }
    public String setCalender(String val){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(val));
        Date d = (Date) c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String time = format.format(d);
        return time;
    }

    private void startDatePick(){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(start_Date);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        start_Date = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                        isSelectStartDate = true;
                        setDateText(startdate, AdditionalFunc.getDateString(start_Date));
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle(getString(R.string.startdate));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void dobDatePick(){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(date_of_Birth);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        date_of_Birth = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                        isSelectDob = true;
                        setDateText(dob, AdditionalFunc.getDateString(date_of_Birth));
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle(getString(R.string.dob));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void setDateText(TextView tv, String text){
        tv.setText(text);
        tv.setTextColor(getColorId(R.color.dark_gray));
        tv.setTypeface(tv.getTypeface(), Typeface.NORMAL);
    }

    private boolean CheckInfo(){
        boolean nurseinfo1 = !nurse_fn.getText().toString().isEmpty();
        boolean nurseinfo2 = !nurse_ln.getText().toString().isEmpty();
        boolean nurseinfo3 = !nurse_address.getText().toString().isEmpty();
        boolean nurseinfo4 = !nurse_phone.getText().toString().isEmpty();
        boolean nurseinfo5 = !nurse_zip.getText().toString().isEmpty();
        boolean nurseinfo6 = !nurse_email.getText().toString().isEmpty();
        boolean nurseinfo7 = checkradiobtn();
        boolean nurseinfo8 = !nurse_major.getText().toString().isEmpty();
        boolean nurseinfo9 = !nurse_license.getText().toString().isEmpty();
        boolean nurseinfo10 = !dob.getText().toString().isEmpty();
        boolean nurseinfo12 = !startdate.getText().toString().isEmpty();
        boolean status = nurseinfo7 && nurseinfo1 && nurseinfo2 && nurseinfo3 && nurseinfo4 && nurseinfo5 && nurseinfo6 && nurseinfo8 && nurseinfo9 && nurseinfo10 && nurseinfo12;

        if(status){
            return true;
        }
        else{
            progressDialog.setContent(R.string.fail);
            progressDialog.show();
            return false;
        }
    }

    public int getColorId(int id){
        return ContextCompat.getColor(getApplicationContext(), id);
    }

    private boolean checkradiobtn(){
        if(male.isChecked()||female.isChecked()){
            return true;
        }
        else
            return false;
    }

    @NonNull
    private String textradio(){
        RadioButton selradio = (RadioButton)findViewById(selgender.getCheckedRadioButtonId());
        String value = selradio.getText().toString();
        if(value.equals("남")) {
            return "male";
        }
        else if(value.equals("여")) {
            return "female";
        }
        return "error";
    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SUCCESS:
                    progressDialog.hide();
                    new MaterialDialog.Builder(EditMyInfoNurseActivity.this)
                            .title(R.string.success_srt)
                            .content(R.string.successfully_edit)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    EditMyInfoNurseActivity.this.finish();
                                }
                            })
                            .positiveText(R.string.ok)
                            .show();
                    break;
                case MSG_MESSAGE_FAIL:
                    progressDialog.hide();
                    new MaterialDialog.Builder(EditMyInfoNurseActivity.this)
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
