package com.rakuten.soundscript.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

sealed class BackgroundResource {

    open class ImageResource: BackgroundResource() {
        class NetworkResource(val url: String, val scaleType: ContentScale = ContentScale.Crop) : ImageResource()
        class DrawableResource(val drawable: Int, val scaleType: ContentScale = ContentScale.Crop, val tint: Color? = null) : ImageResource()
    }

    open class NonImageResource: BackgroundResource() {
        class ColorResource(val color: Color) : NonImageResource()
    }
}