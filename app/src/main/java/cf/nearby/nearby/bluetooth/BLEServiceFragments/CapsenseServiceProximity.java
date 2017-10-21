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

import android.bluetooth.BluetoothGattService;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cf.nearby.nearby.R;


/**
 * Fragment to display the CapSense Proximity
 */
public class CapsenseServiceProximity extends Fragment {

    // GATT Service
    private static BluetoothGattService mService;

    // Data variables
    private static ImageView mProximityViewForeground;
    private static ImageView mProximityViewBackground;
    private static MediaPlayer mMediaPlayer;

    private static final int PROXIMITY_WATERMARK_LOW = 0;
    private static final int PROXIMITY_WATERMARK_MAX = 255;
    private static final int PROXIMITY_WATERMARK_INDICATOR = 127;
    private static boolean mValueIncreased = false;



    public CapsenseServiceProximity create(BluetoothGattService service) {
        CapsenseServiceProximity fragment = new CapsenseServiceProximity();
        mService = service;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.capsense_proximity,
                container, false);
        mProximityViewForeground = (ImageView) rootView
                .findViewById(R.id.proximity_view_1);
        mProximityViewBackground = (ImageView) rootView
                .findViewById(R.id.proximity_view_2);
        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep);
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display the proximity value
     *
     * @param proximity_value
     */
    public static void displayLiveData(int proximity_value) {
        int priximity2value = PROXIMITY_WATERMARK_MAX - proximity_value;
        mProximityViewBackground.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, proximity_value));
        mProximityViewForeground.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, priximity2value));

        if (proximity_value >= PROXIMITY_WATERMARK_INDICATOR) {
            try {
                if (mValueIncreased) {
                    mMediaPlayer.start();
                    mValueIncreased = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {//If music is playing already
                    mMediaPlayer.stop();//Stop playing the music
                }

            }

        }
        if (proximity_value >= PROXIMITY_WATERMARK_LOW && proximity_value <= PROXIMITY_WATERMARK_INDICATOR) {
            mValueIncreased = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep);
        mValueIncreased = true;
    }

    @Override
    public void onPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        mValueIncreased = false;
        super.onPause();
    }

}
