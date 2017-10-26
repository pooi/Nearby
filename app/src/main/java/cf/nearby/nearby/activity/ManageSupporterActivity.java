package cf.nearby.nearby.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.Supporter;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class ManageSupporterActivity extends BaseActivity {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_SUCCESS = 504;
    private final int MSG_MESSAGE_FAIL = 505;

    private Patient selectedPatient;

    private CardView addSupporterBtn;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;

    private LinearLayout li_supporter;
    private ArrayList<Supporter> supporters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_supporter);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        supporters = new ArrayList<>();

        init();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addSupporterBtn = (CardView)findViewById(R.id.cv_add_supporter);
        addSupporterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);
        loading = (AVLoadingIndicatorView)findViewById(R.id.loading);

        li_supporter = (LinearLayout)findViewById(R.id.li_supporter);



        ImageView img = (ImageView)findViewById(R.id.img);
        if(selectedPatient.getPic() != null && !"".equals(selectedPatient.getPic())) {
            Picasso.with(getApplicationContext())
                    .load(selectedPatient.getPic())
                    .into(img);
        }
        TextView tv_name = (TextView)findViewById(R.id.tv_name);
        TextView tv_dob = (TextView)findViewById(R.id.tv_dob);
        TextView tv_registeredDate = (TextView)findViewById(R.id.tv_registered_date);
        tv_name.setText(selectedPatient.getName());
        tv_dob.setText(AdditionalFunc.getDateString(selectedPatient.getDob()));
        tv_registeredDate.setText(AdditionalFunc.getDateString(selectedPatient.getRegisteredDate()));

        getSupporterList();

    }

    private void getSupporterList(){

        loading.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "getSupporterList");
        map.put("patient_id", selectedPatient.getId());

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                supporters = Supporter.getSrtSupporterList(data);
                if(supporters.size() > 0){
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                }else{
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_PROGRESS_HIDE));
                }

            }
        }.start();

    }

    private void makeList(){

        if(supporters.size() > 0){
            tv_msg.setVisibility(View.GONE);
        }else{
            tv_msg.setVisibility(View.VISIBLE);
        }

        li_supporter.removeAllViews();
        for(Supporter supporter : supporters){

            View v = getLayoutInflater().inflate(R.layout.supporter_list_custom_item, null, false);

            CardView cv = (CardView)v.findViewById(R.id.cv);
            TextView tv_name = (TextView)v.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView)v.findViewById(R.id.tv_phone);
            TextView tv_relationship = (TextView)v.findViewById(R.id.tv_relationship);

            tv_name.setText(supporter.getName());
            tv_phone.setText(supporter.getPhone());
            tv_relationship.setText(supporter.getRelationship());

            li_supporter.addView(v);
        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_MAKE_LIST:
//                    progressDialog.hide();
                    loading.hide();
                    makeList();
                    break;
                case MSG_MESSAGE_PROGRESS_HIDE:
//                    progressDialog.hide();
                    loading.hide();
                    break;
                case MSG_MESSAGE_SUCCESS:
                    loading.hide();
                    break;
                case MSG_MESSAGE_FAIL:
                    loading.hide();
//                    progressDialog.hide();
                    new MaterialDialog.Builder(ManageSupporterActivity.this)
                            .title(R.string.fail_srt)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                default:
                    break;
            }
        }
    }
}
