package com.chainslarp.app.application.network.airtable

import com.chainslarp.app.application.network.airtable.models.Player
import com.chainslarp.app.application.network.airtable.models.Quest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class AirtableRepository(private val api: AirtableApi) : CoroutineScope by CoroutineScope(Job()) {

    companion object {
        private const val AIRTABLE_BASE_ID = "appax8byV5cWOTLGK"
    }

    suspend fun fetchUser(id: String, characterType: String = "Pjs") =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getPlayer(
                    baseId = AIRTABLE_BASE_ID,
                    views = characterType,
                    filterFormula = "{IDRFID}=$id"
                ).records.getOrNull(0)
            }
        }

    suspend fun fetchQuests(): List<Quest> = api.getQuests(AIRTABLE_BASE_ID).records

    suspend fun loginUser(
        username: String,
        password: String
    ): Player =
        api.login(
            baseId = AIRTABLE_BASE_ID,
            filterFormula = "AND({User}='$username',{Pwd}='$password')"
        ).records.firstOrNull() ?: throw LoginException()
}

class LoginException : Exception()
