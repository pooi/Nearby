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

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cf.nearby.nearby.bluetooth.BLEConnectionServices.BluetoothLeService;
import cf.nearby.nearby.bluetooth.CommonUtils.Constants;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display the heart rate service
 */
public class HealthTemperatureService extends Fragment {

    // GATT service and characteristics
    private static BluetoothGattService mService;
    private static BluetoothGattCharacteristic mNotifyCharacteristic;
    private static BluetoothGattCharacteristic mReadCharacteristic;

    // Data view variables
    private TextView mDataFieldHTM;
    private TextView mDataFieldHSL;
    private TextView mDataFieldThermoUnit;
    private boolean mHandlerFlag = true;

    /**
     * aChart variables
     */
    private LinearLayout mGraphLayoutParent;
    private double mGraphLastXValue = 0;
    private double mPreviosTime = 0;
    private double mCurrentTime = 0;
    private GraphicalView mChart;
    private XYSeries mTempEratureDataSeries;

    // Creating a XYMultipleSeriesRenderer to customize the whole chart
    XYMultipleSeriesRenderer mMultiRenderer;

    //ProgressDialog
    private ProgressDialog mProgressDialog;

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                // Check Health temperature
                if (extras.containsKey(Constants.EXTRA_HTM_VALUE)) {
                    ArrayList<String> received_htm_data = intent
                            .getStringArrayListExtra(Constants.EXTRA_HTM_VALUE);
                    displayLiveData(received_htm_data);

                }
                // Check health sensor location
                else {
                    String received_hsl_data = intent
                            .getStringExtra(Constants.EXTRA_HSL_VALUE);
                    prepareBroadcastDataIndicate(mNotifyCharacteristic);
                    displayBSLData(received_hsl_data);
                }

            }
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
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
                    getGattData();

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

    public HealthTemperatureService create(BluetoothGattService service) {
        HealthTemperatureService fragment = new HealthTemperatureService();
        mService = service;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.health_temp_measurement, container, false);

        mDataFieldHTM = (TextView) rootView.findViewById(R.id.temp_measure);
        mDataFieldHSL = (TextView) rootView.findViewById(R.id.hsl_sensor_data);
        mDataFieldThermoUnit = (TextView) rootView.findViewById(R.id.temp_unit);
        mProgressDialog = new ProgressDialog(getActivity());
        mDataFieldHSL.setSelected(true);
        setHasOptionsMenu(true);
        getGattData();
        // Setting up chart
        mMultiRenderer = new XYMultipleSeriesRenderer();
        setupChart(rootView);
        return rootView;
    }

    private void displayLiveData(final ArrayList<String> htm_data) {
        if (htm_data != null) {
            try {
                mDataFieldHTM.setText(htm_data.get(0));
                mDataFieldThermoUnit.setText(htm_data.get(1));
            } catch (Exception e) {
                e.printStackTrace();
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
                double val = Double.valueOf(htm_data.get(0));
                String unit = htm_data.get(1);
                if (unit.equalsIgnoreCase("°F")) {
                    val = convertFahrenheitToCelcius((float) val);
                    Logger.i("convertFahrenheitToCelcius--->" + val);
                }
                mTempEratureDataSeries.add(mGraphLastXValue, val);
                mChart.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void displayBSLData(String htm_sensor_data) {
        if (htm_sensor_data != null) {
            mDataFieldHSL.setText(htm_sensor_data);
        }
    }

    // Converts to celcius
    private float convertFahrenheitToCelcius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandlerFlag = true;
        getActivity().registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar(getActivity(),
                getResources().getString(R.string.health_thermometer_fragment));
    }

    @Override
    public void onDestroy() {
        mHandlerFlag = false;
        getActivity().unregisterReceiver(mGattUpdateReceiver);
        stopBroadcastDataIndiacte(mNotifyCharacteristic);
        super.onDestroy();
    }


    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void stopBroadcastDataIndiacte(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            if (gattCharacteristic != null) {
                BluetoothLeService.setCharacteristicIndication(
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
    void prepareBroadcastDataIndicate(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            BluetoothLeService.setCharacteristicIndication(gattCharacteristic,
                    true);
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
     * Method to get required characteristics from service
     */
    void getGattData() {
        List<BluetoothGattCharacteristic> gattCharacteristics = mService
                .getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            String uuidchara = gattCharacteristic.getUuid().toString();
            if (uuidchara.equalsIgnoreCase(GattAttributes.TEMPERATURE_TYPE)) {
                prepareBroadcastDataRead(gattCharacteristic);
            }
            if (uuidchara
                    .equalsIgnoreCase(GattAttributes.HEALTH_TEMP_MEASUREMENT)) {
                mNotifyCharacteristic = gattCharacteristic;
                // prepareBroadcastDataIndicate(mNotifyCharacteristic);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setIcon(new ColorDrawable(getResources().getColor(
                    android.R.color.transparent)));
        }
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
        String graphTitle = getResources().getString(R.string.health_temperature_graph);
        String graphXAxis = getResources().getString(R.string.health_temperature_time);
        String graphYAxis = getResources().getString(R.string.health_temperature_temperature);


        // Creating an  XYSeries for temperature
        mTempEratureDataSeries = new XYSeries(graphTitle);


        // Creating a dataset to hold each series
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

        // Adding temperature Series to the dataset
        mDataset.addSeries(mTempEratureDataSeries);


        // Creating XYSeriesRenderer to customize
        XYSeriesRenderer mRenderer = new XYSeriesRenderer();
        mRenderer.setColor(getResources().getColor(R.color.colorPrimary));
        mRenderer.setPointStyle(PointStyle.CIRCLE);
        mRenderer.setFillPoints(true);
        mRenderer.setLineWidth(5);

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
}
