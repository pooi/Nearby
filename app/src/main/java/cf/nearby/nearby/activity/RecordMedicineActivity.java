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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.adapter.PatientMedicineListCustomAdapter;
import cf.nearby.nearby.adapter.RecordPatientMedicineListCustomAdapter;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.Medicine;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientMedicine;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;

public class RecordMedicineActivity extends BaseActivity implements OnAdapterSupport {

    public static final int UPDATE_LIST = 100;

    private TextView toolbarTitle;

    private Patient selectedPatient;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;
    private MaterialDialog progressDialog;

    private int page = 0;
    private ArrayList<PatientMedicine> tempList;
    private ArrayList<PatientMedicine> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private RecordPatientMedicineListCustomAdapter adapter;
    private boolean isLoadFinish;

    private Toolbar toolbar;
    private CardView etcBtn;
    private Button saveBtn;

    private LinearLayout li_etcList;

    private ArrayList<TakeMedicine> takeMedicines;
//    private ArrayList<Medicine> etcMedicines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_medicine);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        takeMedicines = (ArrayList<TakeMedicine>)getIntent().getSerializableExtra("take_medicines");
//        etcMedicines = (ArrayList<Medicine>)getIntent().getSerializableExtra("etc_medicines");

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

        saveBtn = (Button)findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("take_medicines", takeMedicines);
//                intent.putExtra("etc_medicines", etcMedicines);
                setResult(NurseRecordActivity.UPDATE_MEDICINE, intent);
                finish();
            }
        });

//        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
//        toolbarTitle.setText(selectedPatient.getName() + "님");

        tv_msg = (TextView)findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        li_etcList = (LinearLayout)findViewById(R.id.li_etc_list);

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
        setCardButtonOnTouchAnimation(findViewById(R.id.cv_etc));
        etcBtn = (CardView)findViewById(R.id.cv_etc);
        etcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordMedicineActivity.this, SearchMedicineActivity.class);
                startActivityForResult(intent, SearchMedicineActivity.SELECTED_MEDICINE);
            }
        });

        makeEtcMedicineList();

    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getPatientMedicineList(){
        if(!isLoadFinish) {
            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "getPatientMedicineList");
            map.put("patient_id", selectedPatient.getId());
            map.put("isAvailable", "1");
            map.put("page", Integer.toString(page));

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = PatientMedicine.getPatientMedicineList(data);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();
                        tempList = PatientMedicine.getPatientMedicineList(data);
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

        adapter = new RecordPatientMedicineListCustomAdapter(getApplicationContext(), list, takeMedicines, rv, this, this);

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

    private void makeEtcMedicineList(){

        li_etcList.removeAllViews();

        for(int i=0; i<takeMedicines.size(); i++){

            if(takeMedicines.get(i).getPatientMedicine() == null) {

                Medicine medicine = takeMedicines.get(i).getMedicine();

                View v = getLayoutInflater().inflate(R.layout.medicine_list_custom_item, null, false);

                TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
                tv_name.setGravity(View.TEXT_ALIGNMENT_CENTER);
                TextView tv_code = (TextView) v.findViewById(R.id.tv_code);
                tv_code.setGravity(View.TEXT_ALIGNMENT_CENTER);
                Button btn_select = (Button) v.findViewById(R.id.btn_select);

                tv_name.setText(medicine.getNameSrt(30));
                tv_code.setText(medicine.getCode());

                btn_select.setBackgroundResource(R.drawable.two_btn_active_right_radius_red);
                btn_select.setText(R.string.delete_srt);
                btn_select.setTag(i);
                btn_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int) view.getTag();
                        takeMedicines.remove(index);
                        makeEtcMedicineList();
                    }
                });

                li_etcList.addView(v);

            }

        }

//        for(int i=0; i<etcMedicines.size(); i++){
//
//            Medicine medicine = etcMedicines.get(i);
//
//            View v = getLayoutInflater().inflate(R.layout.medicine_list_custom_item, null, false);
//
//            TextView tv_name = (TextView)v.findViewById(R.id.tv_name);
//            tv_name.setGravity(View.TEXT_ALIGNMENT_CENTER);
//            TextView tv_code = (TextView)v.findViewById(R.id.tv_code);
//            tv_code.setGravity(View.TEXT_ALIGNMENT_CENTER);
//            Button btn_select = (Button)v.findViewById(R.id.btn_select);
//
//            tv_name.setText(medicine.getNameSrt(30));
//            tv_code.setText(medicine.getCode());
//
//            btn_select.setBackgroundResource(R.drawable.two_btn_active_right_radius_red);
//            btn_select.setText(R.string.delete_srt);
//            btn_select.setTag(i);
//            btn_select.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int index = (int)view.getTag();
//                    etcMedicines.remove(index);
//                    makeEtcMedicineList();
//                }
//            });
//
//            li_etcList.addView(v);
//
//        }


    }

    @Override
    public void showView() {
//        etcBtn.setVisibility(View.VISIBLE);
//        setFadeInAnimation(etcBtn);
//        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideView() {
//        etcBtn.setVisibility(View.GONE);
//        setFadeOutAnimation(etcBtn);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case UPDATE_LIST:
                initLoadValue();
                getPatientMedicineList();
                break;
            case SearchMedicineActivity.SELECTED_MEDICINE:
                Medicine medicine = (Medicine)data.getSerializableExtra("medicine");
                TakeMedicine takeMedicine = new TakeMedicine();
                takeMedicine.setMedicine(medicine);
                takeMedicines.add(takeMedicine);
//                etcMedicines.add(medicine);
                makeEtcMedicineList();
//                addMedicine(medicine);
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
}
