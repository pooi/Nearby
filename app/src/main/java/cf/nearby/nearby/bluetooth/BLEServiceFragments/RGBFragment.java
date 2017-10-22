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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.GattAttributes;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
import cf.nearby.nearby.R;

import java.util.List;

/**
 * Fragment to display the RGB service
 */
public class RGBFragment extends Fragment {

    // GATT service and characteristics
    private static BluetoothGattService mCurrentservice;
    private static BluetoothGattCharacteristic mReadCharacteristic;

    // Data view variables
    private ImageView mRGBcanavs;
    private ImageView mcolorpicker;
    private ViewGroup mViewContainer;
    private TextView mTextred;
    private TextView mTextgreen;
    private TextView mTextblue;
    private TextView mTextalpha;
    private ImageView mColorindicator;
    private SeekBar mIntensityBar;
    private RelativeLayout mParentRelLayout;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    // Data variables
    private float mWidth;
    private float mHeight;
    private String mHexRed;
    private String mHexGreen;
    private String mHexBlue;
    private View mRootView;
    // Flag
    private boolean mIsReaded = false;
    private Bitmap mBitmap;
    private int mRed, mGreen, mBlue, mIntensity;
    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    Logger.i("Bonding is in process....");
                    Utils.bondingProgressDialog(getActivity(), mProgressDialog, true);
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getmBluetoothDeviceName() + "|"
                            + BluetoothLeService.getmBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_paired);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(getActivity(), mProgressDialog, false);
                    getGattData();

                } else if (state == BluetoothDevice.BOND_NONE) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getmBluetoothDeviceName() + "|"
                            + BluetoothLeService.getmBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_unpaired);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(getActivity(), mProgressDialog, false);
                }
            }

        }

    };

    public RGBFragment create(BluetoothGattService currentservice) {
        mCurrentservice = currentservice;
        RGBFragment fragment = new RGBFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mRootView = inflater.inflate(R.layout.rgb_view_landscape, container,
                    false);
        } else {
            mRootView = inflater.inflate(R.layout.rgb_view_portrait, container,
                    false);
        }
