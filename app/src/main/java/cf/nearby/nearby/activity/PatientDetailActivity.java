package cf.nearby.nearby.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ppamorim.dragger.DraggerCallback;
import com.github.ppamorim.dragger.DraggerPosition;
import com.github.ppamorim.dragger.DraggerView;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.obj.MedicineDetail;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;

public class PatientDetailActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SHOW_PROGRESS = 500;
    private final int MSG_MESSAGE_HIDE_PROGRESS = 501;
    private final int MSG_MESSAGE_FILL_CONTENT = 502;

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
//    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
//    private int mFabMargin;
//    private boolean mFabIsShown;

    private DraggerView draggerView;

    private ImageView img_main;
//    private TextView tv_content;
    private ImageView img;
    private LinearLayout li_detail;

    private MaterialDialog progressDialog;

    private Patient selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");

        init();

        if(selectedPatient != null){
            makeDetailInfo();
        }

//        getMedicineDetailInfo();

    }

    private void draggerInit(){
        draggerView = new DraggerView(getApplicationContext());
        clearSingleton(); // 프레임 중복 해결
        draggerView = (DraggerView)findViewById(R.id.dragger_view);
        draggerView.setDraggerPosition((DraggerPosition)getIntent().getSerializableExtra("drag_position"));
        draggerView.setDraggerCallback(new DraggerCallback() {
            @Override
            public void onProgress(double v) {

            }

            @Override
            public void notifyOpen() {

            }

            @Override
            public void notifyClose() {
                finish();
            }
        });
    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        draggerInit();
        initObserval();
        initProgressDialog();

        mTitleView.setText(selectedPatient.getName());
        img_main = (ImageView)findViewById(R.id.image);
        Picasso.with(getApplicationContext())
                .load(selectedPatient.getPic())
                .into(img_main);
        img = (ImageView)findViewById(R.id.img);
        Picasso.with(getApplicationContext())
                .load(selectedPatient.getPic())
                .into(img);
//        tv_content = (TextView)findViewById(R.id.tv_content);
//        tv_content.setText(attraction.contents);

        li_detail = (LinearLayout)findViewById(R.id.li_detail);
//        makeDetailInfo();

    }

    private void initProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .build();
    }

    private void initObserval(){

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = 0;//getActionBarSize();

        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        setTitle(null);
//        mFab = findViewById(R.id.fab);
//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(DetailActivity.this, "FAB is clicked", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(attraction.url));
//                startActivity(intent);
//            }
//        });
//        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
//        ViewHelper.setScaleX(mFab, 0);
//        ViewHelper.setScaleY(mFab, 0);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
//                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                //mScrollView.scrollTo(0, 1);
                //mScrollView.scrollTo(0, 0);
            }
        });

    }

    private void makeDetailInfo(){

        li_detail.removeAllViews();

        ((TextView)findViewById(R.id.tv_name)).setText(selectedPatient.getName());
        ((TextView)findViewById(R.id.tv_dob)).setText(AdditionalFunc.getDateString(selectedPatient.getDob()));
        ((TextView)findViewById(R.id.tv_registered_date)).setText(AdditionalFunc.getDateString(selectedPatient.getRegisteredDate()));

        if(selectedPatient.getAddress() != null && !selectedPatient.getAddress().isEmpty()){
            View v = getLayoutInflater().inflate(R.layout.medicine_detail_info_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.address);
            tv_content.setText(selectedPatient.getAddress());

            li_detail.addView(v);
        }

        if(selectedPatient.getZip() != null && !selectedPatient.getZip().isEmpty()){
            View v = getLayoutInflater().inflate(R.layout.medicine_detail_info_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.zip);
            tv_content.setText(selectedPatient.getZip());

            li_detail.addView(v);
        }

        if(selectedPatient.getEmail() != null && !selectedPatient.getEmail().isEmpty()){
            View v = getLayoutInflater().inflate(R.layout.medicine_detail_info_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.email);
            tv_content.setText(selectedPatient.getEmail());

            li_detail.addView(v);
        }

        if(selectedPatient.getGender() != null && !selectedPatient.getGender().isEmpty()){
            View v = getLayoutInflater().inflate(R.layout.medicine_detail_info_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.gender);
            String gender = selectedPatient.getGender();
            if ("female".equals(gender)){
                gender = "여";
            }else{
                gender = "남";
            }
            tv_content.setText(gender);

            li_detail.addView(v);
        }

        if(selectedPatient.getPhone() != null && !selectedPatient.getPhone().isEmpty()){
            View v = getLayoutInflater().inflate(R.layout.medicine_detail_info_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.phone);
            tv_content.setText(selectedPatient.getPhone());

            li_detail.addView(v);
        }

        if(selectedPatient.getBasicLivingAllowance() != null){
            View v = getLayoutInflater().inflate(R.layout.medicine_detail_info_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.bla);
            if("0".equals(selectedPatient.getBasicLivingAllowance() + "")){
                tv_content.setText(R.string.yes);
            }else{
                tv_content.setText(R.string.no);
            }

            li_detail.addView(v);
        }

        if(selectedPatient.getDescription() != null && !selectedPatient.getDescription().isEmpty()){
            View v = getLayoutInflater().inflate(R.layout.medicine_detail_info_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.description);
            tv_content.setText(selectedPatient.getDescription());

            li_detail.addView(v);
        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SHOW_PROGRESS:
                    progressDialog.show();
                    break;
                case MSG_MESSAGE_HIDE_PROGRESS:
                    initProgressDialog();
                    break;
                case MSG_MESSAGE_FILL_CONTENT:
                    makeDetailInfo();
                    break;
                default:
                    break;
            }
        }
    }

//    private void getMedicineDetailInfo(){
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("service", "getMedicineDetail");
//        map.put("code", medicineDetail.getCode());
//        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {
//
//            @Override
//            protected void afterThreadFinish(String data) {
//
//                medicineDetail.build(data);
//
//                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_FILL_CONTENT));
//
//            }
//        }.start();
//
//    }

    @Override public void onBackPressed() {
        draggerView.closeActivity();
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        draggerView.setSlideEnabled(scrollY <= 0);
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));

        // Change alpha of overlay
//        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - 0 / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - 0 / 2,
                mActionBarSize - 0 / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
//            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
//            lp.topMargin = (int) fabTranslationY;
//            mFab.requestLayout();
        } else {
//            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
//            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private void showFab() {
//        if (!mFabIsShown) {
//            ViewPropertyAnimator.animate(mFab).cancel();
//            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
//            mFabIsShown = true;
//        }
    }

    private void hideFab() {
//        if (mFabIsShown) {
//            ViewPropertyAnimator.animate(mFab).cancel();
//            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
//            mFabIsShown = false;
//        }
    }

    private void clearSingleton(){
        try{
            Field field = DraggerView.class.getDeclaredField("singleton");
            field.setAccessible(true);
            field.set(this, null);
        }catch(NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }


}
