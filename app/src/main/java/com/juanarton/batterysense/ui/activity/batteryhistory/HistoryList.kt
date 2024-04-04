package com.juanarton.batterysense.ui.activity.batteryhistory

import android.content.Context
import android.util.TypedValue
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.juanarton.batterysense.R
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory

@Composable
fun HistoryList(historyList: List<BatteryHistory>, context: Context, paddingValues: PaddingValues) {
    val histories = remember { historyList }

    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                top = paddingValues.calculateTopPadding(), bottom = 20.dp
            )
    ){

        val titles = listOf(
            R.string.no,
            R.string.time,
            R.string.level,
            R.string.current,
            R.string.temp,
            R.string.power,
            R.string.volt
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, Color(typedValue.data))
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val commonRowModifier = Modifier
                .weight(1F)
                .border(1.dp, Color(typedValue.data), CutCornerShape(1F))

            titles.forEach { title ->
                Row(modifier = commonRowModifier) {
                    Text(
                        text = context.getString(title),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
            }
        }

        LazyColumn {
            itemsIndexed(
                items = histories,
                itemContent = {index, it ->
                    HistoryItem(history = it, index+1, context)
                }
            )
        }
    }
}