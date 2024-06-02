package com.example.bluetoothwificontroller


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
            //initialize connection
            Log.d("myAppBT", "run called")
            try {
                socket = device.createRfcommSocketToServiceRecord(device.uuids[0].uuid)
                Log.d("myAppBT", "socket created")
            } catch (e: IOException) {
                Log.d("myAppBT", "Couldn't create socket")
                SendErrorCode(-1)
                return
            }
            try {
                socket.connect()
                Log.d("myAppBT", "socket connected")
            } catch(e: IOException) {
                Log.d("myAppBT", "socket connection error")
                if (socket.isConnected) {
                    Log.d("myAppBT", "trying to disconnect from socket after failed connection")
                    socket.close()
                }
                SendErrorCode(-1)
                return
            }

            mmInStream = socket.inputStream
            mmOutStream = socket.outputStream
            Log.d("myApp", "Setup completed")

            //tell main-activity to load the control screen
            SendActionCode(1)


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

        private fun SendErrorCode(code: Int) {
            val data = Bundle()
            data.putInt("error", code)
            val message = Message.obtain()
            message.data = data
            handler.dispatchMessage(message)
        }

        private fun SendActionCode(code: Int) {
            val data = Bundle()
            data.putInt("action", code)
            val message = Message.obtain()
            message.data = data
            handler.dispatchMessage(message)
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
