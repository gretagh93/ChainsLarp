package com.chains.larp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Custom app Snackbar with app's text style.
 */
@Composable
fun AppSnackbar(modifier: Modifier = Modifier, snackbarData: SnackbarData) {
    AppSnackbar(modifier = modifier,
        message = snackbarData.message,
        actionLabel = snackbarData.actionLabel,
        action = { snackbarData.performAction() })
}

/**
 * Custom app Snackbar with app's text style.
 */
@Composable
fun AppSnackbar(modifier: Modifier = Modifier,
                message: String,
                actionLabel: String? = null,
                action: (() -> Unit)? = null) {
    val actionComposable: (@Composable () -> Unit)? = if (actionLabel != null) {
        @Composable {
            ButtonText(
                modifier = if (action != null) Modifier.clickable { action() } else Modifier,
                text = actionLabel
            )
        }
    } else {
        null
    }

    Snackbar(
        modifier = modifier.padding(12.dp),
        action = actionComposable,
        content = {
            Body1Text(text = message)
        }
    )
}

@Composable
@Preview
fun AppSnackbarPreview() {
    Column(Modifier.fillMaxSize()) {
        AppSnackbar(message = "This is a snackbar")
        AppSnackbar(message = "You expected a snackbar, but it was me, DIO!!", actionLabel = "Ignore")
    }
}