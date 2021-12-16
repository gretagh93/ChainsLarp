package com.chainslarp.app.application.network.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import java.nio.channels.ClosedChannelException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Coroutine support to Firebase Task interface
 *
 * This method is not intended to be used in your code. You should probably use [await] since it's
 * more idiomatic.
 *
 * The implementation is pretty simple. It justs use a [suspendCoroutine] to encapsulate the
 * Firebase [com.google.android.gms.tasks.OnCompleteListener] interface.
 *
 */
private suspend fun <T> awaitTask(task: Task<T>): T =
    suspendCancellableCoroutine { continuation ->
        task.addOnSuccessListener { continuation.resume(it) }
            .addOnFailureListener { continuation.resumeWithException(it) }
            .addOnCanceledListener { continuation.cancel() }
    }

/**
 * Coroutine support to Firebase Task interface
 *
 * This extension function allows you to interact with a Firebase
 * [com.google.android.gms.tasks.Task] using the `await()` method instead of the standard listeners.
 *
 * There is a sample code below comparing the two approaches. Assuming that
 * `auth` variable has the value returned from `FirebaseAuth.getInstance()`
 * method call then your code can be something like:
 *
 * ```
 * auth.getUserByEmail(email)
 *   .addOnSuccessListener { user -> println(user) }
 *   .addOnFailureListener { exception -> println(exception) }
 * ```
 *
 * When using the coroutine approach, it should be more like this:
 *
 * ```
 * try {
 *   val user = auth.getUserByEmail(email).await()
 *   println(user)
 * } catch (exception: Exception) {
 *   println(exception)
 * }
 * ```
 *
 * @param T The type of the value been returned
 * @throws Exception Thrown in case of network error or other reasons described in the Firebase docs
 * @return The value returned by the Firebase success callback
 */
suspend fun <T> Task<T>.await(): T = awaitTask(this)

/**
 * Wrapper method over [await] to return a typed [Pair] of the Firebase API [Task] together with a
 * result [mini.Task] to reduce boilerplate over controllers methods.
 */
suspend fun <T> Task<T>.awaitForTask(): Pair<T?, mini.Task> {
    val result = runCatching {
        awaitTask(this)
    }
    return result.getOrNull() to
        if (result.isSuccess) mini.Task.success() else mini.Task.failure(result.exceptionOrNull())
}

/**
 * Creates a [Flow] that will emit [SnapshotListenerEvent] event.
 * Flow calls works as cold observables, they are create and only start working after [Flow.collect]
 * is called.
 * [Flow] can't be cancelled, the only way to dispose them is to cancel the [Job] where the
 * [Flow.collect] was called.
 *
 * This specific case is using a [Channel] as a flow to be able to use the [Channel.invokeOnClose]
 * method to remove the listener when this [Flow] is canceled.
 */
inline fun <reified FirebaseModel, Model> Query.snapshotChangesFlow(
    includeMetadata: Boolean = true,
    crossinline documentMapping: (DocumentSnapshot) -> FirebaseModel? = { it.toObject(FirebaseModel::class.java) },
    crossinline mappingFn: (FirebaseModel, String) -> Model
): Flow<SnapshotListenerEvent<Model>> {

    return callbackFlow {
        val metadataChanges =
            if (includeMetadata) MetadataChanges.INCLUDE else MetadataChanges.EXCLUDE
        val listener =
            this@snapshotChangesFlow.addSnapshotListener(metadataChanges) { querySnapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener // TODO do we want to close and return?
                }

                querySnapshot?.let {
                    if (!it.isEmpty) {
                        channel.offer(
                            SnapshotListenerEvent.fromSnapshot(
                                it,
                                mappingFn,
                                documentMapping
                            )
                        )
                    } else {
                        channel.offer(SnapshotListenerEvent.IsEmpty<Model>())
                    }

                    if (includeMetadata && !it.metadata.hasPendingWrites()) {
                        channel.offer(SnapshotListenerEvent.HasNotPendingWrites<Model>())
                    }
                }
            }
        awaitClose { listener.remove() }
    }
}

/**
 * Creates a [Flow] to filter [SnapshotListenerEvent.FirestoreDocumentChanges]
 * coming from [SnapshotListenerEvent] events to produce a list of [Model] typed values.
 * Flow calls works as cold observables, they are create and only start working after
 * [Flow.collect] is called.
 *
 * [Flow] can't be cancelled, the only way to dispose them is to cancel the [Job] where the
 * [Flow.collect] was called.
 *
 * This specific case is using a [Channel] as a flow to be able to use the [Channel.invokeOnClose]
 * method to remove the listener when this [Flow] is canceled.
 */
inline fun <reified FirebaseModel, Model> Query.snapshotFlow(
    includeMetadata: Boolean = true,
    crossinline mappingFn: (FirebaseModel, String) -> Model
): Flow<List<Model>> {
    val modelFlow: Flow<SnapshotListenerEvent.FirestoreDocumentChanges<Model>> =
        snapshotChangesFlow(
            mappingFn = mappingFn,
            includeMetadata = includeMetadata
        ).filterIsInstance()
    return modelFlow.map { it.changes.map { change -> change.document.model } }
}

/**
 * Creates a [Flow] that will emit [SnapshotListenerEvent] events.
 * Flow calls work as cold observables, they are created and only start working after [Flow.collect]
 * is called.
 *
 * [Flow] can't be cancelled , the only way to dispose them is to cancel the [Job] where the
 * [Flow.collect] was called.
 *
 * This specific case is using a [Channel] as a flow to be able to use the [Channel.invokeOnClose]
 * method to remove the listener when this [Flow] is canceled.
 */
