package com.example.bluetoothwificontroller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

class TemporaryData {
    companion object {
        var connectedDevice: BluetoothDevice? = null
        var bluetoothAdapter: BluetoothAdapter? = null
        var connectedThread: BluetoothConnection.ConnectedThread? = null
    }
}