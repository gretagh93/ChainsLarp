package com.chainslarp.app.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mini.Dispatcher
import mini.android.FluxFragment
import org.jetbrains.annotations.TestOnly
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

abstract class BaseFragment : FluxFragment(), KodeinAware {

    /**
     * Container activity [Kodein].
     * Since we are single activity this should always hold true.
     */
    override val kodein: Kodein
        get() = testKodein ?: (requireActivity() as KodeinAware).kodein

    private var testKodein: Kodein? = null

    val dispatcher: Dispatcher by instance<Dispatcher>()

    /**
     * Set the dependency injector, only for testing.
     */
    @TestOnly
    fun setTestKodein(kodein: Kodein) {
        testKodein = kodein
    }

    @LayoutRes
    open val layout: Int = 0

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(inflater.context).inflate(layout, container, false)
    }

    fun Dispatcher.dispatchOnUi(action: Any, onComplete: suspend () -> Unit = {}): Job {
        return lifecycleScope.launch {
            try {
                this@dispatchOnUi.dispatch(action)
            } finally {
                onComplete()
            }
        }
    }
}