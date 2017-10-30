package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.adapter.PatientSearchListCustomAdapter;
import cf.nearby.nearby.nurse.NurseManageDetailActivity;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.OnSearchPatientSupport;
import cf.nearby.nearby.util.ParsePHP;

public class SearchPatientActivity extends BaseActivity implements OnAdapterSupport {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;

    private int page = 0;
    private String searchName;
    private ArrayList<Patient> tempList;
    private ArrayList<Patient> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private PatientSearchListCustomAdapter adapter;
    private boolean isLoadFinish;

    private String nextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_patient);

        Intent intent = getIntent();
        nextActivity = intent.getStringExtra("nextActivity");
        if(nextActivity == null){
            nextActivity = "";
        }

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

        getPatientList();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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


    }


    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getPatientList(){
        if(!isLoadFinish) {
            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getPatientList");
            map.put("location_id", StartActivity.employee.getLocation().getId());
            map.put("page", Integer.toString(page));

            if (searchName != null && (!"".equals(searchName))) {
                map.put("name", searchName);
            }
            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = Patient.getPatientList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();
                        tempList = Patient.getPatientList(data);
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

        adapter = new PatientSearchListCustomAdapter(getApplicationContext(), list, rv, this, this);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getPatientList();
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

    }

    @Override
    public void hideView() {

    }

    @Override
    public void redirectActivityForResult(Intent intent) {
        startActivityForResult(intent, 0);
    }

    @Override
    public void redirectActivity(Intent intent) {

    }

    public void redirectNextActivity(Patient patient){
        switch (nextActivity){
            case Information.NURSE_MANAGE_MENU: {
                Intent intent = new Intent(SearchPatientActivity.this, NurseManageDetailActivity.class);
                intent.putExtra("patient", patient);
                startActivity(intent);
                break;
            }
            case Information.NURSE_RECORD_MENU: {
                Intent intent = new Intent(SearchPatientActivity.this, NurseRecordActivity.class);
                intent.putExtra("patient", patient);
                startActivity(intent);
                break;
            }
            case Information.MANAGE_SUPPORTER_MENU:{
                Intent intent = new Intent(SearchPatientActivity.this, ManageSupporterActivity.class);
                intent.putExtra("patient", patient);
                startActivity(intent);
                break;
            }
            case Information.INQUIRY_MAIN_MENU:{
                Intent intent = new Intent(SearchPatientActivity.this, InquiryMainActivity.class);
                intent.putExtra("patient", patient);
                startActivity(intent);
                break;
            }
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
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
