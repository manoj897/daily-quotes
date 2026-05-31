package com.dailyquotes.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimePickerComponent(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    pickerWidth: Dp,
    pickerHeight: Dp,
    itemHeight: Dp,
    columnGap: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hour picker (0-23)
        NumberPicker(
            value = hour,
            onValueChange = onHourChange,
            range = 0..23,
            label = "Hour",
            pickerWidth = pickerWidth,
            pickerHeight = pickerHeight,
            itemHeight = itemHeight
        )

        Spacer(modifier = Modifier.width(columnGap))

        Text(
            text = ":",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.width(columnGap))

        // Minute picker (0-59)
        NumberPicker(
            value = minute,
            onValueChange = onMinuteChange,
            range = 0..59,
            label = "Minute",
            pickerWidth = pickerWidth,
            pickerHeight = pickerHeight,
            itemHeight = itemHeight
        )
    }
}

@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String,
    pickerWidth: Dp,
    pickerHeight: Dp,
    itemHeight: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFFBBBBBB),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF111111)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(pickerWidth)
                .height(pickerHeight)
        ) {
            val listState = rememberLazyListState()
            val verticalPadding = ((pickerHeight - itemHeight) / 2f).coerceAtLeast(0.dp)

            LaunchedEffect(value) {
                // Scroll to selected value when it changes
                val index = value - range.first
                if (index >= 0 && index < range.count()) {
                    listState.animateScrollToItem(index.coerceIn(0, range.count() - 1))
                }
            }

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = verticalPadding),
                modifier = Modifier.fillMaxSize()
            ) {
                items(range.count()) { index ->
                    val itemValue = range.first + index
                    val isSelected = itemValue == value

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .clickable { onValueChange(itemValue) }
                            .background(
                                if (isSelected) Color(0xFF222222)
                                else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = itemValue.toString().padStart(2, '0'),
                            fontSize = if (isSelected) 24.sp else 18.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF888888),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
