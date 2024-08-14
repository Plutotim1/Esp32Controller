package com.example.bluetoothwificontroller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice


class TemporaryData {
    companion object {
        var scannedDevices: MutableSet<BluetoothDevice> = mutableSetOf()
        var connectedDevice: BluetoothDevice? = null
        var bluetoothAdapter: BluetoothAdapter? = null
        var connectedThread: BluetoothConnection.ConnectedThread? = null
        var unknownDeviceCount: Int = 0
    }
}