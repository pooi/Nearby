package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.SearchPatientActivity;

public class NurseInquiryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_inquiry);


        init();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.cv_inquiry_location_patient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchPatientActivity.class);
//                intent.putExtra("nextActivity", Information.NURSE_MANAGE_MENU);
                startActivity(intent);

            }
        });

        findViewById(R.id.cv_inquiry_patient_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchPatientActivity.class);
                intent.putExtra("nextActivity", Information.INQUIRY_MAIN_MENU);
                startActivity(intent);
            }
        });

    }
}
