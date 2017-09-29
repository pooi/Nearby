package cf.nearby.nearby.nurse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;

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


    }
}
