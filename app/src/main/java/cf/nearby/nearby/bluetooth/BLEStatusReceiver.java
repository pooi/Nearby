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

package cf.nearby.nearby.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cf.nearby.nearby.R;
import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonFragments.ProfileScanningFragment;
import cf.nearby.nearby.bluetooth.CommonFragments.ServiceDiscoveryFragment;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
//import cf.nearby.nearby.bluetooth.DataLoggerFragments.DataLoggerHistoryList;
//import cf.nearby.nearby.bluetooth.OTAFirmwareUpdate.OTAFilesListingActivity;
//import cf.nearby.nearby.bluetooth.OTAFirmwareUpdate.OTAFirmwareUpgradeFragment;


/**
 * Receiver class for BLE disconnect Event.
 * This receiver will be called when a disconnect message from the connected peripheral
 * is received by the application
 */
public class BLEStatusReceiver extends BroadcastReceiver {
    static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            Logger.e("onReceive--" + ConnectBLEActivity.mApplicationInBackground);
//            if (!ConnectBLEActivity.mApplicationInBackground
//                    || !OTAFilesListingActivity.mApplicationInBackground
//                    || !DataLoggerHistoryList.mApplicationInBackground)
            if (!ConnectBLEActivity.mApplicationInBackground)
            {
                Toast.makeText(context,
                        context.getResources().getString(R.string.alert_message_bluetooth_disconnect),
                        Toast.LENGTH_SHORT).show();
//                if (OTAFirmwareUpgradeFragment.mFileupgradeStarted) {
//                    //Resetting all preferences on Stop Button
//                    Utils.setStringSharedPreference(context, Constants.PREF_OTA_FILE_ONE_NAME, "Default");
//                    Utils.setStringSharedPreference(context, Constants.PREF_OTA_FILE_TWO_PATH, "Default");
//                    Utils.setStringSharedPreference(context, Constants.PREF_OTA_FILE_TWO_NAME, "Default");
//                    Utils.setStringSharedPreference(context, Constants.PREF_BOOTLOADER_STATE, "Default");
//                    Utils.setIntSharedPreference(context, Constants.PREF_PROGRAM_ROW_NO, 0);
//                }
                if (!ProfileScanningFragment.isInFragment &&
                        !ServiceDiscoveryFragment.isInServiceFragment&&!ConnectBLEActivity.mApplicationInBackground) {
                    Logger.e("Not in PSF and SCF");
                    if (BluetoothLeService.getConnectionState() == BluetoothLeService.STATE_DISCONNECTED) {
                        Toast.makeText(context,
                                context.getResources().getString(R.string.alert_message_bluetooth_disconnect),
                                Toast.LENGTH_SHORT).show();
                        Intent homePage = new Intent(context, ConnectBLEActivity.class);
                        homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(homePage);
                    }
                }
            }
        }
    }
}
