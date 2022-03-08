package com.chains.larp.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chains.larp.domain.character.AdminUpdateCharacterAction
import com.chains.larp.domain.character.TagNotInRangeException
import com.chains.larp.domain.character.WrongUserIdException
import com.chains.larp.domain.character.CharacterTagInfo
import com.chains.larp.ui.components.AppScaffold
import com.chains.larp.ui.components.ButtonText
import com.chains.larp.ui.components.Header2Text
import com.chains.larp.ui.components.Subtitle1Text
import com.chains.larp.ui.theme.BlueConsole
import com.chains.larp.ui.theme.ChainsBackgroundGradient
import com.chains.larp.ui.theme.GreenConsole
import com.chains.larp.utils.createAppStateSelector
import com.chains.larp.utils.toast
import com.chains.larp.utils.useStore
import com.minikorp.duo.select
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

const val MAX_IDRFID_LENGHT = 7
const val MAX_SEED_LENGHT = 4
const val MAX_GAME_LENGHT = 5

val adminUpdateCharacterSelector =
    createAppStateSelector(p1 = { it.character }, selector = { state -> state.adminTagUpdate })

@Composable
fun AdminScreen(scaffoldState: ScaffoldState = rememberScaffoldState()) {
    val scope = rememberCoroutineScope()
    val store = useStore()

    val idrfid = remember { mutableStateOf(TextFieldValue("")) }
    val gameId = remember { mutableStateOf(TextFieldValue("")) }
    val seedId = remember { mutableStateOf(TextFieldValue("")) }

    AdminScreen(
        onWriteTag = { id, seed, game ->
            scope.launch {
                store.dispatch(AdminUpdateCharacterAction.Request(CharacterTagInfo(id, seed, game)))
                store.flow
                    .select { it.character.adminTagUpdate }
                    .onEach { adminUpdateResource ->
                        if (adminUpdateResource.isSuccess) {
                            toast(text = "Tag successfully wrote")
                            idrfid.value = TextFieldValue("")
                            gameId.value = TextFieldValue("")
                            seedId.value = TextFieldValue("")
                        } else if (adminUpdateResource.isFailure) {
                            adminUpdateResource.exceptionOrNull()?.let { error ->
                                when (error) {
                                    WrongUserIdException -> toast("The tag id doesn't match the actual character")
                                    TagNotInRangeException -> toast("The tag is not in range for writting")
                                    else -> toast("There was an error writing the character. Try again")
                                }
                            }
                        }
                    }.launchIn(this)
            }
        },
        idrfid = idrfid,
        gameId = gameId,
        seedId = seedId,
        scaffoldState = scaffoldState
    )
}

@Composable
private fun AdminScreen(
    onWriteTag: (String, String, String) -> Unit,
    idrfid: MutableState<TextFieldValue>,
    gameId: MutableState<TextFieldValue>,
    seedId: MutableState<TextFieldValue>,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    AppScaffold(scaffoldState = scaffoldState) {
        AdminScreenContent(idrfid, gameId, seedId, onWriteTag)
    }
}

@Composable
private fun AdminScreenContent(
    idrfid: MutableState<TextFieldValue>,
    gameId: MutableState<TextFieldValue>,
    seedId: MutableState<TextFieldValue>,
    onWriteTag: (String, String, String) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChainsBackgroundGradient)
            .padding(20.dp, 40.dp)
    ) {
        Header2Text(
            text = "Saludos administrador molon",
            color = BlueConsole,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = idrfid.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { if (it.text.length <= MAX_IDRFID_LENGHT) idrfid.value = it },
            label = { Subtitle1Text("IDRFID", color = GreenConsole) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlueConsole,
                unfocusedBorderColor = BlueConsole,
                textColor = GreenConsole,
                backgroundColor = Black
            ),
        )
        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = seedId.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { if (it.text.length <= MAX_SEED_LENGHT) seedId.value = it },
            label = { Subtitle1Text("Seed ID", color = GreenConsole) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlueConsole,
                unfocusedBorderColor = BlueConsole,
                textColor = GreenConsole,
                backgroundColor = Black
            )
        )
        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = gameId.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { if (it.text.length <= MAX_GAME_LENGHT) gameId.value = it },
            label = { Subtitle1Text("Game ID", color = GreenConsole) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlueConsole,
                unfocusedBorderColor = BlueConsole,
                textColor = GreenConsole,
                backgroundColor = Black
            )
        )
        Spacer(modifier = Modifier.height(80.dp))
        OutlinedButton(
            onClick = { onWriteTag(idrfid.value.text, seedId.value.text, gameId.value.text) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(4.dp, BlueConsole),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = BlueConsole,
                backgroundColor = Transparent
            )
        ) {
            ButtonText("WRITE TAG")
        }
    }
}

@Preview
@Composable
private fun AdminScreenPreview() {
    AdminScreen()
}
