package com.chainslarp.app.application.crashreporting

import mini.Action
import mini.SilentAction

/**
 * Action used to log handled actions across the application code.
 */
@Action
data class LogHandledCrashAction(val throwable: Throwable?) : SilentAction

/**
 * Action used to log messages in the crash reporting tool.
 */
@Action
data class LogCrashReportingMessageAction(val message: String) : SilentAction

/**
 * Generic action used to set the user identifiers on the crash reporting service.
 * Also remember that every class that implements this action MUST also inherit from [BaseAction].
 */
@Action
interface AddCrashReportingUserDataAction {
    val userIdentifier: String?
    val userEmail: String?
    val userName: String?
}

/**
 * Generic action to add a key-value map to the current crash reporting instance.
 */
@Action
interface AddCrashReportingKeysAction : SilentAction {
    val keyMap: Map<String, Any>?
}