package cf.nearby.nearby.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.R;
import cf.nearby.nearby.bluetooth.ConnectBLEActivity;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.obj.MainRecord;
import cf.nearby.nearby.obj.VitalSign;

public class RecordVitalSignActivity extends BaseActivity {

    private int MEASUREMENT_TIME = 60000;

    private final String BLOOD_PRESSURE = "blood_pressure";
    private final String PULSE = "pulse";
    private final String TEMPERATURE = "temperature";

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private Button saveBtn;
    private RelativeLayout rl_bluetooth;
    private CardView cv_bluetooth;
    private TextView tv_bleMsg;
    private AVLoadingIndicatorView loadingPulse;

    private RelativeLayout rl_menu;
    private CardView cv_temperature;
    private CardView cv_pulse;
    private CardView cv_bp;

    private RelativeLayout rl_measurement;
    private TextView tv_time;
    private LinearLayout li_result;
    private ScrollView sc_result;

    private LinearLayout li_pulse;
    private RelativeLayout rl_graphPulse;
//    private LinearLayout li_resultPulse;
    private TextView tv_bpm;
    private LineChartView mChartPulse;

    private LinearLayout li_resultMsg;
    private TextView tv_resultMsg;
    private CardView cv_delMeasurement;
    private CardView cv_reMeasurement;
    private CardView cv_measurementSave;

    // Bluetooth
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private String[] btList;

    private boolean isConnectBluetooth = false;

    private boolean flagPulse = false;
    private boolean flagTemp = false;

    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data

    private VitalSign vitalSign;
    private ArrayList<Double> pulseList;
    private ArrayList<Double> pulseSignalList;
    private ArrayList<Double> tempuratureList;
    private int time;
    private boolean recordEnable;
    private String currentMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_vital_sign);

        vitalSign = (VitalSign)getIntent().getSerializableExtra("vital_sign");
        pulseList = new ArrayList<>();
        pulseSignalList = new ArrayList<>();
        tempuratureList = new ArrayList<>();
        setInitMeasurementValue();

        init();

        connectNearbyBluetoothDevice();

    }

    private void setInitMeasurementValue(){
        if(vitalSign.getPulse() != null){
            pulseList.add(vitalSign.getPulse());
        }
        if(vitalSign.getTemperature() != null){
            tempuratureList.add(vitalSign.getTemperature());
        }
    }

    private void init(){

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveBtn = (Button)findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            save();
            }
        });
//        saveBtn.setVisibility(View.GONE);

        rl_menu = (RelativeLayout)findViewById(R.id.rl_menu);
        cv_temperature = (CardView)findViewById(R.id.cv_temperature);
        cv_temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurementTemperature();
            }
        });
        cv_pulse = (CardView)findViewById(R.id.cv_pulse);
        cv_pulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurementPulse();
            }
        });
        cv_bp = (CardView)findViewById(R.id.cv_bp);

        rl_measurement = (RelativeLayout)findViewById(R.id.rl_measurement);
        tv_time = (TextView)findViewById(R.id.tv_time);
        li_result = (LinearLayout)findViewById(R.id.li_result);
        rl_bluetooth = (RelativeLayout)findViewById(R.id.rl_bluetooth);
        rl_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(RecordVitalSignActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()){
                            Intent intent = new Intent(RecordVitalSignActivity.this, ConnectBLEActivity.class);
                            startActivity(intent);
                        }

                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
            }
        });
        cv_bluetooth = (CardView)findViewById(R.id.cv_bluetooth);
        cv_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = view;
                Dexter.withActivity(RecordVitalSignActivity.this)
                        .withPermissions(
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){

                            showBluetoothConnectionMenu(v);

                        }else{
                            showNeedPermissionDialog();
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        showNeedPermissionDialog();

                    }
                }).check();
            }
        });
        tv_bleMsg = (TextView)findViewById(R.id.tv_ble_msg);

        // Init Bluetooth
        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio



        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    mReadBuffer.setText(readMessage);
