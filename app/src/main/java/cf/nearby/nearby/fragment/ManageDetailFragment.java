package cf.nearby.nearby.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cf.nearby.nearby.R;
import cf.nearby.nearby.activity.InquiryDetailActivity;
import cf.nearby.nearby.activity.InquiryPhotoActivity;
import cf.nearby.nearby.activity.InquiryVitalSignActivity;
import cf.nearby.nearby.activity.ManageMedicineActivity;
import cf.nearby.nearby.activity.ManageSymptomActivity;
import cf.nearby.nearby.activity.ManageWeightActivity;
import cf.nearby.nearby.activity.SearchEmployeeActivity;
import cf.nearby.nearby.obj.Patient;
import cf.nearby.nearby.util.AdditionalFunc;

/**
 * Created by tw on 2017-10-28.
 */
public class ManageDetailFragment extends BaseFragment {

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
        view = inflater.inflate(R.layout.fragment_manage_detail, container, false);
        context = container.getContext();

        init();

        return view;

    }


    public void init(){

        view.findViewById(R.id.cv_manage_symptom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ManageSymptomActivity.class);
                intent.putExtra("patient", selectedPatient);
                intent.putExtra("isSupporter", true);
                context.startActivity(intent);
            }
        });
        view.findViewById(R.id.cv_manage_medicine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ManageMedicineActivity.class);
                intent.putExtra("patient", selectedPatient);
                intent.putExtra("isSupporter", true);
                context.startActivity(intent);

            }
        });
        view.findViewById(R.id.cv_manage_weight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ManageWeightActivity.class);
                intent.putExtra("patient", selectedPatient);
                intent.putExtra("isSupporter", true);
                context.startActivity(intent);

            }
        });

    }


}
