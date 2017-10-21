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

package cf.nearby.nearby.bluetooth.BLEServiceFragments;

import android.app.ActionBar;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.DepthPageTransformer;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.PagerFooterview;
import cf.nearby.nearby.bluetooth.CommonUtils.UUIDDatabase;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
import cf.nearby.nearby.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fragment to display the CapSenseService
 */
public class CapsenseService extends Fragment {

    // Service and characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mNotifyCharacteristicProx;
    private static BluetoothGattCharacteristic mNotifyCharacteristicSlid;
    private static BluetoothGattCharacteristic mNotifyCharacteristicBut;

    // Flag for notify
    private boolean mNotifyset = false;

    // Separate fragments for each capsense service
    private CapsenseServiceProximity mCapsenseProximity;
    private CapsenseServiceSlider mCapsenseSlider;
    private CapsenseServiceButtons mCapsenseButton;

    // ViewPager variables
    private static int mViewpagerCount;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private PagerFooterview mPagerView;
    private LinearLayout mPagerLayout;

    // Fragment list
    private ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

    public CapsenseService create(BluetoothGattService service, int pageCount) {
        CapsenseService fragment = new CapsenseService();
        mService = service;
        mViewpagerCount = pageCount;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capsense_main, container,
                false);

        mCapsenseProximity = new CapsenseServiceProximity();
        mCapsenseSlider = new CapsenseServiceSlider();
        mCapsenseButton = new CapsenseServiceButtons();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) rootView.findViewById(R.id.capsenseViewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity()
                .getSupportFragmentManager());

        mPagerLayout = (LinearLayout) rootView
                .findViewById(R.id.capsense_page_indicator);
        mPagerView = new PagerFooterview(getActivity(), mViewpagerCount,
                mPagerLayout.getWidth());
        mPagerLayout.addView(mPagerView);

        if (mViewpagerCount == 1) {
            mPagerLayout.setVisibility(View.INVISIBLE);
        }

        /**
         * get required characteristics from service
         */
        List<BluetoothGattCharacteristic> gattCharacteristics = mService
                .getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

            UUID uuidchara = gattCharacteristic.getUuid();

            if (uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_PROXIMITY) || uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_PROXIMITY_CUSTOM)) {
                Logger.i("UUID Charsss proximity"
                        + gattCharacteristic.getUuid().toString());
                mNotifyCharacteristicProx = gattCharacteristic;
                if (!mNotifyset) {
                    mNotifyset = true;
                    prepareBroadcastDataNotify(mNotifyCharacteristicProx);
                }
                fragmentsList.add(mCapsenseProximity.create(mService));
            } else if (uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_SLIDER) || uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_SLIDER_CUSTOM)) {
                Logger.i("UUID Charsss slid"
                        + gattCharacteristic.getUuid().toString());
                mNotifyCharacteristicSlid = gattCharacteristic;
                if (!mNotifyset) {
                    mNotifyset = true;
                    prepareBroadcastDataNotify(mNotifyCharacteristicSlid);
                }
                fragmentsList.add(mCapsenseSlider.create(mService));
            } else if (uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_BUTTONS) || uuidchara.equals(UUIDDatabase.UUID_CAPSENSE_BUTTONS_CUSTOM)) {
                Logger.i("UUID Charsss buttons"
                        + gattCharacteristic.getUuid().toString());
                mNotifyCharacteristicBut = gattCharacteristic;
                if (!mNotifyset) {
                    mNotifyset = true;
                    prepareBroadcastDataNotify(mNotifyCharacteristicBut);
                }
                fragmentsList.add(mCapsenseButton.create(mService));
            }

        }
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mPagerView.Update(position);
                if (position == 0) {
                    prepareBroadcastDataNotify(mNotifyCharacteristicProx);
                    if (mNotifyCharacteristicSlid != null) {
                        stopBroadcastDataNotify(mNotifyCharacteristicSlid);
                    }
                    if (mNotifyCharacteristicBut != null) {
                        stopBroadcastDataNotify(mNotifyCharacteristicBut);
                    }

                }
                if (position == 1) {
                    prepareBroadcastDataNotify(mNotifyCharacteristicSlid);
                    if (mNotifyCharacteristicProx != null) {
                        stopBroadcastDataNotify(mNotifyCharacteristicProx);
                    }
                    if (mNotifyCharacteristicBut != null) {
                        stopBroadcastDataNotify(mNotifyCharacteristicBut);
                    }

                }
                if (position == 2) {
                    prepareBroadcastDataNotify(mNotifyCharacteristicBut);
                    if (mNotifyCharacteristicSlid != null) {
                        stopBroadcastDataNotify(mNotifyCharacteristicSlid);
                    }
                    if (mNotifyCharacteristicProx != null) {
                        stopBroadcastDataNotify(mNotifyCharacteristicProx);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //Not needed
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                //Not needed
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNotifyset = false;
        getActivity().registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar(getActivity(),
                getResources().getString(R.string.capsense));
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mGattUpdateReceiver);
        if (mNotifyCharacteristicSlid != null) {
            stopBroadcastDataNotify(mNotifyCharacteristicSlid);
        }
        if (mNotifyCharacteristicProx != null) {
            stopBroadcastDataNotify(mNotifyCharacteristicProx);
        }
        if (mNotifyCharacteristicBut != null) {
            stopBroadcastDataNotify(mNotifyCharacteristicBut);
        }
        super.onDestroy();
    }

    /**
     * A simple pager adapter that represents CapsenseFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(gattCharacteristic,
                    true);
        }

    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    private static void stopBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (gattCharacteristic != null) {
                Logger.d("Stopped notification");
                BluetoothLeService.setCharacteristicNotification(
                        gattCharacteristic, false);
            }

        }

    }

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // GATT Data Available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Bundle extras = intent.getExtras();
                if (extras.containsKey(Constants.EXTRA_CAPPROX_VALUE)) {
                    int received_proximity_rate = extras
                            .getInt(Constants.EXTRA_CAPPROX_VALUE);
                    CapsenseServiceProximity.displayLiveData(received_proximity_rate);
                }

            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setIcon(new ColorDrawable(getResources().getColor(
                    android.R.color.transparent)));
        }
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
