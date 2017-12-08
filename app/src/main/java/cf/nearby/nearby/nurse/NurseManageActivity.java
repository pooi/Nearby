package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.SearchPatientActivity;
import cf.nearby.nearby.activity.SearchPatientByLocationIdActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.OnSearchPatientSupport;

public class NurseManageActivity extends BaseActivity implements Serializable {

    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_manage);

        init();

    }

    private void init(){
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setCardButtonOnTouchAnimation(findViewById(R.id.cv_edit_my_info));
        findViewById(R.id.cv_edit_my_info).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), EditMyInfoNurseActivity.class);
                intent.putExtra("nextActivity", Information.NURSE_MANAGE_MENU);
                startActivity(intent);

            }
        });

        setCardButtonOnTouchAnimation(findViewById(R.id.cv_edit_patient_info));
        findViewById(R.id.cv_edit_patient_info).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchPatientByLocationIdActivity.class);
                intent.putExtra("nextActivity", Information.NURSE_MANAGE_MENU);
                startActivity(intent);

            }
        });

        setCardButtonOnTouchAnimation(findViewById(R.id.cv_edit_patient_record));
        findViewById(R.id.cv_edit_patient_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchPatientActivity.class);
                intent.putExtra("nextActivity", Information.NURSE_MANAGE_MENU);
                startActivity(intent);

            }
        });

    }
}
