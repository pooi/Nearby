package cf.nearby.nearby.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.StartActivity;
import cf.nearby.nearby.adapter.MedicineSearchListCustomAdapter;
import cf.nearby.nearby.obj.Medicine;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;

public class SearchMedicineActivity extends BaseActivity implements OnAdapterSupport{

    public static final int SELECTED_MEDICINE = 1259;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;

    private int page = 0;
    private String searchName;
    private ArrayList<Medicine> tempList;
    private ArrayList<Medicine> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private MedicineSearchListCustomAdapter adapter;
    private boolean isLoadFinish;

    private Toolbar toolbar;
    private CardView searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medicine);

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        init();

        getMedicineList();
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


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchBtn = (CardView)findViewById(R.id.cv_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMedicine();
            }
        });

    }

    private void searchMedicine(){

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
                                searchName = "";
                                initLoadValue();
                                progressDialog.show();
                                getMedicineList();
                    }
                })
                .input(getResources().getString(R.string.please_enter_search_medicine_content), searchName, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String search = input.toString();
                        searchName = search;
                        initLoadValue();
                        progressDialog.show();
                        getMedicineList();

                    }
                })
                .show();

    }

    public void selectMedicine(final Medicine medicine){

        String content = "";
        content = "이 약을 새롭게 추가하겠습니까?\n\n" +
                "약명 : " + medicine.getName() + "\n" +
                "보험코드 : " + medicine.getCode() + "\n" +
                "제조회사 : " + medicine.getCompany() + "\n" +
                "용량 : " + medicine.getStandard() + medicine.getUnit();

        new MaterialDialog.Builder(this)
                .title(R.string.ok)
                .content(content)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent();
                        intent.putExtra("medicine", medicine);
                        setResult(SELECTED_MEDICINE, intent);
                        finish();
                    }
                })
                .show();

    }


    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getMedicineList(){
        if(!isLoadFinish) {
            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getMedicineList");
            map.put("page", Integer.toString(page));

            if (searchName != null && (!"".equals(searchName))) {
                map.put("name", searchName);
            }
            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = Medicine.getMedicineList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();
                        tempList = Medicine.getMedicineList(data);
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

        adapter = new MedicineSearchListCustomAdapter(getApplicationContext(), list, rv, this, this);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getMedicineList();
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
        searchBtn.setVisibility(View.VISIBLE);
        setFadeInAnimation(searchBtn);
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideView() {
        searchBtn.setVisibility(View.GONE);
        setFadeOutAnimation(searchBtn);
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
