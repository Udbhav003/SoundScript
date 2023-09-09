package com.rakuten.soundscript.ui.composable.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> GridLayout(
    items: List<T>,
    columns: Int,
    contentPadding: PaddingValues,
    itemSpacing: Dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable (T, Dp) -> Unit
) {
    val itemWidth= (LocalConfiguration.current.screenWidthDp.dp) / columns
    val itemWidthWithoutPadding = (LocalConfiguration.current.screenWidthDp.dp - contentPadding.calculateStartPadding(LayoutDirection.Ltr).times(2) - itemSpacing).div(columns)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = columns,
        horizontalArrangement = horizontalArrangement,
    ) {
        for (index in items.indices) {
            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .padding(
                        start = if (index % columns == 0) contentPadding.calculateStartPadding(LayoutDirection.Ltr) else itemSpacing / 2,
                        end = if ((index + 1) % columns == 0) contentPadding.calculateEndPadding(LayoutDirection.Ltr) else itemSpacing / 2,
                        top = if (index < columns) contentPadding.calculateTopPadding() else itemSpacing / 2,
                        bottom = if (items.size - index + 1 < columns) contentPadding.calculateBottomPadding() else itemSpacing / 2
                    )
            ) {
                content(items[index], itemWidthWithoutPadding)
            }
        }
    }
}