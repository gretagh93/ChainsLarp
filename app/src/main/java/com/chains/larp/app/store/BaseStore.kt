package com.chains.larp.app.store

import com.chains.larp.app.AppState
import com.minikorp.duo.ActionLogConfig
import com.minikorp.duo.CustomLogAction
import com.minikorp.duo.Saga
import com.minikorp.duo.Store
import org.kodein.di.DI
import org.kodein.di.DIAware

/**
 * Alias for the application state.
 */
typealias AppStore = Store<AppState>

/**
 * Base [Saga] implementation which implements [DIAware] for all the dependency injection.
 */
abstract class BaseSaga<T : Any>(override val di: DI) : Saga<T>, DIAware

/**
 * Base [CustomLogAction] to properly list triggered nested actions.
 */
interface RootLogAction : CustomLogAction {
    override val logConfig: ActionLogConfig
        get() = ActionLogConfig(effectAction = true)
}