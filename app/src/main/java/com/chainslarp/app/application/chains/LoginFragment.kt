package com.chainslarp.app.application.chains

import android.content.Intent
import android.os.Bundle
import com.chainslarp.app.BuildConfig
import com.chainslarp.app.R
import com.chainslarp.app.application.BaseFragment
import com.chainslarp.app.application.core.toast
import com.chainslarp.app.application.network.auth.AuthStore
import com.chainslarp.app.application.network.auth.LoginUserAction
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.flow.onEach
import mini.android.makeGone
import mini.android.makeVisible
import mini.flow.flow
import mini.flow.select
import org.kodein.di.generic.instance

class LoginFragment : BaseFragment() {

    companion object {
        const val TAG = "login_fragment"
    }

    override val layout = R.layout.login_fragment
    private val authStore by instance<AuthStore>()

    override suspend fun whenCreated(savedInstanceState: Bundle?) {
        super.whenCreated(savedInstanceState)

        authStore.flow()
            .select { it.userLogged }
            .onEach { resource ->
                when {
                    resource.isLoading -> showLoadingUi(true)
                    resource.isSuccess -> navigateToHome()
                    resource.isFailure -> {
                        showLoadingUi(false)
                        toast("El usuario o contraseña introducidos no son válidos")
                    }
                }
            }.launchOnUi()

        if (BuildConfig.DEBUG) {
            dispatcher.dispatchOnUi(
                LoginUserAction(
                    username = "kazedetodo",
                    password = "cacatua"
                )
            )
        }
        loginButton.setOnClickListener {
            if (checkFields()) {
                dispatcher.dispatchOnUi(
                    LoginUserAction(
                        username = user_field.text.toString(),
                        password = password_field.text.toString()
                    )
                )
            } else {
                toast("Debes rellenar todos los campos")
            }
        }
    }

    private fun showLoadingUi(show: Boolean) {
        if (show) {
            progress.makeVisible()
            user_field.makeGone()
            password_field.makeGone()
        } else {
            progress.makeGone()
            user_field.makeVisible()
            password_field.makeVisible()
        }
    }

    private fun checkFields(): Boolean {
        val user = user_field.text
        val password = password_field.text
        return !user.isNullOrBlank() && !password.isNullOrBlank()
    }

    private fun navigateToHome() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
    }
}