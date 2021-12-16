package com.chainslarp.app.application.crashreporting

import com.crashlytics.android.Crashlytics
import com.minikorp.grove.Grove

/**
 * Interface that a crash reporting tool should provide.
 */
interface CrashReportingManager {
    /**
     * Set custom properties for crash reports.
     */
    fun setProperties(keyMap: Map<String, Any>)

    /**
     * Sets the user id and identifier parameters for crash reports.
     */
    fun setUserData(userId: String?, email: String?, userName: String?)

    /**
     * Logs an exception.
     */
    fun logException(throwable: Throwable?)

    /**
     * Logs a custom message associated to the current crash reporting session.
     */
    fun log(msg: String)
}

/**
 * Crashlytics implementation of [CrashReportingManager].
 */
class CrashReportingManagerImpl : CrashReportingManager {
    override fun setProperties(keyMap: Map<String, Any>) {
        keyMap.forEach { (key, any) ->
            when (any) {
                is Float -> Crashlytics.setFloat(key, any)
                is Boolean -> Crashlytics.setBool(key, any)
                is Double -> Crashlytics.setDouble(key, any)
                is String -> Crashlytics.setString(key, any)
                is Int -> Crashlytics.setInt(key, any)
                else -> Grove.e { "The value $any with key $key is not an accepted value for Crashlytics" }
            }
        }
    }

    override fun setUserData(userId: String?, email: String?, userName: String?) {
        userId?.let { Crashlytics.setUserIdentifier(userId) }
        email?.let { Crashlytics.setUserEmail(it) }
        userName?.let { Crashlytics.setUserName(it) }
    }

    override fun logException(throwable: Throwable?) {
        Crashlytics.logException(throwable)
    }

    override fun log(msg: String) {
        Crashlytics.log(msg)
    }
}