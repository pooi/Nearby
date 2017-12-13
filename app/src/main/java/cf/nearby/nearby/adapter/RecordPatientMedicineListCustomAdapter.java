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
import cf.nearby.nearby.activity.ManageMedicineActivity;
import cf.nearby.nearby.activity.RecordMedicineActivity;
import cf.nearby.nearby.activity.ShowPatientMedicineDetailActivity;
import cf.nearby.nearby.obj.PatientMedicine;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;


/**
 * Created by tw on 2017-10-01.
 */
public class RecordPatientMedicineListCustomAdapter extends RecyclerView.Adapter<RecordPatientMedicineListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private RecordMedicineActivity activity;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<PatientMedicine> list;
    private ArrayList<TakeMedicine> takeMedicines;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public RecordPatientMedicineListCustomAdapter(Context context, ArrayList<PatientMedicine> list, ArrayList<TakeMedicine> takeMedicines, RecyclerView recyclerView, OnAdapterSupport listener, RecordMedicineActivity activity) {
        this.context = context;
        this.takeMedicines = takeMedicines;
        this.list = list;
        this.onAdapterSupport = listener;
        this.activity = (RecordMedicineActivity) activity;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_patient_medicine_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PatientMedicine pm = list.get(position);
        final int pos = position;

        holder.tv_title.setText(pm.getTitle());
        holder.tv_period.setText(pm.getPeriodString());

        holder.btn_show_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowPatientMedicineDetailActivity.class);
                intent.putExtra("patient_medicine", pm);
                onAdapterSupport.redirectActivity(intent);
            }
        });

        holder.btn_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record(holder, pm);
            }
        });

        changeBtnColor(holder, containTakeMedicine(pm));

    }

    private boolean containTakeMedicine(PatientMedicine pm){
        for(TakeMedicine tm : takeMedicines){
            if(tm.getPatientMedicine() != null){
                if(tm.getPatientMedicine().getId().equals(pm.getId())){
                    return true;
                }
            }
        }
        return false;
    }

    private void record(ViewHolder holder, PatientMedicine pm){

        if(containTakeMedicine(pm)){ // 이미 목록에 있을 경우
            int index = -1;
            for(int i=0; i<takeMedicines.size(); i++){
                PatientMedicine tempPm = takeMedicines.get(i).getPatientMedicine();
                if(tempPm != null && tempPm.getId().equals(pm.getId())){
                    index = i;
                    break;
                }
            }
            if(0 <= index && index < takeMedicines.size()){
                takeMedicines.remove(index);
            }
            changeBtnColor(holder, false);
        }else{ // 목록에 없을 경우
            TakeMedicine takeMedicine = new TakeMedicine();
            takeMedicine.setPatientMedicine(pm);
            takeMedicines.add(takeMedicine);
            changeBtnColor(holder, true);
        }

    }

    private void changeBtnColor(ViewHolder holder, boolean check){
        CardView cv = holder.cv;
        Button btn_medicine = holder.btn_medicine;
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
            btn_medicine.setText(R.string.cancel);
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
            btn_medicine.setText(R.string.take_medicine_srt);
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
        TextView tv_period;
        Button btn_medicine;
        Button btn_show_detail;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            setCardButtonOnTouchAnimation(cv);
            tv_title = (TextView)v.findViewById(R.id.tv_title);
            tv_period = (TextView)v.findViewById(R.id.tv_period);
            btn_medicine = (Button)v.findViewById(R.id.btn_medicine);
            btn_show_detail = (Button)v.findViewById(R.id.btn_show_detail);
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
