package com.chains.larp.app

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.chains.larp.ui.views.AdminScreen
import com.chains.larp.ui.views.CharacterScreen
import com.chains.larp.ui.views.ScanningScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

const val DEFAULT_NAVIGATION_ANIMATION_DURATION = 300

/**
 * Destinations used in the ([GrizzlyGritApp]).
 */
object MainDestinations {
    const val DEBUG_ROUTE = "debug_route"
    const val LOGIN_ROUTE = "login_route"
    const val ADMIN_ROUTE = "admin_route"
    const val SCANNING_ROUTE = "scan_route"
    const val CHARACTER_ROUTE = "character_route"
}

@Composable
fun ChainsNavGraph(
    navController: NavHostController = rememberAnimatedNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    startDestination: String = MainDestinations.SCANNING_ROUTE
) {
    val actions = remember(navController) { MainActions(navController) }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { initial, target -> fadeIn(animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION)) },
        exitTransition = { initial, target -> fadeOut(animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION)) }
    ) {
        //Login disabled for now
        //  composable(MainDestinations.LOGIN_ROUTE) {
        //      LoginScreen(actions.navigateToScanning, scaffoldState)
        //  }
        composable(MainDestinations.SCANNING_ROUTE) {
            ScanningScreen(actions.navigateToCharacter, actions.navigateToAdmin, scaffoldState)
        }

        composable(MainDestinations.ADMIN_ROUTE) {
            AdminScreen(scaffoldState)
        }
        composable(
            route = "${MainDestinations.CHARACTER_ROUTE}?characterId={characterId}&tagId={tagId}",
            arguments = listOf(
                navArgument("characterId") { type = NavType.StringType },
                navArgument("tagId") { type = NavType.StringType }
            )) {
            val characterId = it.arguments!!.getString("characterId")!!
            val tagId = it.arguments!!.getString("tagId")!!
            CharacterScreen(characterId, tagId, actions.navigateToScanning, scaffoldState)
        }
        //Debug
        composable(MainDestinations.DEBUG_ROUTE) { DebugScreen() }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val navigateToLogin: () -> Unit = {
        navController.navigate(MainDestinations.LOGIN_ROUTE)
    }

    val navigateToAdmin: () -> Unit = {
        navController.navigate(MainDestinations.ADMIN_ROUTE)
    }

    val navigateToCharacter: (String, String) -> Unit = { characterId, tagId ->
        navController.navigate("${MainDestinations.CHARACTER_ROUTE}?characterId=$characterId&tagId=$tagId")
    }

    val navigateToScanning: () -> Unit = {
        navController.navigate(MainDestinations.SCANNING_ROUTE)
    }
}