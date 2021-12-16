package com.chainslarp.app.application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import mini.Dispatcher
import mini.Store
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

abstract class BaseStore<S>(override val kodein: Kodein) : Store<S>(), KodeinAware {
    protected val dispatcher: Dispatcher by instance<Dispatcher>()

    val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
}