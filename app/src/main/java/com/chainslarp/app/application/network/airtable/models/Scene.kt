package com.chainslarp.app.application.network.airtable.models

import com.chainslarp.app.application.network.airtable.AirtableRecord
import com.chainslarp.app.application.network.airtable.AirtableRecordResponse

data class SceneFields(
    val Name : String,
    val Notes : String,
    val When : String?,
    val Relations : List<String>?,
    val People : List<String>?,
    val Quests : List<String>?,
    val Props : List<String>?,
    val Type : String,
    val Description : String
)

typealias Scene = AirtableRecord<SceneFields>
typealias ScenesResponse = AirtableRecordResponse<Scene>