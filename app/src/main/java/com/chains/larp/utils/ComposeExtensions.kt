package com.chains.larp.utils

import androidx.compose.runtime.*
import com.chains.larp.app.AppState
import com.minikorp.duo.Action
import com.minikorp.duo.Store
import com.minikorp.duo.select
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map

@Composable
fun <T : Any> Store<T>.observeAsState(): State<T> {
    return flow.collectAsState()
}

@Composable
fun <T : Any, R> Store<T>.observeAsState(selector: (state: T) -> R): State<R> {
    return flow.select { selector(it) }.collectAsState(selector(state))
}

val StoreComposition: ProvidableCompositionLocal<Store<*>> =
    compositionLocalOf { error("Missing Store!") }

@Composable
fun <S : Store<*>> withStore(store: S, content: @Composable () -> Unit) {
    CompositionLocalProvider(StoreComposition provides store) { content() }
}

@Composable
fun dispatch(action: Action): Job {
    return StoreComposition.current.offer(action)
}

//Auto Generate this

@Suppress("UNCHECKED_CAST")
@Composable
private fun appStoreComposition() = StoreComposition.current as Store<AppState>

@Composable
fun useStore(): Store<AppState> {
    return appStoreComposition()
}

@Composable
fun <T> useSelector(selector: (AppState) -> T): State<T> {
    val store = StoreComposition.current as Store<AppState>
    return store.flow.map { selector(it) }.collectAsState(selector(store.state))
}

class StateSelector<S : Any, T>(
    val keys: Array<(S) -> Any?>,
    val selector: (args: Array<Any?>) -> T
) {

    fun evaluate(store: Store<S>): T {
        val keys = Array(keys.size) { i -> keys[i](store.state) }
        return selector(keys)
    }
}

fun <T, P1> createAppStateSelector(
    p1: (AppState) -> P1,
    selector: (P1) -> T
): StateSelector<AppState, T> {
    return StateSelector(arrayOf(p1)) { args -> selector(args[0] as P1) }
}

fun <T, P1, P2> createAppStateSelector(
    p1: (AppState) -> P1,
    p2: (AppState) -> P2,
    selector: (P1, P2) -> T
): StateSelector<AppState, T> {
    return StateSelector(arrayOf(p1, p2)) { args -> selector(args[0] as P1, args[1] as P2) }
}

@Composable
fun <S : Any, T> rememberSelector(selector: StateSelector<S, T>): T {
    @Suppress("UNCHECKED_CAST")
    val store = StoreComposition.current as Store<S>

    val initialValue = remember(selector) {
        selector.evaluate(store)
    }

    return produceState(initialValue = initialValue, selector, producer = {
        val keys = Array(selector.keys.size) { i -> selector.keys[i](store.state) }
        //Skip first once since it will always match the initial value
        store.flow.drop(1).collect { state ->
            var changed = false
            selector.keys.forEachIndexed { i, fn ->
                val newKey = fn(state)
                changed = changed || keys[i] != newKey
                keys[i] = newKey
            }
            if (changed) {
                val newValue = selector.selector(keys)
                value = newValue
            }
        }
    }).value
}






