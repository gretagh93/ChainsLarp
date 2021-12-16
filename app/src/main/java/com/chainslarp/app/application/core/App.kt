package com.chainslarp.app.application.core

import android.app.Application
import android.widget.Toast
import com.minikorp.grove.BuildConfig
import com.minikorp.grove.ConsoleLogTree
import com.minikorp.grove.Grove
import com.chainslarp.app.application.crashreporting.CrashReportingModule
import com.chainslarp.app.application.network.airtable.AirtableModule
import com.chainslarp.app.application.network.airtable.DataModule
import com.chainslarp.app.application.network.auth.AuthModule
import com.chainslarp.app.application.network.firebase.FirebaseModule
import mini.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

private lateinit var _app: App
val app get() = _app

class App : Application(), KodeinAware {
    companion object {
        const val KODEIN_APP_TAG = "AppTag"
    }

    private val dispatcher: Dispatcher by instance<Dispatcher>()
    private val stores: Set<Store<*>> by instance<Set<Store<*>>>()

    override fun onCreate() {
        super.onCreate()
        _app = this

        @Suppress("ConstantConditionIf")
        if (BuildConfig.DEBUG) {
            Grove.plant(ConsoleLogTree())
        }

        Grove.plant(ConsoleLogTree())
        dispatcher.addMiddleware(
            LoggerMiddleware(
                stores = stores,
                logger = { priority, tag, msg -> Grove.tag(tag).log(priority) { msg } },
                diffFunction = ObjectDiff::computeDiff
            )
        )

        val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Grove.e(e) { "Uncaught exception" }
            exceptionHandler?.uncaughtException(t, e)
        }
        MiniGen.subscribe(dispatcher, stores.toList())
    }

    /**
     * Application wide [Kodein]. For Controllers / Repositories / Interactors.
     */
    override val kodein: Kodein by Kodein.lazy {
        import(AppModule.create())
        import(FirebaseModule.create())
        import(DataModule.create())
        import(CrashReportingModule.create())
        import(AuthModule.create())
        import(AirtableModule.create())
    }
}

fun toast(what: Any?) {
    Toast.makeText(app, what.toString(), Toast.LENGTH_SHORT).show()
}