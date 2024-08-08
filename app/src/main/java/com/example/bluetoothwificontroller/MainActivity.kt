package com.example.bluetoothwificontroller

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.bluetoothwificontroller.ui.theme.BluetoothWiFiControllerTheme

//directions
const val LEFT: Byte = 0
const val RIGHT: Byte = 1
const val UP: Byte = 2
const val DOWN: Byte = 3
const val NO_DIRECTION: Byte = 4


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        //register receiver for scanning bluetooth devices
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)


        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            errorMessage("bluetooth adapter not available")
            return
        }



        TemporaryData.bluetoothAdapter = bluetoothAdapter

        //check permissions
        if ( checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // permissions are granted
            PermissionSuccess()
        } else {
            //ask for permissions
            val requestPermissionsLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions() )
            {permissions ->
                var accepted = true
                if (!permissions.getOrDefault(Manifest.permission.BLUETOOTH_SCAN, false)) {
                    accepted = false
                }
                if (!permissions.getOrDefault(Manifest.permission.BLUETOOTH_CONNECT, false)) {
                    accepted = false
                }

                if (accepted) {
                    PermissionSuccess()
                } else {
                    //permissions were denied
                    setContent {
                        BluetoothWiFiControllerTheme {
                            errorMessage("This App needs bluetooth permissions to function, please enable them")
                        }
                    }
                }

            }

            requestPermissionsLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT))

        }






    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action!!
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    if (device != null) {
                        //replace true with a setting which decides wether to show unknown devices
                        if (true && device.name == null) {
                            TemporaryData.unknownDeviceCount++
                            return
                        }
                        TemporaryData.scannedDevices.add(device)

                    }
                }
            }
        }
    }


    private fun PermissionSuccess() {
        //permissions were granted
        //TODO use new API
        if (!TemporaryData.bluetoothAdapter?.isEnabled!!) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                1
            )
        }
        else {
            TemporaryData.bluetoothAdapter?.startDiscovery()
            setContent {
                BluetoothWiFiControllerTheme {
                    MainView()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                errorMessage("This app needs Bluetooth Permissions to function")
                return
            }
            TemporaryData.bluetoothAdapter?.startDiscovery()
            setContent {
                BluetoothWiFiControllerTheme {
                    MainView()
                }
            }
        }
    }




    private fun errorMessage(message: String) {
        setContent {
            BluetoothWiFiControllerTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                        Text(
                            text = message,

                            modifier = Modifier
                                .padding(50.dp)
                                .align(Alignment.Center)
                        )
                }

            }
        }
    }
}

var handler: Handler? = null

@Composable
fun Title(modifier: Modifier = Modifier) {
    Text(
        text = "Connect to your Controller!",
        fontSize = 20.sp,
        modifier = modifier
    )
}


@SuppressLint("MissingPermission")
@Composable
fun TestCard(
    device: BluetoothDevice,
    modifier: Modifier = Modifier,
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
                text = """selected: ${if (device.name != null) device.name else "unknown"}""",
                fontSize = 30.sp,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        } else {
            Text(
                text = if (device.name != null) device.name else "unknown",
                fontSize =  30.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MainView() {
    val showConnectScreen = remember {
        mutableStateOf(true)
    }

    val showErrorScreen = remember {
        mutableStateOf<String?>(null)
    }

    val showLoadingScreen = remember {
        mutableStateOf(false)
    }

    handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.data.getInt("error")) {
                -1 -> {
                    Log.d("MyApp", "handler error callback called code: -1")
                    TemporaryData.connectedThread?.cancel()
                    TemporaryData.connectedDevice = null
                    showErrorScreen.value = "Couldn't connect to device"
                    showLoadingScreen.value = false
                }
            }
            when(msg.data.getInt("action")) {
                1 -> {
                    Log.d("Myapp", "Loading screen over")
                    showLoadingScreen.value = false
                }
            }
        }
    }

    if (showErrorScreen.value != null) {
        Log.d("Myapp", "errorscreen called!")
        ErrorScreen(text = showErrorScreen.value!!) {
            showErrorScreen.value = null
            showConnectScreen.value = true
        }
    }else if (showConnectScreen.value) {
        ConnectScreen {
            showConnectScreen.value = false
            showLoadingScreen.value = true
        }
    } else if (showLoadingScreen.value) {
        LoadingScreen()
    } else {
        ControlScreen(
            onDisconnect = {
                //TODO make work
                TemporaryData.connectedThread?.cancel()
                TemporaryData.connectedDevice = null
                showConnectScreen.value = true
            }
        )
    }
}

@Composable
fun ErrorScreen (text: String, onclick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(30.dp)
        )
        Button(onClick = onclick) {
            Text(
                "Ok"
            )
        }
    }
}


