package cf.nearby.nearby.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.InquiryDetailActivity;
import cf.nearby.nearby.adapter.AllInOneInquiryListCustomAdapter;
import cf.nearby.nearby.adapter.AllInOneInquiryVitalListCustomAdapter;
import cf.nearby.nearby.obj.HaveMeal;
import cf.nearby.nearby.obj.MainRecord;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.obj.VitalSign;
import cf.nearby.nearby.util.DividerItemDecoration;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.ParsePHP;
import cf.nearby.nearby.util.RecordListSupporter;

/**
 * Created by tw on 2017-06-01.
 */
public class InquiryVitalSignFragment extends BaseFragment implements OnAdapterSupport, RecordListSupporter {

    public static final int TYPE_PULSE = 0;
    public static final int TYPE_TEMPERATURE = 1;
    public static final int TYPE_BP = 2;

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_MAKE_LIST = 500;
    private final int MSG_MESSAGE_MAKE_ENDLESS_LIST = 501;
    private final int MSG_MESSAGE_PROGRESS_HIDE = 502;
    private final int MSG_MESSAGE_MARK_SUCCESS = 506;
    private final int MSG_MESSAGE_MARK_FAIL = 507;
    private final int MSG_MESSAGE_SHOW_GRAPH = 508;
    private final int MSG_MESSAGE_HIDE_GRAPH = 509;

    // BASIC UI
    private View view;
    private Context context;

    // DATA
    private int position;
    private Patient selectedPatient;
    private String title;
    private int type;

    // UI
    private RelativeLayout root;
    private TextView tv_title;
    private CardView cv_graph;

    // Chart
    private LineChartView mChart;

    private TextView tv_msg;
    private AVLoadingIndicatorView loading;

    private int page = 0;
    private ArrayList<MainRecord> tempList;
    private ArrayList<MainRecord> list;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private AllInOneInquiryVitalListCustomAdapter adapter;
    private boolean isLoadFinish;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            position = getArguments().getInt("position");
            selectedPatient = (Patient)getArguments().getSerializable("patient");
            title = getArguments().getString("title");
            type = getArguments().getInt("type");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inquiry_vital_sign, container, false);
        context = container.getContext();

        list = new ArrayList<>();
        tempList = new ArrayList<>();

        initUI();

        getPatientVitalSignList();

        return view;


    }

    private void initUI(){

        root = (RelativeLayout)view.findViewById(R.id.root);

        tv_title = (TextView)view.findViewById(R.id.tv_title);
        cv_graph = (CardView)view.findViewById(R.id.cv_graph);
        cv_graph.setVisibility(View.GONE);

        mChart = (LineChartView)view.findViewById(R.id.chart_weight);

        tv_title.setText(title);

        tv_msg = (TextView)view.findViewById(R.id.tv_msg);
        tv_msg.setVisibility(View.GONE);

        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLinearLayoutManager);
//        rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));

        loading = (AVLoadingIndicatorView)view.findViewById(R.id.loading);

    }

    private void initLoadValue(){
        page = 0;
        isLoadFinish = false;
    }

    private void getPatientVitalSignList(){

        if(!isLoadFinish) {
//            loading.show();
            HashMap<String, String> map = new HashMap<>();
            map.put("service", "inquiryPatientVitalSign");
            switch (type){
                case TYPE_PULSE:
                    map.put("type", "pulse");
                    break;
                case TYPE_TEMPERATURE:
                    map.put("type", "temperature");
                    break;
                case TYPE_BP:
                    map.put("type", "bp");
                    break;
            }
            map.put("patient_id", selectedPatient.getId());
            map.put("page", Integer.toString(page));

            new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

                @Override
                protected void afterThreadFinish(String data) {

                    if (page <= 0) {
                        list.clear();

                        list = MainRecord.getVitalSignGroupingList(VitalSign.getVitalSignList(data));
                        makeChart(list);

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_MAKE_LIST));
                    } else {

                        tempList.clear();

                        tempList = MainRecord.getVitalSignGroupingList(VitalSign.getVitalSignList(data));

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


        adapter = new AllInOneInquiryVitalListCustomAdapter(context, list, rv, this, this, type);

        rv.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page+=1;
                getPatientVitalSignList();
            }
        });

        adapter.notifyDataSetChanged();

    }

    private void makeChart(ArrayList<MainRecord> list){


        if(list.size() > 0) {

            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_GRAPH));

            mChart.reset();

            float min = 1000000.0f;
            float max = -1000000.0f;

            LineSet dataset = new LineSet();
            for (int i = Math.max(0, list.size() - 6); i < list.size(); i++) {
                MainRecord mr = list.get(i);
                for (MainRecord mr2 : mr.getGroupList()) {
                    VitalSign vs = (VitalSign) mr2;

                    Double data = 0.0;// = vs.getTemperature();
                    switch (type) {
                        case TYPE_PULSE:
                            data = vs.getPulse();
                            break;
                        case TYPE_TEMPERATURE:
                            data = vs.getTemperature();
                            break;
                        case TYPE_BP:
                            break;
                    }
                    dataset.addPoint(vs.getRegisteredDateStringSrt(), Float.parseFloat(data + ""));

                    if (data < min)
                        min = Float.parseFloat(data + "");
                    if (max < data)
                        max = Float.parseFloat(data + "");

                }
            }
            dataset.setColor(Color.parseColor("#53c1bd"))
                    .setFill(Color.parseColor("#3d6c73"))
                    .setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")},
                            null);
            mChart.addData(dataset);

            dataset = new LineSet();
            for (int i = Math.max(0, list.size() - 6); i < list.size(); i++) {
                MainRecord mr = list.get(i);
                for (MainRecord mr2 : mr.getGroupList()) {
                    VitalSign vs = (VitalSign) mr2;
                    switch (type) {
                        case TYPE_PULSE:
                            dataset.addPoint(vs.getRegisteredDateStringSrt(), Float.parseFloat(vs.getPulse() + ""));
                            break;
                        case TYPE_TEMPERATURE:
                            dataset.addPoint(vs.getRegisteredDateStringSrt(), Float.parseFloat(vs.getTemperature() + ""));
                            break;
                        case TYPE_BP:
                            break;
                    }

                }
            }
            dataset.setColor(Color.parseColor("#b3b5bb"))
                    .setFill(Color.parseColor("#2d374c"))
                    .setDotsColor(Color.parseColor("#ffc755"))
                    .setThickness(4);
            mChart.addData(dataset);

            mChart.setAxisBorderValues(min - 5, max + 5);

            mChart.show();

        }else{
            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_HIDE_GRAPH));
        }

    }

    private void addList(){

        for(int i=0; i<tempList.size(); i++){
            list.add(tempList.get(i));
            adapter.notifyItemInserted(list.size());
        }

        makeChart(list);

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
                    getPatientVitalSignList();
                    break;
                case MSG_MESSAGE_MARK_FAIL:
//                    progressDialog.hide();
                    new MaterialDialog.Builder(context)
                            .title(R.string.fail_srt)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                case MSG_MESSAGE_SHOW_GRAPH:
                    cv_graph.setVisibility(View.VISIBLE);
                    break;
                case MSG_MESSAGE_HIDE_GRAPH:
                    cv_graph.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void showView() {
//        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        cv_graph.setVisibility(View.VISIBLE);
        setFadeInAnimation(cv_graph);
    }

    @Override
    public void hideView() {
        cv_graph.setVisibility(View.GONE);
        setFadeOutAnimation(cv_graph);
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

    @Override
    public int getColorId(int id) {
        return super.getColorId(context, id);
    }

}
