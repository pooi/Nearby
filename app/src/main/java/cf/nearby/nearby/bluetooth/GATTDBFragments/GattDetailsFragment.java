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
 * arising out of the mApplication or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or mApplication assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

package cf.nearby.nearby.bluetooth.GATTDBFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.DialogListner;
import cf.nearby.nearby.bluetooth.CommonUtils.GattAttributes;
import cf.nearby.nearby.bluetooth.CommonUtils.HexKeyBoard;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.UUIDDatabase;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
import cf.nearby.nearby.CustomApplication;
import cf.nearby.nearby.R;

public class GattDetailsFragment extends Fragment implements DialogListner, OnClickListener {

    // Indicate/Notify/Read Flag
    public static boolean mIsNotifyEnabled;
    public static boolean mIsIndicateEnabled;

    //characteristics
    private BluetoothGattCharacteristic mReadCharacteristic;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mIndicateCharacteristic;

    // GUI variables
    private TextView mServiceName;
    private TextView mCharacteristiceName;
    private TextView mHexValue;
    private EditText mAsciivalue;
    private TextView mDatevalue;
    private TextView mTimevalue;
    private TextView mBtnread;
    private TextView mBtnwrite;
    private TextView mBtnnotify;
    private TextView mBtnIndicate;
    private ImageView mBackbtn;
    private ProgressDialog mProgressDialog;

    // Application
    private CustomApplication mApplication;
    private boolean mIsReadEnabled;

    //Descriptor button
    private Button mBtnDescriptor;

    //Status buttons
    private String mStartNotifyText;
    private String mStopNotifyText;
    private String mStartIndicateText;
    private String mStopIndicateText;


