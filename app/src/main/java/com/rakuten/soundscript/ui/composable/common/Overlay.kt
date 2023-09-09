package com.rakuten.soundscript.ui.composable.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun GradientOverlay(
    modifier: Modifier = Modifier,
    gradient: LinearGradient,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(
                brush = gradient
            )
    ){
        content()
    }
}

@Composable
fun ColorOverlay(
    modifier: Modifier = Modifier,
    color: Color,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(
                color = color
            )
    ){
        content()
    }
}

