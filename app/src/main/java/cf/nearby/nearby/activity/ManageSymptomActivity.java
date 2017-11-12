package cf.nearby.nearby.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.adapter.PatientMedicineListCustomAdapter;
import cf.nearby.nearby.adapter.PatientSymptomListCustomAdapter;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientMedicine;
import cf.nearby.nearby.obj.PatientSymptom;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.LogManager;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;

public class ManageSymptomActivity extends BaseActivity implements OnAdapterSupport {

    public static final int UPDATE_LIST = 100;

    private TextView toolbarTitle;

    private Patient selectedPatient;
    private boolean isSupporter;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_SUCCESS = 504;
    private final int MSG_MESSAGE_FAIL = 505;
    private final int MSG_MESSAGE_MARK_SUCCESS = 506;
    private final int MSG_MESSAGE_MARK_FAIL = 507;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;

    private int page = 0;
    private ArrayList<PatientSymptom> tempList;
    private ArrayList<PatientSymptom> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private PatientSymptomListCustomAdapter adapter;
    private boolean isLoadFinish;

    private Toolbar toolbar;
    private CardView addPatientSymptomBtn;

    // Custom Dialog
    private View positiveAction;
    private MaterialEditText form_symptom;
    private TextView btn_date_select;
    private long addDate;
    private boolean isDateSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_symptom);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        isSupporter = getIntent().getBooleanExtra("isSupporter", false);

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

        getPatientSymptomList();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText(selectedPatient.getName() + "님");

        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLinearLayoutManager);
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));

        loading = (AVLoadingIndicatorView)findViewById(R.id.loading);
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addPatientSymptomBtn = (CardView)findViewById(R.id.cv_add_medicine);
        addPatientSymptomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ManageSymptomActivity.this, AddPatientMedicineActivity.class);