@SuppressLint("MissingPermission")
@Composable
fun ConnectScreen(onConnect: () -> Unit) {
    val clickedCard: MutableState<Int?> = remember {
        mutableStateOf(null)
    }
    //0 for paired device, 1 for scanned Device
    val clickedCardType = remember {
        mutableStateOf(0)
    }
    val shouldUpdate = remember {
        mutableStateOf(true)
    }
    val bondedDevices = remember {
        mutableStateOf(emptySet<BluetoothDevice>())
    }
    val scannedDevices = remember {
        mutableStateOf(emptySet<BluetoothDevice>())
    }
    if (shouldUpdate.value) {
        if (TemporaryData.bluetoothAdapter?.isDiscovering == false) {
            TemporaryData.bluetoothAdapter!!.startDiscovery()
        }
        bondedDevices.value = TemporaryData.bluetoothAdapter!!.bondedDevices
        scannedDevices.value = TemporaryData.scannedDevices
        Log.d("ble", TemporaryData.scannedDevices.toString())
        Log.d("ble", TemporaryData.bluetoothAdapter?.bondedDevices.toString())
        shouldUpdate.value = false
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
                    //cancel discovery
                    TemporaryData.bluetoothAdapter?.cancelDiscovery()
                    Log.d("myApp", "Canceled Discovery")
                    //get device
                    val device =
                        if (clickedCardType.value == 0) bondedDevices.value.elementAt(clickedCard.value!!)
                        else scannedDevices.value.elementAt(clickedCard.value!!)

                    TemporaryData.connectedDevice = device

                    //create bluetooth thread
                    TemporaryData.connectedThread = BluetoothConnection(handler!!).ConnectedThread(device)
                    Log.d("myApp", "Initialized Thread")

                    //start bluetooth connection
                    TemporaryData.connectedThread!!.start()
                    Log.d("myApp", "Started Thread")

                    //transition to control ui screen
                    onConnect()
                    Log.d("Myapp", "onConnect called")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect to device" +
                    if (clickedCard.value != null)
                            (": " + if (clickedCardType.value == 0) bondedDevices.value.elementAt(clickedCard.value!!).name else scannedDevices.value.elementAt(
                                clickedCard.value!!
                            ).name )
                    else "")
            }
        },
        floatingActionButton = {
            //TODO make it work
                               Button(onClick = {shouldUpdate.value = true}) {
                                   Text("update!")
                               }
        },
        modifier = Modifier
            .fillMaxSize()
    ) {paddingValues ->
        Column {
            Text(text = "paired Devices")
            LazyColumn(
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                if (bondedDevices.value.isNotEmpty()) {
                    items(bondedDevices.value.size) { index ->
                        TestCard(
                            device = bondedDevices.value.elementAt(index),
                            modifier = Modifier
                                .padding(10.dp)
                                .height(50.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            clicked = (index == clickedCard.value && clickedCardType.value == 0)
                        ) {
                            clickedCard.value = index
                            clickedCardType.value = 0
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Nearby devices", modifier = Modifier.align(Alignment.TopStart))
                Text(text = "unknown devices:" + TemporaryData.unknownDeviceCount.toString(), modifier = Modifier.align(Alignment.TopEnd))

            }

            LazyColumn(
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                if (scannedDevices.value.isNotEmpty()) {
                    items(scannedDevices.value.size) { index ->
                        TestCard(
                            device = scannedDevices.value.elementAt(index),
                            modifier = Modifier
                                .padding(10.dp)
                                .height(50.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            clicked = (index == clickedCard.value && clickedCardType.value == 1)
                        ) {
                            clickedCard.value = index
                            clickedCardType.value = 1
                        }
                    }
                }
            }
        }
        
    }

}

@Composable
fun LoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        //TODO add animation
        SpinningIcon(id = R.drawable.loading_icon, modifier = Modifier.scale(2f))
        Spacer(modifier = Modifier.height(50.dp))
        Text("Connecting")
    }
}

@Composable
fun SpinningIcon(id: Int, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = InfiniteRepeatableSpec(
            animation = TweenSpec(
                durationMillis = 750,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    /*androidx.compose.material3.Icon(
        imageVector = icon,
        contentDescription ="spinning loading icon",
        modifier = Modifier.rotate(rotation)
    )*/

    Image(
        painter = painterResource(id = id),
        contentDescription = "spinning icon",
        modifier = modifier.rotate(rotation)
    )


}


@SuppressLint("MissingPermission")
@Composable
fun ControlScreen(onDisconnect: () -> Unit) {
    Scaffold(
        topBar = {
                 Column(
                     modifier = Modifier.fillMaxWidth()
                 ) {
                     Spacer(Modifier.height(40.dp))
                     Text(
                         text = "connected to device: ${TemporaryData.connectedDevice?.name}",
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
                    DirectionalInput(UP)
                    Row{
                        DirectionalInput(LEFT)
                        DirectionalInput()
                        DirectionalInput(RIGHT)
                    }
                    DirectionalInput(DOWN)
                }
            }
    }
}


@Composable
fun DirectionalInput(direction: Byte = NO_DIRECTION) {
    val interActionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interActionSource.collectIsPressedAsState()
    val isActive = remember {
        mutableStateOf(false)
    }
    if (isActive.value != isPressed) {
        //emitSignal(isPressed)
        val data = arrayOf<Byte>(
            'd'.code.toByte(),
            direction,
            if (isPressed) 1 else 0
        ).toByteArray()
        TemporaryData.connectedThread!!.write(data)
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
            LEFT -> Icons.Default.KeyboardArrowLeft
            RIGHT -> Icons.Default.KeyboardArrowRight
            UP -> Icons.Default.KeyboardArrowUp
            DOWN -> Icons.Default.KeyboardArrowDown
            else -> Icons.Default.AddCircle
        }
        androidx.compose.material3.Icon(imageVector = icon, contentDescription = "arrow pointing $direction")
    }
}


@Preview(showBackground = true)
@Composable
fun ConnectScreenPreview() {
    BluetoothWiFiControllerTheme {
        ControlScreen {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControlScreenPreview() {
    BluetoothWiFiControllerTheme {
        ConnectScreen {
        }
    }
}