//                    System.out.println(readMessage);
                    if(flagPulse){
                        createNewDataFromBluetooth(pulseList, readMessage);
                    }
                    if(flagTemp){
                        createNewDataFromBluetooth(tempuratureList, readMessage);
                    }
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1){
                        setEnableRecord();
                    }else{
                        tv_bleMsg.setVisibility(View.VISIBLE);
                        tv_bleMsg.setText(R.string.bluetooth_connection_fail);
                        loadingPulse.hide();
                        isConnectBluetooth = false;
//                        new MaterialDialog.Builder(RecordVitalSignActivity.this)
//                                .title(R.string.fail_srt)
//                                .positiveText(R.string.ok)
//                                .show();
                    }
//                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
//                    else
//                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };

        //======================

        sc_result = (ScrollView)findViewById(R.id.sc_result);
        li_resultMsg = (LinearLayout)findViewById(R.id.li_result_msg);
        tv_resultMsg = (TextView)findViewById(R.id.tv_result_msg);

        li_pulse = (LinearLayout)findViewById(R.id.li_pulse);
        rl_graphPulse = (RelativeLayout)findViewById(R.id.rl_graph_pulse);
        loadingPulse = (AVLoadingIndicatorView)findViewById(R.id.loading_pulse);
        loadingPulse.hide();
//        li_resultPulse = (LinearLayout)findViewById(R.id.li_result_pulse);
        tv_bpm = (TextView)findViewById(R.id.tv_bpm);
        mChartPulse = (LineChartView)findViewById(R.id.chart_pulse);

        cv_delMeasurement = (CardView)findViewById(R.id.cv_del_measurement);
        cv_delMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentMeasurement){
                    case TEMPERATURE:
                        tempuratureList.clear();
                        setScreen(false);
                        break;
                    case PULSE:
                        pulseList.clear();
                        pulseSignalList.clear();
                        setScreen(false);
                        break;
                    case BLOOD_PRESSURE:
                        break;
                }
            }
        });
        cv_reMeasurement = (CardView)findViewById(R.id.cv_re_measurement);
        cv_reMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMeasurement = (currentMeasurement == null)? "" : currentMeasurement;
                switch (currentMeasurement){
                    case TEMPERATURE:
                        if(isConnectBluetooth) {
                            tempuratureList.clear();
                            measurementTemperature();
                        }else{
                            new MaterialDialog.Builder(RecordVitalSignActivity.this)
                                    .title(R.string.warning_srt)
                                    .content(R.string.no_bluetooth_connection)
                                    .positiveText(R.string.ok)
                                    .show();
                        }
                        break;
                    case PULSE:
                        if(isConnectBluetooth) {
                            pulseList.clear();
                            pulseSignalList.clear();
                            measurementPulse();
                        }else{
                            new MaterialDialog.Builder(RecordVitalSignActivity.this)
                                    .title(R.string.warning_srt)
                                    .content(R.string.no_bluetooth_connection)
                                    .positiveText(R.string.ok)
                                    .show();
                        }
                        break;
                    case BLOOD_PRESSURE:
                        break;
                }
            }
        });
        cv_measurementSave = (CardView)findViewById(R.id.cv_measurement_save);
        cv_measurementSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMeasurement = null;
                setScreen(false);
            }
        });

        setScreen(false);

    }

    private void setEnableRecord(){

        cv_bluetooth.setCardBackgroundColor(getColorId(R.color.pastel_green));
        loadingPulse.hide();
        tv_bleMsg.setVisibility(View.VISIBLE);
        tv_bleMsg.setText(R.string.bluetooth_connection_success);
        tv_bleMsg.setTextColor(getColorId(R.color.white));
        isConnectBluetooth = true;

    }

    private void initMeasurementData(){

        time = 0;
        recordEnable = true;

    }

    private void initFlag(){
        flagPulse = false;
        flagTemp = false;
    }

    private void connectNearbyBluetoothDevice(){

        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            btList = new String[mPairedDevices.size()];
            int i=0;
            for (BluetoothDevice device : mPairedDevices) {
                btList[i] = device.getName() + "\n" + device.getAddress();
                //mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                if(device.getName().startsWith("nearby")){

                    connectBluetoothDevice(btList[i]);
                    break;
                }
                i++;
            }


            //new MaterialDialog.Builder(this).title(R.string.connect_bluetooth).adapter(mBTArrayAdapter, null).show();
