package com.chainslarp.app.application.crashreporting

import com.chainslarp.app.application.BaseStore
import com.chainslarp.app.application.core.bindStore
import mini.Reducer
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * Crash reporting state. Empty since it's mandatory.
 */
class CrashReportingState

/**
 * Store that handles reporting data to the crash reporting service.
 */
@Suppress("UndocumentedPublicFunction")
class CrashReportingStore(kodein: Kodein) : BaseStore<CrashReportingState>(kodein) {
    private val controller: CrashReportingController by instance<CrashReportingController>()

    @Reducer
    fun onLogHandledCrash(action: LogHandledCrashAction) {
        controller.logException(action.throwable)
    }

    @Reducer
    fun onLogMessage(action: LogCrashReportingMessageAction) {
        controller.log(action.message)
    }

    @Reducer
    fun onIncludeCrashReportingUserData(action: AddCrashReportingUserDataAction) {
        controller.setUserData(
            userId = action.userIdentifier,
            email = action.userEmail,
            userName = action.userName
        )
    }

    @Reducer
    fun onAddCrashReportingKeys(action: AddCrashReportingKeysAction) {
        action.keyMap?.let { controller.setProperties(it) }
    }
}

@Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction")
object CrashReportingModule {
    fun create() = Kodein.Module("CrashReportingModule") {
        bindStore { CrashReportingStore(kodein) }
        bind<CrashReportingController>() with singleton { CrashReportingControllerImpl(instance()) }
        bind<CrashReportingManager>() with singleton { CrashReportingManagerImpl() }
    }
}