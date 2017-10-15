package cf.nearby.nearby.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;

public class AdminInquiryActivity extends BaseActivity implements Serializable {

    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inquiry);

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
