package com.chains.larp.domain.nfc

import com.chains.larp.app.BootstrapAction
import com.chains.larp.app.store.BaseSaga
import com.chains.larp.domain.auth.LogoutAction
import com.chains.larp.domain.models.Character
import com.chains.larp.domain.models.Quest
import com.minikorp.duo.*
import com.minikorp.grove.Grove
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.Serializable

@State
data class CharacterState(
    val scanningMedal: Resource<CharacterTagInfo> = Resource.empty(),
    val updateCharacter: Task = Resource.empty(),
    val adminTagUpdate: Task = Resource.empty(),
    val loadCharacter: Resource<Character> = Resource.empty(),
    val quests: Resource<List<Quest>> = Resource.empty(),
    val nfcState: Resource<NfcState> = Resource.empty(),
    val currentTagRef: CharacterTagRef? = null
) : Serializable

@TypedHandler
class CharacterSaga(di: DI) : BaseSaga<CharacterState>(di) {
    private val controller: CharacterController by instance()

    @TypedHandler.Fun
    suspend fun bootstrap(context: DispatchContext<CharacterState>, action: BootstrapAction) {
        controller.loadQuests()
            .onSuccess { context.reduce { state -> state.copy(quests = Resource.success(it)) } }
            .onFailure { context.reduce { state -> state.copy(quests = Resource.failure(it)) } }
        //Test value
        //context.reduce { it.copy(scanningMedal = Resource.success(TestData.Character.satanas)) }
    }

    @TypedHandler.Fun
    suspend fun handleReadCharacterMedal(
        context: DispatchContext<CharacterState>,
        action: ReadCharacterMedalAction.Request
    ) {
        if (action.medalTag == null) return //TODO control error

        val nfcTag = NfcTag(action.medalTag)
        context.reduce { it.copy(scanningMedal = Resource.loading()) }
        controller.readCharacterTag(nfcTag)
            .onSuccess {
                context.reduce { state ->
                    state.copy(
                        scanningMedal = Resource.success(it),
                        currentTagRef = it.characterId to nfcTag
                    )
                }
            }
            .onFailure { context.reduce { state -> state.copy(scanningMedal = Resource.failure(it)) } }
    }

    @TypedHandler.Fun
    suspend fun handleLoadCharacter(
        context: DispatchContext<CharacterState>,
        action: LoadCharacterAction.Request
    ) {
        context.reduce { it.copy(loadCharacter = Resource.loading()) }
        controller.loadCharacterData(action.characterId)
            .onSuccess { context.reduce { state -> state.copy(loadCharacter = Resource.success(it)) } }
            .onFailure { context.reduce { state -> state.copy(loadCharacter = Resource.failure(it)) } }
    }

    @TypedHandler.Fun
    suspend fun handleUpdateCharacter(
        context: DispatchContext<CharacterState>,
        action: UpdateCharacterAction.Request
    ) {
        val currentReadingTag = context.state.currentTagRef
        if (currentReadingTag != null) {
            val (characterTagId, tag) = currentReadingTag
            Grove.i { "Current id: ${action.character.id}, current tag: $currentReadingTag" }
            if (characterTagId != action.character.fields.tagId.toString()) {
                context.reduce { state ->
                    state.copy(updateCharacter = Resource.failure(WrongUserIdException))
                }
            } else {
                context.reduce { it.copy(updateCharacter = Resource.loading()) }
                controller.updateCharacterData(
                    action.character.id,
                    action.character.fields,
                    action.updatedQuests,
                    tag
                )
                    .onSuccess { context.reduce { state -> state.copy(updateCharacter = Resource.success(), scanningMedal = Resource.idle(), loadCharacter = Resource.idle(), currentTagRef = null, adminTagUpdate = Resource.empty()) } }
                    .onFailure { context.reduce { state -> state.copy(updateCharacter = Resource.failure(it)) } }
            }
        } else {
            context.reduce { state -> state.copy(updateCharacter = Resource.failure(TagNotInRangeException)) }
        }
    }

    @TypedHandler.Fun
    suspend fun handleAdminUpdateCharacter(
        context: DispatchContext<CharacterState>,
        action: AdminUpdateCharacterAction.Request
    ) {
        val currentReadingTag = context.state.currentTagRef
        if (currentReadingTag != null) {
            val (characterTagId, tag) = currentReadingTag
            Grove.i { "Current info: ${action.characterTagInfo}, current tag: $currentReadingTag" }

            context.reduce { it.copy(adminTagUpdate = Resource.loading()) }
            controller.overrideCharacterTag(action.characterTagInfo, tag)
                .onSuccess { context.reduce { state -> state.copy(adminTagUpdate = Resource.success()) } }
                .onFailure { context.reduce { state -> state.copy(adminTagUpdate = Resource.failure(it)) } }

        } else {
            context.reduce { state -> state.copy(adminTagUpdate = Resource.failure(TagNotInRangeException)) }
        }
    }

    @TypedHandler.Fun
    suspend fun handleLoadQuests(
        context: DispatchContext<CharacterState>,
        action: LoadQuestsAction.Request
    ) {
        context.reduce { it.copy(quests = Resource.loading()) }
        controller.loadQuests()
            .onSuccess { context.reduce { state -> state.copy(quests = Resource.success(it)) } }
            .onFailure { context.reduce { state -> state.copy(quests = Resource.failure(it)) } }
    }

    @TypedHandler.Fun
    suspend fun handleNfcState(
        context: DispatchContext<CharacterState>,
        action: ManageNfcReaderState
    ) {
        context.reduce { it.copy(nfcState = Resource.loading()) }
        controller.enableCharacterNfcReader(action.enable, action.activity)
            .onSuccess { state -> context.reduce { it.copy(nfcState = Resource.success(state)) } }
            .onFailure { error -> context.reduce { it.copy(nfcState = Resource.failure(error)) } }
    }

    @TypedHandler.Fun
    suspend fun logout(context: DispatchContext<CharacterState>, action: LogoutAction) {
        context.reduce { CharacterState() }
    }

    @TypedHandler.Root
    override suspend fun handle(context: DispatchContext<CharacterState>, action: Action) {
        handleTyped(context, action)
    }
}

/**
 * NFC tags exceptions.
 */
object WrongUserIdException : Exception()
object TagNotInRangeException : Exception()