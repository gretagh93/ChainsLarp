package com.chains.larp.ui.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chains.larp.R
import com.chains.larp.domain.models.Character
import com.chains.larp.domain.models.CharacterFields
import com.chains.larp.domain.models.Quest
import com.chains.larp.domain.models.QuestFields
import com.chains.larp.domain.nfc.*
import com.chains.larp.ui.components.*
import com.chains.larp.ui.theme.BlueConsole
import com.chains.larp.ui.theme.ChainsBackgroundGradient
import com.chains.larp.ui.theme.DarkBlueConsole
import com.chains.larp.ui.theme.GreenConsole
import com.chains.larp.utils.*
import com.minikorp.duo.Resource
import com.minikorp.duo.select
import com.minikorp.grove.Grove
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

val characterSelector = createAppStateSelector(p1 = { it },
    selector = { appState ->
        val userResource = appState.character.loadCharacter
        val questResource = appState.character.quests

        when {
            userResource.isSuccess && questResource.isSuccess -> {
                val character = userResource.getOrThrow()
                val quests = questResource.getResourceValue() ?: emptyList()
                val characterQuests =
                    quests.filter { it.fields.characterIdRelated.contains(character.id) }
                Resource.success(CharacterViewData(character, characterQuests))
            }
            questResource.isFailure || userResource.isFailure ->
                Resource.failure(userResource.exceptionOrNull() ?: questResource.exceptionOrNull())
            questResource.isLoading || userResource.isLoading -> Resource.loading()
            questResource.isEmpty || userResource.isEmpty -> Resource.empty()
            else -> Resource.idle()
        }
    })

val updateCharacterSelector =
    createAppStateSelector(p1 = { it.character }, selector = { state -> state.updateCharacter })

data class CharacterViewData(
    val character: Character,
    val associatedQuests: List<Quest> = emptyList()
)

@Composable
fun CharacterScreen(
    characterId: String,
    onBack: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val scope = rememberCoroutineScope()
    val store = useStore()
    val selector = rememberSelector(selector = characterSelector)
    val updateSelector = rememberSelector(selector = updateCharacterSelector)

    val onRefresh = {
        scope.launch {
            store.dispatch(LoadCharacterAction.Request(characterId))
            store.dispatch(LoadQuestsAction.Request)
        }
    }
    //Data came from NFT and it has priority over backend, so no refresh
    LaunchedEffect(characterId) {
        if (selector.getOrNull() == null)
            onRefresh()
    }

    withLocalLoadingState(!selector.isTerminate) {
        CharacterScreen(
            scaffoldState = scaffoldState,
            onRefresh = { onRefresh() },
            characterSelector = selector
        ) { character, completedQuestsIds ->
            scope.launch {
                store.dispatch(UpdateCharacterAction.Request(character, completedQuestsIds))

                store.flow
                    .select { it.character.updateCharacter }
                    .onEach { updateResource ->
                        if (updateResource.isSuccess) {
                            toast(text = "Character ${selector.getOrNull()?.character?.fields?.name}")
                            onBack()
                        } else if (updateResource.isFailure) {
                            updateResource.exceptionOrNull()?.let { error ->
                                when (error) {
                                    WrongUserIdException -> toast("The tag id doesn't match the actual character")
                                    TagNotInRangeException -> toast("The tag is not in range for writting")
                                    else -> toast("There was an error writing the character. Try again")
                                }
                            }
                        }
                    }.launchIn(this)
            }
        }
    }
}

@Composable
private fun CharacterScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onRefresh: () -> Unit = {},
    characterSelector: Resource<CharacterViewData>,
    onSaveButtonClick: (Character, Map<String, QuestFields>) -> Unit = { _, _ -> }
) {
    AppScaffold(scaffoldState = scaffoldState) {
        SwipeToRefreshPlaceholderContent(
            resource = characterSelector,
            placeholderValue = FakeData.Character.characterViewData,
            onRefresh = onRefresh
        ) {
            CharacterScreenContent(it, onSaveButtonClick)
        }

    }
}

