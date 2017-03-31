package com.dotincorp.touchingdot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dotincorp.watchservice.BluetoothLeService;

import static android.content.ContentValues.TAG;

/**
 * Created by wjddk on 2017-03-30.
 */

public class ConnectionReceiver extends BroadcastReceiver {

    public int connectionStatus = BluetoothLeService.STATE_DISCONNECTED;
    @Override
    public void onReceive
            (Context context, Intent intent) {
        final String action = intent.getAction();

        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // 연결되었을 때
            Log.i(TAG, "Received ACTION_GATT_CONNECTED");
        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            Log.i(TAG, "Received ACTION_GATT_SERVICES_DISCOVERED");
        } else if (BluetoothLeService.ACTION_GATT_OBSERVER_SETTED.equals(action)) {
            Log.i(TAG, "Received ACTION_GATT_OBSERVER_SETTED");
            connectionStatus = BluetoothLeService.STATE_CONNECTED;
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { // 연결 해제 되었을 때
            Log.i(TAG, "Received ACTION_GATT_DISCONNECTED");
            connectionStatus = BluetoothLeService.STATE_DISCONNECTED;
        }
    }

}
