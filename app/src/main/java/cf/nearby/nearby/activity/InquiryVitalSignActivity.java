package cf.nearby.nearby.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.fragment.InquiryVitalSignFragment;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.obj.VitalSign;
import cf.nearby.nearby.util.CustomViewPager;
import cf.nearby.nearby.util.PagerContainer;

public class InquiryVitalSignActivity extends BaseActivity {

    private Patient selectedPatient;

    private PagerContainer viewPagerContainer;
    private CustomViewPager viewPager;
    private NavigationAdapter pagerAdapter;

    private ArrayList<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_vital_sign);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        titles = new ArrayList<>();
        // 순서 변경하면 안됨! 변경 시 InquiryVitalSignFragment에서 TYPE 순서 변경
        titles.add(getString(R.string.pulse_srt));
        titles.add(getString(R.string.temperature_srt));
        titles.add(getString(R.string.blood_pressure_srt));

        init();

    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewPagerContainer = (PagerContainer)findViewById(R.id.view_pager_container);
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);

        pagerAdapter = new NavigationAdapter(getSupportFragmentManager(), titles, selectedPatient);
        viewPager.setOffscreenPageLimit(titles.size());
        viewPager.setAdapter(pagerAdapter);
//        viewPager.setPageMargin(0);
        viewPager.setClipChildren(false);

    }



    private static class NavigationAdapter extends FragmentPagerAdapter {

        private ArrayList<String> titles;
        private Patient selectedPatient;

        public NavigationAdapter(FragmentManager fm, ArrayList<String> titles, Patient selectedPatient){
            super(fm);
            this.titles = titles;
            this.selectedPatient = selectedPatient;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            final int pattern = position % titles.size();

            f = new InquiryVitalSignFragment();
            Bundle bdl = new Bundle(1);
            bdl.putInt("position", pattern);
            bdl.putSerializable("patient", selectedPatient);
            bdl.putString("title", titles.get(pattern));
            bdl.putInt("type", pattern);
            f.setArguments(bdl);
//            f = new ArticleItemFragment();

            return f;
        }

        @Override
        public int getCount(){
            return titles.size();
        }


    }
}
