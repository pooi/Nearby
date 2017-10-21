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
package cf.nearby.nearby.bluetooth.BLEProfileDataParserClasses;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.R;

import java.util.ArrayList;

/**
 * Class used for parsing Health temperature related information
 */
public class HTMParser {

    private static ArrayList<String> mTempInfo = new ArrayList<String>();

    //Byte character format
    private static final String BYTE_CHAR_FORMAT = "%02X ";

    //Switch case Constants
    private static final int CASE_ARMPIT = 1;
    private static final int CASE_BODY = 2;
    private static final int CASE_EAR_LOBE = 3;
    private static final int CASE_FINGER = 4;
    private static final int CASE_GIT = 5;
    private static final int CASE_MOUTH = 6;
    private static final int CASE_RECTUM = 7;
    private static final int CASE_TYMPANUM = 8;
    private static final int CASE_TOE = 9;
    private static final int CASE_TOE_REP = 10;

    /**
     * Get the thermometer reading
     *
     * @param characteristic
     * @return
     */
    public static ArrayList<String> getHealthThermo(
            BluetoothGattCharacteristic characteristic, Context context) {
        String tempUnit = "";
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            byte flagByte = data[0];
            if ((flagByte & 0x01) != 0) {
                tempUnit = context.getString(R.string.tt_fahren_heit);
            } else {
                tempUnit = context.getString(R.string.tt_celcius);
            }
            for (byte byteChar : data)
                stringBuilder.append(String.format(BYTE_CHAR_FORMAT, byteChar));
        }
        final float temperature = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);

        Logger.i("tempRate " + temperature);

        mTempInfo.add(0, "" + temperature);
        mTempInfo.add(1, tempUnit);
        return mTempInfo;
    }

    /**
     * Get the thermometer sensor location
     *
     * @param characteristic
     * @return
     */
    public static String getHealthThermoSensorLocation(
            BluetoothGattCharacteristic characteristic, Context context) {
        String healthTherSensorLocation = "";
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format(BYTE_CHAR_FORMAT, byteChar));
            int healthBodySensor = Integer.valueOf(stringBuilder.toString()
                    .trim());
            switch (healthBodySensor) {
                case CASE_ARMPIT:
                    healthTherSensorLocation = context.getString(R.string.armpit);
                    break;
                case CASE_BODY:
                    healthTherSensorLocation = context.getString(R.string.body);
                    break;
                case CASE_EAR_LOBE:
                    healthTherSensorLocation = context.getString(R.string.ear);
                    break;
                case CASE_FINGER:
                    healthTherSensorLocation = context.getString(R.string.finger);
                    break;
                case CASE_GIT:
                    healthTherSensorLocation = context.getString(R.string.intestine);
                    break;
                case CASE_MOUTH:
                    healthTherSensorLocation = context.getString(R.string.mouth);
                    break;
                case CASE_RECTUM:
                    healthTherSensorLocation = context.getString(R.string.rectum);
                    break;
                case CASE_TYMPANUM:
                    healthTherSensorLocation = context.getString(R.string.tympanum);
                    break;
                case CASE_TOE:
                    healthTherSensorLocation = context.getString(R.string.toe_1);
                    break;
                case CASE_TOE_REP:
                    healthTherSensorLocation = context.getString(R.string.toe_2);
                    break;
                default:
                    healthTherSensorLocation = context.getString(R.string.reserverd);
                    break;
            }

        }
        return healthTherSensorLocation;
    }
}
