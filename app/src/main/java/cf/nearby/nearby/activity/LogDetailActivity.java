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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.ppamorim.dragger.DraggerPosition;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.adapter.LogListCustomAdapter;
import cf.nearby.nearby.adapter.PatientSearchListCustomAdapter;
import cf.nearby.nearby.nurse.NurseManageDetailActivity;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.NearbyLog;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;

public class LogDetailActivity extends BaseActivity implements OnAdapterSupport {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;

    private CardView cv_search;

    private int page = 0;
    private String searchMsg;
    private Long searchStartDate;
    private Long searchFinishDate;
    private MaterialEditText form_msg;
    private TextView btn_searchStartDate;
    private TextView btn_searchFinishDate;
    private ArrayList<NearbyLog> tempList;
    private ArrayList<NearbyLog> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private LogListCustomAdapter adapter;
    private boolean isLoadFinish;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_detail);

        type = getIntent().getStringExtra("type");

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

        getLogList();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogDetailActivity.this.finish();
            }
        });

        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLinearLayoutManager);
//        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));

        cv_search = (CardView)findViewById(R.id.cv_search);
        cv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchMenu();
            }
        });

        loading = (AVLoadingIndicatorView)findViewById(R.id.loading);
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .build();

    }

    private void searchLogMsg(){

        new MaterialDialog.Builder(this)
                .title(R.string.search_srt)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .theme(Theme.LIGHT)
                .positiveText(R.string.search_srt)
                .negativeText(R.string.cancel)
                .neutralText(R.string.reset)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        searchMsg = "";
                        initLoadValue();
                        progressDialog.show();
                        getLogList();
                    }
                })
                .input(getResources().getString(R.string.please_input_patient_name), searchMsg, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String search = input.toString();
                        searchMsg = search;
                        initLoadValue();
                        progressDialog.show();
                        getLogList();

                    }
                })
                .show();

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
                                searchMsg = form_msg.getText().toString();
                                initLoadValue();
                                progressDialog.show();
                                getLogList();
                            }
                        })
                        .neutralText(R.string.reset)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                searchMsg = "";
                                searchStartDate = null;
                                searchFinishDate = null;
                                initLoadValue();
                                progressDialog.show();
                                getLogList();
                            }
                        })
                        .cancelable(false)
                        .build();

//        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        form_msg = (MaterialEditText)dialog.getCustomView().findViewById(R.id.form_msg);
        btn_searchStartDate = (TextView)dialog.getCustomView().findViewById(R.id.btn_start_date_select);
        btn_searchFinishDate = (TextView)dialog.getCustomView().findViewById(R.id.btn_finish_date_select);

        form_msg.setText(searchMsg);
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


    private void setDateText(TextView tv, String text){

        tv.setText(text);
        tv.setTextColor(getColorId(R.color.dark_gray));
        tv.setTypeface(tv.getTypeface(), Typeface.NORMAL);

    }

    private void getLogList(){
        if(!isLoadFinish) {
            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "inquiryLog");
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
            map.put("type", type);
            map.put("location_id", StartActivity.employee.getLocation().getId());
            map.put("page", page + "");
            if (searchMsg != null && (!"".equals(searchMsg))) {
                map.put("msg", searchMsg);
            }

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = NearbyLog.getLogList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();
                        tempList = NearbyLog.getLogList(data);
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

        adapter = new LogListCustomAdapter(getApplicationContext(), list, rv, this);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getLogList();
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
