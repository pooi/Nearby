package cf.nearby.nearby.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.HaveMeal;

public class RecordMealActivity extends BaseActivity {

    private CardView cv_nursingHome;
    private CardView cv_waterGruel;

    private Button saveBtn;

    private HaveMeal haveMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_meal);

        haveMeal = (HaveMeal)getIntent().getSerializableExtra("have_meal");

        init();

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
                intent.putExtra("have_meal", haveMeal);
                setResult(NurseRecordActivity.UPDATE_MEAL, intent);
                finish();
            }
        });

        setCardButtonOnTouchAnimation(findViewById(R.id.cv_nursing_home));
        cv_nursingHome = (CardView)findViewById(R.id.cv_nursing_home);
        cv_nursingHome.setTag("nursing_home");
        cv_nursingHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = (String)view.getTag();
                record(tag);
            }
        });
        setCardButtonOnTouchAnimation(findViewById(R.id.cv_water_gruel));
        cv_waterGruel = (CardView)findViewById(R.id.cv_water_gruel);
        cv_waterGruel.setTag("water_gruel");
        cv_waterGruel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = (String)view.getTag();
                record(tag);
            }
        });

        if("nursing_home".equals(haveMeal.getType())){ // 병원 식단

            changeBtnColor(cv_nursingHome, true);
            changeBtnColor(cv_waterGruel, false);

        }else if("water_gruel".equals(haveMeal.getType())){ // 미음

            changeBtnColor(cv_waterGruel, true);
            changeBtnColor(cv_nursingHome, false);

        }

    }

    private void record(String type){

        if("nursing_home".equals(type)){ // 병원 식단

            if(type.equals(haveMeal.getType())){

                haveMeal.setType(null);
                changeBtnColor(cv_nursingHome, false);

            }else{

                haveMeal.setType(type);
                changeBtnColor(cv_nursingHome, true);
                changeBtnColor(cv_waterGruel, false);

            }

        }else if("water_gruel".equals(type)){ // 미음

            if(type.equals(haveMeal.getType())){

                haveMeal.setType(null);
                changeBtnColor(cv_waterGruel, false);

            }else{

                haveMeal.setType(type);
                changeBtnColor(cv_waterGruel, true);
                changeBtnColor(cv_nursingHome, false);

            }

        }

    }

    private void changeBtnColor(CardView cv, boolean check){
        if(check){
            cv.setCardBackgroundColor(getColorId(R.color.pastel_green));
            for(int i=0; i<cv.getChildCount(); i++){
                ViewGroup vg = (ViewGroup)cv.getChildAt(i);
                for(int j=0; j<vg.getChildCount(); j++){
                    View v = vg.getChildAt(j);
                    if(v instanceof TextView){
                        ((TextView) v).setTextColor(getColorId(R.color.white));
                    }
                }
            }
        }else{
            cv.setCardBackgroundColor(getColorId(R.color.white));
            for(int i=0; i<cv.getChildCount(); i++){
                ViewGroup vg = (ViewGroup)cv.getChildAt(i);
                for(int j=0; j<vg.getChildCount(); j++){
                    View v = vg.getChildAt(j);
                    if(v instanceof TextView){
                        ((TextView) v).setTextColor(getColorId(R.color.dark_gray));
                    }
                }
            }
        }

    }

}
