package com.ae.devlens.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ae.devlens.AEDevLens
import com.ae.devlens.AEDevLensProvider
import com.ae.devlens.DevLensConfig
import com.ae.devlens.DevLensUiConfig
import com.ae.devlens.createDefault
import com.ae.devlens.plugins.logs.log
import com.ae.devlens.plugins.logs.model.LogSeverity

@Composable
fun App() {
    // createDefault() installs LogsPlugin automatically (via :devlens aggregator extension)
    val inspector = remember {
        AEDevLens.createDefault(DevLensConfig(maxLogEntries = 500))
    }

    MaterialTheme {
        AEDevLensProvider(
            inspector = inspector,
            uiConfig = DevLensUiConfig(
                showFloatingButton = true,
                enableLongPress = true,
            ),
            enabled = true,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("AEDevLens Sample App")
                Text("The Developer Tool SDK is active!")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    inspector.log(LogSeverity.INFO, "SampleApp", "Hello from the sample app!")
                }) {
                    Text("Trigger Info Log")
                }

                Button(onClick = {
                    inspector.log(LogSeverity.ERROR, "SampleApp", "Uh oh! Something went wrong!")
                }) {
                    Text("Trigger Error Log")
                }

                Button(onClick = {
                    inspector.log(LogSeverity.DEBUG, "SampleApp", "Debug message with details")
                }) {
                    Text("Trigger Debug Log")
                }
            }
        }
    }
}
