package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.ManageMedicineActivity;
import cf.nearby.nearby.activity.ManageSymptomActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;

public class NurseManageDetailActivity extends AppCompatActivity {

    private Button backBtn;

    private Patient selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_manage_detail);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");

        init();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backBtn = (Button)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.cv_manage_symptom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManageSymptomActivity.class);
                intent.putExtra("patient", selectedPatient);
                startActivity(intent);
            }
        });
        findViewById(R.id.cv_manage_medicine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ManageMedicineActivity.class);
                intent.putExtra("patient", selectedPatient);
                startActivity(intent);

            }
        });

        ImageView img = (ImageView)findViewById(R.id.img);
        Picasso.with(getApplicationContext())
                .load(selectedPatient.getPic())
                .into(img);
        TextView tv_name = (TextView)findViewById(R.id.tv_name);
        TextView tv_dob = (TextView)findViewById(R.id.tv_dob);
        TextView tv_registeredDate = (TextView)findViewById(R.id.tv_registered_date);
        tv_name.setText(selectedPatient.getName());
        tv_dob.setText(AdditionalFunc.getDateString(selectedPatient.getDob()));
        tv_registeredDate.setText(AdditionalFunc.getDateString(selectedPatient.getRegisteredDate()));

    }
}
