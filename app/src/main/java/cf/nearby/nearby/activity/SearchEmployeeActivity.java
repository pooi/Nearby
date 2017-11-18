package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import cf.nearby.nearby.adapter.EmployeeSearchListCustomAdapter;
import cf.nearby.nearby.adapter.PatientSearchListCustomAdapter;
import cf.nearby.nearby.nurse.NurseManageDetailActivity;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.Employee;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;
import cf.nearby.nearby.util.SearchPatientSupporter;

public class SearchEmployeeActivity extends BaseActivity implements OnAdapterSupport, SearchPatientSupporter {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;

    private String searchName;
    private ArrayList<Employee> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private EmployeeSearchListCustomAdapter adapter;

    private Patient selectedPatient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_employee);

        Intent intent = getIntent();
        selectedPatient = (Patient)intent.getSerializableExtra("patient");

        list = new ArrayList<>();

        init();

        getEmployeeList();

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

    private void getEmployeeList(){
        loading.show();
        HashMap<String, String> map = new HashMap<>();
        map.put("service", "inquiryPatientRelatedNurse");
        map.put("patient_id", selectedPatient.getId());

        if (searchName != null && (!"".equals(searchName))) {
            map.put("name", searchName);
        }
        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                list.clear();

                list = Employee.getEmployeeList(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));

            }
        }.start();
    }

    public void makeList(){

        adapter = new EmployeeSearchListCustomAdapter(getApplicationContext(), list, rv, this, this);

        rv.setAdapter(adapter);

//        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                page+=1;
//                getEmployeeList();
//            }
//        });

        adapter.notifyDataSetChanged();

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
        startActivity(intent);
    }

    @Override
    public void redirectNextActivity(Patient patient){

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
