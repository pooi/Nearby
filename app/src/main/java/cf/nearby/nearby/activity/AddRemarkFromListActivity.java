package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.adapter.RecordPatientMedicineListCustomAdapter;
import cf.nearby.nearby.adapter.RecordPatientRemarkListCustomAdapter;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.PatientMedicine;
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.Symptom;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;

public class AddRemarkFromListActivity extends BaseActivity implements OnAdapterSupport {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;

    private int page = 0;
    private ArrayList<Symptom> tempList;
    private ArrayList<Symptom> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private RecordPatientRemarkListCustomAdapter adapter;
    private boolean isLoadFinish;

    private Toolbar toolbar;
    private Button saveBtn;

    private ArrayList<PatientRemark> remarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remark_from_list);

        remarks = (ArrayList<PatientRemark>)getIntent().getSerializableExtra("remarks");

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

        getSymptomList();

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
                setResult(RecordRemarkActivity.UPDATE_LIST, intent);
                finish();
            }
        });

//        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
//        toolbarTitle.setText(selectedPatient.getName() + "님");

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

    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getSymptomList(){
        if(!isLoadFinish) {
            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getSymptomList");
            map.put("page", Integer.toString(page));

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = Symptom.getSymptomList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();
                        tempList = Symptom.getSymptomList(data);
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

        adapter = new RecordPatientRemarkListCustomAdapter(getApplicationContext(), list, remarks, rv, this, this);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getSymptomList();
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
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideView() {
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
