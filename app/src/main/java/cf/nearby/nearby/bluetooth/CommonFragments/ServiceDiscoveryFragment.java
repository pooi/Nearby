package cf.nearby.nearby.bluetooth.CommonFragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.UUIDDatabase;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
import cf.nearby.nearby.CustomApplication;
import cf.nearby.nearby.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by smirno on 7/20/2015.
 */
public class ServiceDiscoveryFragment extends Fragment {
    // UUID key
    private static final String LIST_UUID = "UUID";
    // Stops scanning after 2 seconds.
    private static final long DELAY_PERIOD = 500;
    private static final long SERVICE_DISCOVERY_TIMEOUT = 10000;
    static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceFindMeData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceProximityData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceSensorHubData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattdbServiceData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    private static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceMasterData =
            new ArrayList<HashMap<String, BluetoothGattService>>();
    // Application
    private CustomApplication mApplication;
    private ProgressDialog mProgressDialog;
    private Timer mTimer;
    private TextView mNoserviceDiscovered;
    public  static boolean isInServiceFragment = false;

    private final BroadcastReceiver mServiceDiscoveryListner=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                Logger.e("Service discovered");
                if(mTimer!=null)
                mTimer.cancel();
                prepareGattServices(BluetoothLeService.getSupportedGattServices());

