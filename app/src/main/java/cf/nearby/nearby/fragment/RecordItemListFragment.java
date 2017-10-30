package cf.nearby.nearby.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.InquiryDetailActivity;
import cf.nearby.nearby.activity.InquiryVitalSignActivity;
import cf.nearby.nearby.obj.Patient;

/**
 * Created by tw on 2017-10-28.
 */
public class RecordItemListFragment extends BaseFragment {

    // UI
    private View view;
    private Context context;
    private Patient selectedPatient;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if(getArguments() != null) {
            selectedPatient = (Patient)getArguments().getSerializable("patient");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // UI
        view = inflater.inflate(R.layout.fragment_record_item_list, container, false);
        context = container.getContext();

        init();

        return view;

    }


    public void init(){

        view.findViewById(R.id.cv_inquiry_vital_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InquiryVitalSignActivity.class);
                intent.putExtra("patient", selectedPatient);
                context.startActivity(intent);
            }
        });
        view.findViewById(R.id.cv_inquiry_patient_medicine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InquiryDetailActivity.class);
                intent.putExtra("patient", selectedPatient);
                intent.putExtra("type", InquiryDetailActivity.INQUIRY_TYPE_MEDICINE);
                context.startActivity(intent);
            }
        });
        view.findViewById(R.id.cv_inquiry_patient_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InquiryDetailActivity.class);
                intent.putExtra("patient", selectedPatient);
                intent.putExtra("type", InquiryDetailActivity.INQUIRY_TYPE_HAVE_MEAL);
                context.startActivity(intent);
            }
        });
        view.findViewById(R.id.cv_inquiry_remark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InquiryDetailActivity.class);
                intent.putExtra("patient", selectedPatient);
                intent.putExtra("type", InquiryDetailActivity.INQUIRY_TYPE_REMARK);
                context.startActivity(intent);
            }
        });


    }


}
