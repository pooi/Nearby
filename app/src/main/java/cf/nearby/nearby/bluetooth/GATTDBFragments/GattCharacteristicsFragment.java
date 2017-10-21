/*
 * Copyright Cypress Semiconductor Corporation, 2014-2015 All rights reserved.
 * 
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 * 
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

package cf.nearby.nearby.bluetooth.GATTDBFragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.GattAttributes;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.UUIDDatabase;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
import cf.nearby.nearby.CustomApplication;
import cf.nearby.nearby.bluetooth.ListAdapters.GattCharacteriscticsListAdapter;
import cf.nearby.nearby.R;

import java.util.List;

/**
 * GATT Characteristic Fragment under GATT DB
 */
public class GattCharacteristicsFragment extends Fragment {

    // List for storing BluetoothGattCharacteristics
    private List<BluetoothGattCharacteristic> mGattCharacteristics;

    // Application
    private CustomApplication mApplication;

    // ListView
    private ListView mGattListView;

    // Text Heading
    private TextView mTextHeading;

    // GATT Service name
    private String mGattServiceName = "";

    // Back button
    private ImageView mBackButton;

    public GattCharacteristicsFragment create() {
        GattCharacteristicsFragment fragment = new GattCharacteristicsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gatt_list,
                container, false);
        mApplication = (CustomApplication) getActivity().getApplication();
        getActivity().getActionBar().setTitle(R.string.gatt_db);
        mGattListView = (ListView) rootView
                .findViewById(R.id.ListView_gatt_services);
        mTextHeading = (TextView) rootView.findViewById(R.id.txtservices);
        mTextHeading.setText(getString(R.string.gatt_characteristics_heading));
        mBackButton = (ImageView) rootView.findViewById(R.id.imgback);

        // Back button listener
        mBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });
        // Getting the GATT characteristics from application
        mGattCharacteristics = mApplication.getGattCharacteristics();

        // Getting the selected service from the arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mGattServiceName = bundle
                    .getString(Constants.GATTDB_SELECTED_SERVICE);
        }
        // Preparing list data
        GattCharacteriscticsListAdapter mAdapter = new GattCharacteriscticsListAdapter(
                getActivity(), mGattCharacteristics);
        if (mAdapter != null) {
            mGattListView.setAdapter(mAdapter);
            Logger.e("LIst characteristice sizee>>" + mGattCharacteristics.size());
        }

        // List listener
        mGattListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos,
                                    long arg3) {
                mApplication.setBluetoothgattcharacteristic(mGattCharacteristics
                        .get(pos));
                String characteristicuuid = mGattCharacteristics.get(pos).getUuid().toString();
                String characteristicsname = GattAttributes.lookupUUID(mGattCharacteristics.get(pos).getUuid(),
                        characteristicuuid);
                //Report Reference lookup based on InstanceId
                if (mGattCharacteristics.get(pos).getUuid().equals(UUIDDatabase.UUID_REP0RT)) {
                    characteristicsname = GattAttributes.lookupReferenceRDK(mGattCharacteristics.get(pos).getInstanceId(), characteristicsname);
                }

                /**
                 * Passing the characteristic details to GattDetailsFragment and
                 * adding that fragment to the view
                 */
                Bundle bundle = new Bundle();
                bundle.putString(Constants.GATTDB_SELECTED_SERVICE,
                        mGattServiceName);
                bundle.putString(Constants.GATTDB_SELECTED_CHARACTERISTICE,
                        characteristicsname);
                FragmentManager fragmentManager = getFragmentManager();
                GattDetailsFragment gattDetailsfragment = new GattDetailsFragment()
                        .create();
                gattDetailsfragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.container, gattDetailsfragment)
                        .addToBackStack(null).commit();
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem log = menu.findItem(R.id.log);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem pairCache = menu.findItem(R.id.pairing);
        if (Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_PAIR_CACHE_STATUS)) {
            pairCache.setChecked(true);
        } else {
            pairCache.setChecked(false);
        }
        search.setVisible(false);
        graph.setVisible(false);
        log.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
