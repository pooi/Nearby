package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.adapter.AllInOneInquiryListCustomAdapter;
import cf.nearby.nearby.obj.HaveMeal;
import cf.nearby.nearby.obj.MainRecord;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.util.AdditionalFunc;
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
    private Long searchStartDate;
    private Long searchFinishDate;
    private ArrayList<MainRecord> tempList;
    private ArrayList<MainRecord> list;

    // Search
    private TextView btn_searchStartDate;
    private TextView btn_searchFinishDate;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private AllInOneInquiryListCustomAdapter adapter;
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

        findViewById(R.id.cv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchMenu();
            }
        });

    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void showSearchMenu(){

        MaterialDialog dialog =
                new MaterialDialog.Builder(this)
                        .title(R.string.search_srt)
                        .customView(R.layout.search_log_custom_layout, true)
                        .positiveText(R.string.search_srt)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                search = form_msg.getText().toString();
                                initLoadValue();
//                                progressDialog.show();
                                getPatientMedicineList();
                            }
                        })
                        .neutralText(R.string.reset)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                search = "";
                                searchStartDate = null;
                                searchFinishDate = null;
                                initLoadValue();
//                                progressDialog.show();
                                getPatientMedicineList();
                            }
                        })
                        .cancelable(false)
                        .build();

//        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        dialog.getCustomView().findViewById(R.id.tv_msg).setVisibility(View.GONE);
        dialog.getCustomView().findViewById(R.id.form_msg).setVisibility(View.GONE);
//        form_msg = (MaterialEditText)dialog.getCustomView().findViewById(R.id.form_msg);
        btn_searchStartDate = (TextView)dialog.getCustomView().findViewById(R.id.btn_start_date_select);
        btn_searchFinishDate = (TextView)dialog.getCustomView().findViewById(R.id.btn_finish_date_select);

//        form_msg.setText(searchMsg);
        if(searchStartDate != null){
            setDateText(btn_searchStartDate, AdditionalFunc.getDateString(searchStartDate));
        }
        if(searchFinishDate != null){
            setDateText(btn_searchFinishDate, AdditionalFunc.getDateString(searchFinishDate));
        }
        btn_searchStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                searchStartDate = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                                setDateText(btn_searchStartDate, AdditionalFunc.getDateString(searchStartDate));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setTitle(getString(R.string.search_start_date));
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        btn_searchFinishDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                searchFinishDate = AdditionalFunc.getMilliseconds(year, monthOfYear+1, dayOfMonth);
                                setDateText(btn_searchFinishDate, AdditionalFunc.getDateString(searchFinishDate));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setTitle(getString(R.string.search_finish_date));
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        dialog.show();
//        positiveAction.setEnabled(false);


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
            if(searchStartDate != null){
                map.put("isDate", "1");
                map.put("start_date", searchStartDate.toString());
                if(searchFinishDate != null)
                    map.put("finish_date", (searchFinishDate + 86400000) + "");
                else
                    map.put("finish_date", (AdditionalFunc.getTodayMilliseconds() + 86400000) + "");
            }else if(searchFinishDate != null){
                map.put("isDate", "1");
                map.put("start_date", AdditionalFunc.getMilliseconds(1950, 1, 1) + "");
                map.put("finish_date", (searchFinishDate + 86400000) + "");
            }
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


        adapter = new AllInOneInquiryListCustomAdapter(getApplicationContext(), list, rv, this, this);

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


}
