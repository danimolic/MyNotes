package com.example.mynotes.ui

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


const val DESIGN_WIDTH_PX = 712f

/** Convierte "píxeles del diseño" a dp, para que cada elemento quede en su lugar en cualquier pantalla. */
class DesignScale(private val dpPerPx: Float) {
    fun dp(px: Number): Dp = (px.toFloat() * dpPerPx).dp
    fun sp(px: Number): TextUnit = (px.toFloat() * dpPerPx).sp
}

/** En tablets el diseño no se agranda más allá de 560 dp. */
fun designScaleFor(availableWidth: Dp): DesignScale =
    DesignScale(minOf(availableWidth.value, 560f) / DESIGN_WIDTH_PX)


fun Modifier.pressClickable(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "press")
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = interaction, indication = null, onClick = onClick)
}


@Composable
fun rememberImageBitmap(uriString: String?): ImageBitmap? {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, uriString) {
        value = if (uriString == null) null
        else withContext(Dispatchers.IO) { decodeUri(context, Uri.parse(uriString)) }
    }
    return bitmap
}

private fun decodeUri(context: Context, uri: Uri, maxSide: Int = 1600): ImageBitmap? = try {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        val longest = maxOf(info.size.width, info.size.height)
        if (longest > maxSide) {
            val f = maxSide.toFloat() / longest
            decoder.setTargetSize(
                (info.size.width * f).toInt().coerceAtLeast(1),
                (info.size.height * f).toInt().coerceAtLeast(1)
            )
        }
    }.asImageBitmap()
} catch (e: Exception) {
    null // archivo borrado o sin permiso: simplemente no se muestra
}
