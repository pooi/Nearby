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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.adapter.AllInOneRecordListCustomAdapter;
import cf.nearby.nearby.obj.HaveMeal;
import cf.nearby.nearby.obj.MainRecord;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;
import cf.nearby.nearby.util.RecordListSupporter;

public class InquiryDetailActivity extends BaseActivity implements OnAdapterSupport, RecordListSupporter{

    public static final int INQUIRY_TYPE_MEDICINE = 0;
    public static final int INQUIRY_TYPE_HAVE_MEAL = 1;
    public static final int INQUIRY_TYPE_REMARK = 2;

    private Patient selectedPatient;
    private int type;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_MARK_SUCCESS = 506;
    private final int MSG_MESSAGE_MARK_FAIL = 507;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;

    private int page = 0;
    private ArrayList<MainRecord> tempList;
    private ArrayList<MainRecord> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private AllInOneRecordListCustomAdapter adapter;
    private boolean isLoadFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_detail);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        type = getIntent().getIntExtra("type", -1);
        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

        getPatientMedicineList();

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

    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getPatientMedicineList(){

        if(!isLoadFinish) {
//            loading.show();
            HashMap<String, String> map = new HashMap<>();
            switch (type){
                case INQUIRY_TYPE_MEDICINE:
                    map.put("service", "inquiryPatientMedicine");
                    break;
                case INQUIRY_TYPE_HAVE_MEAL:
                    map.put("service", "inquiryPatientMeal");
                    break;
                case INQUIRY_TYPE_REMARK:
                    map.put("service", "inquiryPatientRemarks");
                    break;
            }
            map.put("patient_id", selectedPatient.getId());
            map.put("page", Integer.toString(page));

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        switch (type){
                            case INQUIRY_TYPE_MEDICINE:
                                list = MainRecord.getTakeMedicineGroupingList(TakeMedicine.getTakeMedicineList(data));
                                break;
                            case INQUIRY_TYPE_HAVE_MEAL:
                                list = MainRecord.getHaveMealGroupingList(HaveMeal.getHaveMealList(data));
                                break;
                            case INQUIRY_TYPE_REMARK:
                                list = MainRecord.getPatientRemarkGroupingList(PatientRemark.getPatientRemarkList(data));
                                break;
                        }

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();

                        switch (type){
                            case INQUIRY_TYPE_MEDICINE:
                                tempList = MainRecord.getTakeMedicineGroupingList(TakeMedicine.getTakeMedicineList(data));
                                break;
                            case INQUIRY_TYPE_HAVE_MEAL:
                                tempList = MainRecord.getHaveMealGroupingList(HaveMeal.getHaveMealList(data));
                                break;
                            case INQUIRY_TYPE_REMARK:
                                tempList = MainRecord.getPatientRemarkGroupingList(PatientRemark.getPatientRemarkList(data));
                                break;
                        }

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


        adapter = new AllInOneRecordListCustomAdapter(getApplicationContext(), list, rv, this, this);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getPatientMedicineList();
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
                case MSG_MESSAGE_MAKE_ENDLESS_LIST:
//                    progressDialog.hide();
                    loading.hide();
                    addList();
                    break;
                case MSG_MESSAGE_PROGRESS_HIDE:
//                    progressDialog.hide();
                    loading.hide();
                    break;
                case MSG_MESSAGE_MARK_SUCCESS:
//                    progressDialog.hide();
                    initLoadValue();
                    getPatientMedicineList();
                    break;
                case MSG_MESSAGE_MARK_FAIL:
//                    progressDialog.hide();
                    new MaterialDialog.Builder(InquiryDetailActivity.this)
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
    public void showView() {
//        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideView() {
//        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void redirectActivityForResult(Intent intent) {
        startActivityForResult(intent, 0);
    }

    @Override
    public void redirectActivity(Intent intent) {
        startActivity(intent);
    }

//    @Override
//    public int getColorId(int id){
//        return super.getColorId(id);
//    }


}
