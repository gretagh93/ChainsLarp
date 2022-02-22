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
import com.chains.larp.domain.auth.LoginAction
import com.chains.larp.domain.auth.models.Player
import com.chains.larp.ui.components.AppScaffold
import com.chains.larp.ui.components.ButtonText
import com.chains.larp.ui.components.Header2Text
import com.chains.larp.ui.components.Subtitle1Text
import com.chains.larp.ui.theme.BlueConsole
import com.chains.larp.ui.theme.ChainsBackgroundGradient
import com.chains.larp.ui.theme.GreenConsole
import com.chains.larp.utils.*
import com.minikorp.duo.Resource
import com.minikorp.duo.select
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private val userLoggedSelector =
    createAppStateSelector(p1 = { it.auth }, selector = { it.userLogged })

@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val scope = rememberCoroutineScope()
    val store = useStore()
    val selector = rememberSelector(selector = userLoggedSelector)

    LaunchedEffect(Unit){
        scope.launch {
            navigateToHome()
          //  store.dispatch(LoginAction.Request("kaze", "randomPassword"))
//
          //  store.flow.select { it.auth.userLogged }
          //      .filter { it.isTerminate }
          //      .takeUntil { it.isTerminate }
          //      .collect {
          //          if (it.isSuccess) navigateToHome()
          //          else it.exceptionOrNull()?.let { exception ->
          //              scope.launch {
          //                  val errorMessage = exception.message ?: "Ha ocurrido un error"
          //                  scaffoldState.snackbarHostState.showSnackbar(errorMessage)
          //              }
          //          }
          //      }
        }
    }

    LoginScreen(
        onLoginButtonClicked = { username, password ->
            scope.launch {
                store.dispatch(LoginAction.Request(username, password))

                store.flow.select { it.auth.userLogged }
                    .filter { it.isTerminate }
                    .takeUntil { it.isTerminate }
                    .collect {
                        if (it.isSuccess) navigateToHome()
                        else it.exceptionOrNull()?.let { exception ->
                            scope.launch {
                                val errorMessage = exception.message ?: "Ha ocurrido un error"
                                scaffoldState.snackbarHostState.showSnackbar(errorMessage)
                            }
                        }
                    }
            }
        },
        userLoggedSelector = selector,
        scaffoldState = scaffoldState
    )
}

@Composable
private fun LoginScreen(
    onLoginButtonClicked: (String, String) -> Unit = { _, _ -> },
    userLoggedSelector: Resource<Player>,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    AppScaffold(scaffoldState = scaffoldState) {
        LoginScreenContent(
            onLoginButtonClicked = onLoginButtonClicked,
            userLoggedSelector = userLoggedSelector
        )
    }
}

@Composable
private fun LoginScreenContent(
    onLoginButtonClicked: (String, String) -> Unit,
    userLoggedSelector: Resource<Player>,
) {
    val username = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChainsBackgroundGradient)
            .padding(20.dp, 40.dp)
    ) {
        Header2Text(
            text = "Por favor, ingrese sus credenciales",
            color = BlueConsole,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = username.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { username.value = it },
            label = { Subtitle1Text("Username", color = GreenConsole) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlueConsole,
                unfocusedBorderColor = BlueConsole,
                textColor = GreenConsole,
                backgroundColor = Black
            )
        )
        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = password.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { password.value = it },
            label = { Subtitle1Text("Password", color = GreenConsole) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlueConsole,
                unfocusedBorderColor = BlueConsole,
                textColor = GreenConsole,
                backgroundColor = Black
            )
        )
        Spacer(modifier = Modifier.height(80.dp))
        OutlinedButton(
            onClick = { onLoginButtonClicked(username.value.text, password.value.text) },
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
            ButtonText("LOG IN")
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(userLoggedSelector = Resource.empty())
}
