package cf.nearby.nearby.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.RegisterPatientActivity;


public class AdminRegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);
        init();
    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setCardButtonOnTouchAnimation(findViewById(R.id.cv_register_patient));
        findViewById(R.id.cv_register_patient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminRegisterActivity.this,RegisterPatientActivity.class);
                intent.putExtra("nextActivity", Information.NURSE_REGISTER_MENU);
                startActivity(intent);
            }
        });
    }

}
