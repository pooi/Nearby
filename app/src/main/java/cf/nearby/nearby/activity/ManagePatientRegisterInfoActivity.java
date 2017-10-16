package cf.nearby.nearby.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.ParsePHP;

public class ManagePatientRegisterInfoActivity extends AppCompatActivity {

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

    private Patient selectedPatient;
    private Patient patient;

    private ArrayList<Patient> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_patient_register_info);
        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        getPaientInfo();
        init();
    }

    public void init() {
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

        patient_fn.setText(patient.getFn());
    }

    public void getPaientInfo(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("service","ManagePatientRegisterInfo");
        map.put("location_id", StartActivity.employee.getLocation().getId());
        map.put("patient_fn",selectedPatient.getFn());
        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {
            @Override
            protected void afterThreadFinish(String data) {
                try {
                    System.out.print(data);
                    list.clear();
                    list = Patient.getPatientList(data);
                    patient = list.get(0);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
