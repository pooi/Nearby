package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;

public class NurseMainActivity extends BaseActivity {

    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_main);

        init();
    }

    private void init(){

        logoutBtn = (Button)findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        showSnackbar(String.format(getString(R.string.print_hello_msg), StartActivity.employee.getName()));
        

        findViewById(R.id.cv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseMainActivity.this,NurseRegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.cv_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseMainActivity.this, NurseManageActivity.class);
                startActivity(intent);
            }
        });

    }
}
