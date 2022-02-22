package com.chains.larp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.chains.larp.ui.theme.BlueConsole
import com.chains.larp.ui.theme.DarkBlueConsole
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder

val LocalLoadingState = compositionLocalOf { true }

fun Modifier.withPlaceholder(force: Boolean = false) = composed {
    this.placeholder(
        visible = LocalLoadingState.current || force,
        color = BlueConsole,
        highlight = PlaceholderHighlight.fade(DarkBlueConsole)
    )
}

@Composable
fun withLocalLoadingState(isLoading: Boolean, content: @Composable () -> Unit) {
    val showPlaceholders = isLoading
    CompositionLocalProvider(LocalLoadingState provides showPlaceholders) {
        content()
    }
}