package cf.nearby.nearby.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.ppamorim.dragger.DraggerPosition;

import java.util.ArrayList;

import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.MedicineDetailActivity;
import cf.nearby.nearby.activity.ShowPatientMedicineDetailActivity;
import cf.nearby.nearby.obj.HaveMeal;
import cf.nearby.nearby.obj.MainRecord;
import cf.nearby.nearby.obj.Medicine;
import cf.nearby.nearby.obj.MedicineDetail;
import cf.nearby.nearby.obj.PatientRemark;
import cf.nearby.nearby.obj.TakeMedicine;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.OnAdapterSupport;
import cf.nearby.nearby.util.OnLoadMoreListener;
import cf.nearby.nearby.util.RecordListSupporter;


/**
 * Created by tw on 2017-10-01.
 */
public class AllInOneInquiryListCustomAdapter extends RecyclerView.Adapter<AllInOneInquiryListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private RecordListSupporter recordSupporter;

    private OnAdapterSupport onAdapterSupport;

    public ArrayList<MainRecord> list;

    // 무한 스크롤
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading = false;

    // 생성자
    public AllInOneInquiryListCustomAdapter(Context context, ArrayList<MainRecord> list, RecyclerView recyclerView, OnAdapterSupport listener, RecordListSupporter recordSupporter) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;
        this.recordSupporter = recordSupporter;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_in_one_inquiry_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MainRecord mr = list.get(position);
        final int pos = position;

        holder.tv_dateTitle.setText(mr.getDate());

        ArrayList<MainRecord> groupList = mr.getGroupList();
        for(int i=0; i<groupList.size(); i++){
            MainRecord mr2 = groupList.get(i);

            if(mr2 instanceof TakeMedicine){

                final TakeMedicine tm = (TakeMedicine)mr2;


                TableLayout.LayoutParams tlps=new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                TableRow.LayoutParams trps=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                TableRow tr = new TableRow(context);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView tv_title = new TextView(context);
                tv_title.setText( "0".equals(tm.getPatientMedicineId()) ? tm.getMedicine().getCode() :  tm.getTitle() );
                tv_title.setLayoutParams(trps);
                tv_title.setTextColor(recordSupporter.getColorId(R.color.dark_gray));
                tv_title.setGravity(Gravity.CENTER);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.default_font_small_size));

                TextView tv_time = new TextView(context);
                tv_time.setText( "0".equals(tm.getPatientMedicineId()) ? tm.getMedicine().getNameSrt(10) : AdditionalFunc.getTimeString(tm.getRegisteredDate()) );
                tv_time.setLayoutParams(trps);
                tv_time.setTextColor(recordSupporter.getColorId(R.color.dark_gray));
                tv_time.setGravity(Gravity.CENTER);
                tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.default_font_small_size));

                TextView tv_detail = new TextView(context);
                tv_detail.setText(R.string.detail_srt);
                tv_detail.setLayoutParams(trps);
                tv_detail.setTextColor(recordSupporter.getColorId(R.color.colorPrimary));
                tv_detail.setGravity(Gravity.CENTER);
                tv_detail.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.default_font_small_size));
                tv_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if("0".equals(tm.getPatientMedicineId())){
                            Medicine medicine = tm.getMedicine();
                            Intent intent = new Intent(context, MedicineDetailActivity.class);
                            intent.putExtra("drag_position", DraggerPosition.TOP);
                            intent.putExtra("detail", new MedicineDetail(medicine.getCode(), medicine.getName(), medicine.getCompany(), "http://nearby.cf/medicine/" + medicine.getCode() + ".jpg"));
                            onAdapterSupport.redirectActivity(intent);
                        }else{
                            Intent intent = new Intent(context, ShowPatientMedicineDetailActivity.class);
                            intent.putExtra("patient_medicine_id", tm.getPatientMedicineId());
                            onAdapterSupport.redirectActivity(intent);
                        }
                    }
                });

                if(tm.getPatientMedicine() != null){

                }else{

                }

                tr.addView(tv_title);
                tr.addView(tv_time);
                tr.addView(tv_detail);

                holder.tl.addView(tr, tlps);

            }else if(mr2 instanceof HaveMeal){

                HaveMeal hm = (HaveMeal)mr2;

                TableLayout.LayoutParams tlps=new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                TableRow.LayoutParams trps=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                TableRow tr = new TableRow(context);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView tv_title = new TextView(context);
                if("nursing_home".equals(hm.getType())){
                    tv_title.setText(context.getString(R.string.nursing_home_meal_srt));
                }else if("water_gruel".equals(hm.getType())){
                    tv_title.setText(context.getString(R.string.water_gruel));
                }else{
                    tv_title.setText(hm.getType());
                }
                tv_title.setLayoutParams(trps);
                tv_title.setTextColor(recordSupporter.getColorId(R.color.dark_gray));
                tv_title.setGravity(Gravity.CENTER);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.default_font_small_size));

                TextView tv_time = new TextView(context);
                tv_time.setText(AdditionalFunc.getTimeString(hm.getRegisteredDate()));
                tv_time.setLayoutParams(trps);
                tv_time.setTextColor(recordSupporter.getColorId(R.color.dark_gray));
                tv_time.setGravity(Gravity.CENTER);
                tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_title);
                tr.addView(tv_time);

                holder.tl.addView(tr, tlps);

            }else if(mr2 instanceof PatientRemark){

                PatientRemark pr = (PatientRemark)mr2;

                TableLayout.LayoutParams tlps=new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                TableRow.LayoutParams trps=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                TableRow tr = new TableRow(context);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView tv_title = new TextView(context);
                tv_title.setText(pr.getDescription());
                tv_title.setLayoutParams(trps);
                tv_title.setTextColor(recordSupporter.getColorId(R.color.dark_gray));
                tv_title.setGravity(Gravity.CENTER);
                tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.default_font_small_size));

                TextView tv_time = new TextView(context);
                tv_time.setText(AdditionalFunc.getTimeString(pr.getRegisteredDate()));
                tv_time.setLayoutParams(trps);
                tv_time.setTextColor(recordSupporter.getColorId(R.color.dark_gray));
                tv_time.setGravity(Gravity.CENTER);
                tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.default_font_small_size));

                tr.addView(tv_title);
                tr.addView(tv_time);

                holder.tl.addView(tr, tlps);

            }



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
        TextView tv_dateTitle;
        TableLayout tl;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            setCardButtonOnTouchAnimation(cv);
            tv_dateTitle = (TextView)v.findViewById(R.id.tv_date_title);
            tl = (TableLayout)v.findViewById(R.id.tl);
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
