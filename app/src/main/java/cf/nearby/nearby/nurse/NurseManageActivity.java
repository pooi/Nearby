package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.SearchPatientActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.OnSearchPatientSupport;

public class NurseManageActivity extends BaseActivity implements Serializable {

    private static class OnSearchPatientRecordSupport implements OnSearchPatientSupport{
        @Override
        public void onPatientSelected(Patient patient){
            System.out.println(patient.getName());
        }
    };

    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_manage);

        init();

    }

    private void init(){

        backBtn = (Button)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.cv_edit_patient_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OnSearchPatientSupport patientSupport = new OnSearchPatientRecordSupport();

                Intent intent = new Intent(getApplicationContext(), SearchPatientActivity.class);
                intent.putExtra("patientSupport", patientSupport);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);

            }
        });

    }
}
