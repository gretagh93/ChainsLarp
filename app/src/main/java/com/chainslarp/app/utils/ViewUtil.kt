package com.chainslarp.app.utils

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment

inline fun <T : View> onViews(vararg views: T, crossinline fn: (T) -> Unit) {
    //contract { callsInPlace(fn, InvocationKind.EXACTLY_ONCE) }
    for (view in views) {
        fn(view)
    }
}

fun makeSelector(@ColorInt baseColor: Int, @ColorInt alternativeColor: Int): StateListDrawable {
    val res = StateListDrawable()
    res.setExitFadeDuration(333)
    res.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(alternativeColor))
    res.addState(intArrayOf(android.R.attr.state_selected), ColorDrawable(alternativeColor))
    res.addState(intArrayOf(), ColorDrawable(baseColor))
    return res
}

inline fun <reified T> Fragment.argument(key: String): Lazy<T> {
    return lazy {
        arguments?.get(key) as T
    }
}

