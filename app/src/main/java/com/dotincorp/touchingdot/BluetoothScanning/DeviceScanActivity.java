package com.dotincorp.touchingdot.BluetoothScanning;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dotincorp.touchingdot.Constants;
import com.dotincorp.touchingdot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
    private LeDeviceListAdapter leDeviceListAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private ArrayList<BluetoothDevice> deviceList;

    BluetoothDevice device;

    // Stops scanning after 20 seconds.
    private static final long SCAN_PERIOD = 20000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "This device doesn't support Bluetooth Low Energy", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "This device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        leDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(leDeviceListAdapter);

        loadBonded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_scan, menu);

        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setEnabled(false);
            menu.findItem(R.id.menu_scan).setEnabled(true);
        } else {
            menu.findItem(R.id.menu_stop).setEnabled(true);
            menu.findItem(R.id.menu_scan).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                leDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Scan Device
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        leDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        device = leDeviceListAdapter.getDevice(position);
        if ((device == null) || (device.getName() == null) || (device.getAddress() == null)) {
            return;
        }
        saveDeviceInfo();
        Intent dataIntent = new Intent();
        dataIntent.putExtra(Constants.EXTRA_DEVICE_NAME, device.getName());
        dataIntent.putExtra(Constants.EXTRA_DEVICE_ADDRESS, device.getAddress());
        setResult(RESULT_OK, dataIntent);

        finish();
    }

    public void saveDeviceInfo(){
        SharedPreferences pref = getSharedPreferences("device", Activity.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("mac", device.getAddress());
        editor.putString("name",device.getName());
        editor.commit();
    }

    private void loadBonded() {
        List<BluetoothDevice> bondedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);

        for (BluetoothDevice device : bondedDevices) {
            leDeviceListAdapter.addDevice(device);
        }
    }

    /**
     * Start or Stop scanning
     *
     * @param start true to start scan
     */
    private void scanLeDevice(final boolean start) {
        if (start) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopLeScan();
                }
            }, SCAN_PERIOD);

            startLeScan();
        } else {
            stopLeScan();
        }
    }

    /**
     * Start LE Scan
     */
    private void startLeScan() {
        mScanning = true;
        bluetoothAdapter.startLeScan(leScanCallback);
        invalidateOptionsMenu();
    }

    /**
     * Stop LE Scan
     */
    private void stopLeScan() {
        mScanning = false;
        bluetoothAdapter.stopLeScan(leScanCallback);
        invalidateOptionsMenu();
    }

    /**
     * Adapter for holding devices
     */
    private class LeDeviceListAdapter extends BaseAdapter {
        private final int PADDING;

        LeDeviceListAdapter() {
            super();
            deviceList = new ArrayList<>();
            PADDING = (int) (8 * getResources().getDisplayMetrics().density);
        }

        void addDevice(BluetoothDevice device) {
            if (!deviceList.contains(device)) {
                deviceList.add(device);
            }
            notifyDataSetChanged();
        }

        BluetoothDevice getDevice(int position) {
            return deviceList.get(position);
        }

        void clear() {
            deviceList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public Object getItem(int i) {
            return deviceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = new TextView(viewGroup.getContext());
                view.setPadding(PADDING, PADDING, PADDING, PADDING);
            }

            BluetoothDevice device = deviceList.get(i);
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();
            if (deviceName == null || deviceName.isEmpty())
                deviceName = "Unknown Device";

            String deviceString = String.format("%s\n%s", deviceName, deviceAddress);
            ((TextView) view).setText(deviceString);

            return view;
        }
    }

    /**
     * BLE Scan Callback for < LOLLIPOP
     */
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null && device.getName() != null && !device.getName().isEmpty()) {
                        leDeviceListAdapter.addDevice(device);
                        leDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

}
