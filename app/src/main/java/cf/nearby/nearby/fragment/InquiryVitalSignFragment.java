package cf.nearby.nearby.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.R;

/**
 * Created by tw on 2017-06-01.
 */
public class InquiryVitalSignFragment extends BaseFragment {

    // BASIC UI
    private View view;
    private Context context;

    // DATA
    private int position;
    private String title;

    // UI
    private RelativeLayout root;
    private TextView tv_title;
    private RelativeLayout rl_graph;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            position = getArguments().getInt("position");
            title = getArguments().getString("title");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inquiry_vital_sign, container, false);
        context = container.getContext();

        initUI();
        fillContent();

        return view;

    }

    private void initUI(){

        root = (RelativeLayout)view.findViewById(R.id.root);

        tv_title = (TextView)view.findViewById(R.id.tv_title);
        rl_graph = (RelativeLayout)view.findViewById(R.id.rl_graph);

        tv_title.setText(title);

    }

    private void fillContent(){

    }

}
