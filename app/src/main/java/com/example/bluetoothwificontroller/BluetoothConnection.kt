package com.example.bluetoothwificontroller


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private const val TAG = "MY_APP_DEBUG_TAG"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
// ... (Add other message types here as needed.)

class BluetoothConnection(
    // handler that gets info from Bluetooth service
    private val handler: Handler
) {
    inner class ConnectedThread(private val device: BluetoothDevice) : Thread() {


        private lateinit var socket: BluetoothSocket
        private lateinit var mmInStream: InputStream
        private lateinit var mmOutStream: OutputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        @SuppressLint("MissingPermission")
        override fun run() {
            //initialize connecteion
            Log.d("myAppBT", "run called")
            socket = device.createRfcommSocketToServiceRecord(device.uuids[0].uuid)
            Log.d("myAppBT", "socket created")
            socket.connect()
            Log.d("myAppBT", "socket connected")
            if (!socket.isConnected) {
                Log.d("myAppBT", "Socket couldn't connect")
                return
            }
            mmInStream = socket.inputStream
            mmOutStream = socket.outputStream
            Log.d("myApp", "Setup completed")


            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                // Send the obtained bytes to the UI activity.
                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer)
                readMsg.sendToTarget()
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
            }
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }
}
