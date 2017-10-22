package cf.nearby.nearby.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cf.nearby.nearby.R;
import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonFragments.ProfileScanningFragment;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;

public class ConnectBLEActivity extends AppCompatActivity {


    public static Boolean mApplicationInBackground = false;
    String attachmentFileName = "attachment.nbycf";
    /**
     * Used to manage connections of the Blue tooth LE Device
     */
    private static BluetoothLeService mBluetoothLeService;

    private boolean BLUETOOTH_STATUS_FLAG = true;
    private String Paired;
    private String Unpaired;

    /**
     * Code to manage Service life cycle.
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            // Initializing the service
            if (!mBluetoothLeService.initialize()) {
                Logger.d("Service not initialized");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // progress dialog variable
    private ProgressDialog mpdia;
    private AlertDialog mAlert;
    private InputStream attachment = null;

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

        // Set the Clear cahce on disconnect as true by devfault
        if (!Utils.ifContainsSharedPreference(this, Constants.PREF_PAIR_CACHE_STATUS)) {
            Utils.setBooleanSharedPreference(this, Constants.PREF_PAIR_CACHE_STATUS, true);
        }


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

    // Get intent, action and MIME type
    private void catchUpgradeFile() throws IOException, NullPointerException {
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        File targetLocationparent = new File("/storage/emulated/0/nearby_cf");

        if (Intent.ACTION_VIEW.equalsIgnoreCase(action) && data != null) {
            if (intent.getScheme().compareTo("content") == 0) {
                try {
                    Cursor c = getContentResolver().query(
                            intent.getData(), null, null, null, null);
                    c.moveToFirst();
                    final int fileNameColumnId = c.getColumnIndex(
                            MediaStore.MediaColumns.DISPLAY_NAME);
                    if (fileNameColumnId >= 0)
                        attachmentFileName = c.getString(fileNameColumnId);
                    Logger.e("Filename>>>" + attachmentFileName);
                    // Fetch the attachment
                    attachment = getContentResolver().openInputStream(data);
                    if (attachment == null) {
                        Logger.e("onCreate" + "cannot access mail attachment");
                    } else {
                        if (fileExists(attachmentFileName, targetLocationparent)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ConnectBLEActivity.this);
                            builder.setMessage(getResources().getString(R.string.alert_message_file_copy))
                                    .setCancelable(false)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setPositiveButton(
                                            getResources()
                                                    .getString(R.string.alert_message_yes),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    try {
                                                        FileOutputStream tmp = new FileOutputStream("/storage/emulated/0/nearby_cf" + File.separator + attachmentFileName);
                                                        byte[] buffer = new byte[1024];
                                                        int bytes = 0;
                                                        while ((bytes = attachment.read(buffer)) > 0)
                                                            tmp.write(buffer, 0, bytes);
                                                        tmp.close();
                                                        attachment.close();
                                                        getIntent().setData(null);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            })
                                    .setNegativeButton(
                                            getResources().getString(
                                                    R.string.alert_message_no),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // Cancel the dialog box
                                                    dialog.cancel();
                                                    getIntent().setData(null);
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            try {
                                FileOutputStream tmp = new FileOutputStream("/storage/emulated/0/nearby_cf" + File.separator + attachmentFileName);
                                byte[] buffer = new byte[1024];
                                int bytes = 0;
                                while ((bytes = attachment.read(buffer)) > 0)
                                    tmp.write(buffer, 0, bytes);
                                tmp.close();
                                attachment.close();
                                Toast.makeText(this, getResources().getString(R.string.toast_file_copied), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                String sourcePath = data.getPath();
                Logger.e("Action>>>" + action + "Uri" + data.toString() + "Source path>>" + sourcePath);

                final File sourceLocation = new File(sourcePath);
                String sourceFileName = sourceLocation.getName();

                final File targetLocation = new File("/storage/emulated/0/nearby_cf" + File.separator + sourceFileName);

                if (fileExists(sourceFileName, targetLocationparent)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            ConnectBLEActivity.this);
                    builder.setMessage(getResources().getString(R.string.alert_message_file_copy))
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setPositiveButton(
                                    getResources()
                                            .getString(R.string.alert_message_yes),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                copyDirectory(sourceLocation, targetLocation);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                            .setNegativeButton(
                                    getResources().getString(
                                            R.string.alert_message_no),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Cancel the dialog box
                                            dialog.cancel();
                                            getIntent().setData(null);
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    try {
                        copyDirectory(sourceLocation, targetLocation);
                        Toast.makeText(this, getResources().getString(R.string.toast_file_copied), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*
    Checks whether a file exists in the folder specified
     */
    public boolean fileExists(String name, File file) {
        File[] list = file.listFiles();
        if (list != null)
            for (File fil : list) {
                if (fil.isDirectory()) {
                    fileExists(name, fil);
                } else if (name.equalsIgnoreCase(fil.getName())) {
                    Logger.e("File>>" + fil.getName());
                    return true;
                }
            }
        return false;
    }

    // If targetLocation does not exist, it will be created.
    public void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation.getAbsolutePath());
            OutputStream out = new FileOutputStream(targetLocation.getAbsolutePath());

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            getIntent().setData(null);
        }
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

    @Override
    protected void onPause() {
        getIntent().setData(null);
        // Getting the current active fragment
//        Fragment currentFragment = getSupportFragmentManager()
//                .findFragmentById(R.id.container);
//        if (currentFragment instanceof ProfileScanningFragment || currentFragment instanceof
//                AboutFragment) {
//            Intent gattServiceIntent = new Intent(getApplicationContext(),
//                    BluetoothLeService.class);
//            stopService(gattServiceIntent);
//        }
        mApplicationInBackground = true;
        BLUETOOTH_STATUS_FLAG = false;
        unregisterReceiver(mBondStateReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Logger.e("onResume-->activity");
        try {
            catchUpgradeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mApplicationInBackground = false;
        BLUETOOTH_STATUS_FLAG = true;
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_PAIR_REQUEST);
        registerReceiver(mBondStateReceiver, intentFilter);
        super.onResume();
    }

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
