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

package cf.nearby.nearby.bluetooth.BLEServiceFragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
import cf.nearby.nearby.bluetooth.CommonUtils.DecimalTextWatcher;
import cf.nearby.nearby.bluetooth.CommonUtils.GattAttributes;
import cf.nearby.nearby.bluetooth.CommonUtils.Logger;
import cf.nearby.nearby.bluetooth.CommonUtils.Utils;
import cf.nearby.nearby.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display the running speed and cadence
 */
public class RSCService extends Fragment {

    // GATT Services and characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;

    // Data view variables
    private TextView mDistanceRan;
    private TextView mAverageSpeed;
    private TextView mCaloriesBurnt;
    private Chronometer mTimer;
    private EditText mWeightEdittext;

    private String mAvgSpeed;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    /**
     * aChart variables
     */
    private LinearLayout mGraphLayoutParent;
    private double mGraphLastXValue = 0;
    private double mPreviosTime = 0;
    private double mCurrentTime = 0;
    private GraphicalView mChart;
    private XYSeries mDataSeries;

    private boolean mHandlerFlag = false;

    private double mWeightInt;
    private String mWeightString;

    //Constants
    private static final int MAX_WEIGHT = 200;
    private static final int ZERO= 0;
    private static final float WEIGHT_ONE = 1;

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Check CSC service
                if (extras.containsKey(Constants.EXTRA_RSC_VALUE)) {
                    ArrayList<String> received_rsc_data = intent
                            .getStringArrayListExtra(Constants.EXTRA_RSC_VALUE);
                    displayLiveData(received_rsc_data);
                }
            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDING) {
                    // Bonding...
                    Logger.i("Bonding is in process....");
                    Utils.bondingProgressDialog(getActivity(), mProgressDialog, true);
                }  else if (state == BluetoothDevice.BOND_BONDED) {
                    String dataLog=getResources().getString(R.string.dl_commaseparator)
                            +"["+ BluetoothLeService.getmBluetoothDeviceName()+"|"
                            + BluetoothLeService.getmBluetoothDeviceAddress()+"]"+
                            getResources().getString(R.string.dl_commaseparator)+
                            getResources().getString(R.string.dl_connection_paired);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(getActivity(), mProgressDialog, false);
                    getGattData();

                } else if (state == BluetoothDevice.BOND_NONE) {
                    String dataLog=getResources().getString(R.string.dl_commaseparator)
                            +"["+ BluetoothLeService.getmBluetoothDeviceName()+"|"
                            + BluetoothLeService.getmBluetoothDeviceAddress()+"]"+
                            getResources().getString(R.string.dl_commaseparator)+
                            getResources().getString(R.string.dl_connection_unpaired);
                    Logger.datalog(dataLog);
                    Utils.bondingProgressDialog(getActivity(), mProgressDialog, false);
                }
            }

        }
    };

    public RSCService create(BluetoothGattService service) {
        mService = service;
        return new RSCService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.runningspeed_n_cadence,
                container, false);
        mDistanceRan = (TextView) rootView.findViewById(R.id.running_distance);
        mAverageSpeed = (TextView) rootView.findViewById(R.id.running_speed);
        mCaloriesBurnt = (TextView) rootView.findViewById(R.id.calories_burnt);
        mTimer = (Chronometer) rootView.findViewById(R.id.time_counter);
        mWeightEdittext = (EditText) rootView.findViewById(R.id.weight_data);
        mWeightEdittext.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mWeightEdittext.addTextChangedListener(new DecimalTextWatcher(mWeightEdittext));
        mProgressDialog = new ProgressDialog(getActivity());

        // Setting up chart
        setupChart(rootView);

        // Start/Stop listener
        Button start_stop_btn = (Button) rootView
                .findViewById(R.id.start_stop_btn);
        start_stop_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button mBtn = (Button) v;
                String buttonText = mBtn.getText().toString();
                String startText = getResources().getString(
                        R.string.blood_pressure_start_btn);
                String stopText = getResources().getString(
                        R.string.blood_pressure_stop_btn);
                mWeightString = mWeightEdittext.getText().toString();
                try {
                    mWeightInt = Double.parseDouble(mWeightString);
                } catch (NumberFormatException e) {
                    mWeightInt = ZERO;
                }

                //Weight Entered validation
                if ((mWeightString.equalsIgnoreCase("") || mWeightString.equalsIgnoreCase(".")) && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_empty), Toast.LENGTH_SHORT).show();
                    mCaloriesBurnt.setText("0.00");
                }

                if ((mWeightString.equalsIgnoreCase("0.")
                        || mWeightString.equalsIgnoreCase("0")) && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_zero), Toast.LENGTH_SHORT).show();
                }

                if (mWeightInt < WEIGHT_ONE && mWeightInt > ZERO && buttonText.equalsIgnoreCase(startText)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_zero), Toast.LENGTH_SHORT).show();
                    mCaloriesBurnt.setText("0.00");
                }

                if (mWeightInt <= MAX_WEIGHT && buttonText.equalsIgnoreCase(startText)) {
                    if (buttonText.equalsIgnoreCase(startText)) {
                        mBtn.setText(stopText);
                        mCaloriesBurnt.setText("0.00");
                        mWeightEdittext.setEnabled(false);
                        getGattData();
                        mTimer.start();
                        mTimer.setBase(SystemClock.elapsedRealtime());
                        mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometer) {
                                showCaloriesBurnt();
                            }
                        });
                    } else {
                        mWeightEdittext.setEnabled(true);
                        mBtn.setText(startText);
                        stopBroadcastDataNotify(mNotifyCharacteristic);
                        mTimer.stop();
                    }
                } else {
                    if (buttonText.equalsIgnoreCase(startText)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.csc_weight_toast_greater), Toast.LENGTH_SHORT).show();
                        mBtn.setText(stopText);
                        mCaloriesBurnt.setText("0.00");
                        mWeightEdittext.setEnabled(false);
                        getGattData();
                        mTimer.start();
                        mTimer.setBase(SystemClock.elapsedRealtime());
                        mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometer) {
                               showCaloriesBurnt();
                            }
                        });
                    } else {
                        mWeightEdittext.setEnabled(true);
                        mBtn.setText(startText);
                        stopBroadcastDataNotify(mNotifyCharacteristic);
                        mCaloriesBurnt.setText("0.00");
                        mTimer.stop();
                    }
                }

            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display live running data
     */
    private void displayLiveData(final ArrayList<String> rsc_data) {
        if (rsc_data != null) {
            try {
                /**
                 * Number formatting to two fractional decimals
                 */
                Number cycledDist = NumberFormat.getInstance().parse(
                        rsc_data.get(1));
                Number averageSpeed = NumberFormat.getInstance().parse(
                        rsc_data.get(0));
                NumberFormat distformatter = NumberFormat.getNumberInstance();
                distformatter.setMinimumFractionDigits(2);
                distformatter.setMaximumFractionDigits(2);
                String distRan = distformatter.format(cycledDist);
                mAvgSpeed = distformatter.format(averageSpeed);
                mDistanceRan.setText(distRan);
                mAverageSpeed.setText(mAvgSpeed);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (mCurrentTime == 0) {
                mGraphLastXValue = 0;
                mCurrentTime = Utils.getTimeInSeconds();
            } else {
                mPreviosTime = mCurrentTime;
                mCurrentTime = Utils.getTimeInSeconds();
                mGraphLastXValue = mGraphLastXValue + (mCurrentTime - mPreviosTime) / 1000;
            }
            try {
                float val = Float.valueOf(mAvgSpeed);
                mDataSeries.add(mGraphLastXValue, val);
                mChart.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private float showElapsedTime() {
        float SECOND = 1000;
        float MINUTE = 60 * SECOND;
        float elapsedMillis = SystemClock.elapsedRealtime() - mTimer.getBase();
        elapsedMillis = elapsedMillis / MINUTE;
        return elapsedMillis;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandlerFlag = true;
        getActivity().registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar(getActivity(),
                getResources().getString(R.string.rsc_fragment));
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandlerFlag = false;
        if (mAverageSpeed != null && mDistanceRan != null) {
            mAverageSpeed.setText("");
            mDistanceRan.setText("");
        }

    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mGattUpdateReceiver);
        if (mNotifyCharacteristic != null) {
            stopBroadcastDataNotify(mNotifyCharacteristic);
        }
        super.onDestroy();
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
                Logger.d("Stopped notification");
                BluetoothLeService.setCharacteristicNotification(
                        gattCharacteristic, false);
                mNotifyCharacteristic = null;
            }
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
     * Method to get required characteristics from service
     */
    void getGattData() {
        List<BluetoothGattCharacteristic> gattCharacteristics = mService
                .getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            String uuidCharacteristics = gattCharacteristic.getUuid().toString();
            if (uuidCharacteristics.equalsIgnoreCase(GattAttributes.RSC_MEASUREMENT)) {
                mNotifyCharacteristic = gattCharacteristic;
                prepareBroadcastDataNotify(gattCharacteristic);
            }
        }
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
        graph.setVisible(true);
        log.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.graph:
                if (mGraphLayoutParent.getVisibility() != View.VISIBLE) {
                    mGraphLayoutParent.setVisibility(View.VISIBLE);

                } else {
                    mGraphLayoutParent.setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Setting up the aChart Third party library
     *
     * @param parent
     */
    private void setupChart(View parent) {
        /**
         * Setting graph titles
         */
        String graphTitle = getResources().getString(R.string.rsc_fragment);
        String graphXAxis = getResources().getString(R.string.health_temperature_time);
        String graphYAxis = getResources().getString(R.string.rsc_avg_speed);


        // Creating an  XYSeries for running speed
        mDataSeries = new XYSeries(graphTitle);

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

        // Adding temperature Series to the dataset
        mDataset.addSeries(mDataSeries);

        // Creating XYSeriesRenderer to customize
        XYSeriesRenderer mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(getResources().getColor(R.color.colorPrimary));
        mRenderer.setPointStyle(PointStyle.CIRCLE);
        mRenderer.setFillPoints(true);
        mRenderer.setLineWidth(5);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer mMultiRenderer = new XYMultipleSeriesRenderer();
        int deviceDPi = getResources().getDisplayMetrics().densityDpi;
        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_XHIGH:
                mMultiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_40, Constants.GRAPH_MARGIN_90,
                        Constants.GRAPH_MARGIN_25, Constants.GRAPH_MARGIN_10});
                mMultiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_XHDPI);
                mMultiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_XHDPI);
                mMultiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_XHDPI);
                mMultiRenderer.setLegendTextSize(Constants.TEXT_SIZE_XHDPI);
                break;
            case DisplayMetrics.DENSITY_HIGH:
                mMultiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_30, Constants.GRAPH_MARGIN_50,
                        Constants.GRAPH_MARGIN_25, Constants.GRAPH_MARGIN_10});
                mMultiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_HDPI);
                mMultiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_HDPI);
                mMultiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_HDPI);
                mMultiRenderer.setLegendTextSize(Constants.TEXT_SIZE_HDPI);
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                mMultiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_50, Constants.GRAPH_MARGIN_100,
                        Constants.GRAPH_MARGIN_35, Constants.GRAPH_MARGIN_20});
                mMultiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_XXHDPI);
                mMultiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_XXHDPI);
                mMultiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_XXHDPI);
                mMultiRenderer.setLegendTextSize(Constants.TEXT_SIZE_XXHDPI);
                break;
            default:
                if (deviceDPi > DisplayMetrics.DENSITY_XXHIGH && deviceDPi <=
                        DisplayMetrics.DENSITY_XXXHIGH) {
                    mMultiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_70, Constants.GRAPH_MARGIN_130,
                            Constants.GRAPH_MARGIN_35, Constants.GRAPH_MARGIN_20});
                    mMultiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_XXXHDPI);
                    mMultiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_XXXHDPI);
                    mMultiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_XXXHDPI);
                    mMultiRenderer.setLegendTextSize(Constants.TEXT_SIZE_XXXHDPI);
                } else {
                    mMultiRenderer.setMargins(new int[]{Constants.GRAPH_MARGIN_30, Constants.GRAPH_MARGIN_50,
                            Constants.GRAPH_MARGIN_25, Constants.GRAPH_MARGIN_10});
                    mMultiRenderer.setAxisTitleTextSize(Constants.TEXT_SIZE_LDPI);
                    mMultiRenderer.setChartTitleTextSize(Constants.TEXT_SIZE_LDPI);
                    mMultiRenderer.setLabelsTextSize(Constants.TEXT_SIZE_LDPI);
                    mMultiRenderer.setLegendTextSize(Constants.TEXT_SIZE_LDPI);
                }
                break;
        }
        mMultiRenderer.setXTitle(graphXAxis);
        mMultiRenderer.setLabelsColor(Color.BLACK);
        mMultiRenderer.setYTitle(graphYAxis);
        mMultiRenderer.setYAxisMin(0);
        mMultiRenderer.setXAxisMin(0);
        mMultiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        mMultiRenderer.setPanEnabled(true, true);
        mMultiRenderer.setZoomEnabled(false,false);
        mMultiRenderer.setGridColor(Color.LTGRAY);
        mMultiRenderer.setLabelsColor(Color.BLACK);
        mMultiRenderer.setYLabelsColor(0, Color.DKGRAY);
        mMultiRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mMultiRenderer.setXLabelsColor(Color.DKGRAY);
        mMultiRenderer.setYLabelsColor(0, Color.BLACK);
        mMultiRenderer.setXLabelsColor(Color.BLACK);
        mMultiRenderer.setApplyBackgroundColor(true);
        mMultiRenderer.setBackgroundColor(Color.WHITE);
        mMultiRenderer.setGridColor(Color.BLACK);
        mMultiRenderer.setShowGrid(true);
        mMultiRenderer.setShowLegend(false);

        // Adding mRenderer to multipleRenderer
        mMultiRenderer.addSeriesRenderer(mRenderer);

        // Getting a reference to LinearLayout of the MainActivity Layout
        mGraphLayoutParent = (LinearLayout) parent.findViewById(R.id.chart_container);


        mChart = ChartFactory.getLineChartView(getActivity(),
                mDataset, mMultiRenderer);


        // Adding the Line Chart to the LinearLayout
        mGraphLayoutParent.addView(mChart);

    }

    private void showCaloriesBurnt(){
        try {
            Number numWeight = NumberFormat.getInstance().parse(mWeightString);
            mWeightInt = numWeight.doubleValue();
            double caloriesBurntInt = (((showElapsedTime()) * mWeightInt) * 8);
            caloriesBurntInt = caloriesBurntInt / 1000;
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(4);
            formatter.setMaximumFractionDigits(4);
            String finalBurn = formatter.format(caloriesBurntInt);
            mCaloriesBurnt.setText(finalBurn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
