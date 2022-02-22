package com.chains.larp.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val BOTTOM_BAR_HEIGHT = 56
val navigationBottomBarPaddingValues = PaddingValues(bottom = BOTTOM_BAR_HEIGHT.dp)

@Composable
fun BottomBarNestedScrollConnection(): Pair<MutableState<Float>, NestedScrollConnection> {
    val bottomBarHeight = BOTTOM_BAR_HEIGHT.dp
    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

    // connection to the nested scroll system and listen to the scroll
    // happening inside child LazyColumn
    return bottomBarOffsetHeightPx to remember<NestedScrollConnection> {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                val delta = available.y
                val newOffset = bottomBarOffsetHeightPx.value + delta
                bottomBarOffsetHeightPx.value = newOffset.coerceIn(-bottomBarHeightPx, 0f)

                return Offset.Zero
            }
        }
    }
}

@Composable
fun GrizzlyBottomNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onClick: (BottomNavigationScreen) -> Unit,
) {
  // val bottomBarItems = listOf(
  //     BottomNavigationScreen.Home,
  //     BottomNavigationScreen.Training,
  //  //   BottomNavigationScreen.Social,
  //     BottomNavigationScreen.Profile,
  // )
    BottomNavigation(
        elevation = 4.dp,
        modifier = modifier.height(BOTTOM_BAR_HEIGHT.dp)
    ) {
     //   Column {
     //       Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
     //           bottomBarItems.forEach { screen ->
     //               val isSelected = currentRoute == screen.route || screen.nestedRoutes.contains(currentRoute)
     //               val iconTint = if (isSelected) MaterialTheme.colors.secondary
     //               else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
//
     //               BottomNavigationItem(
     //                   icon = {
     //                       Icon(
     //                           painterResource(id = screen.drawableIcon),
     //                           null,
     //                           tint = iconTint,
     //                           modifier = Modifier.height(28.dp).padding(vertical = 2.dp)
     //                       )
     //                   },
     //                   label = { Body1Text(text = stringResource(screen.resourceId), color = iconTint) },
     //                   selected = isSelected,
     //                   onClick = { onClick(screen) })
     //           }
     //       }
     //   }
    }
}

sealed class BottomNavigationScreen(
    val route: String,
    val nestedRoutes: List<String>,
    @StringRes val resourceId: Int,
    @DrawableRes val drawableIcon: Int,
) {
   // object Home :
   //     BottomNavigationScreen(MainDestinations.HOME_ROUTE, emptyList(), R.string.navigation_home, R.drawable.ic_home)
//
   // object Training : BottomNavigationScreen(
   //     MainDestinations.TRAINING_ROUTE,
   //     listOf(MainDestinations.TRAINING_LIST_ROUTE),
   //     R.string.navigation_training,
   //     R.drawable.ic_app_icon
   // )
//
   // object Social :
   //     BottomNavigationScreen(
   //         MainDestinations.SOCIAL_ROUTE,
   //         emptyList(),
   //         R.string.navigation_social,
   //         R.drawable.ic_social
   //     )
//
   // object Profile :
   //     BottomNavigationScreen(
   //         MainDestinations.PROFILE_ROUTE,
   //         emptyList(),
   //         R.string.navigation_profile,
   //         R.drawable.ic_profile
   //     )
}

@Preview("Bottom Bar")
@Composable
private fun PreviewBottomBar() {
  // GrizzlyPreview(useGradient = false) {
  //     GrizzlyBottomNavigationBar(currentRoute = null) {}
  // }
}