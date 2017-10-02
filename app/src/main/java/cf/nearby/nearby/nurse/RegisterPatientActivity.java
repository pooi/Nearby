package cf.nearby.nearby.nurse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cf.nearby.nearby.R;

public class RegisterPatientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);

        init();
        
    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
