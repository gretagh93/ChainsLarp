package com.chainslarp.app.application

import android.app.Application
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

/**
 * Base ViewModel which implements [KodeinAware].
 */
abstract class BaseViewModel(app: Application) : KodeinAware {
    override val kodein by closestKodein(app)
}