package cf.nearby.nearby.nurse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.RecordMealActivity;
import cf.nearby.nearby.obj.HaveMeal;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;

public class NurseRecordActivity extends BaseActivity {

    public static final int UPDATE_MEAL = 400;

    private Button saveBtn;

    private Patient selectedPatient;
    private HaveMeal haveMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_record);

        selectedPatient = (Patient)getIntent().getSerializableExtra("patient");
        haveMeal = new HaveMeal();

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

        findViewById(R.id.cv_record_patient_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NurseRecordActivity.this, RecordMealActivity.class);
                intent.putExtra("have_meal", haveMeal);
                startActivityForResult(intent, UPDATE_MEAL);
            }
        });

        ImageView img = (ImageView)findViewById(R.id.img);
        if(selectedPatient.getPic() != null && !"".equals(selectedPatient.getPic())) {
            Picasso.with(getApplicationContext())
                    .load(selectedPatient.getPic())
                    .into(img);
        }
        TextView tv_name = (TextView)findViewById(R.id.tv_name);
        TextView tv_dob = (TextView)findViewById(R.id.tv_dob);
        TextView tv_registeredDate = (TextView)findViewById(R.id.tv_registered_date);
        tv_name.setText(selectedPatient.getName());
        tv_dob.setText(AdditionalFunc.getDateString(selectedPatient.getDob()));
        tv_registeredDate.setText(AdditionalFunc.getDateString(selectedPatient.getRegisteredDate()));


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

    private void checkChangeBtn(){

        // 식사 여부
        boolean isHaveMeal = haveMeal.getType() != null && !haveMeal.getType().isEmpty();
        changeBtnColor((CardView)findViewById(R.id.cv_record_patient_meal), isHaveMeal);

        boolean status = isHaveMeal;

        saveBtn.setEnabled(status);
        if(status){
            saveBtn.setBackgroundColor(getColorId(R.color.colorPrimary));
        }else{
            saveBtn.setBackgroundColor(getColorId(R.color.dark_gray));
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case UPDATE_MEAL:
                if(data != null){
                    haveMeal = (HaveMeal)data.getSerializableExtra("have_meal");
                    checkChangeBtn();
                }
                break;
            default:
                break;
        }

    }
}