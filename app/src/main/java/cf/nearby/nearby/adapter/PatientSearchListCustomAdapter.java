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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ppamorim.dragger.DraggerPosition;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.PatientDetailActivity;
import cf.nearby.nearby.activity.SearchPatientActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.SearchPatientSupporter;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


/**
 * Created by tw on 2017-10-01.
 */
public class PatientSearchListCustomAdapter extends RecyclerView.Adapter<PatientSearchListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
//    private SearchPatientActivity activity;
    private SearchPatientSupporter supporter;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<Patient> list;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public PatientSearchListCustomAdapter(Context context, ArrayList<Patient> list, RecyclerView recyclerView, OnAdapterSupport listener, SearchPatientSupporter supporter) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;
//        this.activity = (SearchPatientActivity) activity;
        this.supporter = supporter;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_search_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Patient patient = list.get(position);
        final int pos = position;

        if(patient.getPic() != null && !"".equals(patient.getPic())) {
            Picasso.with(context)
                    .load(patient.getPic())
                    .into(holder.img);
        }
        holder.tv_name.setText(patient.getName());
        holder.tv_dob.setText(AdditionalFunc.getDateString((long)patient.getDob()));
        holder.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supporter.redirectNextActivity(patient);
            }
        });
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PatientDetailActivity.class);
                intent.putExtra("patient", patient);
                intent.putExtra("drag_position", DraggerPosition.TOP);
                onAdapterSupport.redirectActivity(intent);
            }
        });


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
        RoundedImageView img;
        TextView tv_name;
        TextView tv_dob;
        Button btn_select;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            setCardButtonOnTouchAnimation(cv, cv);
            img = (RoundedImageView) v.findViewById(R.id.img);
            tv_name = (TextView)v.findViewById(R.id.tv_name);
            tv_dob = (TextView)v.findViewById(R.id.tv_dob);
            btn_select = (Button)v.findViewById(R.id.btn_select);
            setCardButtonOnTouchAnimation(btn_select, cv);
        }

        public void setCardButtonOnTouchAnimation(final View v, final View animV){

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
                            animV.startAnimation(anim);
                            animV.requestLayout();
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
                            animV.startAnimation(anim);
                            animV.requestLayout();
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
