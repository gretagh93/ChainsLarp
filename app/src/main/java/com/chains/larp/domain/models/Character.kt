package com.chains.larp.domain.models

import com.chains.larp.domain.AirtableRecord
import com.chains.larp.domain.AirtableRecordResponse
import com.squareup.moshi.Json

data class CharacterUpdateRecord(val fields: CharacterFields)

data class CharacterFields(
    @field:Json(name = "IDRFID") val tagId: Int,
    @field:Json(name = "Name") val name: String,
    @field:Json(name = "Realidad") val reality: String,
    @field:Json(name = "Notes") val notes: String?,
    @field:Json(name = "Type") val type: String,
    @field:Json(name = "Timeline") val timeline: List<String>?,
    @field:Json(name = "Ciudadana") val ciudadana: Int,
    @field:Json(name = "Inocente") val inocente: Int,
    @field:Json(name = "Sabia") val sabia: Int,
    @field:Json(name = "Gobernante") val gobernante: Int,
    @field:Json(name = "Heroína") val heroica: Int,
    @field:Json(name = "Cuidadora") val cuidadora: Int,
    @field:Json(name = "Creadora") val creadora: Int,
    @field:Json(name = "Exploradora") val exploradora: Int,
    @field:Json(name = "Bufón / Loco") val bufon: Int,
    @field:Json(name = "Rebelde") val rebelde: Int,
    @field:Json(name = "Amante") val amante: Int,
    @field:Json(name = "Maga") val maga: Int
) {
    override fun toString(): String { //Avoid Bio
        return "CharacterFields(name='$name', reality='$reality', type='$type', timeline=$timeline, ciudadana=$ciudadana, inocente=$inocente, sabia=$sabia, gobernante=$gobernante, heroica=$heroica, cuidadora=$cuidadora, creadora=$creadora, exploradora=$exploradora, bufon=$bufon, rebelde=$rebelde, amante=$amante, maga=$maga)"
    }
}

fun CharacterFields.toArchetypesIntList() = listOf(ciudadana, inocente, sabia, gobernante, heroica, cuidadora, creadora, exploradora, bufon, rebelde, amante, maga)
fun CharacterFields.mergeWithQuestArchetypes(quests: List<QuestFields>): CharacterFields {
    return this.copy( //TODO pls make me pretty and a bit optimal :D
        ciudadana = this.ciudadana + quests.filter { it.completed }.map { it.ciudadana }.sum() - quests.filter { !it.completed }.map { it.ciudadana }.sum(),
        inocente = this.inocente + quests.filter { it.completed }.map { it.inocente }.sum() - quests.filter { !it.completed }.map { it.inocente }.sum(),
        sabia = this.sabia + quests.filter { it.completed }.map { it.sabia }.sum() - quests.filter { !it.completed }.map { it.sabia }.sum(),
        gobernante = this.gobernante + quests.filter { it.completed }.map { it.gobernante }.sum() - quests.filter { !it.completed }.map { it.gobernante }.sum(),
        heroica = this.heroica + quests.filter { it.completed }.map { it.heroica }.sum() - quests.filter { !it.completed }.map { it.heroica }.sum(),
        cuidadora = this.cuidadora + quests.filter { it.completed }.map { it.cuidadora }.sum() - quests.filter { !it.completed }.map { it.cuidadora }.sum(),
        creadora = this.creadora + quests.filter { it.completed }.map { it.creadora }.sum() - quests.filter { !it.completed }.map { it.creadora }.sum(),
        exploradora = this.exploradora + quests.filter { it.completed }.map { it.exploradora }.sum() - quests.filter { !it.completed }.map { it.exploradora }.sum(),
        bufon = this.bufon + quests.filter { it.completed }.map { it.bufon }.sum() - quests.filter { !it.completed }.map { it.bufon }.sum(),
        rebelde = this.rebelde + quests.filter { it.completed }.map { it.rebelde }.sum() - quests.filter { !it.completed }.map { it.rebelde }.sum(),
        amante = this.amante + quests.filter { it.completed }.map { it.amante }.sum() - quests.filter { !it.completed }.map { it.amante }.sum(),
        maga = this.maga + quests.filter { it.completed }.map { it.maga }.sum() - quests.filter { !it.completed }.map { it.maga }.sum()
    )
}

typealias Character = AirtableRecord<CharacterFields>
typealias CharactersResponse = AirtableRecordResponse<Character>
typealias CharacterResponse = AirtableRecord<CharacterFields>