package com.chainslarp.app.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.chainslarp.app.application.core.app
import mini.Store
import org.kodein.di.direct
import org.kodein.di.generic.instance

inline fun <reified T : Store<*>> findStore(): T {
    return app.kodein.direct.instance()
}

inline fun <reified T : View> View.requireView(@IdRes id: Int): T {
    return findViewById(id) ?: error("View not found!")
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T) -> Unit) {
    return this.observe(owner, Observer { observer(it) })
}

fun <T> LiveData<T>.get(): T {
    return value ?: error("No value for $this")
}