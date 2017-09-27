package cf.nearby.nearby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.rengwuxian.materialedittext.MaterialEditText;

import cf.nearby.nearby.nurse.NurseMainActivity;

public class StartActivity extends AppCompatActivity {


    // UI
    private KenBurnsView kenBurnsView;
    private RelativeLayout rl_background;

    private LinearLayout formLogin;
    private MaterialEditText formId;
    private MaterialEditText formPw;
    private Button formLoginBtn;

    private TextView tv_signupSupporterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        init();

    }

    private void init(){

        kenBurnsView = (KenBurnsView)findViewById(R.id.image);
        kenBurnsView.setImageResource(R.drawable.loading);

        rl_background = (RelativeLayout)findViewById(R.id.rl_background);

        initLoginForm();

        tv_signupSupporterBtn = (TextView)findViewById(R.id.tv_signup_supporter);

    }

    private void initLoginForm(){

        formLogin = (LinearLayout)findViewById(R.id.form_login);
        formId = (MaterialEditText)findViewById(R.id.form_id);
        formPw = (MaterialEditText)findViewById(R.id.form_pw);
        formLoginBtn = (Button)findViewById(R.id.form_login_btn);
        formLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, NurseMainActivity.class);
                startActivity(intent);
            }
        });

    }
}
