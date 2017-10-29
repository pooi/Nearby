package cf.nearby.nearby.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;


import java.util.ArrayList;
import java.util.HashMap;

import cf.nearby.nearby.R;

/**
 * Created by tw on 2017-10-28.
 */
public class RecordItemListFragment extends BaseFragment {

    // UI
    private View view;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // UI
        view = inflater.inflate(R.layout.fragment_record_item_list, container, false);
        context = container.getContext();

        init();

        return view;

    }


    public void init(){



    }


}
