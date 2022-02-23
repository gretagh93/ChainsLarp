package com.chains.larp.app

import android.app.Application
import android.content.Context
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chains.larp.app.store.AppStore
import com.chains.larp.domain.DataModule
import com.chains.larp.domain.character.CharacterState
import com.chains.larp.utils.withStore
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.minikorp.duo.Action
import com.minikorp.duo.State
import com.minikorp.duo.TypedAction
import com.minikorp.grove.ConsoleLogTree
import com.minikorp.grove.Grove
import kotlinx.coroutines.*
import org.kodein.di.*
import org.kodein.di.compose.instance
import org.kodein.di.compose.withDI
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

/**
 * Main [Application] for the project. No needed at all for a Compose project, but it allow us to have an entry
 * point to initialize all the needed libraries and so.
 */
private var appInstance: ChainsApplication by Delegates.notNull()
val app: ChainsApplication get() = appInstance
val appContext: Context get() = app

class ChainsApplication : Application(), DIAware {

    val scope = CoroutineScope(Job())

    override val di: DI = DI.lazy {
        bind<Context>() with instance(this@ChainsApplication)
        import(AppModule.create())
        import(DataModule.create())
    }

    override fun onCreate() {
        appInstance = this
        super.onCreate()
        Grove.plant(ConsoleLogTree())

        val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Grove.e(e) { "Uncaught exception" }
            exceptionHandler?.uncaughtException(t, e)
        }

        val store: AppStore = di.direct.instance()

        scope.launch(Dispatchers.Main) {
            Grove.d { "App gonna initialize" }
            val bootstrapTime = measureTimeMillis {
                store.dispatch(BootstrapAction)
            }
            Grove.d { "App got initialized in $bootstrapTime ms" }
        }
    }
}

/**
 * Application State. Stored by [AppStore].
 */
@State
data class AppState(val character: CharacterState = CharacterState())

/**
 * Bootstrap action throw when the app passes the splash. It would suspend until all the side effects for the
 * launch of the app will be ready.
 */
@TypedAction
object BootstrapAction : Action

@Composable
fun ChainsApp() = withDI {
    val store: AppStore by instance()
    withStore(store) {
        MaterialTheme {
            ProvideWindowInsets {
                val navController = rememberAnimatedNavController()
                val scaffoldState = rememberScaffoldState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                Grove.d { "Current Route $currentRoute" }

                Scaffold(scaffoldState = scaffoldState) { innerPadding ->
                    ChainsNavGraph(
                        navController = navController,
                        scaffoldState = scaffoldState
                    )
                }
            }
        }
    }
}