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

import java.util.Calendar;
import java.util.Locale;

public class DateTimeParser {
	/**
	 * Parses the date and time info.
	 * 
	 * @param characteristic
	 * @return time in human readable format
	 */
	public static String parse(final BluetoothGattCharacteristic characteristic) {
		return parse(characteristic, 0);
	}

	/**
	 * Parses the date and time info. This data has 7 bytes
	 * 
	 * @param characteristic
	 * @param offset
	 *            offset to start reading the time
	 * @return time in human readable format
	 */
	/* package */static String parse(final BluetoothGattCharacteristic characteristic, final int offset) {
		final int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
		final int month = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
		final int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3);
		final int hours = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4);
		final int minutes = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5);
		final int seconds = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6);

		final Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, hours, minutes, seconds);

		return String.format(Locale.US, "%1$te %1$tb %1$tY, %1$tH:%1$tM:%1$tS", calendar);
	}
}