//            showSnackbar("Show Paired Devices");
//            showBluetoothDeviceList();
            //Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else{
//            showSnackbar("Bluetooth not on");
        }

    }

    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            btList = new String[mPairedDevices.size()];
            int i=0;
            for (BluetoothDevice device : mPairedDevices) {
                btList[i] = device.getName() + "\n" + device.getAddress();
                //mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                i++;
            }


            //new MaterialDialog.Builder(this).title(R.string.connect_bluetooth).adapter(mBTArrayAdapter, null).show();
            showSnackbar("Show Paired Devices");
            showBluetoothDeviceList();
            //Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else{
            showSnackbar("Bluetooth not on");
        }
            //Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private void showBluetoothDeviceList(){

        new MaterialDialog.Builder(this)
                .items(btList)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        connectBluetoothDevice(btList[position]);
                    }
                })
                .negativeText(R.string.cancel)
                .show();

    }

    private void connectBluetoothDevice(String info){

//        String info = ((TextView) v).getText().toString();
        final String address = info.substring(info.length() - 17);
        final String name = info.substring(0,info.length() - 17);

        tv_bleMsg.setVisibility(View.GONE);
        loadingPulse.show();

        // Spawn a new thread to avoid blocking the GUI one
        new Thread()
        {
            public void run() {
                boolean fail = false;

                BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                try {
                    mBTSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();
                        mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if(fail == false) {
                    mConnectedThread = new ConnectedThread(mBTSocket);
                    mConnectedThread.start();

                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();
                }
            }
        }.start();

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e("RecordVitalSignActivity", "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }


    private void showBluetoothConnectionMenu(final View v){

        String[] items = {
                "페어링된 기기 보기",
                "새로운 기기 연결"
        };

        new MaterialDialog.Builder(this)
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position){
                            case 0:
                                listPairedDevices(v);
                                break;
                            case 1:
                                break;
                        }
                    }
                })
                .show();


    }

    private void showNeedPermissionDialog(){

        new MaterialDialog.Builder(this)
                .title(R.string.check_srt)
                .content(R.string.required_permission)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myAppSettings);

                    }
                })
                .show();

    }

    private void setScreen(boolean isMeasurement){

        if(isMeasurement){
            rl_menu.setVisibility(View.GONE);
            rl_measurement.setVisibility(View.VISIBLE);

            initMeasurementData();
            tv_time.setText("");
            li_result.removeAllViews();
//            li_resultPulse.removeAllViews();
            mChartPulse.reset();
            tv_bpm.setText("");

            if(PULSE.equals(currentMeasurement)){
                sc_result.setVisibility(View.GONE);
                li_pulse.setVisibility(View.VISIBLE);
            }else {
                sc_result.setVisibility(View.VISIBLE);
                li_pulse.setVisibility(View.GONE);
            }
            li_resultMsg.setVisibility(View.GONE);

//            saveBtn.setVisibility(View.GONE);
        }else{
            rl_menu.setVisibility(View.VISIBLE);
            rl_measurement.setVisibility(View.GONE);

            boolean isPulse = pulseList.size() > 0;
            changeBtnColor(cv_pulse, isPulse);

            boolean isTemper = tempuratureList.size() > 0;
            changeBtnColor(cv_temperature, isTemper);

//            boolean status = isPulse || isTemper;
//            if(status){
//                saveBtn.setVisibility(View.VISIBLE);
//            }else{
//                saveBtn.setVisibility(View.GONE);
//            }
        }

    }

    private void showMeasurementResult(String value){
        tv_time.setText("");
        tv_resultMsg.setText(value);
        sc_result.setVisibility(View.GONE);
        li_resultMsg.setVisibility(View.VISIBLE);
        li_pulse.setVisibility(View.GONE);
    }

    private void measurementPulse(){

        if(isConnectBluetooth) {

            MEASUREMENT_TIME = 60000;
            currentMeasurement = PULSE;
            setScreen(true);
            initFlag();

            if (pulseList.isEmpty()) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(MEASUREMENT_TIME);
                            recordEnable = false;
                            Thread.sleep(1100);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flagPulse = false;
                                    double avg = Math.round(getAverage(pulseList));
                                    showMeasurementResult(avg + "");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                new Thread() {
                    @Override
                    public void run() {
                        while (recordEnable) {
                            try {
                                Thread.sleep(1000);
                                time += 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        flagPulse = true;
                                        //createNewData(pulseList, "pulse");
                                        tv_time.setText("경과시간 : " + time + "s");
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            } else {
                showMeasurementResult(getAverage(pulseList) + "");
            }

        }else{

            if(!pulseList.isEmpty()){
                currentMeasurement = PULSE;
                setScreen(true);
                showMeasurementResult(getAverage(pulseList) + "");
            }else {

                new MaterialDialog.Builder(this)
                        .title(R.string.warning_srt)
                        .content(R.string.no_bluetooth_connection)
                        .positiveText(R.string.ok)
                        .show();

            }

        }

    }

    private void measurementTemperature(){

        if(isConnectBluetooth) {

            MEASUREMENT_TIME = 5000;
            currentMeasurement = TEMPERATURE;
            setScreen(true);
            initFlag();

            if (tempuratureList.isEmpty()) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(MEASUREMENT_TIME);
                            recordEnable = false;
                            Thread.sleep(1100);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flagTemp = false;
                                    double avg = getAverage(tempuratureList);
                                    showMeasurementResult(avg + "");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                new Thread() {
                    @Override
                    public void run() {
                        while (recordEnable) {
                            try {
                                Thread.sleep(1000);
                                time += 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        flagTemp = true;
//                                    createNewData(tempuratureList, "temperature");
                                        tv_time.setText("경과시간 : " + time + "s");
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            } else {
                showMeasurementResult(getAverage(tempuratureList) + "");
            }

        }else{

            if(!tempuratureList.isEmpty()){
                currentMeasurement = TEMPERATURE;
                setScreen(true);
                showMeasurementResult(getAverage(tempuratureList) + "");
            }else {

                new MaterialDialog.Builder(this)
                        .title(R.string.warning_srt)
                        .content(R.string.no_bluetooth_connection)
                        .positiveText(R.string.ok)
                        .show();
            }

        }

    }

    private double getAverage(ArrayList<Double> list){
        double avg = 0.0;
        for(double d : list){
            avg += d;
        }
        avg /= list.size();
        return Math.round(avg * 100d) / 100d;
    }

    private void createNewData(ArrayList<Double> list, String type){

        Random rand = new Random();
        Double temp = 0.0;
        if("pulse".equals(type)){
            temp = 80.0 + (int)Math.round(rand.nextDouble());
        }else if("temperature".equals(type)){
            temp = 36.0 + rand.nextDouble();
        }
//        int temp = 80 + (int)Math.round(rand.nextDouble());
        list.add(temp);

        TextView msg = new TextView(this);
        msg.setText(temp + "");
        msg.setTextColor(getColorId(R.color.dark_gray));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 0);
        params.gravity = Gravity.CENTER;
        msg.setLayoutParams(params);
        msg.setGravity(Gravity.CENTER);
        li_result.addView(msg, 0);

        tv_time.setText("경과시간 : " + time + "s");

    }

    private void createNewDataFromBluetooth(ArrayList<Double> list, String readMsg){

        for(String s : readMsg.split("\r\n")){
            if(flagPulse){
                if(s.startsWith("B")){
                    //System.out.println(s);

                    try {
                        list.add(Double.parseDouble(s.substring(1))/2);

                        tv_bpm.setText(Math.round(getAverage(list)) + "");

//                        TextView msg = new TextView(this);
//                        msg.setText(s.substring(1));
//                        msg.setTextColor(getColorId(R.color.dark_gray));
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(0, 20, 0, 0);
//                        params.gravity = Gravity.CENTER;
//                        msg.setLayoutParams(params);
//                        msg.setGravity(Gravity.CENTER);
//                        li_resultPulse.addView(msg, 0);

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }else if(s.startsWith("S")){
                    pulseSignalList.add(Double.parseDouble(s.substring(1)));
                    makePulseChart();
                }
            }
            if(flagTemp){
                if(s.startsWith("T")){

                    try{
                        Double temp = Double.parseDouble(s.substring(1));
                        list.add(temp);

                        TextView msg = new TextView(this);
                        msg.setText(temp + "");
                        msg.setTextColor(getColorId(R.color.dark_gray));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 20, 0, 0);
                        params.gravity = Gravity.CENTER;
                        msg.setLayoutParams(params);
                        msg.setGravity(Gravity.CENTER);
                        li_result.addView(msg, 0);

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                }
            }
        }

    }

    private void makePulseChart(){

        if(pulseSignalList.size() > 0) {

            //handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_GRAPH));

            mChartPulse.reset();

            float min = 1000000.0f;
            float max = -1000000.0f;

            LineSet dataset = new LineSet();
            for (int i = Math.max(0, pulseSignalList.size() - 50); i < pulseSignalList.size(); i++) {
                Double pul = pulseSignalList.get(i);
                dataset.addPoint("", Float.parseFloat(pul + ""));

                if (pul < min)
                    min = Float.parseFloat(pul + "");
                if (max < pul)
                    max = Float.parseFloat(pul + "");

            }
            dataset.setColor(Color.parseColor("#53c1bd"));
//                    .setFill(Color.parseColor("#3d6c73"))
//                    .setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")},
//                            null);
            mChartPulse.addData(dataset);

            dataset = new LineSet();
            for (int i = Math.max(0, pulseSignalList.size() - 50); i < pulseSignalList.size(); i++) {
                Double pul = pulseSignalList.get(i);
                dataset.addPoint("", Float.parseFloat(pul + ""));
            }
            dataset.setColor(Color.parseColor("#b3b5bb"))
                    //.setFill(Color.parseColor("#2d374c"))
                    //.setDotsColor(Color.parseColor("#ffc755"))
                    .setThickness(2);
            mChartPulse.addData(dataset);

            mChartPulse.setAxisBorderValues(min - 5, max + 5);

            mChartPulse.show();

        }else{
            //handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_HIDE_GRAPH));
        }

    }

    private void save(){

        if(pulseList.size() > 0){
            vitalSign.setPulse(getAverage(pulseList));
        }else{
            vitalSign.setPulse(null);
        }
        if(tempuratureList.size() > 0){
            vitalSign.setTemperature(getAverage(tempuratureList));
        }else{
            vitalSign.setTemperature(null);
        }
        Intent intent = new Intent();
        intent.putExtra("vital_sign", vitalSign);
        setResult(NurseRecordActivity.UPDATE_VITAL, intent);
        finish();

    }

    private void changeBtnColor(CardView cv, boolean check){

        if(check){
            cv.setCardBackgroundColor(getColorId(R.color.pastel_green));
            for(int i=0; i<cv.getChildCount(); i++){
                ViewGroup vg = (ViewGroup)cv.getChildAt(i);
                for(int j=0; j<vg.getChildCount(); j++){
                    View v = vg.getChildAt(j);
                    if(v instanceof TextView){
                        ((TextView) v).setTextColor(getColorId(R.color.white));
                    }
                }
            }
        }else{
            cv.setCardBackgroundColor(getColorId(R.color.white));
            for(int i=0; i<cv.getChildCount(); i++){
                ViewGroup vg = (ViewGroup)cv.getChildAt(i);
                for(int j=0; j<vg.getChildCount(); j++){
                    View v = vg.getChildAt(j);
                    if(v instanceof TextView){
                        ((TextView) v).setTextColor(getColorId(R.color.dark_gray));
                    }
                }
            }
        }

    }

    private class ConnectedThread extends Thread {
        private  BluetoothSocket mmSocket;
        private  InputStream mmInStream;
        private  OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                if (mmInStream != null) {
                    try {mmInStream.close();} catch (Exception e) {}
                    mmInStream = null;
                }

                if (mmOutStream != null) {
                    try {mmOutStream.close();} catch (Exception e) {}
                    mmOutStream = null;
                }

                if (mBTSocket != null) {
                    try {mBTSocket.close();} catch (Exception e) {}
                    mBTSocket = null;
                }
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            if(mConnectedThread != null)
                mConnectedThread.cancel();

            if(mBTSocket != null)
                mBTSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
