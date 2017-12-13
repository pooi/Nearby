package cf.nearby.nearby.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.AddRemarkFromListActivity;
import cf.nearby.nearby.activity.RecordMedicineActivity;
import cf.nearby.nearby.activity.RecordRemarkActivity;
import cf.nearby.nearby.activity.ShowPatientMedicineDetailActivity;
import cf.nearby.nearby.obj.PatientMedicine;
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.Symptom;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;


/**
 * Created by tw on 2017-10-01.
 */
public class RecordPatientRemarkListCustomAdapter extends RecyclerView.Adapter<RecordPatientRemarkListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private AddRemarkFromListActivity activity;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<Symptom> list;
    private ArrayList<PatientRemark> remarks;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public RecordPatientRemarkListCustomAdapter(Context context, ArrayList<Symptom> list, ArrayList<PatientRemark> remarks, RecyclerView recyclerView, OnAdapterSupport listener, AddRemarkFromListActivity activity) {
        this.context = context;
        this.remarks = remarks;
        this.list = list;
        this.onAdapterSupport = listener;
        this.activity = (AddRemarkFromListActivity) activity;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            recyclerView.addOnScrollListener(new ScrollListener() {
                @Override
                public void onHide() {
                    hideViews();
                }

                @Override
                public void onShow() {
                    showViews();
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //recycler view에 반복될 아이템 레이아웃 연결
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_remark_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Symptom symptom = list.get(position);
        final int pos = position;

        holder.tv_title.setText(symptom.getDescription());

        holder.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record(holder, symptom);
            }
        });

        changeBtnColor(holder, containRemark(symptom));

    }

    private boolean containRemark(Symptom symptom){
        for(PatientRemark pr : remarks){
            if(pr.getDescription() != null){
                if(pr.getDescription().equals(symptom.getDescription())){
                    return true;
                }
            }
        }
        return false;
    }

    private void record(ViewHolder holder, Symptom symptom){

        if(containRemark(symptom)){ // 이미 목록에 있을 경우
            int index = -1;
            for(int i=0; i<remarks.size(); i++){
                String description = remarks.get(i).getDescription();
                if(description != null && description.equals(symptom.getDescription())){
                    index = i;
                    break;
                }
            }
            if(0 <= index && index < remarks.size()){
                remarks.remove(index);
            }
            changeBtnColor(holder, false);
        }else{ // 목록에 없을 경우
            PatientRemark pr = new PatientRemark();
            pr.setSymptomId(symptom.getId());
            pr.setDescription(symptom.getDescription());
            remarks.add(pr);
            changeBtnColor(holder, true);
        }

    }

    private void changeBtnColor(ViewHolder holder, boolean check){
        CardView cv = holder.cv;
        Button btn_select = holder.btn_select;
        if(check){
            cv.setCardBackgroundColor(activity.getColorId(R.color.colorPrimary));
            for(int i=0; i<cv.getChildCount(); i++){
                ViewGroup vg = (ViewGroup)cv.getChildAt(i);
                for(int k=0; k<Math.min(1,vg.getChildCount()); k++){
                    ViewGroup vg2 = (ViewGroup)vg.getChildAt(k);
                    for(int j=0; j<vg.getChildCount(); j++){
                        View v = vg2.getChildAt(j);
                        if(v instanceof TextView){
                            ((TextView) v).setTextColor(activity.getColorId(R.color.white));
                        }
                    }
                }
            }
            btn_select.setText(R.string.cancel);
        }else{
            cv.setCardBackgroundColor(activity.getColorId(R.color.white));
            for(int i=0; i<cv.getChildCount(); i++){
                ViewGroup vg = (ViewGroup)cv.getChildAt(i);
                for(int k=0; k<Math.min(1,vg.getChildCount()); k++){
                    ViewGroup vg2 = (ViewGroup)vg.getChildAt(k);
                    for(int j=0; j<vg.getChildCount(); j++){
                        View v = vg2.getChildAt(j);
                        if(v instanceof TextView){
                            ((TextView) v).setTextColor(activity.getColorId(R.color.dark_gray));
                        }
                    }
                }
            }
            btn_select.setText(R.string.add_srt);
        }

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    private void hideViews() {
        if (onAdapterSupport != null) {
            onAdapterSupport.hideView();
        }
    }

    private void showViews() {
        if (onAdapterSupport != null) {
            onAdapterSupport.showView();
        }
    }

    // 무한 스크롤
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setLoaded() {
        loading = false;
    }

    public abstract class ScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 30;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            totalItemCount = linearLayoutManager.getItemCount();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                // End has been reached
                // Do something
                loading = true;
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore();
                }
            }
            // 여기까지 무한 스크롤

            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }

            if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                scrolledDistance += dy;
            }
            // 여기까지 툴바 숨기기
        }

        public abstract void onHide();
        public abstract void onShow();

    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {

        Rect rect;

        CardView cv;
        TextView tv_title;
        Button btn_select;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            setCardButtonOnTouchAnimation(cv);
            tv_title = (TextView)v.findViewById(R.id.tv_title);
            btn_select = (Button)v.findViewById(R.id.btn_select);
        }

        public void setCardButtonOnTouchAnimation(final View v){

            View.OnTouchListener onTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
//                System.out.println(motionEvent.getAction());
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN: {
                            rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
//                        System.out.println("action down");
                            Animation anim = new ScaleAnimation(
                                    1f, 0.95f, // Start and end values for the X axis scaling
                                    1f, 0.95f, // Start and end values for the Y axis scaling
                                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                            anim.setFillAfter(true); // Needed to keep the result of the animation
                            anim.setDuration(300);
                            v.startAnimation(anim);
                            v.requestLayout();
                            return true;
                        }
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP: {
//                        System.out.println("action up");
                            Animation anim = new ScaleAnimation(
                                    0.95f, 1f, // Start and end values for the X axis scaling
                                    0.95f, 1f, // Start and end values for the Y axis scaling
                                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                            anim.setFillAfter(true); // Needed to keep the result of the animation
                            anim.setDuration(300);
                            v.startAnimation(anim);
                            v.requestLayout();
                            if(!rect.contains(v.getLeft() + (int) motionEvent.getX(), v.getTop() + (int) motionEvent.getY())){
                                // User moved outside bounds
                            }else{
                                v.callOnClick();
                            }
                            return true;
                        }
                    }
                    return false;
                }
            };
            v.setOnTouchListener(onTouchListener);
        }

    }

}
