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

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.nurse.NurseRegisterActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;


public class RegisterPatientActivity extends BaseActivity {

    private String nextActivity;

    private MaterialEditText patient_ln;
    private MaterialEditText patient_fn;
    private MaterialEditText patient_address;
    private MaterialEditText patient_zip;
    private MaterialEditText patient_phone;
    private MaterialEditText patient_height;
    private MaterialEditText patient_description;

    private Button back_btn;
    private Button register_btn;

    private MaterialDialog progressDialog;

    private CheckBox bla;

    private TextView startdate;
    private TextView registerdate;
    private TextView dob;

    private RadioGroup selgender;

    private RadioButton male;
    private RadioButton female;

    private long start_Date;
    private long register_Date;
    private long date_of_Birth;

    private boolean isSelectStartDate;
    private boolean isSelectRegisterDate;
    private boolean isSelectDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);
        Intent intent = getIntent();
        nextActivity = intent.getStringExtra("nextActivity");
        init();
    }

    private void init(){
            patient_ln = (MaterialEditText)findViewById(R.id.patient_ln);
            patient_fn = (MaterialEditText)findViewById(R.id.patient_fn);
            patient_address = (MaterialEditText)findViewById(R.id.patient_address);
            patient_zip = (MaterialEditText)findViewById(R.id.patient_zip);
            patient_phone = (MaterialEditText)findViewById(R.id.patient_phone);
            patient_height = (MaterialEditText)findViewById(R.id.patient_height);
            patient_description = (MaterialEditText)findViewById(R.id.patient_description);

            selgender = (RadioGroup)findViewById(R.id.selectgender);
            male = (RadioButton)findViewById(R.id.male);
            female = (RadioButton)findViewById(R.id.female);

            back_btn = (Button)findViewById(R.id.btn_back);
            register_btn = (Button)findViewById(R.id.register_btn);

            bla = (CheckBox)findViewById(R.id.patient_bla);

            startdate = (TextView)findViewById(R.id.startdate);
            registerdate = (TextView)findViewById(R.id.registerdate);
            dob = (TextView)findViewById(R.id.dob);

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

            registerdate.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                 registerDatePick();
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
                int a = register_patient();
                if(a==1){
                Intent intent = new Intent(RegisterPatientActivity.this,NurseRegisterActivity.class);
                startActivity(intent);}
                else{
                    progressDialog.hide();
                }
            }
            });
    }
    private int register_patient() {
        boolean check = CheckInfo();
        if (check) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("service", "registerPatient");
            map.put("patient_ln", patient_ln.getText().toString());
            map.put("patient_fn", patient_fn.getText().toString());
            map.put("patient_address", patient_address.getText().toString());
            map.put("patient_zip", patient_zip.getText().toString());
            map.put("patient_gender", textradio());
            map.put("patient_pic","");
            map.put("start_date", Long.toString(start_Date));
            map.put("register_date", Long.toString(register_Date));
            map.put("dob", Long.toString(date_of_Birth));
            map.put("location_id", StartActivity.employee.getLocation().getId());
            map.put("patient_phone", patient_phone.getText().toString());
            map.put("patient_bla", chbla());
            map.put("patient_height", patient_height.getText().toString());
            map.put("patient_description", patient_description.getText().toString());

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
            return true;
        }
        else
            return false;
    }

    private String textradio(){
        RadioButton selradio = (RadioButton)findViewById(selgender.getCheckedRadioButtonId());
        String value = selradio.getText().toString();
        if(value=="남자")
            value = "male";
        else if(value=="여자")
            value = "female";
        return value;
    }

    private String chbla(){
        if(bla.isChecked()){
            return "1";
        }
        else
            return "0";
    }

    private void startDatePick(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        start_Date = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                        isSelectStartDate = true;
                        setDateText(startdate, AdditionalFunc.getDateString(start_Date));
                        registerDatePick();
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

    public int getColorId(int id){
        return ContextCompat.getColor(getApplicationContext(), id);
    }

    private boolean CheckInfo(){

        boolean patientinfo1 = !patient_fn.getText().toString().isEmpty();
        boolean patientinfo2 = !patient_ln.getText().toString().isEmpty();
        boolean patientinfo3 = !patient_address.getText().toString().isEmpty();
        boolean patientinfo4 = !patient_phone.getText().toString().isEmpty();
        boolean patientinfo5 = !patient_zip.getText().toString().isEmpty();
        boolean patientinfo = !patient_height.getText().toString().isEmpty();
        boolean patientinfo6 = checkradiobtn();
        boolean status = patientinfo && patientinfo1 && patientinfo2 && patientinfo3 && patientinfo4 && patientinfo5 && patientinfo6 &&isSelectDob &&isSelectRegisterDate&&isSelectStartDate;

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

    public void redirectNextActivity(Patient patient){
        Intent intent = new Intent(RegisterPatientActivity.this, RegisterPatientActivity.class);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
