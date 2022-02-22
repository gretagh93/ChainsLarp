package com.chains.larp.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.minikorp.duo.Resource

@Composable
fun <T> SwipeToRefreshPlaceholderContent(
    modifier: Modifier = Modifier,
    resource: Resource<T>,
    placeholderValue: T,
    onRefresh: () -> Unit = { },
    isRefreshing: Boolean = !resource.isTerminate,
    failureContent: @Composable (Throwable?) -> Unit = {},
    successContent: @Composable (T) -> Unit
) {
    withLocalLoadingState(!resource.isTerminate) {
        Box(modifier) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { onRefresh() }
            ) {
                when (resource.state) {
                    Resource.State.FAILURE -> failureContent(resource.exceptionOrNull())
                    Resource.State.SUCCESS -> successContent(resource.getOrThrow())
                    else -> successContent(placeholderValue)
                }
            }
        }
    }
}