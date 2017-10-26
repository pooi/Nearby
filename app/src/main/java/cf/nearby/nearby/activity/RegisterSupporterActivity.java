package cf.nearby.nearby.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.nurse.NurseRegisterActivity;
import cf.nearby.nearby.nurse.RegisterPatientActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class RegisterSupporterActivity extends AppCompatActivity {
    private String nextActivity;

    private MaterialEditText supporter_id;
    private MaterialEditText supporter_password;
    private MaterialEditText supporter_ln;
    private MaterialEditText supporter_fn;
    private MaterialEditText supporter_email;
    private MaterialEditText supporter_address;
    private MaterialEditText supporter_zip;
    private MaterialEditText supporter_phone;
    private MaterialEditText supporter_description;

    private Button back_btn;
    private Button register_btn;

    private MaterialDialog progressDialog;


    private TextView startdate;
    private TextView registerdate;
    private TextView dob;

    private RadioGroup selgender;
    private RadioGroup selrole;

    private RadioButton male;
    private RadioButton female;
    private RadioButton spouse;
    private RadioButton child;
    private RadioButton familymem;

    private long start_Date;
    private long register_Date;
    private long date_of_Birth;

    private boolean isSelectStartDate;
    private boolean isSelectRegisterDate;
    private boolean isSelectDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_supporter);
        Intent intent = getIntent();
        nextActivity = intent.getStringExtra("nextActivity");
        init();
    }

    private void init(){
        supporter_id = (MaterialEditText)findViewById(R.id.supporter_id);
        supporter_password = (MaterialEditText)findViewById(R.id.supporter_password);
        supporter_email = (MaterialEditText)findViewById(R.id.supporter_email);
        supporter_ln = (MaterialEditText)findViewById(R.id.supporter_ln);
        supporter_fn = (MaterialEditText)findViewById(R.id.supporter_fn);
        supporter_address = (MaterialEditText)findViewById(R.id.supporter_address);
        supporter_zip = (MaterialEditText)findViewById(R.id.supporter_zip);
        supporter_phone = (MaterialEditText)findViewById(R.id.supporter_phone);
        supporter_description = (MaterialEditText)findViewById(R.id.supporter_description);

        selgender = (RadioGroup)findViewById(R.id.selectgender);
        selrole = (RadioGroup)findViewById(R.id.selectrole);

        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);
        spouse = (RadioButton)findViewById(R.id.spouse);
        child = (RadioButton)findViewById(R.id.child);
        familymem = (RadioButton)findViewById(R.id.familymem);

        back_btn = (Button)findViewById(R.id.btn_back);
        register_btn = (Button)findViewById(R.id.register_btn);
        ;

        startdate = (TextView)findViewById(R.id.startdate);
        registerdate = (TextView)findViewById(R.id.registerdate);
        dob = (TextView)findViewById(R.id.dob);

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.back)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

        registerdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                registerDatePick();
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
                progressDialog.show();
                finish();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = register_supporter();
                if(a==1){
                    finish();
                }
                else{
                    progressDialog.hide();
                }
            }
        });
    }
    private int register_supporter() {
        boolean check = CheckInfo();
        if (check) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("service", "registerSupporter");
            map.put("supporter_id", supporter_id.getText().toString());
            map.put("supporter_password", supporter_password.getText().toString());
            map.put("supporter_email", supporter_email.getText().toString());
            map.put("supporter_ln", supporter_ln.getText().toString());
            map.put("supporter_fn", supporter_fn.getText().toString());
            map.put("supporter_address", supporter_address.getText().toString());
            map.put("supporter_zip", supporter_zip.getText().toString());
            map.put("supporter_gender", textradio(selgender));
            map.put("supporter_role",textradio(selrole));
            map.put("register_date", Long.toString(register_Date));
            map.put("dob", Long.toString(date_of_Birth));
            map.put("supporter_phone", supporter_phone.getText().toString());

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {
                @Override
                protected void afterThreadFinish(String data) {
                    System.out.print(data);
                }
            }.start();
            return 1;
        }
        else
            return 0;
    }

    private boolean checkradiobtn(){
        if(male.isChecked()||female.isChecked()){
            if(child.isChecked()||spouse.isChecked()||familymem.isChecked())
                return true;
            else
                return false;
        }
        else
            return false;
    }


    private String textradio(RadioGroup rb){
        RadioButton selradio = (RadioButton)findViewById(rb.getCheckedRadioButtonId());
        String value = selradio.getText().toString();
        if(value=="남자")
            value = "male";
        else if(value=="여자")
            value = "female";
        else if(value=="배우자")
            value = "spouse";
        else if(value=="아들/딸")
            value = "child";
        else if(value=="친인척")
            value = "familymem";
        return value;
    }

    private void registerDatePick(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        register_Date = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                        isSelectRegisterDate = true;
                        setDateText(registerdate, AdditionalFunc.getDateString(register_Date));
                        dobDatePick();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle(getString(R.string.registerdate));
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void dobDatePick(){
        Calendar now = Calendar.getInstance();
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

    public int getColorId(int id){
        return ContextCompat.getColor(getApplicationContext(), id);
    }

    private boolean CheckInfo(){

        boolean supporterinfo1 = !supporter_fn.getText().toString().isEmpty();
        boolean supporterinfo2 = !supporter_ln.getText().toString().isEmpty();
        boolean supporterinfo3 = !supporter_address.getText().toString().isEmpty();
        boolean supporterinfo4 = !supporter_phone.getText().toString().isEmpty();
        boolean supporterinfo5 = !supporter_zip.getText().toString().isEmpty();
        boolean supporterinfo6 = checkradiobtn();
        boolean supporterinfo7 = !supporter_id.getText().toString().isEmpty();
        boolean supporterinfo8 = !supporter_password.getText().toString().isEmpty();
        boolean supporterinfo9 = !supporter_email.getText().toString().isEmpty();
        boolean status = supporterinfo1 && supporterinfo2 && supporterinfo3 && supporterinfo4 && supporterinfo5 && supporterinfo6 &&isSelectDob &&isSelectRegisterDate&&supporterinfo7&&supporterinfo8&&supporterinfo9;

        if(status){
            register_btn.setBackgroundColor(getColorId(R.color.colorAccent));
            return true;
        }
        else{
            progressDialog.setContent(R.string.fail);
            progressDialog.show();

            return false;
        }
    }

}
