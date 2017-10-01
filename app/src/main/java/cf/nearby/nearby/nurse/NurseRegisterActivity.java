package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;


import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;



public class NurseRegisterActivity extends BaseActivity {
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_register);
        init();
    }

    private void init(){
        TextView register_patient = (TextView)findViewById(R.id.register_patient);
        register_patient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseRegisterActivity.this,RegisterPatientActivity.class);
                startActivity(intent);
                finish();
            }
        });
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();
    }
}
