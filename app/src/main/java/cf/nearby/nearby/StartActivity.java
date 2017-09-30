package cf.nearby.nearby;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

import cf.nearby.nearby.admin.AdminMainActivity;
import cf.nearby.nearby.nurse.NurseMainActivity;
import cf.nearby.nearby.obj.Employee;
import cf.nearby.nearby.util.ParsePHP;

public class StartActivity extends BaseActivity {

    public static boolean isEmployeeLogin;
    public static Employee employee;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SUCCESS = 500;
    private final int MSG_MESSAGE_FAIL = 501;

    // UI
    private KenBurnsView kenBurnsView;
    private RelativeLayout rl_background;

    private LinearLayout formLogin;
    private MaterialEditText formId;
    private MaterialEditText formPw;
    private Button formLoginBtn;

    private TextView tv_signupSupporterBtn;

    private MaterialDialog progressDialog;

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

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

    }

    private void initLoginForm(){

        formLogin = (LinearLayout)findViewById(R.id.form_login);
        formId = (MaterialEditText)findViewById(R.id.form_id);
        formPw = (MaterialEditText)findViewById(R.id.form_pw);
        formLoginBtn = (Button)findViewById(R.id.form_login_btn);
        formLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmployee();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkLoginBtn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        formId.addTextChangedListener(textWatcher);
        formPw.addTextChangedListener(textWatcher);

    }

    private void checkLoginBtn(){

        boolean isInputId = !formId.getText().toString().isEmpty();
        boolean isInputPw = !formPw.getText().toString().isEmpty();

        boolean status = isInputId && isInputPw;

        formLoginBtn.setEnabled(status);

        if(status){
            formLoginBtn.setBackgroundColor(getColorId(R.color.colorPrimary));
        }else{
            formLoginBtn.setBackgroundColor(getColorId(R.color.dark_gray));
        }

    }

    private class MyHandler extends Handler implements Serializable {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SUCCESS:
                    progressDialog.hide();
                    break;
                case MSG_MESSAGE_FAIL:
                    progressDialog.hide();
                    new MaterialDialog.Builder(StartActivity.this)
                            .title(R.string.fail_srt)
                            .content(getString(R.string.login_failed_srt) + "\n" + getString(R.string.please_check_id_or_pw))
                            .positiveText(R.string.ok)
                            .show();
                    formPw.setText("");
                    break;
                default:
                    break;
            }
        }
    }

    private void loginEmployee(){

        if(formId.getText().toString().length() <= 0 && formPw.getText().toString().length() <= 0){
            showSnackbar(R.string.please_enter_id_and_pw);
            return;
        }else if(formId.getText().toString().length() <= 0){
            showSnackbar(R.string.please_enter_id);
            return;
        }else if(formPw.getText().toString().length() <= 0){
            showSnackbar(R.string.please_enter_pw);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "login_employee");
        map.put("login_id", formId.getText().toString());
        map.put("login_pw", formPw.getText().toString());

        progressDialog.show();

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {

                Employee em = new Employee(data);

                if(!em.isEmpty()){
                    isEmployeeLogin = true;
                    employee = em;
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));

                    if("admin".equals(em.getRole()))
                        redirectAdminMainActivity();
                    else
                        redirectNurseMainActivity();

                }else{
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                }

            }
        }.start();

    }

    private void redirectNurseMainActivity(){
        Intent intent = new Intent(StartActivity.this, NurseMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void redirectAdminMainActivity(){
        Intent intent = new Intent(StartActivity.this, AdminMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static void initLoginData(){
        isEmployeeLogin = false;
        employee = null;
    }
}
