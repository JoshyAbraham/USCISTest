package com.joshy.civictestuscis.data

import android.content.Context
import kotlinx.serialization.json.Json

class QuestionRepository(
    private val context: Context,
    private val officialsResolver: OfficialsResolver = OfficialsResolver()
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val prefsManager = PreferencesManager(context)

    suspend fun loadQuestions(settings: QuizSettings): List<CivicsQuestion> {
        val payload = context.assets.open("questions_2025_withDistractorOptions.json").bufferedReader().use { reader ->
            json.decodeFromString<CivicsPayload>(reader.readText())
        }

        val overrides = DynamicAnswerOverrides(
            presidentName = prefsManager.getPresidentOverride(),
            vicePresidentName = prefsManager.getVicePresidentOverride(),
            speakerName = prefsManager.getSpeakerOverride(),
            chiefJusticeName = prefsManager.getChiefJusticeOverride(),
            senators = prefsManager.getSenatorsOverride(),
            representativeName = prefsManager.getRepresentativeOverride(),
            governorName = prefsManager.getGovernorOverride(),
            capitalName = prefsManager.getCapitalOverride()
        )

        // Check if we already have cached overrides for all dynamic fields
        val hasCachedAnswers = overrides.presidentName != null || 
                               overrides.senators != null || 
                               overrides.representativeName != null ||
                               overrides.governorName != null ||
                               overrides.capitalName != null

        return if (hasCachedAnswers) {
            // Use cached overrides immediately - no API delay
            android.util.Log.d("QuestionRepository", "✅ Using cached dynamic answers")
            val officials = OfficialAnswers(
                stateCode = "",
                stateName = "",
                presidentName = overrides.presidentName ?: "Current President of the United States",
                vicePresidentName = overrides.vicePresidentName ?: "Current Vice President of the United States",
                speakerName = overrides.speakerName ?: "Current Speaker of the House of Representatives",
                chiefJusticeName = overrides.chiefJusticeName ?: "Current Chief Justice of the United States",
                senators = overrides.senators ?: listOf("Visit senate.gov for current senators"),
                representativeName = overrides.representativeName ?: "Visit house.gov to find your representative",
                governorName = overrides.governorName ?: "Current governor",
                capitalName = overrides.capitalName ?: "State capital"
            )
            injectDynamicAnswers(payload.questions, officials)
        } else {
            // No cached data - return questions immediately without API delay
            // Dynamic questions will use placeholder answers from JSON
            android.util.Log.d("QuestionRepository", "⚠️ No cached answers - quiz starts immediately with placeholders")
            payload.questions
        }
    }

    private fun injectDynamicAnswers(
        questions: List<CivicsQuestion>,
        officials: OfficialAnswers
    ): List<CivicsQuestion> {
        return questions.map { q ->
            if (!q.requiresDynamicAnswer || q.dynamicAnswerType == null) {
                q
            } else {
                when (q.dynamicAnswerType) {
                    "senators" -> q.copy(acceptedAnswers = officials.senators)
                    "representative" -> q.copy(acceptedAnswers = listOfNotNull(officials.representativeName))
                    "speaker" -> q.copy(acceptedAnswers = listOfNotNull(officials.speakerName))
                    "president" -> q.copy(acceptedAnswers = listOfNotNull(officials.presidentName))
                    "vicePresident" -> q.copy(acceptedAnswers = listOfNotNull(officials.vicePresidentName))
                    "chiefJustice" -> q.copy(acceptedAnswers = listOfNotNull(officials.chiefJusticeName))
                    "governor" -> q.copy(acceptedAnswers = listOfNotNull(officials.governorName))
                    "capital" -> q.copy(acceptedAnswers = listOfNotNull(officials.capitalName))
                    else -> q
                }
            }
        }
    }

    fun getPreferencesManager(): PreferencesManager = prefsManager

    suspend fun fetchDynamicAnswers(zipCode: String): OfficialAnswers {
        return officialsResolver.resolve(zipCode, overrides = null)
    }
}