inline fun <reified FirebaseModel, Model> DocumentReference.snapshotFlow(
    crossinline documentMapping: (DocumentSnapshot) -> FirebaseModel? = { it.toObject(FirebaseModel::class.java) },
    crossinline mappingFn: (FirebaseModel, String) -> Model,
    includeMetadata: Boolean = true
): Flow<SnapshotListenerEvent<Model>> =
    callbackFlow {
        val metadataChanges =
            if (includeMetadata) MetadataChanges.INCLUDE else MetadataChanges.EXCLUDE
        val listener =
            this@snapshotFlow.addSnapshotListener(metadataChanges) { documentSnapshot, error ->
                if (error != null) {
                    channel.close(error)
                    return@addSnapshotListener // TODO do we want to close and return?
                }

                documentSnapshot?.let {
                    if (it.exists()) {
                        channel.offer(
                            SnapshotListenerEvent.fromSnapshot(
                                it,
                                mappingFn,
                                documentMapping
                            )
                        )
                    } else {
                        channel.offer(SnapshotListenerEvent.IsEmpty<Model>())
                    }

                    if (includeMetadata && !it.metadata.hasPendingWrites()) {
                        channel.offer(SnapshotListenerEvent.HasNotPendingWrites<Model>())
                    }

                }
            }
        awaitClose { listener.remove() }
    }

/**
 * Creates a [Channel] that offers all the changes inside the current [DocumentReference] and map
 * it to the provided field given by [fieldName]. The [ListenerRegistration] will be removed when
 * the channel closes or an error happens.
 *
 * This method should not be used outside of [onFieldUpdated] and [onFieldUpdatedOrNull].
 * For cases where we would like to receive all the changes of a specific field we should use
 * [snapshotFlow] with a mapping clause.
 */
inline fun <reified T> DocumentReference.documentFieldChannel(fieldName: String): ReceiveChannel<T?> {
    val channel = Channel<T?>()
    val listener: ListenerRegistration = this.addSnapshotListener { snapshot, e ->
        e?.let { channel.close(e) }

        val value = snapshot?.get(fieldName, T::class.java)
        channel.offer(value)
    }
    channel.invokeOnClose { listener.remove() }
    return channel
}

/**
 * Creates a [CompletableDeferred] that will wait until the given [fieldName] changes to a new
 * nullable value.
 *
 * If an error happens while waiting for the result, the [CompletableDeferred] will complete
 * with a null value.
 */
suspend inline fun <reified T> DocumentReference.onFieldUpdatedOrNull(
    fieldName: String,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): CompletableDeferred<T?> {

    val deferred = CompletableDeferred<T?>()
    val docChannel: ReceiveChannel<T?> = documentFieldChannel(fieldName)
    deferred.invokeOnCompletion { docChannel.cancel() }

    withContext(coroutineDispatcher) {
        while (!docChannel.isClosedForReceive) {
            val value = docChannel.receiveOrClosed()
            if (value.isClosed) {
                deferred.complete(null)
            } else {
                deferred.complete(value.value)
            }
        }
    }
    return deferred
}

/**
 * Creates a [CompletableDeferred] with a timeout that will wait until the given [fieldName]
 * changes to a new nullable value or the timeout expires.
 *
 * If an error happens while waiting for the result, the [CompletableDeferred] will complete
 * exceptionally with the given error.
 *
 * If the timeout expires, a [TimeoutCancellationException] will be throw.
 */
suspend inline fun <reified T> DocumentReference.onFieldUpdatedOrNull(
    fieldName: String,
    timeOutMillis: Long
): T? =
    withTimeout(timeOutMillis) { return@withTimeout onFieldUpdatedOrNull<T>(fieldName).await() }

/**
 * Creates a [CompletableDeferred] that will wait until the given [fieldName] changes to a new
 * non nullable value.
 *
 * If an error happens while waiting for the result, the [CompletableDeferred] will complete
 * exceptionally with the given error.
 */
suspend inline fun <reified T> DocumentReference.onFieldUpdated(
    fieldName: String,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): CompletableDeferred<T> {

    val deferred = CompletableDeferred<T>()
    val docChannel = documentFieldChannel<T?>(fieldName)
    deferred.invokeOnCompletion { docChannel.cancel() }

    withContext(coroutineDispatcher) {
        while (!docChannel.isClosedForReceive) {
            val value = docChannel.receiveOrClosed()
            if (value.isClosed) {
                deferred.completeExceptionally(value.closeCause ?: ClosedChannelException())
            } else {
                value.value?.let { deferred.complete(it) }
            }
        }
    }

    return deferred
}

/**
 * Creates a [CompletableDeferred] with a timeout that will wait until the given [fieldName]
 * changes to a new non nullable value or the timeout expires.
 *
 * If an error happens while waiting for the result, the [CompletableDeferred] will complete
 * exceptionally with the given error.
 *
 * If the timeout expires, a [TimeoutCancellationException] will be throw.
 */
suspend inline fun <reified T> DocumentReference.onFieldUpdated(
    fieldName: String,
    timeOutMillis: Long
): T =
    withTimeout(timeOutMillis) { return@withTimeout onFieldUpdated<T>(fieldName).await() }
