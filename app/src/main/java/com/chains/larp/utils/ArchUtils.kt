package com.chains.larp.utils

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.chains.larp.app.appContext
import com.minikorp.duo.Resource
import com.minikorp.duo.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformWhile
import java.lang.IllegalArgumentException

/**
 * Returns a flow that contains the elements of the given flow until the element that matches
 * the [predicate].
 *
 * It behaves the same way as RxJava's takeUntil.
 */
fun <T> Flow<T>.takeUntil(predicate: suspend (T) -> Boolean): Flow<T> =
    transformWhile { emit(it); !predicate(it) }

val <T> Resource<T>.isTerminate get() = isSuccess || isFailure

fun <T> Resource<T>.toTask(): Task = when(state){
    Resource.State.SUCCESS -> Task.success()
    Resource.State.FAILURE -> Task.failure(exceptionOrNull())
    Resource.State.EMPTY -> Task.empty()
    Resource.State.LOADING -> Task.loading()
}

fun <T> Resource<T>.getOrThrow(): T = getOrNull()!!

fun <T> Resource<T>.getSuccessValueOrNull(): T? = if (isSuccess) value as T? else null
fun <T> Resource<T>.getResourceValue(): T? = if (isSuccess || isLoading) value as T? else null

fun <T> Task.toTypedResource(value: T?): Resource<T> = when(state){
    Resource.State.SUCCESS -> Resource.success(value!!)
    Resource.State.EMPTY -> Resource.empty()
    Resource.State.LOADING -> Resource.loading()
    else -> throw IllegalArgumentException("Tasks of failure type can't be casted to typed Resource")
}

@Composable
fun showToast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(LocalContext.current, textId, duration).show()
}

@Composable
fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(LocalContext.current, text, duration).show()
}


fun toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(appContext, text, duration).show()
}