                /*
                / Changes the MTU size to 512 in case LOLLIPOP and above devices
                */
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BluetoothLeService.exchangeGattMtu(512);
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL
                    .equals(action)) {
                    mProgressDialog.dismiss();
                      if(mTimer!=null)
                    mTimer.cancel();
                    showNoServiceDiscoverAlert();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.servicediscovery_temp_fragment, container, false);
        mNoserviceDiscovered=(TextView)rootView.findViewById(R.id.no_service_text);
        mProgressDialog=new ProgressDialog(getActivity());
        mTimer=showServiceDiscoveryAlert(false);
        mApplication = (CustomApplication) getActivity().getApplication();
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.e("Discover service called");
                if(BluetoothLeService.getConnectionState()== BluetoothLeService.STATE_CONNECTED)
                BluetoothLeService.discoverServices();
            }
        }, DELAY_PERIOD);
        setHasOptionsMenu(true);
        return rootView;
    }

    private Timer showServiceDiscoveryAlert(boolean isReconnect) {
        mProgressDialog.setTitle(getString(R.string.progress_tile_service_discovering));
        if(!isReconnect){
            mProgressDialog.setMessage(getString(R.string.progress_message_service_discovering));
        }else{
            mProgressDialog.setMessage(getString(R.string.progress_message_reconnect));
        }
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                    mNoserviceDiscovered.post(new Runnable() {
                        @Override
                        public void run() {
                         mNoserviceDiscovered.setVisibility(View.VISIBLE);
                        }
                    });
                }

            }
        }, SERVICE_DISCOVERY_TIMEOUT);
        return timer;
    }

    /**
     * Getting the GATT Services
     *
     * @param gattServices
     */
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        // Optimization code for Sensor HUb
        if (isSensorHubPresent(gattServices)) {
            prepareSensorHubData(gattServices);
        } else {
            prepareData(gattServices);
        }

    }
    /**
     * Check whether SensorHub related services are present in the discovered
     * services
     *
     * @param gattServices
     * @return {@link Boolean}
     */
    boolean isSensorHubPresent(List<BluetoothGattService> gattServices) {
        boolean present = false;
        for (BluetoothGattService gattService : gattServices) {
            UUID uuid = gattService.getUuid();
            if (uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)) {
                present = true;
            }
        }
        return present;
    }
    private void prepareSensorHubData(List<BluetoothGattService> gattServices) {

        boolean mGattSet = false;
        boolean mSensorHubSet = false;

        if (gattServices == null)
            return;
        // Clear all array list before entering values.
        mGattServiceData.clear();
        mGattServiceMasterData.clear();
        mGattServiceSensorHubData.clear();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, BluetoothGattService> mCurrentServiceData = new HashMap<String, BluetoothGattService>();
            UUID uuid = gattService.getUuid();
            // Optimization code for SensorHub Profile
            if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_ACCELEROMETER_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_ANALOG_TEMPERATURE_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_BATTERY_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_DEVICE_INFORMATION_SERVICE)) {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(mCurrentServiceData);
                if (!mGattServiceSensorHubData.contains(mCurrentServiceData)) {
                    mGattServiceSensorHubData.add(mCurrentServiceData);
                }
                if (!mSensorHubSet
                        && uuid.equals(UUIDDatabase.UUID_BAROMETER_SERVICE)) {
                    mSensorHubSet = true;
                    mGattServiceData.add(mCurrentServiceData);
                }

            }
            // Optimization code for GATTDB
            else if (uuid
                    .equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattdbServiceData.add(mCurrentServiceData);
                if (!mGattSet) {
                    mGattSet = true;
                    mGattServiceData.add(mCurrentServiceData);
                }

            } else {
                mCurrentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(mCurrentServiceData);
                mGattServiceData.add(mCurrentServiceData);
            }
        }
        mApplication.setGattServiceMasterData(mGattServiceMasterData);
        if(mGattdbServiceData.size()>0){
            updateWithNewFragment();
        }else{
            Logger.e("No service found");
            mProgressDialog.dismiss();
            showNoServiceDiscoverAlert();
        }
    }

    private void updateWithNewFragment() {
        mProgressDialog.dismiss();
        FragmentManager fragmentManager = getFragmentManager();
        ProfileControlFragment profileControlFragment = new ProfileControlFragment().
                create(BluetoothLeService.getmBluetoothDeviceName(),
                        BluetoothLeService.getmBluetoothDeviceAddress());
        fragmentManager
                .beginTransaction()
                .replace(R.id.container, profileControlFragment,
                        Constants.PROFILE_CONTROL_FRAGMENT_TAG)
                .commit();

    }
    /**
     * Prepare GATTServices data.
     *
     * @param gattServices
     */
    private void prepareData(List<BluetoothGattService> gattServices) {
        boolean mFindmeSet = false;
        boolean mProximitySet = false;
        boolean mGattSet = false;
        if (gattServices == null)
            return;
        // Clear all array list before entering values.
        mGattServiceData.clear();
        mGattServiceFindMeData.clear();
        mGattServiceMasterData.clear();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, BluetoothGattService> currentServiceData = new HashMap<String, BluetoothGattService>();
            UUID uuid = gattService.getUuid();
            // Optimization code for FindMe Profile
            if (uuid.equals(UUIDDatabase.UUID_IMMEDIATE_ALERT_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                if (!mGattServiceFindMeData.contains(currentServiceData)) {
                    mGattServiceFindMeData.add(currentServiceData);
                }
                if (!mFindmeSet) {
                    mFindmeSet = true;
                    mGattServiceData.add(currentServiceData);
                }

            }
            // Optimization code for Proximity Profile
            else if (uuid.equals(UUIDDatabase.UUID_LINK_LOSS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                if (!mGattServiceProximityData.contains(currentServiceData)) {
                    mGattServiceProximityData.add(currentServiceData);
                }
                if (!mProximitySet) {
                    mProximitySet = true;
                    mGattServiceData.add(currentServiceData);
                }

            }// Optimization code for GATTDB
            else if (uuid.equals(UUIDDatabase.UUID_GENERIC_ACCESS_SERVICE)
                    || uuid.equals(UUIDDatabase.UUID_GENERIC_ATTRIBUTE_SERVICE)) {
                currentServiceData.put(LIST_UUID, gattService);
                mGattdbServiceData.add(currentServiceData);
                if (!mGattSet) {
                    mGattSet = true;
                    mGattServiceData.add(currentServiceData);
                }

            } //Optimization code for HID
            else if (uuid.equals(UUIDDatabase.UUID_HID_SERVICE)){
                /**
                 * Special handling for KITKAT devices
                 */
                if (Build.VERSION.SDK_INT < 21) {
                    Logger.e("Kitkat RDK device found");
                    List<BluetoothGattCharacteristic> allCharacteristics=
                        gattService.getCharacteristics();
                    List<BluetoothGattCharacteristic> RDKCharacteristics=new
                            ArrayList<BluetoothGattCharacteristic>();
                    List<BluetoothGattDescriptor> RDKDescriptors=new
                        ArrayList<BluetoothGattDescriptor>();


                    //Find all Report characteristics
                    for(BluetoothGattCharacteristic characteristic:allCharacteristics){
                        if(characteristic.getUuid().equals(UUIDDatabase.UUID_REP0RT)){
                            RDKCharacteristics.add(characteristic);
                        }
                    }

                    //Find all Report descriptors
                    for(BluetoothGattCharacteristic rdkcharacteristic:RDKCharacteristics){
                        List<BluetoothGattDescriptor> descriptors = rdkcharacteristic.
                                getDescriptors();
                        for(BluetoothGattDescriptor descriptor:descriptors){
                            RDKDescriptors.add(descriptor);
                        }
                    }
                    /**
                     * Wait for all  descriptors to receive
                     */
                    if(RDKDescriptors.size()==RDKCharacteristics.size()*2){

                        for(int pos=0,descPos=0;descPos<RDKCharacteristics.size();pos++,descPos++){
                            BluetoothGattCharacteristic rdkcharacteristic=
                                    RDKCharacteristics.get(descPos);
                            //Mapping the characteristic and descriptors
                            Logger.e("Pos-->"+pos);
                            Logger.e("Pos+1-->"+(pos+1));
                            BluetoothGattDescriptor clientdescriptor=RDKDescriptors.get(pos);
                            BluetoothGattDescriptor reportdescriptor=RDKDescriptors.get(pos+1);
                            if(!rdkcharacteristic.getDescriptors().contains(clientdescriptor))
                            rdkcharacteristic.addDescriptor(clientdescriptor);
                            if(!rdkcharacteristic.getDescriptors().contains(reportdescriptor))
                            rdkcharacteristic.addDescriptor(reportdescriptor);
                            pos++;
                        }
                    }
                    currentServiceData.put(LIST_UUID, gattService);
                    mGattServiceMasterData.add(currentServiceData);
                    mGattServiceData.add(currentServiceData);
                }else{
                    currentServiceData.put(LIST_UUID, gattService);
                    mGattServiceMasterData.add(currentServiceData);
                    mGattServiceData.add(currentServiceData);
                }

            }else {
                currentServiceData.put(LIST_UUID, gattService);
                mGattServiceMasterData.add(currentServiceData);
                mGattServiceData.add(currentServiceData);
            }

        }
        mApplication.setGattServiceMasterData(mGattServiceMasterData);
        if(mGattdbServiceData.size()>0){
            updateWithNewFragment();
        }else{
            mProgressDialog.dismiss();
            showNoServiceDiscoverAlert();
        }


    }

    private void showNoServiceDiscoverAlert() {
        if(mNoserviceDiscovered!=null)
            mNoserviceDiscovered.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        isInServiceFragment=false;
        getActivity().unregisterReceiver(mServiceDiscoveryListner);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("Service discovery onResume");
        isInServiceFragment=true;
        getActivity().registerReceiver(mServiceDiscoveryListner,
                Utils.makeGattUpdateIntentFilter());
        // Initialize ActionBar as per requirement
        Utils.setUpActionBar(getActivity(),
                getResources().getString(R.string.profile_control_fragment));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem search=menu.findItem(R.id.search);
        MenuItem pairCache = menu.findItem(R.id.pairing);
        if (Utils.getBooleanSharedPreference(getActivity(), Constants.PREF_PAIR_CACHE_STATUS)) {
            pairCache.setChecked(true);
        } else {
            pairCache.setChecked(false);
        }
        graph.setVisible(false);
        search.setVisible(false);
    }
}
