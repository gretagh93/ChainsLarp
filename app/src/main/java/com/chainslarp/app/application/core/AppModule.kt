package com.chainslarp.app.application.core

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.chainslarp.app.application.core.App.Companion.KODEIN_APP_TAG
import com.chainslarp.app.utils.KodeinViewModelFactory
import mini.Dispatcher
import mini.MiniGen
import mini.Store
import org.kodein.di.Kodein
import org.kodein.di.bindings.NoArgSimpleBindingKodein
import org.kodein.di.direct
import org.kodein.di.generic.*

object AppModule {
    fun create() = Kodein.Module("AppModule") {
        bind<Dispatcher>() with singleton { Dispatcher(MiniGen.actionTypes) }
        bind<App>(KODEIN_APP_TAG) with singleton { app }
        bind<Context>() with singleton { app }

        bind<ViewModelProvider.Factory>() with singleton {
            KodeinViewModelFactory(kodein.direct)
        }

        bind() from setBinding<Store<*>>()
    }
}

inline fun <reified T : Store<*>> Kodein.Builder.bindStore(
    noinline creator: NoArgSimpleBindingKodein<*>.() -> T
) {
    bind<T>() with singleton(creator = creator)
    bind<Store<*>>().inSet() with singleton { instance<T>() }
}