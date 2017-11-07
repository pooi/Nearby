package cf.nearby.nearby.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.ManageMedicineActivity;
import cf.nearby.nearby.activity.ManageSymptomActivity;
import cf.nearby.nearby.activity.ShowPatientMedicineDetailActivity;
import cf.nearby.nearby.obj.PatientMedicine;
import cf.nearby.nearby.obj.PatientSymptom;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;


/**
 * Created by tw on 2017-10-01.
 */
public class PatientSymptomListCustomAdapter extends RecyclerView.Adapter<PatientSymptomListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private ManageSymptomActivity activity;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<PatientSymptom> list;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public PatientSymptomListCustomAdapter(Context context, ArrayList<PatientSymptom> list, RecyclerView recyclerView, OnAdapterSupport listener, ManageSymptomActivity activity) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;
        this.activity = (ManageSymptomActivity) activity;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_symptom_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PatientSymptom ps = list.get(position);
        final int pos = position;

        holder.tv_title.setText(ps.getDescription());

        if(ps.getFinishDate() == 0.0){
            String text = "발병일 : " + AdditionalFunc.getDateString(ps.getStartDate());
            holder.tv_period.setText(text);

            if(activity.isSupporter())
                holder.li_btn.setVisibility(View.GONE);
            else
                holder.li_btn.setVisibility(View.VISIBLE);
            holder.btn_complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.markCompleteDate(pos);
                }
            });
            holder.tv_title.setTextColor(activity.getColorId(R.color.dark_gray));
            holder.tv_period.setTextColor(activity.getColorId(R.color.gray));
            holder.cv.setCardBackgroundColor(activity.getColorId(R.color.white));
        }else{
            String text = "기간 : " + AdditionalFunc.getDateString(ps.getStartDate()) + " ~ " + AdditionalFunc.getDateString(ps.getFinishDate());
            holder.tv_period.setText(text);

            holder.li_btn.setVisibility(View.GONE);
            holder.tv_title.setTextColor(activity.getColorId(R.color.white));
            holder.tv_period.setTextColor(activity.getColorId(R.color.light_gray2));
            holder.cv.setCardBackgroundColor(activity.getColorId(R.color.dark_gray));
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

        CardView cv;
        TextView tv_title;
        TextView tv_period;
        Button btn_complete;
        LinearLayout li_btn;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            tv_title = (TextView)v.findViewById(R.id.tv_title);
            tv_period = (TextView)v.findViewById(R.id.tv_period);
            btn_complete = (Button)v.findViewById(R.id.btn_complete);
            li_btn = (LinearLayout)v.findViewById(R.id.li_btn);
        }
    }

}
