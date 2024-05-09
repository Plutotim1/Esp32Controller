package com.example.bluetoothwificontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetoothwificontroller.ui.theme.BluetoothWiFiControllerTheme
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BluetoothWiFiControllerTheme {
                MainView()
            }
        }
    }
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

//will be replaced with a clickable element, showing basic information about a bluetooth device
@Composable
fun TestCard(
    modifier: Modifier = Modifier,
    text: String = "Test",
    shouldShow: Boolean,
    onclick: () -> Unit
) {
    val clicked = remember { mutableStateOf(false)}
    Card(
        modifier = modifier
            .clickable {
                clicked.value = !clicked.value
            }
            ) {
        if (clicked.value) {
            Text(
                text = "clicked"
            )
        } else {
            Text(
                text = text
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
        ConnectScreen()
    }
}

@Composable
fun ConnectScreen() {
    val clickedCard: MutableState<Int?> = remember {
        mutableStateOf(null)
    }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(
                Modifier.height(30.dp)
            )
            Title(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp))
            Greeting(
                name = "test",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            for (i in 1..3) {
                TestCard(modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth()
                    .padding(5.dp),
                    shouldShow = (if (clickedCard.value == i) true else false),
                    onclick = {
                        clickedCard.value = i
                    }

                    )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BluetoothWiFiControllerTheme {
        MainView()
    }
}