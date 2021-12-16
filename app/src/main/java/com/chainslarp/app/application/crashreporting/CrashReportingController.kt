package com.chainslarp.app.application.crashreporting

/**
 * Controller interface that a class must match in order to provide crash reporting functionality.
 */
interface CrashReportingController {

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

@SuppressWarnings("UndocumentedPublicClass")
class CrashReportingControllerImpl(private val manager: CrashReportingManager) :
    CrashReportingController {
    override fun setProperties(keyMap: Map<String, Any>) {
        manager.setProperties(keyMap)
    }

    override fun setUserData(userId: String?, email: String?, userName: String?) {
        manager.setUserData(userId, email, userName)
    }

    override fun logException(throwable: Throwable?) {
        manager.logException(throwable)
    }

    override fun log(msg: String) {
        manager.log(msg)
    }
}