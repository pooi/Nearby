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

package cf.nearby.nearby.bluetooth.CommonFragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
import cf.nearby.nearby.CustomApplication;
import cf.nearby.nearby.bluetooth.ListAdapters.CarouselPagerAdapter;
import cf.nearby.nearby.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileControlFragment extends Fragment {

    public static final float BIG_SCALE = 1.0f;
    public static final float SMALL_SCALE = 0.7f;
    public static final float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    // Argument Constants
    private static final String ARG_DEVICE_NAME = "devicename";
    private static final String ARG_DEVICE_ADDRESS = "deviceaddress";
    public static int LOOPS = 100;
    // CarouselView related variables
    public static int mPages = 0;
    public static int FIRST_PAGE = mPages * LOOPS / 2;
    // Device name and address variables
    private static String mDeviceNameProfile;
    private static String mDeviceAddressProfile;
    // BluetoothGattCharacteristic list variable
    public ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;
    // ViewPager for CarouselView
    public ViewPager mPager;
    // GattService and Characteristics Mapping
    ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData;
    // Base Layout
    private RelativeLayout mRelativeLayout;
    // Adapter for loading data to CarouselView
    private CarouselPagerAdapter mAdapter;
    private int mWidth = 0;

    // Application variable
    private CustomApplication mApplication;
    // progress dialog variable
    private ProgressDialog mProgressDialog;


    /**
     * Method for passing data between fragments when created.
     *
     * @param device_name
     * @param device_address
     * @return ProfileControlFragment
     */
    public ProfileControlFragment create(String device_name,
                                         String device_address) {
        ProfileControlFragment fragment = new ProfileControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEVICE_NAME, device_name);
        args.putString(ARG_DEVICE_ADDRESS, device_address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_control, container,
                false);
        mRelativeLayout = (RelativeLayout) rootView
                .findViewById(R.id.gatt_service_carousel);
        mPager = (ViewPager) rootView.findViewById(R.id.myviewpager);
        mApplication = (CustomApplication) getActivity().getApplication();
        mPages = 0;
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        setCarouselView();
        setHasOptionsMenu(true);

        /**
         * Getting the orientation of the device. Set margin for pages as a
         * negative number, so a part of next and previous pages will be showed
         */
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPager.setPageMargin(-mWidth / 3);
        } else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPager.setPageMargin(-mWidth / 2);
        }
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Getting the width on orientation changed
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        /**
         * Getting the orientation of the device. Set margin for pages as a
         * negative number, so a part of next and previous pages will be showed
         */
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPager.setPageMargin(-width / 2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPager.setPageMargin(-width / 3);
        }
        mPager.refreshDrawableState();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting the device name and address passed
        mDeviceNameProfile = getArguments().getString(ARG_DEVICE_NAME);
        mDeviceAddressProfile = getArguments().getString(ARG_DEVICE_ADDRESS);
        mApplication = (CustomApplication) getActivity().getApplication();

        //Hiding the softkeyboard if visible
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }

        // Getting the width of the device
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;

    }

    @Override
    public void onResume() {
        super.onResume();
        // Initialize ActionBar as per requirement
        Utils.setUpActionBar(getActivity(),
                getResources().getString(R.string.profile_control_fragment));
        checkConnectionStatus();
    }

    private void checkConnectionStatus() {
        if (BluetoothLeService.getConnectionState() == 0) {
            // Guiding the user back to profile scanning fragment
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.slide_left, R.anim.push_left);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_right, R.anim.push_right);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem log = menu.findItem(R.id.log);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem clearcache = menu.findItem(R.id.clearcache);
        MenuItem pairCache = menu.findItem(R.id.pairing);
        if (Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_PAIR_CACHE_STATUS)) {
            pairCache.setChecked(true);
        } else {
            pairCache.setChecked(false);
        }

        if (!search.isActionViewExpanded()) {
            search.collapseActionView();
            search.getActionView().clearFocus();
            Logger.e("Action view" + search.isActionViewExpanded());
            search.setActionView(null);
        }
        search.setVisible(false);
        graph.setVisible(false);
        log.setVisible(true);
        clearcache.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Setting the CarouselView with data
     */
    private void setCarouselView() {

        // Getting the number of services discovered
        mPages = ServiceDiscoveryFragment.mGattServiceData.size();
        FIRST_PAGE = mPages * LOOPS / 2;

        // Setting the adapter
        mAdapter = new CarouselPagerAdapter(getActivity(),
                ProfileControlFragment.this, getActivity()
                .getSupportFragmentManager(),
                ServiceDiscoveryFragment.mGattServiceData);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(mAdapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        mPager.setCurrentItem(FIRST_PAGE);

        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        mPager.setOffscreenPageLimit(3);

        if (mPages == 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_no_services_found), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_swipe_profiles), Toast.LENGTH_SHORT).show();
        }

    }

}
