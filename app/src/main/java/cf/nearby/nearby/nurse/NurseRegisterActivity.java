package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.RegisterPatientActivity;
import cf.nearby.nearby.activity.SearchPatientActivity;


public class NurseRegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_register);
        init();
    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.cv_register_patient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseRegisterActivity.this,RegisterPatientActivity.class);
                intent.putExtra("nextActivity", Information.NURSE_REGISTER_MENU);
                startActivity(intent);
            }
        });

        findViewById(R.id.cv_register_supporter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchPatientActivity.class);
                intent.putExtra("nextActivity", Information.MANAGE_SUPPORTER_MENU);
                startActivity(intent);
            }
        });

//        findViewById(R.id.monthly_event_register).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(NurseRegisterActivity.this,SearchPatientByLocationIdActivity.class);
//                intent.putExtra("nextActivity", Information.NURSE_REGISTER_MENU);
//                startActivity(intent);
//            }
//        });

    }

}
