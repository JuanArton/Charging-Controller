package com.juanarton.batterysense.ui.activity.batteryhistory

import android.content.Context
import android.util.TypedValue
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juanarton.batterysense.R
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.utils.Utils.convertMillisToDateTime
import java.util.Locale

@Composable
fun HistoryItem(history: BatteryHistory, index: Int, context: Context) {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val commonRowModifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .border(1.dp, Color(typedValue.data), CutCornerShape(1F))

            Row (
                modifier = commonRowModifier,
                horizontalArrangement = Arrangement.Center
            ){
                DataText(text = "$index")
            }

            Row (
                modifier = commonRowModifier,
                horizontalArrangement = Arrangement.Center
            ){
                DataText(text = convertMillisToDateTime(history.timestamp))
            }

            Row (
                modifier = commonRowModifier,
                horizontalArrangement = Arrangement.Center
            ){
                DataText(text = "${history.level}%")
            }

            Row (
                modifier = commonRowModifier,
                horizontalArrangement = Arrangement.Center
            ){
                DataText(text = "${history.current}${context.getString(R.string.ma)}")
            }

            Row (
                modifier = commonRowModifier,
                horizontalArrangement = Arrangement.Center
            ){
                DataText(text = "${history.temperature}${context.getString(com.juanarton.core.R.string.degree_symbol)}")
            }

            Row (
                modifier = commonRowModifier,
                horizontalArrangement = Arrangement.Center
            ){
                DataText(text = "${String.format(Locale.getDefault(), "%.2f", history.power)}${context.getString(
                    com.juanarton.core.R.string.wattage)}")
            }

            Row (
                modifier = commonRowModifier,
                horizontalArrangement = Arrangement.Center
            ){
                DataText(text = "${String.format(Locale.getDefault(), "%.2f", history.voltage)}${context.getString(
                    com.juanarton.core.R.string.volt_unit)}")
            }
        }
    }
}

@Composable
fun DataText(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .wrapContentHeight(),
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
    )
}
