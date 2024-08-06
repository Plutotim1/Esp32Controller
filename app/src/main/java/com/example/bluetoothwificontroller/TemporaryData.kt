package com.example.bluetoothwificontroller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import java.util.logging.Handler

class TemporaryData {
    companion object {
        var scannedDevices: MutableSet<BluetoothDevice> = mutableSetOf()
        var connectedDevice: BluetoothDevice? = null
        var bluetoothAdapter: BluetoothAdapter? = null
        var connectedThread: BluetoothConnection.ConnectedThread? = null
    }
}