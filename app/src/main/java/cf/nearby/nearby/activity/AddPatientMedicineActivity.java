package cf.nearby.nearby.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.obj.PatientMedicineDetail;

public class AddPatientMedicineActivity extends BaseActivity {

    private MaterialEditText editTitle;
    private TextView startPeriodBtn;
    private TextView finishPeriodBtn;

    private CardView addMedicineBtn;
    private LinearLayout li_medicineList;

    private Button backBtn;
    private Button saveBtn;

    private boolean isSelectStartDate;
    private boolean isSelectFinishDate;

    private ArrayList<PatientMedicineDetail> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_medicine);

        list = new ArrayList<>();

        init();


    }

    private void init(){

        editTitle = (MaterialEditText)findViewById(R.id.edit_title);
        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkSaveBtn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        startPeriodBtn = (TextView)findViewById(R.id.tv_take_period_start);
        finishPeriodBtn = (TextView)findViewById(R.id.tv_take_period_finish);
        addMedicineBtn = (CardView)findViewById(R.id.cv_add_medicine);
        li_medicineList = (LinearLayout)findViewById(R.id.li_medicine_list);
        backBtn = (Button)findViewById(R.id.btn_back);
        saveBtn = (Button)findViewById(R.id.btn_save);

    }

    private void checkSaveBtn(){

        boolean isTitle = editTitle.isCharactersCountValid();
        boolean isList = list.size() > 0;


        boolean status = isTitle && isSelectStartDate && isSelectFinishDate && isList;

        saveBtn.setEnabled(status);

        if(status){
            saveBtn.setBackgroundColor(getColorId(R.color.colorPrimary));
        }else{
            saveBtn.setBackgroundColor(getColorId(R.color.dark_gray));
        }

    }
}