    public GattDetailsFragment create() {
        GattDetailsFragment fragment = new GattDetailsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gattdb_details, container,
                false);
        mApplication = (CustomApplication) getActivity().getApplication();
        mServiceName = (TextView) rootView.findViewById(R.id.txtservicename);
        mHexValue = (TextView) rootView.findViewById(R.id.txthex);
        mCharacteristiceName = (TextView) rootView
                .findViewById(R.id.txtcharatrname);
        mBtnnotify = (TextView) rootView.findViewById(R.id.txtnotify);
        mBtnIndicate = (TextView) rootView.findViewById(R.id.txtindicate);
        mBtnread = (TextView) rootView.findViewById(R.id.txtread);
        mBtnwrite = (TextView) rootView.findViewById(R.id.txtwrite);
        mAsciivalue = (EditText) rootView.findViewById(R.id.txtascii);
        mTimevalue = (TextView) rootView.findViewById(R.id.txttime);
        mDatevalue = (TextView) rootView.findViewById(R.id.txtdate);
        mBackbtn = (ImageView) rootView.findViewById(R.id.imgback);
        mProgressDialog = new ProgressDialog(getActivity());
        mBtnDescriptor = (Button) rootView.findViewById(R.id.characteristic_descriptors);
        /**
         * Soft back button listner
         */
        mBackbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });

        /**
         * Checking descriptors available in the current characteristic
         */

       if (mApplication.getBluetoothgattcharacteristic().getDescriptors().size() == 0) {
            mBtnDescriptor.setVisibility(View.GONE);
        }
        /**
         * Descriptor button listner
         */
        mBtnDescriptor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Passing the characteristic details to GattDetailsFragment and
                 * adding that fragment to the view
                 */
                Bundle bundle = new Bundle();
                bundle.putString(Constants.GATTDB_SELECTED_SERVICE,
                        mServiceName.getText().toString());
                bundle.putString(Constants.GATTDB_SELECTED_CHARACTERISTICE,
                        mCharacteristiceName.getText().toString());
                FragmentManager fragmentManager = getFragmentManager();
                GattDescriptorFragment gattDescriptorFragment = new GattDescriptorFragment()
                        .create();
                gattDescriptorFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.container, gattDescriptorFragment)
                        .addToBackStack(null).commit();
            }
        });

        /**
         * button listeners
         */
        mBtnread.setOnClickListener(this);
        mBtnnotify.setOnClickListener(this);
        mBtnIndicate.setOnClickListener(this);
        mBtnwrite.setOnClickListener(this);
        mHexValue.setOnClickListener(this);
        mAsciivalue.setOnClickListener(this);
        mAsciivalue.setEnabled(false);
        mHexValue.setEnabled(false);
        /**
         * Ascii done click listner
         */
        mAsciivalue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String result=mAsciivalue.getText().toString();
                    String hexValue=asciiToHex(result);
                    Logger.e("Hex value-->"+hexValue);
                    byte[] convertedBytes = Utils.hexStringToByteArray(hexValue);
                    // Displaying the hex value in hex field
                    displayHexValue(convertedBytes);
                    writeCharaValue(convertedBytes);
                    hideAsciiKeyboard();
                 return true;
                }
                return false;
            }
        });
        mAsciivalue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if(hasfocus){
                    clearall();
                }
            }
        });
        /**
         * GUI Updates
         */
        mServiceName.setSelected(true);
        mCharacteristiceName.setSelected(true);
        mAsciivalue.setSelected(true);
        mHexValue.setSelected(true);

        // Getting the characteristics from the application class
        mReadCharacteristic = mApplication.getBluetoothgattcharacteristic();
        mNotifyCharacteristic = mApplication.getBluetoothgattcharacteristic();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mServiceName.setText(bundle
                    .getString(Constants.GATTDB_SELECTED_SERVICE));
            mCharacteristiceName.setText(bundle
                    .getString(Constants.GATTDB_SELECTED_CHARACTERISTICE));
        }

        //Getting the button text from resources
        mStartNotifyText = getResources().getString(R.string.gatt_services_notify);
        mStopNotifyText = getResources().getString(R.string.gatt_services_stop_notify);
        mStartIndicateText = getResources().getString(R.string.gatt_services_indicate);
        mStopIndicateText = getResources().getString(R.string.gatt_services_stop_indicate);

        //Check the available properties in the characteristics and provide corresponding buttons
        UIbuttonvisibility();

        /**
         * Check for HID Service
         */
        BluetoothGattService mBluetoothGattService = mReadCharacteristic.getService();
        if (mBluetoothGattService.getUuid().toString().
                equalsIgnoreCase(GattAttributes.HUMAN_INTERFACE_DEVICE_SERVICE)) {
            showHIDWarningMessage();
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Method to hide the ascii keyboard
     */
    private void hideAsciiKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        mAsciivalue.clearFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
        mIsNotifyEnabled = false;
        mIsIndicateEnabled = false;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
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

    /**
     * Method to make the Buttons visible to the user based on the available properties in the
     * characteristic
     */
    private void UIbuttonvisibility() {

        // Check read supported on the Charatceristic
        if (getGattCharacteristicsPropertices(
                mReadCharacteristic.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_READ)) {
            // Read property available
            mBtnread.setVisibility(View.VISIBLE);
        }
        // Check write supported on the Charatceristic
        if (getGattCharacteristicsPropertices(
                mReadCharacteristic.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_WRITE)
                | getGattCharacteristicsPropertices(
                mReadCharacteristic.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            // Write property available
            mBtnwrite.setVisibility(View.VISIBLE);
            mAsciivalue.setEnabled(true);
            mHexValue.setEnabled(true);
        }
        // Check notify supported on the Charatceristic
        if (getGattCharacteristicsPropertices(
                mReadCharacteristic.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            // Notify property available
             mBtnnotify.setVisibility(View.VISIBLE);
            BluetoothGattDescriptor descriptor = mReadCharacteristic.
                    getDescriptor(UUIDDatabase.UUID_CLIENT_CHARACTERISTIC_CONFIG);
            if (descriptor != null) {
                BluetoothLeService.readDescriptor(descriptor);
            }
        }
        // Check indicate supported on the Charatceristic
        if (getGattCharacteristicsPropertices(
                mReadCharacteristic.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            // Indicate property available
            mBtnIndicate.setVisibility(View.VISIBLE);
            BluetoothGattDescriptor descriptor = mReadCharacteristic.
                    getDescriptor(UUIDDatabase.UUID_CLIENT_CHARACTERISTIC_CONFIG);
            if (descriptor != null) {
                BluetoothLeService.readDescriptor(descriptor);
            }
        }

    }

    /**
     * Preparing Broadcast receiver to broadcast read characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataRead(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            BluetoothLeService.readCharacteristic(gattCharacteristic);
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
    void stopBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
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
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
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
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            if (gattCharacteristic != null) {
                BluetoothLeService.setCharacteristicIndication(
                        gattCharacteristic, false);
            }

        }

    }

    /**
     * Method to convert the hexvalue to ascii value and displaying to the user
     *
     * @param hexValue
     */
    void displayASCIIValue(String hexValue) {
        mAsciivalue.setText("");
      StringBuilder output = new StringBuilder("");
        try {
            for (int i = 0; i < hexValue.length(); i += 2) {
                String str = hexValue.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAsciivalue.setText(output.toString());
    }

    /**
     * Method to display the hexValue after converting from byte array
     *
     * @param array
     */
    void displayHexValue(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(String.format("%02x", byteChar));
        }
        mHexValue.setText(sb.toString());
    }

    /**
     * Method to display time and date
     */
    private void displayTimeandDate() {

        mTimevalue.setText(Utils.GetTimeFromMilliseconds());
        mDatevalue.setText(Utils.GetDateFromMilliseconds());
    }

    /**
     * Clearing all fields
     */
    private void clearall() {
        Logger.e("Cleared");
        mTimevalue.setText("");
        mDatevalue.setText("");
        mAsciivalue.setText("");
        mHexValue.setText("");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtwrite:
                clearall();
                HexKeyBoard hexKeyBoard = new HexKeyBoard(getActivity(), mReadCharacteristic, true);
                hexKeyBoard.setDialogListner(this);
                hexKeyBoard.show();
                mAsciivalue.clearFocus();
                break;
            case R.id.txthex:
                clearall();
                HexKeyBoard hexKeyBoardet = new HexKeyBoard(getActivity(), mReadCharacteristic, true);
                hexKeyBoardet.setDialogListner(this);
                hexKeyBoardet.show();
                mAsciivalue.clearFocus();
                break;
            case R.id.txtascii:
                clearall();
                break;

            case R.id.txtread:
                clearall();
                prepareBroadcastDataRead(mReadCharacteristic);
                mIsReadEnabled = true;
                mAsciivalue.clearFocus();
                break;

            case R.id.txtnotify:
                clearall();
                mAsciivalue.clearFocus();
                mNotifyCharacteristic = mApplication
                        .getBluetoothgattcharacteristic();
                TextView clickedNotifyText = (TextView) v;
                String buttonNotifyText = clickedNotifyText.getText().toString();
                if (buttonNotifyText.equalsIgnoreCase(mStartNotifyText)) {
                    prepareBroadcastDataNotify(mNotifyCharacteristic);
                    mBtnnotify.setText(mStopNotifyText);
                    mIsNotifyEnabled = true;
                } else if (buttonNotifyText.equalsIgnoreCase(mStopNotifyText)) {
                    stopBroadcastDataNotify(mNotifyCharacteristic);
                    mBtnnotify
                            .setText(mStartNotifyText);
                    mIsNotifyEnabled = false;
                }
                break;

            case R.id.txtindicate:
                clearall();
                mAsciivalue.clearFocus();
                TextView clickedIndicateText = (TextView) v;
                String buttonIndicateText = clickedIndicateText.getText().toString();
                mIndicateCharacteristic = mApplication.getBluetoothgattcharacteristic();
                if (mIndicateCharacteristic != null) {
                    if (buttonIndicateText.equalsIgnoreCase(mStartIndicateText)) {
                        prepareBroadcastDataIndicate(mIndicateCharacteristic);
                        mBtnIndicate
                                .setText(mStopIndicateText);
                        mIsIndicateEnabled = true;

                    } else if (buttonIndicateText.equalsIgnoreCase(mStopIndicateText)) {
                        stopBroadcastDataIndicate(mIndicateCharacteristic);
                        mBtnIndicate
                                .setText(mStartIndicateText);
                        mIsIndicateEnabled = false;
                    }
                }
                break;
        }
    }


    @Override
    public void dialog0kPressed(String result) {
        byte[] convertedBytes = Utils.convertingTobyteArray(result);
        // Displaying the hex and ASCII values
        displayHexValue(convertedBytes);
        displayASCIIValue(mHexValue.getText().toString());
        writeCharaValue(convertedBytes);
    }

    /**
     * Method to write the byte value to the characteristic
     * @param value
     */
    private void writeCharaValue(byte[] value) {
        displayTimeandDate();
        // Writing the hexValue to the characteristic
        try {
            BluetoothLeService.writeCharacteristicGattDb(mReadCharacteristic,
                    value);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to convert ascii to hex
     * @param asciiValue
     * @return
     */
    private String asciiToHex(String asciiValue)
    {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
        {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }
    @Override
    public void dialogCancelPressed(Boolean aBoolean) {

    }

    /**
     * Method to update the Notify/Indicate button status based on the descriptor value received
     * @param array
     */
    private void updateButtonStatus(byte[] array) {
        int status = array[0];
        switch (status) {
            case 0:
                if (mBtnnotify.getVisibility() == View.VISIBLE)
                    mBtnnotify.setText(mStartNotifyText);

                if (mBtnIndicate.getVisibility() == View.VISIBLE)
                    mBtnIndicate.setText(mStartIndicateText);
                break;
            case 1:
                if (mBtnnotify.getVisibility() == View.VISIBLE)
                    mBtnnotify.setText(mStopNotifyText);
                if (mNotifyCharacteristic != null)
                    prepareBroadcastDataNotify(mNotifyCharacteristic);
                break;
            case 2:
                if (mBtnIndicate.getVisibility() == View.VISIBLE)
                    mBtnIndicate.setText(mStopIndicateText);
                if (mIndicateCharacteristic != null)
                    prepareBroadcastDataIndicate(mIndicateCharacteristic);
                break;
        }
    }

    /**
     * HID characteristic alert message
     */
    void showHIDWarningMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        // set title
        alertDialogBuilder
                .setTitle(R.string.app_name);
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.alert_message_hid_warning)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_message_exit_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Error code alert message
     * @param errorcode
     */

    private void displayAlertWithMessage(String errorcode) {
        String errorMessage = getResources().getString(R.string.alert_message_write_error) +
                "\n" + getResources().getString(R.string.alert_message_write_error_code) + errorcode +
                "\n" + getResources().getString(R.string.alert_message_try_again);
        AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView myMsg = new TextView(getActivity());
        myMsg.setText(errorMessage);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(myMsg);
        builder.setTitle(getActivity().getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton(
                        getActivity().getResources().getString(
                                R.string.alert_message_exit_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    /**
     * Broadcast receiver class to receives the broadcast from the service class
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Getting the intent action and extras
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                   BluetoothGattCharacteristic requiredCharacteristic =
                                mApplication.getBluetoothgattcharacteristic();
                    String uuidRequired = requiredCharacteristic.getUuid().toString();
                    String receivedUUID = "";
                    String requiredServiceUUID = requiredCharacteristic.
                            getService().getUuid().toString();
                    String receivedServiceUUID = "";
                    int requiredInstanceID = requiredCharacteristic.getInstanceId();
                    int receivedInstanceID = -1;
                    int receivedServiceInstanceID = -1;
                    int requiredServiceInstanceID =
                            requiredCharacteristic.getService().
                                    getInstanceId();
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        receivedUUID  = intent.getStringExtra(
                                Constants.EXTRA_BYTE_UUID_VALUE);
                    }if (extras.containsKey(Constants.EXTRA_BYTE_INSTANCE_VALUE)) {
                        receivedInstanceID= intent.getIntExtra(Constants.
                                        EXTRA_BYTE_INSTANCE_VALUE,
                                -1);
                    }if(extras.containsKey(Constants.
                                                EXTRA_BYTE_SERVICE_UUID_VALUE)) {
                        receivedServiceUUID = intent.getStringExtra(
                                Constants.EXTRA_BYTE_SERVICE_UUID_VALUE);
                    }if(extras.containsKey(Constants.
                                                        EXTRA_BYTE_SERVICE_INSTANCE_VALUE)) {
                        receivedServiceInstanceID  =
                                intent.getIntExtra(Constants.EXTRA_BYTE_SERVICE_INSTANCE_VALUE,
                                        -1);
                    }
                    if(uuidRequired.equalsIgnoreCase(receivedUUID)
                            && requiredServiceUUID.equalsIgnoreCase(receivedServiceUUID)
                            && requiredInstanceID==receivedInstanceID
                            && requiredServiceInstanceID==receivedServiceInstanceID) {

                        byte[] array = intent
                                .getByteArrayExtra(Constants.
                                        EXTRA_BYTE_VALUE);
                         displayHexValue(array);
                         displayASCIIValue(mHexValue.getText().
                                toString());
                         displayTimeandDate();
                         }
                     }
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.
                            EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID)) {
                        BluetoothGattCharacteristic requiredCharacteristic = mApplication.
                                getBluetoothgattcharacteristic();
                        /**
                         * Checking the recieved characteristic UUID and Expected UUID are same
                         */
                        String uuidRequired = requiredCharacteristic.getUuid().toString();
                        String receivedUUID = intent.getStringExtra(
                                Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID);
                        if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
                            if (extras.containsKey(Constants.
                                    EXTRA_BYTE_DESCRIPTOR_INSTANCE_VALUE)) {
                                /**
                                 * Checking the received characteristic instance ID and Expected
                                 * characteristic instance ID
                                 */
                                int requiredInstanceID=requiredCharacteristic.getInstanceId();
                                int receivedInstanceID=intent.getIntExtra(Constants.
                                                EXTRA_BYTE_DESCRIPTOR_INSTANCE_VALUE,
                                        -1);
                                if (requiredInstanceID==receivedInstanceID) {
                                    byte[] array = intent.getByteArrayExtra(
                                            Constants.EXTRA_DESCRIPTOR_BYTE_VALUE);
                                    updateButtonStatus(array);
                                }
                            }

                        }

                    }
                }
                }
            if (action.equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_ERROR)) {
                if (extras.containsKey(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE)) {
                    String errorMessage = extras.
                            getString(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE);
                    displayAlertWithMessage(errorMessage);
                    mAsciivalue.setText("");
                    mHexValue.setText("");
                }

            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    Logger.i("Bonding is in process....");
                    Utils.bondingProgressDialog(getActivity(), mProgressDialog, true);
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    // Bonded...
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getmBluetoothDeviceName() + "|"
                            + BluetoothLeService.getmBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_paired);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(getActivity(), mProgressDialog, false);
                    if (mIsIndicateEnabled) {
                        prepareBroadcastDataIndicate(mIndicateCharacteristic);
                    }
                    if (mIsNotifyEnabled) {
                        prepareBroadcastDataNotify(mNotifyCharacteristic);
                    }
                    if (mIsReadEnabled) {
                        prepareBroadcastDataRead(mReadCharacteristic);
                    }

                } else if (state == BluetoothDevice.BOND_NONE) {
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + BluetoothLeService.getmBluetoothDeviceName() + "|"
                            + BluetoothLeService.getmBluetoothDeviceAddress() + "]" +
                            getResources().getString(R.string.dl_commaseparator) +
                            getResources().getString(R.string.dl_connection_unpaired);
                    Logger.datalog(dataLog);
                }
            }

        }

    };
}
