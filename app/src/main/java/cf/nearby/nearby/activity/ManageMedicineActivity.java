package cf.nearby.nearby.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cf.nearby.nearby.R;
import cf.nearby.nearby.obj.Patient;

public class ManageMedicineActivity extends AppCompatActivity {

    private TextView toolbarTitle;

    private Patient selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_medicine);

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

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText(selectedPatient.getName());



    }
}
