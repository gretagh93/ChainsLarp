package com.chainslarp.app.application.network.airtable.models

import com.chainslarp.app.application.network.airtable.AirtableRecord
import com.chainslarp.app.application.network.airtable.AirtableRecordResponse
import com.squareup.moshi.Json


data class PlayerFields(
    @field:Json(name="Name") val name : String,
    @field:Json(name="User") val username : String,
    @field:Json(name="Type") val type : String
)

typealias Player = AirtableRecord<PlayerFields>
typealias PlayerResponse = AirtableRecordResponse<Player>