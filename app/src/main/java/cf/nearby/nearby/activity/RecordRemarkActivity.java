package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.ArrayList;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.TakeMedicine;

public class RecordRemarkActivity extends BaseActivity {

    public static final int UPDATE_LIST = 100;

    private TextView toolbarTitle;

    private Patient selectedPatient;

    private TextView tv_msg;
    private MaterialDialog progressDialog;

    private ArrayList<PatientRemark> remarks;

    private Toolbar toolbar;
    private CardView etcBtn;
    private Button saveBtn;

    private LinearLayout li_symptom;
    private CardView cv_addCustom;
    private CardView cv_addList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_remark);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        remarks = (ArrayList<PatientRemark>)getIntent().getSerializableExtra("remarks");

        init();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveBtn = (Button)findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("remarks", remarks);
                setResult(NurseRecordActivity.UPDATE_REMARK, intent);
                finish();
            }
        });

        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        li_symptom = (LinearLayout)findViewById(R.id.li_symptom);
        setCardButtonOnTouchAnimation(findViewById(R.id.cv_add_list));
        cv_addList = (CardView)findViewById(R.id.cv_add_list);
        cv_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordRemarkActivity.this, AddRemarkFromListActivity.class);
                intent.putExtra("remarks", remarks);
                startActivityForResult(intent, UPDATE_LIST);
            }
        });
        setCardButtonOnTouchAnimation(findViewById(R.id.cv_add_custom));
        cv_addCustom = (CardView)findViewById(R.id.cv_add_custom);
        cv_addCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(RecordRemarkActivity.this)
                        .title(R.string.add_custom_remark)
                        .inputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                                InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .theme(Theme.LIGHT)
                        .positiveText(R.string.add_srt)
                        .negativeText(R.string.cancel)
                        .input(getString(R.string.please_input_remark), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                PatientRemark patientRemark = new PatientRemark();
                                patientRemark.setPatientId(selectedPatient.getId());
                                patientRemark.setDescription(input.toString());
                                remarks.add(patientRemark);
                                makeList();

                            }
                        })
                        .show();
            }
        });

        makeList();

    }

    public void makeList(){

        if(remarks.size() > 0){
            tv_msg.setVisibility(View.GONE);
        }else{
            tv_msg.setVisibility(View.VISIBLE);
            setFadeInAnimation(tv_msg);
        }

        li_symptom.removeAllViews();
        for(int i=0; i<remarks.size(); i++){
            PatientRemark patientRemark = remarks.get(i);

            View v = getLayoutInflater().inflate(R.layout.patient_remark_list_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            Button btn_select = (Button)v.findViewById(R.id.btn_select);

            tv_title.setText(patientRemark.getDescription());
            btn_select.setBackgroundResource(R.drawable.two_btn_active_right_radius_red);
            btn_select.setText(R.string.delete_srt);
            btn_select.setTag(i);
            btn_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tag = (int)view.getTag();
                    remarks.remove(tag);
                    makeList();
                }
            });

            li_symptom.addView(v);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case UPDATE_LIST:
                if(data != null){
                    remarks = (ArrayList<PatientRemark>)data.getSerializableExtra("remarks");
                    makeList();
                }
                break;
            default:
                break;
        }

    }
}
