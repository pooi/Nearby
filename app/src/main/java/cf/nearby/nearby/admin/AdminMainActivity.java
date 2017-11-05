package cf.nearby.nearby.admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.nurse.NurseMainActivity;
import cf.nearby.nearby.nurse.NurseRegisterActivity;

public class AdminMainActivity extends BaseActivity {

    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        init();
    }

    private void init(){

        logoutBtn = (Button)findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(AdminMainActivity.this);
            }
        });

        showSnackbar(String.format(getString(R.string.print_hello_msg), StartActivity.employee.getName()));


        findViewById(R.id.cv_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, AdminManageActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.cv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this,AdminRegisterActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.cv_inquiry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, AdminInquiryActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title(R.string.ok)
                .content(R.string.are_you_finish_app)
                .positiveText(R.string.finish)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}
