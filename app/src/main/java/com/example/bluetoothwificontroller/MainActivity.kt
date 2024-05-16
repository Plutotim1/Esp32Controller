package com.example.bluetoothwificontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetoothwificontroller.ui.theme.BluetoothWiFiControllerTheme


class MainActivity : ComponentActivity() {

    //val ENABLE_BT_CODE = 2
    //var permissionsAccepted = false
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
fun Title(modifier: Modifier = Modifier) {
    Text(
        text = "Connect to your Controller!",
        fontSize = 20.sp,
        modifier = modifier
    )
}

//will be replaced with a clickable element, showing basic information about a bluetooth device. Clicking will try to connect  to the device
@Composable
fun TestCard(
    modifier: Modifier = Modifier,
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
        ConnectScreen {
            showConnectScreen.value = false
        }
    } else {
        ControlScreen(
            onDisconnect = {
                TemporaryData.connectedDeviceName = ""
                showConnectScreen.value = true
            }
        )
    }
}


@Composable
fun ConnectScreen(onConnect: () -> Unit) {
    val clickedCard: MutableState<Int?> = remember {
        mutableStateOf(null)
    }
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Title(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

        },
        bottomBar = {
            Button(
                enabled = clickedCard.value != null,
                onClick = {
                    onConnect()
                    TemporaryData.connectedDeviceName = clickedCard.value.toString()
                },
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
                    text = "Card Number: $index",
                    modifier = Modifier
                        .padding(10.dp)
                        .height(50.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    clicked = index == clickedCard.value
                ) {
                    clickedCard.value = index
                }
            }
        }
    }

}

@Composable
fun ControlScreen(onDisconnect: () -> Unit) {
    Scaffold(
        topBar = {
                 Column(
                     modifier = Modifier.fillMaxWidth()
                 ) {
                     Spacer(Modifier.height(40.dp))
                     Text(
                         text = "connected to device: ${TemporaryData.connectedDeviceName}",
                         modifier = Modifier.align(Alignment.CenterHorizontally)
                         )
                 }
        },
        bottomBar = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDisconnect
            ) {
                Text(
                    text = "Disconnect"
                )
            }
        }
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    Spacer(Modifier.height(250.dp))
                    DirectionalInput("up")
                    Row{
                        DirectionalInput("left")
                        DirectionalInput()
                        DirectionalInput("right")
                    }
                    DirectionalInput("down")
                }
            }
    }
}


@Composable
fun DirectionalInput(direction: String = "") {
    val interActionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interActionSource.collectIsPressedAsState()
    val isActive = remember {
        mutableStateOf(false)
    }
    if (isActive.value != isPressed) {
        //emitSignal(isPressed)
        isActive.value = isPressed
    }
    Button(
        interactionSource = interActionSource,
        onClick = {},
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = if (isPressed) Color.Gray else Color.LightGray),
        modifier = Modifier
            .size(90.dp)
    ) {
        val icon = when (direction) {
            "left" -> Icons.Default.KeyboardArrowLeft
            "right" -> Icons.Default.KeyboardArrowRight
            "up" -> Icons.Default.KeyboardArrowUp
            "down" -> Icons.Default.KeyboardArrowDown
            else -> Icons.Default.AddCircle
        }
        androidx.compose.material3.Icon(imageVector = icon, contentDescription = "arrow pointing $direction")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BluetoothWiFiControllerTheme {
        ControlScreen {

        }
    }
}

