package cf.nearby.nearby.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cf.nearby.nearby.R;

/**
 * Created by tw on 2017-10-28.
 */
public class RecordDateListFragment extends BaseFragment {

    // UI
    private View view;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // UI
        view = inflater.inflate(R.layout.fragment_inquiry_date_list, container, false);
        context = container.getContext();

        init();

        return view;

    }


    public void init(){



    }


}
