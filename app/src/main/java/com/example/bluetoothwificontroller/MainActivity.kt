package com.example.bluetoothwificontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ClipData.Item
import android.content.pm.PackageManager
import android.graphics.fonts.FontStyle
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.bluetoothwificontroller.ui.theme.BluetoothWiFiControllerTheme


class MainActivity : ComponentActivity() {

    val ENABLE_BT_CODE = 2
    var permissionsAccepted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /*
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        if (bluetoothAdapter == null) {
            ErrorMessage("bluetooth adapter not available")
            return
        }

        SetUpBluetoothPermissions(
        */

        setContent {
            BluetoothWiFiControllerTheme {
                MainView()
            }
        }
    }
    /*

    //called after permissions are either granted or rejected
    fun StartApp() {
        if (permissionsAccepted) {
            setContent {
                BluetoothWiFiControllerTheme {
                    MainView()
                }
            }
        } else {
            ErrorMessage("This app needs the requested permissions to function")
        }
    }
    */


    fun ErrorMessage(message: String) {
        setContent {
            BluetoothWiFiControllerTheme {
                Text(
                    text = message,
                    modifier = Modifier.padding(50.dp)

                )
            }
        }
    }

    /*
    fun SetUpBluetoothPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED -> {
                permissionsAccepted = true
                StartApp()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN), ENABLE_BT_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ENABLE_BT_CODE

            -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    permissionsAccepted = true
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    permissionsAccepted = false
                }
                StartApp()
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
     */
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun Title(modifier: Modifier = Modifier) {
    Text(
        text = "Connect to your Controller!",
        modifier = modifier
    )
}

//will be replaced with a clickable element, showing basic information about a bluetooth device. Clicking will try to connect  to the device
@Composable
fun TestCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String = "Test",
    clicked: Boolean = false,
    onclick: () -> Unit
) {

    Card(
        modifier = modifier
            .clickable {
                onclick()
            }
    ) {
        if (clicked) {
            Text(
                text = "selected",
                fontSize = 30.sp,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        } else {
            Text(
                text = text,
                fontSize =  30.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun MainView() {
    val showConnectScreen = remember {
        mutableStateOf(true)
    }

    if (showConnectScreen.value) {
        ConnectScreen(){
            showConnectScreen.value = false
        }
    }
}


@Composable
fun ConnectScreen(onclick: () -> Unit) {
    val clickedCard: MutableState<Int?> = remember {
        mutableStateOf(null)
    }
    Scaffold(
        topBar = {
            Text(
                text = "Pick your Microcontroller to connect to!",
                fontSize =  20.sp,
                modifier = Modifier.padding(top = 30.dp))
        },
        bottomBar = {
            Button(
                onClick = onclick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect to device" + if (clickedCard.value != null) ": " + clickedCard.value else "")
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) {paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            items(10) {index ->
                TestCard(
                    text = "Card Number: " + index.toString(),
                    modifier = Modifier
                        .padding(10.dp)
                        .height(50.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    clicked = if (index == clickedCard.value) true else false
                ) {
                    clickedCard.value = index
                }
            }
        }
    }

}

fun SetUpBluetooth() {

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BluetoothWiFiControllerTheme {
        MainView()
    }
}

