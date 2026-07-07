package com.joshy.civictestuscis.data

import kotlinx.serialization.Serializable

@Serializable
data class CivicsPayload(
    val source: SourceInfo,
    val totalQuestions: Int,
    val questions: List<CivicsQuestion>
)

@Serializable
data class SourceInfo(
    val title: String,
    val pdf: String
)

@Serializable
data class CivicsQuestion(
    val id: Int,
    val question: String,
    val acceptedAnswers: List<String>,
    val isStarred: Boolean,
    val requiresDynamicAnswer: Boolean = false,
    val dynamicAnswerType: String? = null,
    val distractorOptions: List<String> = emptyList()
)

enum class QuizMode {
    INTERVIEW_2025,
    SPECIAL_65_20,
    PRACTICE_MCQ,
    PRACTICE_MCQ_ALL
}

data class QuizSettings(
    val zipCode: String = "19425",
    val mode: QuizMode = QuizMode.INTERVIEW_2025,
    val revealImmediately: Boolean = true,
    val maxQuestions: Int? = null  // null = use default, otherwise use this value
)

data class OfficialAnswers(
    val stateCode: String,
    val stateName: String,
    val representativeName: String,
    val senators: List<String>,
    val governorName: String,
    val capitalName: String,
    val presidentName: String,
    val vicePresidentName: String,
    val speakerName: String,
    val chiefJusticeName: String
)

/**
 * User-entered overrides for dynamic answer fields
 */
data class DynamicAnswerOverrides(
    val presidentName: String? = null,
    val vicePresidentName: String? = null,
    val speakerName: String? = null,
    val chiefJusticeName: String? = null,
    val senators: List<String>? = null,
    val representativeName: String? = null,
    val governorName: String? = null,
    val capitalName: String? = null
)
