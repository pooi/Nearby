package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import java.util.HashMap;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.obj.Employee;
import cf.nearby.nearby.util.ParsePHP;


public class RegisterPatientActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);
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
                register_patient();
                Intent intent = new Intent(RegisterPatientActivity.this,NurseRegisterActivity.class);
                startActivity(intent);
            }
            });
    }
    private void register_patient() {
        boolean check = CheckInfo();
        System.out.print(check);
        System.out.print(patient_fn.toString());
        if (check) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("service", "registerPatient");
            map.put("patient_ln", patient_ln.getText().toString());
            map.put("patient_fn", patient_fn.getText().toString());
            map.put("patient_address", patient_address.getText().toString());
            map.put("patient_zip", patient_zip.getText().toString());
            map.put("patient_gender", textradio());
            map.put("patient_pic","");
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
        }
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

    private boolean CheckInfo(){

        boolean patientinfo1 = !patient_fn.getText().toString().isEmpty();
        boolean patientinfo2 = !patient_ln.getText().toString().isEmpty();
        boolean patientinfo3 = !patient_address.getText().toString().isEmpty();
        boolean patientinfo4 = !patient_phone.getText().toString().isEmpty();
        boolean patientinfo5 = !patient_zip.getText().toString().isEmpty();
        boolean patientinfo = !patient_height.getText().toString().isEmpty();
        boolean patientinfo6 = checkradiobtn();
        boolean status = patientinfo && patientinfo1 && patientinfo2 && patientinfo3 && patientinfo4 && patientinfo5 && patientinfo6;

        if(status){
            return true;
        }
        else{
            progressDialog.setContent(R.string.fail);
            progressDialog.show();
            return false;
        }
    }
}
