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
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ppamorim.dragger.DraggerPosition;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.MedicineDetailActivity;
import cf.nearby.nearby.activity.SearchMedicineActivity;
import cf.nearby.nearby.activity.SearchPatientActivity;
import cf.nearby.nearby.obj.Medicine;
import cf.nearby.nearby.obj.MedicineDetail;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;


/**
 * Created by tw on 2017-10-01.
 */
public class MedicineSearchListCustomAdapter extends RecyclerView.Adapter<MedicineSearchListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private SearchMedicineActivity activity;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<Medicine> list;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public MedicineSearchListCustomAdapter(Context context, ArrayList<Medicine> list, RecyclerView recyclerView, OnAdapterSupport listener, SearchMedicineActivity activity) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;
        this.activity = (SearchMedicineActivity) activity;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Medicine medicine = list.get(position);
        final int pos = position;

        Picasso.with(context)
                .load("http://nearby.cf/medicine/" + medicine.getCode() + ".jpg")
                .into(holder.img);

        holder.tv_name.setText(medicine.getName());
        holder.tv_code.setText(medicine.getCode());
        holder.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.selectMedicine(medicine);
            }
        });

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MedicineDetailActivity.class);
                intent.putExtra("drag_position", DraggerPosition.TOP);
                intent.putExtra("detail", new MedicineDetail(medicine.getCode(), medicine.getName(), "http://nearby.cf/medicine/" + medicine.getCode() + ".jpg"));
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

        CardView cv;
        ImageView img;
        TextView tv_name;
        TextView tv_code;
        Button btn_select;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            img = (ImageView)v.findViewById(R.id.img);
            tv_name = (TextView)v.findViewById(R.id.tv_name);
            tv_code = (TextView)v.findViewById(R.id.tv_code);
            btn_select = (Button)v.findViewById(R.id.btn_select);
        }
    }

}
