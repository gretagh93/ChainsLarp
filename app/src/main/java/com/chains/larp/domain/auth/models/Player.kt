package com.chains.larp.domain.auth.models

import com.chains.larp.domain.AirtableRecord
import com.chains.larp.domain.AirtableRecordResponse
import com.squareup.moshi.Json

data class PlayerFields(
    @field:Json(name = "Name") val name: String,
    @field:Json(name = "User") val username: String,
    @field:Json(name = "Type") val type: String
)

typealias Player = AirtableRecord<PlayerFields>
typealias PlayerResponse = AirtableRecordResponse<Player>