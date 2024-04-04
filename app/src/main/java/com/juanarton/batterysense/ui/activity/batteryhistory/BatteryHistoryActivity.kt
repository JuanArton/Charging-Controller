package com.juanarton.batterysense.ui.activity.batteryhistory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.juanarton.batterysense.ui.theme.BatterySenseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BatteryHistoryActivity : ComponentActivity() {

     private val batteryHistoryViewModel: BatteryHistoryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatterySenseTheme {
                HistoryContent(batteryHistoryViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryContent(batteryHistoryViewModel: BatteryHistoryViewModel) {
    batteryHistoryViewModel.getBatteryHistory()
    val histories = batteryHistoryViewModel.batteryHistory.observeAsState().value

    if (!histories.isNullOrEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Battery History") },
                )
            },
            content = { paddingValues ->
                HistoryList(historyList = histories, LocalContext.current, paddingValues)
            },
        )
    }
}
