package com.chains.larp.ui.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chains.larp.domain.models.Character
import com.chains.larp.domain.nfc.LoadCharacterAction
import com.chains.larp.domain.nfc.TagNotInRangeException
import com.chains.larp.ui.components.AppScaffold
import com.chains.larp.ui.components.Header2Text
import com.chains.larp.ui.theme.BlueConsole
import com.chains.larp.ui.theme.ChainsBackgroundGradient
import com.chains.larp.utils.*
import com.minikorp.duo.Resource
import com.minikorp.duo.select
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private val scannedCharacterSelector =
    createAppStateSelector(p1 = { it.character }, selector = { it.scanningMedal })
private val loadedCharacterSelector =
    createAppStateSelector(p1 = { it.character }, selector = { it.loadCharacter })

@Composable
fun ScanningScreen(
    navigateToCharacterScreen: (String) -> Unit,
    navigateToAdmin: () -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val scope = rememberCoroutineScope()
    val store = useStore()
    val tagSelector = rememberSelector(selector = scannedCharacterSelector)
    val characterSelector = rememberSelector(selector = loadedCharacterSelector)

    if (tagSelector.isSuccess) {
        //Update database and only after success navigate
        val characterId = tagSelector.getResourceValue()!!.characterId
        LaunchedEffect(characterId) {
            store.dispatch(LoadCharacterAction.Request(characterId))

            store.flow
                .select { it.character.loadCharacter }
                .onEach { characterResource ->
                    if (characterResource.isSuccess) {
                        val characterSelectorId = characterResource.getResourceValue()!!.id
                        navigateToCharacterScreen(characterSelectorId)

                    } else if (characterResource.isFailure) {
                        toast("There was an error loading the character for the given tag")
                    }
                }.launchIn(this)
        }
    } else {
        tagSelector.exceptionOrNull()?.let { error ->
            when (error) {
                TagNotInRangeException -> showToast("The tag is not in range for reading")
                else -> showToast("There was an error reading the tag. Try again")
            }
        }
    }

    withLocalLoadingState(isLoading = false) {
        ScanningScreen(
            scannedCharacterSelector = characterSelector,
            scaffoldState = scaffoldState,
            navigateToAdmin
        )
    }
}

@Composable
private fun ScanningScreen(
    scannedCharacterSelector: Resource<Character>,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigateToAdmin: () -> Unit = {}
) {
    AppScaffold(scaffoldState = scaffoldState) {
        ScanningScreenContent(
            scannedCharacterSelector = scannedCharacterSelector,
            navigateToAdmin
        )
    }
}

@Composable
private fun ScanningScreenContent(
    scannedCharacterSelector: Resource<Character>,
    navigateToAdmin: () -> Unit = {}
) {
    val adminCounter = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChainsBackgroundGradient)
            .padding(20.dp, 40.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Header2Text(
            text = "Bienvenid@, ${scannedCharacterSelector.getOrNull()?.fields?.name ?: ""}",
            color = BlueConsole,
            fontSize = 32.sp
        )
        PulseLoading(content = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                BlueConsole
            )
        })
        Header2Text(
            text = "Por favor, escané su medalla en el lector",
            color = BlueConsole,
            fontSize = 32.sp,
            modifier = Modifier.clickable {
                if (adminCounter.value == 10) {
                    adminCounter.value = 0; navigateToAdmin()
                } else adminCounter.value++
            }
        )
    }
}

@Composable
fun PulseLoading(
    durationMillis: Int = 1000,
    maxPulseSize: Float = 300f,
    minPulseSize: Float = 50f,
    pulseColor: Color = BlueConsole,
    centreColor: Color = BlueConsole,
    content: @Composable () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition()
    val size by infiniteTransition.animateFloat(
        initialValue = minPulseSize,
        targetValue = maxPulseSize,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        //Card for Pulse Effect
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(size.dp)
                .align(Alignment.Center)
                .alpha(alpha),
            backgroundColor = pulseColor,
            elevation = 0.dp
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun ScanningScreenPreview() {
    ScanningScreen(scannedCharacterSelector = Resource.empty())
}
