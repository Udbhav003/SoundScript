package com.rakuten.soundscript.ui.composable.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult


@Composable
fun ExpandingText(modifier: Modifier = Modifier, text: String, isExpanded: Boolean, setExpanded: (Boolean) -> Unit) {
    val MINIMIZED_MAX_LINES = 5
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    var isClickable by remember { mutableStateOf(false) }
    var finalText by remember { mutableStateOf(text) }

    val textLayoutResult = textLayoutResultState.value
    LaunchedEffect(textLayoutResult) {
        if (textLayoutResult == null) return@LaunchedEffect

        when {
            isExpanded -> {
                finalText = text
            }
            textLayoutResult.hasVisualOverflow -> {
                val lastCharIndex = textLayoutResult.getLineEnd(MINIMIZED_MAX_LINES - 1)
                val adjustedText = text
                    .substring(startIndex = 0, endIndex = lastCharIndex)
                    .dropLastWhile { it == ' ' || it == '.' }

                finalText = adjustedText

                isClickable = true
            }
        }
    }

    Text(
        text = finalText,
        maxLines = if (isExpanded) Int.MAX_VALUE else MINIMIZED_MAX_LINES,
        onTextLayout = { textLayoutResultState.value = it },
        modifier = modifier.animateContentSize(),
    )
}