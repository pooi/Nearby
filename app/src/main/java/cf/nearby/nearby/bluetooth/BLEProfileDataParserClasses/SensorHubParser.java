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

import cf.nearby.nearby.bluetooth.CommonUtils.Logger;


/**
 * Class used for parsing Sensor hub related information
 */
public class SensorHubParser {
    private static final int FIRST_BITMASK = 0x01;
    public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
    public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
    public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
    public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
    public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
    public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
    public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;

    public static int getAcceleroMeterXYZReading(
            BluetoothGattCharacteristic characteristic) {

        int mAccXYZ = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        return mAccXYZ;
    }

    public static float getThermometerReading(
            BluetoothGattCharacteristic characteristic) {

        float mTemp = characteristic.getFloatValue(
                BluetoothGattCharacteristic.FORMAT_FLOAT, 0);
        return mTemp;
    }

    public static int getBarometerReading(
            BluetoothGattCharacteristic characteristic) {

        int mPressure = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        Logger.w("pressure " + mPressure);
        return mPressure;
    }

    public static int getSensorScanIntervalReading(
            BluetoothGattCharacteristic characteristic) {

        int mScaninterval = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return mScaninterval;
    }

    public static int getSensorTypeReading(
            BluetoothGattCharacteristic characteristic) {

        int mSensorType = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return mSensorType;
    }

    public static int getFilterConfiguration(
            BluetoothGattCharacteristic characteristic) {
        int mFilterConfiguration = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return mFilterConfiguration;
    }

    public static int getThresholdValue(
            BluetoothGattCharacteristic characteristic) {
        int mThreshold = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        return mThreshold;
    }
}