//                intent.putExtra("patient", selectedPatient);
//                startActivityForResult(intent, UPDATE_LIST);
                addSymptom();

            }
        });
        if(isSupporter)
            addPatientSymptomBtn.setVisibility(View.GONE);

    }

    private void save(final PatientSymptom ps){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "save_patient_symptom");
        map.put("patient_id", selectedPatient.getId());
        map.put("start_date", Long.toString(ps.getStartDate()));
        map.put("description", ps.getDescription());

        progressDialog.show();

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map){
            @Override
            protected void afterThreadFinish(String data) {

                try {
                    JSONObject jObj = new JSONObject(data);
                    String status = jObj.getString("status");

                    if("success".equals(status)){
                        new LogManager(ManageSymptomActivity.this).buildSymptomMsg(selectedPatient, ps).record();

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SUCCESS));
                    }else{
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FAIL));
                }

            }
        }.start();

    }

    private void addSymptom(){

        MaterialDialog dialog =
                new MaterialDialog.Builder(this)
                        .title(R.string.add_patient_symptom_srt)
                        .customView(R.layout.add_patient_symptom_custom_layout, true)
                        .positiveText(R.string.add_srt)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                PatientSymptom patientSymptom = new PatientSymptom();
                                patientSymptom.setDescription(form_symptom.getText().toString());
                                patientSymptom.setStartDate(addDate);
                                save(patientSymptom);
                            }
                        })
                        .cancelable(false)
                        .build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        form_symptom = (MaterialEditText)dialog.getCustomView().findViewById(R.id.form_symptom);
        btn_date_select = (TextView)dialog.getCustomView().findViewById(R.id.btn_date_select);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkDialog();
            }
        };
        form_symptom.addTextChangedListener(textWatcher);
        btn_date_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                addDate = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                                isDateSelect = true;
                                setDateText(btn_date_select, AdditionalFunc.getDateString(addDate));
                                checkDialog();
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setTitle(getString(R.string.take_period_start_srt));
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        dialog.show();
        positiveAction.setEnabled(false);

    }

    public void markCompleteDate(int index){

        PatientSymptom patientSymptom = list.get(index);

        progressDialog.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "updateSymptomFinishDate");
        map.put("symptom_history_id", patientSymptom.getId());
        map.put("date", Long.toString( AdditionalFunc.getTodayMilliseconds() ) );

        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                try {
                    JSONObject jObj = new JSONObject(data);
                    String status = jObj.getString("status");

                    if("success".equals(status)){
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MARK_SUCCESS));
                    }else{
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MARK_FAIL));
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MARK_FAIL));
                }

            }
        }.start();


    }

    private void checkDialog(){

        boolean isSymptom = form_symptom.isCharactersCountValid();

        boolean status = isSymptom && isDateSelect;

        positiveAction.setEnabled(status);

    }

    private void setDateText(TextView tv, String text){

        tv.setText(text);
        tv.setTextColor(getColorId(R.color.dark_gray));
        tv.setTypeface(tv.getTypeface(), Typeface.NORMAL);

    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getPatientSymptomList(){
        if(!isLoadFinish) {
            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getPatientSymptomList");
            map.put("patient_id", selectedPatient.getId());
            map.put("page", Integer.toString(page));

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = PatientSymptom.getPatientSymptomList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();
                        tempList = PatientSymptom.getPatientSymptomList(data);
                        if (tempList.size() < 30) {
                            isLoadFinish = true;
                        }
                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_ENDLESS_LIST));

                    }

                }
            }.start();
        }else{
            if(adapter != null){
                adapter.setLoaded();
            }
        }
    }

    public void makeList(){

        if(list.size() > 0){
            tv_msg.setVisibility(View.GONE);
        }else{
            tv_msg.setVisibility(View.VISIBLE);
            setFadeInAnimation(tv_msg);
        }

        adapter = new PatientSymptomListCustomAdapter(getApplicationContext(), list, rv, this, this);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getPatientSymptomList();
            }
        });

        adapter.notifyDataSetChanged();

    }

    private void addList(){

        for(int i=0; i<tempList.size(); i++){
            list.add(tempList.get(i));
            adapter.notifyItemInserted(list.size());
        }

        adapter.setLoaded();

    }

    @Override
    public void showView() {
        if(!isSupporter) {
            addPatientSymptomBtn.setVisibility(View.VISIBLE);
            setFadeInAnimation(addPatientSymptomBtn);
        }
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideView() {
        if(!isSupporter) {
            addPatientSymptomBtn.setVisibility(View.GONE);
            setFadeOutAnimation(addPatientSymptomBtn);
        }
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void redirectActivityForResult(Intent intent) {
        startActivityForResult(intent, 0);
    }

    @Override
    public void redirectActivity(Intent intent) {
        startActivity(intent);
    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_MAKE_LIST:
                    progressDialog.hide();
                    loading.hide();
                    makeList();
                    break;
                case MSG_MESSAGE_MAKE_ENDLESS_LIST:
                    progressDialog.hide();
                    loading.hide();
                    addList();
                    break;
                case MSG_MESSAGE_PROGRESS_HIDE:
                    progressDialog.hide();
                    loading.hide();
                    break;
                case MSG_MESSAGE_SUCCESS:
                    progressDialog.hide();
                    initLoadValue();
                    getPatientSymptomList();
                    break;
                case MSG_MESSAGE_FAIL:
                    progressDialog.hide();
                    new MaterialDialog.Builder(ManageSymptomActivity.this)
                            .title(R.string.fail_srt)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                case MSG_MESSAGE_MARK_SUCCESS:
                    progressDialog.hide();
                    initLoadValue();
                    getPatientSymptomList();
                    break;
                case MSG_MESSAGE_MARK_FAIL:
                    progressDialog.hide();
                    new MaterialDialog.Builder(ManageSymptomActivity.this)
                            .title(R.string.fail_srt)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case UPDATE_LIST:
                initLoadValue();
                getPatientSymptomList();
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    public boolean isSupporter() {
        return isSupporter;
    }
}