@Composable
private fun CharacterScreenContent(
    characterViewData: CharacterViewData,
    onSaveButtonClick: (Character, Map<String, QuestFields>) -> Unit = { _, _ -> }
) {
    val character = characterViewData.character
    val editableCharacter = remember(character.id) { mutableStateOf(character) }
    val questsToUpdate = remember(character.id) { mutableStateOf(mutableMapOf<String, QuestFields>()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ChainsBackgroundGradient)
            .padding(20.dp, 40.dp)
    ) {
        item {
            Header2Text(
                text = character.fields.name,
                color = BlueConsole,
                fontSize = 32.sp,
                modifier = Modifier.withPlaceholder()
            )
            Divider(Modifier.width(120.dp), color = BlueConsole)
            Spacer(modifier = Modifier.height(4.dp))
            Divider(Modifier.width(60.dp), color = BlueConsole)
            Spacer(modifier = Modifier.height(26.dp))
            Header1Text(
                text = "Habilidades",
                color = BlueConsole,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(4.dp, BlueConsole))
                    .background(DarkBlueConsole)
            ) {
                editableCharacter.value.fields.toSkillsMaps().entries.toList()
                    .forEach { (key, value) ->
                        CharacterSkillRow(key, value, onIncreaseSkill = {
                            val updatedCharacterFields =
                                editableCharacter.value.fields.updateFields(key, value, true)
                            editableCharacter.value =
                                editableCharacter.value.copy(fields = updatedCharacterFields)
                        }, onDecreaseSkill = {
                            val updatedCharacterFields =
                                editableCharacter.value.fields.updateFields(key, value, false)
                            editableCharacter.value =
                                editableCharacter.value.copy(fields = updatedCharacterFields)
                        })
                    }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
        item {
            Header1Text(
                text = "Registro de Misiones",
                color = BlueConsole,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(4.dp, BlueConsole))
                    .background(DarkBlueConsole)
            ) {
                characterViewData.associatedQuests.forEach { quest ->
                    CharacterQuestRow(
                        quest.id,
                        quest.fields.name,
                        quest.fields.notes,
                        questsToUpdate.value[quest.id]?.completed
                        ?: characterViewData.associatedQuests.firstOrNull{ it.id == quest.id}?.fields?.completed
                        ?: false
                    ) {
                        val questValue = questsToUpdate.value[quest.id] ?: characterViewData.associatedQuests.firstOrNull{ it.id == quest.id}?.fields
                        val isCompleted = !(questValue?.completed ?: false)
                        questsToUpdate.value = questsToUpdate.value.apply { this[quest.id] = quest.fields.copy(completed = isCompleted) }
                        Grove.i { "Current quests to update: ${questsToUpdate.value}" }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = {
                    onSaveButtonClick(
                        editableCharacter.value,
                        questsToUpdate.value
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .withPlaceholder()
                    .height(52.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(4.dp, BlueConsole),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BlueConsole,
                    backgroundColor = Color.Transparent
                )
            ) {
                ButtonText("Actualizar")
            }
        }
    }
}

@Composable
private fun CharacterSkillRow(
    skillName: String,
    skillValue: Int,
    onDecreaseSkill: () -> Unit = {},
    onIncreaseSkill: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Body1Text(
            text = skillName,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1F)
                .withPlaceholder()
                .fillMaxWidth()
                .padding(end = 16.dp),
            color = BlueConsole
        )
        Row(
            Modifier
                .weight(1F)
                .padding(horizontal = 8.dp)
                .fillMaxWidth(), Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_button_minus),
                contentDescription = null,
                modifier = Modifier.clickable { onDecreaseSkill() }
            )
            Body1Text(
                text = skillValue.toString(),
                color = GreenConsole,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.withPlaceholder()
            )
            Image(
                painter = painterResource(id = R.drawable.ic_button_plus),
                contentDescription = null,
                modifier = Modifier.clickable { onIncreaseSkill() }
            )
        }
    }
}

@Composable
private fun CharacterQuestRow(
    questId: String,
    questName: String,
    questDescription: String,
    completed: Boolean,
    onMarkHasCompleteClick: () -> Unit
) {
    val isSelected = remember(questId) { mutableStateOf(completed) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(4F, true), verticalArrangement = Arrangement.Center) {
            Body1Text(
                text = questName,
                fontSize = 16.sp,
                color = BlueConsole,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.withPlaceholder()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Body1Text(
                text = questDescription,
                fontSize = 14.sp,
                color = BlueConsole,
                modifier = Modifier.withPlaceholder()
            )
        }
        Box(
            Modifier
                .weight(1F, true)
                .padding(start = 8.dp), Alignment.Center
        ) {
            Box(
                Modifier
                    .size(28.dp)
                    .withPlaceholder()
                    .clickable { isSelected.value = !isSelected.value; onMarkHasCompleteClick() }
                    .then(
                        if (isSelected.value) Modifier.background(GreenConsole)
                        else Modifier
                            .border(BorderStroke(2.dp, BlueConsole))
                            .background(Color.Transparent)
                    ), Alignment.Center) {
                if (isSelected.value) {
                    Icon(
                        imageVector = Icons.Sharp.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.clickable {
                            isSelected.value = !isSelected.value
                            onMarkHasCompleteClick()
                        }
                    )
                }
            }
        }
    }
}

private fun CharacterFields.toSkillsMaps() = mapOf<String, Int>(
    "Ciudadana" to ciudadana,
    "Inocente" to inocente,
    "Sabia" to sabia,
    "Gobernante" to gobernante,
    "Heroína" to heroica,
    "Cuidadora" to cuidadora,
    "Creadora" to creadora,
    "Exploradora" to exploradora,
    "Bufón" to bufon,
    "Rebelde" to rebelde,
    "Amante" to amante,
    "Maga" to maga,
)

private fun CharacterFields.updateFields(
    key: String,
    value: Int,
    increase: Boolean
): CharacterFields {
    val newValue = if (increase) value.plus(1) else value.minus(1)
    return when (key) {
        "Ciudadana" -> this.copy(ciudadana = newValue)
        "Inocente" -> this.copy(inocente = newValue)
        "Sabia" -> this.copy(sabia = newValue)
        "Gobernante" -> this.copy(gobernante = newValue)
        "Heroína" -> this.copy(heroica = newValue)
        "Cuidadora" -> this.copy(cuidadora = newValue)
        "Creadora" -> this.copy(creadora = newValue)
        "Exploradora" -> this.copy(exploradora = newValue)
        "Bufón" -> this.copy(bufon = newValue)
        "Rebelde" -> this.copy(rebelde = newValue)
        "Amante" -> this.copy(amante = newValue)
        "Maga" -> this.copy(maga = newValue)
        else -> {
            Grove.e { "Wrong field: $key" }
            this
        }
    }
}

@Preview
@Composable
private fun CharacterScreenPreview() {
    withLocalLoadingState(false) {
        CharacterScreen(
            scaffoldState = rememberScaffoldState(),
            characterSelector = Resource.success(FakeData.Character.characterViewData)
        )
    }
}

@Preview
@Composable
private fun CharacterLoadingScreenPreview() {
    withLocalLoadingState(true) {
        CharacterScreen(
            scaffoldState = rememberScaffoldState(),
            characterSelector = Resource.success(FakeData.Character.characterViewData)
        )
    }
}