//        getActivity().getActionBar().setTitle(R.string.rgb_led);
        setUpControls();
        setDefaultColorPickerPositionColor();
        setHasOptionsMenu(true);
        return mRootView;
    }

    private void setDefaultColorPickerPositionColor() {
        ViewTreeObserver observer = mcolorpicker.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mcolorpicker.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] locations = new int[2];
                mcolorpicker.getLocationOnScreen(locations);
                int x = locations[0];
                int y = locations[1];
                if (x < mBitmap.getWidth() && y < mBitmap.getHeight()) {
                    int p = mBitmap.getPixel(x, y);
                    if (p != 0) {
                        mRed = Color.red(p);
                        mGreen = Color.green(p);
                        mBlue = Color.blue(p);
                        Logger.i("r--->" + mRed + "g-->" + mGreen + "b-->" + mBlue);
                        UIupdation();
                    }
                }
            }
        });

    }

    /**
     * Method to set up the GAMOT view
     */
    void setUpControls() {
        mParentRelLayout = (RelativeLayout) mRootView.findViewById(R.id.parent);
        mParentRelLayout.setClickable(true);
        mRGBcanavs = (ImageView) mRootView.findViewById(R.id.imgrgbcanvas);
        mcolorpicker = (ImageView) mRootView.findViewById(R.id.imgcolorpicker);
        mTextalpha = (TextView) mRootView.findViewById(R.id.txtintencity);
        mTextred = (TextView) mRootView.findViewById(R.id.txtred);
        mTextgreen = (TextView) mRootView.findViewById(R.id.txtgreen);
        mTextblue = (TextView) mRootView.findViewById(R.id.txtblue);
        mColorindicator = (ImageView) mRootView
                .findViewById(R.id.txtcolorindicator);
        mViewContainer = (ViewGroup) mRootView.findViewById(R.id.viewgroup);
        mIntensityBar = (SeekBar) mRootView.findViewById(R.id.intencitychanger);
        mProgressDialog = new ProgressDialog(getActivity());
        BitmapDrawable mBmpdwbl = (BitmapDrawable) mRGBcanavs.getDrawable();
        mBitmap = mBmpdwbl.getBitmap();
        Drawable d = getResources().getDrawable(R.drawable.gamut);
        mRGBcanavs.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX();
                    float y = event.getY();
                    if (x >= 0 && y >= 0) {

                        int x1 = (int) x;
                        int y1 = (int) y;
                        if (x < mBitmap.getWidth() && y < mBitmap.getHeight()) {
                            int p = mBitmap.getPixel(x1, y1);
                            if (p != 0) {
                                if (x > mRGBcanavs.getMeasuredWidth())
                                    x = mRGBcanavs.getMeasuredWidth();
                                if (y > mRGBcanavs.getMeasuredHeight())
                                    y = mRGBcanavs.getMeasuredHeight();
                                setwidth(1.f / mRGBcanavs.getMeasuredWidth()
                                        * x);
                                setheight(1.f - (1.f / mRGBcanavs
                                        .getMeasuredHeight() * y));
                                mRed = Color.red(p);
                                mGreen = Color.green(p);
                                mBlue = Color.blue(p);
                                UIupdation();
                                mIsReaded = false;
                                moveTarget();
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });

        mIntensity = mIntensityBar.getProgress();
        mTextalpha.setText(String.format("0x%02x", mIntensity));
        // Seek bar progress change listener
        mIntensityBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                mIntensity = progress;
                UIupdation();
                mIsReaded = false;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsReaded = false;
                BluetoothLeService.writeCharacteristicRGB(mReadCharacteristic,
                        mRed, mGreen, mBlue, mIntensity);

            }
        });
    }

    @Override
    public void onResume() {
        getActivity().registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
        getGattData();
        Utils.setUpActionBar(getActivity(),
                getResources().getString(R.string.rgb_led));
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mGattUpdateReceiver);
        super.onDestroy();
    }


    private void UIupdation() {
        String hexColor = String
                .format("#%02x%02x%02x%02x", mIntensity, mRed, mGreen, mBlue);
        mColorindicator.setBackgroundColor(Color.parseColor(hexColor));
        mTextalpha.setText(String.format("0x%02x", mIntensity));
        mHexRed = String.format("0x%02x", mRed);
        mHexGreen = String.format("0x%02x", mGreen);
        mHexBlue = String.format("0x%02x", mBlue);
        mTextred.setText(mHexRed);
        mTextblue.setText(mHexBlue);
        mTextgreen.setText(mHexGreen);
        mTextalpha.setText(String.format("0x%02x", mIntensity));
        try {
            Logger.i("Writing value-->" + mRed + " " + mGreen + " " + mBlue + " " + mIntensity);
            BluetoothLeService.writeCharacteristicRGB(mReadCharacteristic, mRed,
                    mGreen, mBlue, mIntensity);
        } catch (Exception e) {

        }

    }

    /**
     * Method to get required characteristics from service
     */
    void getGattData() {
        List<BluetoothGattCharacteristic> gattCharacteristics = mCurrentservice
                .getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            String uuidchara = gattCharacteristic.getUuid().toString();
            if (uuidchara.equalsIgnoreCase(GattAttributes.RGB_LED) || uuidchara.equalsIgnoreCase(GattAttributes.RGB_LED_CUSTOM)) {
                mReadCharacteristic = gattCharacteristic;
                break;
            }
        }
    }

    /**
     * Moving the color picker object
     */

    void moveTarget() {
        float x = getwidth() * mRGBcanavs.getMeasuredWidth();
        float y = (1.f - getheigth()) * mRGBcanavs.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mcolorpicker
                .getLayoutParams();
        layoutParams.leftMargin = (int) (mRGBcanavs.getLeft() + x
                - Math.floor(mcolorpicker.getMeasuredWidth() / 2) - mViewContainer
                .getPaddingLeft());
        layoutParams.topMargin = (int) (mRGBcanavs.getTop() + y
                - Math.floor(mcolorpicker.getMeasuredHeight() / 2) - mViewContainer
                .getPaddingTop());
        mcolorpicker.setLayoutParams(layoutParams);
    }

    private float getwidth() {
        return mWidth;
    }

    private float getheigth() {
        return mHeight;
    }

    private void setwidth(float sat) {
        mWidth = sat;
    }

    private void setheight(float val) {
        mHeight = val;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mRootView = inflater.inflate(R.layout.rgb_view_landscape, null);
            ViewGroup rootViewG = (ViewGroup) getView();
            // Remove all the existing views from the root view.
            rootViewG.removeAllViews();
            rootViewG.addView(mRootView);
            setUpControls();
            setDefaultColorPickerPositionColor();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mRootView = inflater.inflate(R.layout.rgb_view_portrait, null);
            ViewGroup rootViewG = (ViewGroup) getView();
            // Remove all the existing views from the root view.
            rootViewG.removeAllViews();
            rootViewG.addView(mRootView);
            setUpControls();
            setDefaultColorPickerPositionColor();

        }

    }
}
