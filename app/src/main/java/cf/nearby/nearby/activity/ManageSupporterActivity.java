package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
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
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.Supporter;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class ManageSupporterActivity extends BaseActivity {

    public final static int ADD_SUPPORTER = 100;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_SUCCESS = 504;
    private final int MSG_MESSAGE_FAIL = 505;
    private final int MSG_MESSAGE_ADD_SUCCESS = 506;
    private final int MSG_MESSAGE_ADD_FAIL = 507;

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
//                Intent intent = new Intent(ManageSupporterActivity.this, RegisterSupporterActivity.class);
//                startActivity(intent);
                addSupporter();
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

    private void addSupporter(){
        new MaterialDialog.Builder(this)
                .title(R.string.search_srt)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .theme(Theme.LIGHT)
                .positiveText(R.string.search_srt)
                .negativeText(R.string.cancel)
//                .neutralText(R.string.reset)
//                .onNeutral(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//
//                    }
//                })
                .input(getResources().getString(R.string.please_input_supporter_name_or_id), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String text = input.toString();
                        Intent intent = new Intent(ManageSupporterActivity.this, SearchSupporterActivity.class);
                        intent.putExtra("search", text);
                        intent.putExtra("patient", selectedPatient);
                        startActivityForResult(intent, ADD_SUPPORTER);
                    }
                })
                .show();
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
        for(int i=0; i<supporters.size(); i++){
            Supporter supporter = supporters.get(i);

            View v = getLayoutInflater().inflate(R.layout.supporter_list_custom_item, null, false);

            CardView cv = (CardView)v.findViewById(R.id.cv);
            cv.setTag(i);
            TextView tv_name = (TextView)v.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView)v.findViewById(R.id.tv_phone);
            TextView tv_relationship = (TextView)v.findViewById(R.id.tv_relationship);

            tv_name.setText(supporter.getName());
            tv_phone.setText(supporter.getPhone());
            tv_relationship.setText(supporter.getRelationship());

            cv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int index = (int)view.getTag();
                    removeSupporter(index);
                    return true;
                }
            });

            li_supporter.addView(v);
        }

    }

    private void removeSupporter(final int index){

        String title = String.format(getString(R.string.are_you_sure_you_want_to_delete_supporter), supporters.get(index).getName());
        new MaterialDialog.Builder(ManageSupporterActivity.this)
                .title(title)
                .negativeText(R.string.cancel)
                .positiveText(R.string.delete_srt)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        loading.show();

                        HashMap<String, String> map = new HashMap<>();
                        map.put("service", "removePatientSupporter");
                        map.put("patient_id", selectedPatient.getId());
                        map.put("user_id", supporters.get(index).getId());

                        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                            @Override
                            protected void afterThreadFinish(String data) {

                                try {
                                    JSONObject jObj = new JSONObject(data);
                                    String status = jObj.getString("status");

                                    if("success".equals(status)){
                                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_ADD_SUCCESS));
                                    }else{
                                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_ADD_FAIL));
                                    }

                                } catch (JSONException e) {
                                    // JSON error
                                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_ADD_FAIL));
                                }

                            }
                        }.start();

                    }
                })
                .show();

    }

    private void addNewSupporter(Supporter supporter, String relationship){

        loading.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "registerPatientSupporter");
        map.put("patient_id", selectedPatient.getId());
        map.put("user_id", supporter.getId());
        map.put("relationship", relationship);

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                try {
                    JSONObject jObj = new JSONObject(data);
                    String status = jObj.getString("status");

                    if("success".equals(status)){
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_ADD_SUCCESS));
                    }else{
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_ADD_FAIL));
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_ADD_FAIL));
                }

            }
        }.start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ADD_SUPPORTER:
                if(data != null){
                    final Supporter supporter = (Supporter)data.getSerializableExtra("supporter");
                    new MaterialDialog.Builder(this)
                            .title(R.string.add_supporter)
                            .inputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                    InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                            .theme(Theme.LIGHT)
                            .positiveText(R.string.save_srt)
                            .negativeText(R.string.cancel)
                            .input(getResources().getString(R.string.please_input_supporter_relationship), "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    String relationship = input.toString();
                                    addNewSupporter(supporter, relationship);
                                }
                            })
                            .show();
                }
                break;
            default:
                break;
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
                case MSG_MESSAGE_ADD_SUCCESS:
                    loading.hide();
                    getSupporterList();
                    break;
                case MSG_MESSAGE_ADD_FAIL:
                    loading.hide();
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
