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


import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.GattAttributes;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
import cf.nearby.nearby.CustomApplication;
import cf.nearby.nearby.R;

/**
 * Descriptor Details Class
 */
public class GattDescriptorDetails extends Fragment implements
        View.OnClickListener {

    //Characteristic
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

    //Descriptor
    private BluetoothGattDescriptor mDescriptor;

    // View
    private ViewGroup mContainer;

    // Application
    private CustomApplication mApplication;

    //View fields
    private TextView mCharacteristicName;
    private TextView mDescriptorName;
    private TextView mDescriptorValue;
    private TextView mHexValue;
    private Button mReadButton;
    private Button mNotifyButton;
    private Button mIndicateButton;
    private ImageView mBackBtn;
    private ProgressDialog mProgressDialog;
    private String mDescriptorStatus = "";
    private String mStartNotifyText;
    private String mStopNotifyText;
    private String mStartIndicateText;
    private String stopIndicateText;

    // flag
    private boolean mGUIUpdateFlag = false;
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_UUID)) {
                    if (mApplication != null) {
                        BluetoothGattDescriptor descriptor = mApplication.getBluetoothgattDescriptor();
                        String uuidRequired = descriptor.getUuid().toString();
                        String uuidReceiving = intent.
                                getStringExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_UUID);
                        if (uuidRequired.equalsIgnoreCase(uuidReceiving)) {
                            // Data Received
                            if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_VALUE)) {
                                mDescriptorStatus = intent.getStringExtra(Constants.EXTRA_DESCRIPTOR_VALUE);
                                displayDescriptorValue(mDescriptorStatus);
                            }
                            if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE)) {
                                byte[] array = intent
                                        .getByteArrayExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE);
                                displayHexValue(array);
                                updateButtonStatus(array);
                            }
                        }

                    }

                }
            }
            if (action.equals(BluetoothLeService.ACTION_WRITE_SUCCESS)) {
                if (mGUIUpdateFlag) {
                    BluetoothLeService.readDescriptor(mDescriptor);
                    mGUIUpdateFlag = false;
                }

            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);

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

    public GattDescriptorDetails create() {
        GattDescriptorDetails fragment = new GattDescriptorDetails();
        return fragment;
    }

    private void updateButtonStatus(byte[] array) {
        int status = array[0];
        switch (status) {
            case 0:
                if (mNotifyButton.getVisibility() == View.VISIBLE)
                    mNotifyButton.setText(mStartNotifyText);

                if (mIndicateButton.getVisibility() == View.VISIBLE)
                    mIndicateButton.setText(mStartIndicateText);
                break;
            case 1:
                if (mNotifyButton.getVisibility() == View.VISIBLE)
                    mNotifyButton.setText(mStopNotifyText);
                break;
            case 2:
                if (mIndicateButton.getVisibility() == View.VISIBLE)
                    mIndicateButton.setText(stopIndicateText);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gattdb_descriptor_details, container,
                false);
        this.mContainer = container;
        mApplication = (CustomApplication) getActivity().getApplication();
        mCharacteristicName = (TextView) rootView.findViewById(R.id.txtcharacteristicname);
        mDescriptorName = (TextView) rootView.findViewById(R.id.txtdescriptorname);
        mDescriptorValue = (TextView) rootView.findViewById(R.id.txtdescriptorvalue);
        mHexValue = (TextView) rootView.findViewById(R.id.txtdescriptorHexvalue);
        mBackBtn = (ImageView) rootView.findViewById(R.id.imgback);

        mProgressDialog = new ProgressDialog(getActivity());

        mBluetoothGattCharacteristic = mApplication.getBluetoothgattcharacteristic();
        String characteristicUUID = mBluetoothGattCharacteristic.getUuid().toString();
        mCharacteristicName.setText(GattAttributes.lookupUUID(mBluetoothGattCharacteristic.getUuid(), characteristicUUID));

        mDescriptor = mApplication.getBluetoothgattDescriptor();
        String DescriptorUUID = mDescriptor.getUuid().toString();
        mDescriptorName.setText(GattAttributes.lookupUUID(mDescriptor.getUuid(), DescriptorUUID));

        mReadButton = (Button) rootView.findViewById(R.id.btn_read);
        mNotifyButton = (Button) rootView.findViewById(R.id.btn_write_notify);
        mIndicateButton = (Button) rootView.findViewById(R.id.btn_write_indicate);
        if (DescriptorUUID.equalsIgnoreCase(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) {
            if (getGattCharacteristicsPropertices(mBluetoothGattCharacteristic.getProperties(),
                    BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
                mNotifyButton.setVisibility(View.VISIBLE);
                mNotifyButton.setText(getResources().getString(R.string.gatt_services_notify));
            }
            if (getGattCharacteristicsPropertices(mBluetoothGattCharacteristic.getProperties(),
                    BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
                mIndicateButton.setVisibility(View.VISIBLE);
                mIndicateButton.setText(getResources().getString(R.string.gatt_services_indicate));
            }
        } else {
            mNotifyButton.setVisibility(View.GONE);
        }
        mStartNotifyText = getResources().getString(R.string.gatt_services_notify);
        mStopNotifyText = getResources().getString(R.string.gatt_services_stop_notify);
        mStartIndicateText = getResources().getString(R.string.gatt_services_indicate);
        stopIndicateText = getResources().getString(R.string.gatt_services_stop_indicate);
        mReadButton.setOnClickListener(this);
        mNotifyButton.setOnClickListener(this);
        mIndicateButton.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        if (mDescriptor != null) {
            BluetoothLeService.readDescriptor(mDescriptor);
        }
        return rootView;
    }

    private void displayDescriptorValue(String value) {
        mDescriptorValue.setText(value);
    }

    void displayHexValue(byte[] array) {
        String descriptorValue = Utils.ByteArraytoHex(array);
        mHexValue.setText(descriptorValue);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_write_notify:
                Button btn = (Button) view;
                String btnText = btn.getText().toString();
                mGUIUpdateFlag = true;
                if (btnText.equalsIgnoreCase(mStartNotifyText)) {
                    prepareBroadcastDataNotify(mBluetoothGattCharacteristic);
                    GattDetailsFragment.mIsNotifyEnabled = true;
                    btn.setText(mStopNotifyText);
                }
                if (btnText.equalsIgnoreCase(mStopNotifyText)) {
                    stopBroadcastDataNotify(mBluetoothGattCharacteristic);
                    GattDetailsFragment.mIsNotifyEnabled = false;
                    btn.setText(mStartNotifyText);
                }
                break;
            case R.id.btn_write_indicate:
                Button btnIndicate = (Button) view;
                String btnTextIndicate = btnIndicate.getText().toString();
                mGUIUpdateFlag = true;
                if (btnTextIndicate.equalsIgnoreCase(mStartIndicateText)) {
                    prepareBroadcastDataIndicate(mBluetoothGattCharacteristic);
                    GattDetailsFragment.mIsIndicateEnabled = true;
                    btnIndicate.setText(stopIndicateText);
                }
                if (btnTextIndicate.equalsIgnoreCase(stopIndicateText)) {
                    stopBroadcastDataIndicate(mBluetoothGattCharacteristic);
                    GattDetailsFragment.mIsIndicateEnabled = false;
                    btnIndicate.setText(mStartIndicateText);
                }
                break;
            case R.id.btn_read:
                if (mDescriptor != null) {
                    BluetoothLeService.readDescriptor(mDescriptor);
                }
                break;
            case R.id.imgback:
                getActivity().onBackPressed();
                break;
        }
    }



    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        Logger.i("Notify called");
        final BluetoothGattCharacteristic characteristic = gattCharacteristic;
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic,
                    true);

        }

    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void stopBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        Logger.i("Notify stopped");
        final BluetoothGattCharacteristic characteristic = gattCharacteristic;
        final int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (gattCharacteristic != null) {
                BluetoothLeService.setCharacteristicNotification(
                        gattCharacteristic, false);
            }

        }

    }

    /**
     * Preparing Broadcast receiver to broadcast indicate characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataIndicate(
            BluetoothGattCharacteristic gattCharacteristic) {
        Logger.i("Indicate called");
        final BluetoothGattCharacteristic characteristic = gattCharacteristic;
        final int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            BluetoothLeService.setCharacteristicIndication(
                    gattCharacteristic, true);

        }
    }

    /**
     * Stopping Broadcast receiver to broadcast indicate characteristics
     *
     * @param gattCharacteristic
     */
    void stopBroadcastDataIndicate(
            BluetoothGattCharacteristic gattCharacteristic) {
        Logger.i("Indicate stopped");
        final BluetoothGattCharacteristic characteristic = gattCharacteristic;
        final int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            if (gattCharacteristic != null) {
                Logger.d("Stopped notification");
                BluetoothLeService.setCharacteristicIndication(
                        gattCharacteristic, false);
            }

        }
    }

    /**
     * Return the property enabled in the characteristic
     *
     * @param characteristics
     * @param characteristicsSearch
     * @return
     */
    boolean getGattCharacteristicsPropertices(int characteristics,
                                              int characteristicsSearch) {
        return (characteristics & characteristicsSearch) == characteristicsSearch;

    }
}
