package com.chains.larp.utils

import com.chains.larp.domain.character.Character
import com.chains.larp.domain.character.CharacterFields
import com.chains.larp.domain.models.Quest
import com.chains.larp.domain.models.QuestFields
import com.chains.larp.ui.views.CharacterViewData
import com.chains.larp.utils.FakeData.Quests.defaultQuest

object TestData {
    object Character {
        val satanas = Character(
            "", CharacterFields(
            tagId = 111,
            "Satanás", "Wizard World",
            timeline = emptyList(),
            ciudadana = 1,
            inocente = 2,
            sabia = 3,
            gobernante = 4,
            heroica = 5,
            cuidadora = 6,
            creadora = 7,
            exploradora = 7,
            bufon = 110,
            rebelde = 10,
            amante = 34,
            maga = 1,
            notes = "Es el Satán bíblico. Forma parte del grupo del Aprendiz.", type = "PJ",
        ), ""
        )
    }
}

object FakeData {
    object Quests {
        val defaultQuest = Quest(
            id = "1",
            fields = QuestFields("Compra un caballo", "Me he quedado sin caballos, vete y comprame uno Gerald", listOf("user1"), "", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, true),
            createdTime = ""
        )
    }

    object Character {
        val defaultCharacter = Character(
            id = "user1",
            fields = CharacterFields(
                tagId = 111,
                name = "Illidan Stormrage",
                notes = "El jugador es ciego",
                type = "PJ",
                timeline = emptyList(),
                reality = "",
                ciudadana = 1,
                inocente = 2,
                sabia = 3,
                gobernante = 4,
                heroica = 5,
                cuidadora = 6,
                creadora = 7,
                exploradora = 7,
                bufon = 110,
                rebelde = 10,
                amante = 34,
                maga = 1
            ),
            ""
        )
        val characterViewData = CharacterViewData(defaultCharacter, listOf(defaultQuest))
    }
}