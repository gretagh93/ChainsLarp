package com.chains.larp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.chains.larp.ui.theme.ChainsBackgroundGradient

/**
 * Custom app scaffold with defaults for our application.
 */
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerGesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colors.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    drawerScrimColor: Color = DrawerDefaults.scrimColor,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (PaddingValues) -> Unit
) {
    // Reuse default SnackbarHost to have default animation and timing handling
    val snackbarHost: @Composable (SnackbarHostState) -> Unit = {
        SnackbarHost(it) { data ->
            AppSnackbar(snackbarData = data)
        }
    }

    Scaffold(
        modifier,
        scaffoldState,
        topBar,
        bottomBar,
        snackbarHost,
        floatingActionButton,
        floatingActionButtonPosition,
        isFloatingActionButtonDocked,
        drawerContent,
        drawerGesturesEnabled,
        drawerShape,
        drawerElevation,
        drawerBackgroundColor,
        drawerContentColor,
        drawerScrimColor,
        backgroundColor,
        contentColor,
        content
    )
}