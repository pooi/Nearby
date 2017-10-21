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
package cf.nearby.nearby.bluetooth.ListAdapters;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonFragments.CarouselFragment;
import cf.nearby.nearby.bluetooth.CommonFragments.ProfileControlFragment;
import cf.nearby.nearby.bluetooth.CommonUtils.CarouselLinearLayout;
import cf.nearby.nearby.bluetooth.CommonUtils.GattAttributes;
import cf.nearby.nearby.bluetooth.CommonUtils.UUIDDatabase;
import cf.nearby.nearby.bluetooth.ConnectBLEActivity;
import cf.nearby.nearby.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Adapter class for the CarouselView. extends FragmentPagerAdapter
 */
public class CarouselPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    /**
     * CarouselLinearLayout variables for animation
     */
    private CarouselLinearLayout mCurrentCarouselLinearLayout = null;
    private CarouselLinearLayout mNextCarouselLinearLayout = null;
    private ConnectBLEActivity mContext;
    private ProfileControlFragment mContainerFragment;
    private FragmentManager mFragmentManager;
    private float mScale;

    private ArrayList<HashMap<String, BluetoothGattService>> mCurrentServiceData;

    public CarouselPagerAdapter(Activity context,
                                ProfileControlFragment containerFragment,
                                FragmentManager fragmentManager,
                                ArrayList<HashMap<String, BluetoothGattService>> currentServiceData) {
        super(fragmentManager);
        this.mFragmentManager = fragmentManager;
        this.mContext = (ConnectBLEActivity) context;
        this.mContainerFragment = containerFragment;
        this.mCurrentServiceData = currentServiceData;

    }

    @Override
    public Fragment getItem(int position) {

        // Make the first pager bigger than others
        if (position == ProfileControlFragment.FIRST_PAGE) {
            mScale = ProfileControlFragment.BIG_SCALE;
        } else {
            mScale = ProfileControlFragment.SMALL_SCALE;
        }
        position = position % ProfileControlFragment.mPages;
        HashMap<String, BluetoothGattService> item = mCurrentServiceData
                .get(position);
        BluetoothGattService bgs = item.get("UUID");

        /**
         * Looking for the image corresponding to the UUID.if no suitable image
         * resource is found assign the default unknown resource
         */

        int imageId = GattAttributes.lookupImage(bgs.getUuid());
        String name = GattAttributes.lookupUUID(bgs.getUuid(),
                mContext.getResources().getString(
                        R.string.profile_control_unknown_service));
        UUID uuid = bgs.getUuid();
        if (uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)) {
            name = mContext.getResources().getString(R.string.findme_fragment);
        }
        if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)) {
            name = mContext.getResources().getString(R.string.proximity_fragment);
        }
        if (uuid.equals(UUIDDatabase.UUID_CAPSENSE_SERVICE) ||
                uuid.equals(UUIDDatabase.UUID_CAPSENSE_SERVICE_CUSTOM)) {
            List<BluetoothGattCharacteristic> gattCharacteristics = bgs
                    .getCharacteristics();
            if (gattCharacteristics.size() > 1) {
                imageId = GattAttributes.lookupImageCapSense(bgs.getUuid());
                name = GattAttributes.lookupNameCapSense(
                        bgs.getUuid(),
                        mContext.getResources().getString(
                                R.string.profile_control_unknown_service));
            } else {
                UUID characteristicUUID = gattCharacteristics.get(0).getUuid();
                imageId = GattAttributes.lookupImageCapSense(
                        characteristicUUID);
                name = GattAttributes.lookupNameCapSense(
                        characteristicUUID,
                        mContext.getResources().getString(
                                R.string.profile_control_unknown_service));
            }
        }
        if (uuid.equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
            name = mContext.getResources().getString(R.string.gatt_db);
        }
        if (uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)
                || uuid.equals(UUIDDatabase.UUID_ACCELEROMETER_SERVICE)
                || uuid.equals(UUIDDatabase.UUID_ANALOG_TEMPERATURE_SERVICE)) {
            name = mContext.getResources().getString(R.string.sen_hub);
        }
        if (uuid.equals(UUIDDatabase.UUID_HID_SERVICE)) {
            String connectedDeviceName = BluetoothLeService.getmBluetoothDeviceName();
            String remoteName = mContext.getString(R.string.rdk_emulator_view);
            if (connectedDeviceName.indexOf(remoteName) != -1) {
                name = mContext.getResources().getString(R.string.rdk_emulator_view);
                imageId = R.drawable.emulator;
            }

        }
        Fragment curFragment = CarouselFragment.newInstance(imageId,
                mScale, name, uuid.toString(), bgs);
        return curFragment;
    }

    @Override
    public int getCount() {
        return ProfileControlFragment.mPages * ProfileControlFragment.LOOPS;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        /**
         * Page scroll animation. Zooming forward and backward based on the
         * scroll
         */
        if (positionOffset >= 0f && positionOffset <= 1f) {
            if (position < getCount() - 1) {
                try {
                    mCurrentCarouselLinearLayout = getRootView(position);
                    mNextCarouselLinearLayout = getRootView(position + 1);
                    mCurrentCarouselLinearLayout
                            .setScaleBoth(ProfileControlFragment.BIG_SCALE
                                    - ProfileControlFragment.DIFF_SCALE
                                    * positionOffset);
                    mNextCarouselLinearLayout
                            .setScaleBoth(ProfileControlFragment.SMALL_SCALE
                                    + ProfileControlFragment.DIFF_SCALE
                                    * positionOffset);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private CarouselLinearLayout getRootView(int position) {
        CarouselLinearLayout ly;
        try {
            ly = (CarouselLinearLayout) mFragmentManager
                    .findFragmentByTag(this.getFragmentTag(position)).getView()
                    .findViewById(R.id.root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ly;
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + mContainerFragment.mPager.getId() + ":"
                + position;
    }
}
