package com.chains.larp.domain.character

import android.app.Activity
import android.nfc.Tag
import com.chains.larp.app.store.RootLogAction
import com.chains.larp.domain.models.QuestFields
import com.minikorp.duo.Action
import com.minikorp.duo.TypedAction

@TypedAction
data class ManageNfcReaderState(val enable: Boolean,
                                val activity: Activity) : Action

interface ReadCharacterMedalAction {
    @TypedAction
    data class Request(val medalTag: Tag?) : Action, RootLogAction
}

interface LoadCharacterAction {
    @TypedAction
    data class Request(val characterId: String) : Action, RootLogAction
}

interface UpdateCharacterAction {
    @TypedAction
    data class Request(val character: Character,
                       val updatedQuests: Map<String, QuestFields>) : Action, RootLogAction
}

interface AdminUpdateCharacterAction {
    @TypedAction
    data class Request(val characterTagInfo: CharacterTagInfo) : Action, RootLogAction

}

interface LoadQuestsAction {
    @TypedAction
    data class Request(val characterId: String) : Action, RootLogAction
}

@TypedAction
object ResetCharacterAction : Action