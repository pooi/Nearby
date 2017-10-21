package cf.nearby.nearby.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cf.nearby.nearby.R;
import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonFragments.ProfileScanningFragment;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;

public class ConnectBLEActivity extends AppCompatActivity {


    private boolean BLUETOOTH_STATUS_FLAG = true;
    private String Paired;
    private String Unpaired;

    // progress dialog variable
    private ProgressDialog mpdia;
    private AlertDialog mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_ble);

        Paired = getResources().getString(R.string.bluetooth_pair);
        Unpaired = getResources().getString(R.string.bluetooth_unpair);
        mpdia = new ProgressDialog(this);
        mAlert = new AlertDialog.Builder(this).create();
        mAlert.setMessage(getResources().getString(
                R.string.alert_message_bluetooth_reconnect));
        mAlert.setCancelable(false);
        mAlert.setTitle(getResources().getString(R.string.app_name));
        mAlert.setButton(Dialog.BUTTON_POSITIVE, getResources().getString(
                R.string.alert_message_exit_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intentActivity = getIntent();
                finish();
                overridePendingTransition(
                        R.anim.slide_left, R.anim.push_left);
                startActivity(intentActivity);
                overridePendingTransition(
                        R.anim.slide_right, R.anim.push_right);
            }
        });
        mAlert.setCanceledOnTouchOutside(false);


        Intent gattServiceIntent = new Intent(getApplicationContext(),
                BluetoothLeService.class);
        startService(gattServiceIntent);

        ProfileScanningFragment profileScanningFragment = new ProfileScanningFragment();
        displayView(profileScanningFragment,
                Constants.PROFILE_SCANNING_FRAGMENT_TAG);

    }


    void displayView(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag).commit();
    }

    private BroadcastReceiver mBondStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //Received when the bond state is changed
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    String dataLog2 = getResources().getString(R.string.dl_commaseparator)
                            + "[" + ProfileScanningFragment.mDeviceName + "|"
                            + ProfileScanningFragment.mDeviceAddress + "] " +
                            getResources().getString(R.string.dl_connection_pairing_request);
                    Logger.datalog(dataLog2);
                    Utils.bondingProgressDialog(ConnectBLEActivity.this, mpdia, true);
                } else if (state == BluetoothDevice.BOND_BONDED) {
                    Logger.e("ConnectBLEActivity--->Bonded");
                    Utils.stopDialogTimer();
                    // Bonded...
                    if (ProfileScanningFragment.mPairButton != null) {
                        ProfileScanningFragment.mPairButton.setText(Paired);
                        if(bondState == BluetoothDevice.BOND_BONDED && previousBondState == BluetoothDevice.BOND_BONDING) {
                            Toast.makeText(ConnectBLEActivity.this, getResources().getString(R.string.toast_paired), Toast.LENGTH_SHORT).show();
                        }
                    }
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + ProfileScanningFragment.mDeviceName + "|"
                            + ProfileScanningFragment.mDeviceAddress + "] " +
                            getResources().getString(R.string.dl_connection_paired);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(ConnectBLEActivity.this, mpdia, false);

                } else if (state == BluetoothDevice.BOND_NONE) {
                    // Not bonded...
                    Logger.e("ConnectBLEActivity--->Not Bonded");
                    Utils.stopDialogTimer();
                    if (ProfileScanningFragment.mPairButton != null) {
                        ProfileScanningFragment.mPairButton.setText(Unpaired);
                        if(bondState == BluetoothDevice.BOND_NONE && previousBondState == BluetoothDevice.BOND_BONDED) {
                            Toast.makeText(ConnectBLEActivity.this, getResources().getString(R.string.toast_unpaired), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ConnectBLEActivity.this,
                                    getResources().getString(R.string.dl_connection_pairing_unsupported),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    String dataLog = getResources().getString(R.string.dl_commaseparator)
                            + "[" + ProfileScanningFragment.mDeviceName + "|"
                            + ProfileScanningFragment.mDeviceAddress + "] " +
                            getResources().getString(R.string.dl_connection_pairing_unsupported);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(ConnectBLEActivity.this, mpdia, false);
                }else{
                    Logger.e("Error received in pair-->"+state);
                }
            }
            else  if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                Logger.i("BluetoothAdapter.ACTION_STATE_CHANGED.");
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) ==
                        BluetoothAdapter.STATE_OFF) {
                    Logger.i("BluetoothAdapter.STATE_OFF");
                    if (BLUETOOTH_STATUS_FLAG) {
                        connectionLostBluetoothalertbox(true);
                    }

                }
                else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) ==
                        BluetoothAdapter.STATE_ON) {
                    Logger.i("BluetoothAdapter.STATE_ON");
                    if (BLUETOOTH_STATUS_FLAG) {
                        connectionLostBluetoothalertbox(false);
                    }

                }

            }
            else if(action.equals(BluetoothLeService.ACTION_PAIR_REQUEST)){
                Logger.e("Pair request received");
                Logger.e("ConnectBLEActivity--->Pair Request");
                Utils.stopDialogTimer();
            }

        }
    };

    public void connectionLostBluetoothalertbox(Boolean status) {
        //Disconnected
        if (status) {
            mAlert.show();
        } else {
            if (mAlert != null && mAlert.isShowing())
                mAlert.dismiss();
        }

    }
}
