package com.joshy.civictestuscis.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshy.civictestuscis.data.AppDatabase
import com.joshy.civictestuscis.data.AttemptEntity
import com.joshy.civictestuscis.data.CivicsQuestion
import com.joshy.civictestuscis.data.QuestionRepository
import com.joshy.civictestuscis.data.QuizMode
import com.joshy.civictestuscis.data.QuizSettings
import com.joshy.civictestuscis.domain.QuizEngine
import com.joshy.civictestuscis.domain.QuizSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface Screen {
    data object Home : Screen
    data object Quiz : Screen
    data object Settings : Screen
    data object Results : Screen
}

data class UiState(
    val loading: Boolean = false,
    val screen: Screen = Screen.Home,
    val settings: QuizSettings = QuizSettings(),
    val session: QuizSession? = null,
    val currentQuestion: CivicsQuestion? = null,
    val showAnswer: Boolean = false,
    val lastCorrect: Boolean? = null,
    val freeTextAnswer: String = "",
    val attempts: List<AttemptEntity> = emptyList(),
    val error: String? = null,

    // All dynamic answer overrides
    val presidentOverride: String = "",
    val vicePresidentOverride: String = "",
    val speakerOverride: String = "",
    val chiefJusticeOverride: String = "",
    val senator1Override: String = "",
    val senator2Override: String = "",
    val representativeOverride: String = "",
    val governorOverride: String = "",
    val capitalOverride: String = "",

    // Show notification when using generic defaults
    val showDefaultsNotification: Boolean = false,

    // First launch flag
    val isFirstLaunch: Boolean = false
)

class CivicViewModel(context: Context) : ViewModel() {
    private val repository = QuestionRepository(context)
    private val dao = AppDatabase.get(context).attemptDao()
    private val prefsManager = repository.getPreferencesManager()

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        // Determine initial screen: Settings on first launch, Home afterwards
        val isFirstTime = prefsManager.isFirstLaunch()
        val initialScreen = if (isFirstTime) Screen.Settings else Screen.Home

        // Load saved overrides on startup
        val senators = prefsManager.getSenatorsOverride() ?: emptyList()
        _state.value = _state.value.copy(
            screen = initialScreen,
            isFirstLaunch = isFirstTime,
            presidentOverride = prefsManager.getPresidentOverride() ?: "",
            vicePresidentOverride = prefsManager.getVicePresidentOverride() ?: "",
            speakerOverride = prefsManager.getSpeakerOverride() ?: "",
            chiefJusticeOverride = prefsManager.getChiefJusticeOverride() ?: "",
            senator1Override = senators.getOrNull(0) ?: "",
            senator2Override = senators.getOrNull(1) ?: "",
            representativeOverride = prefsManager.getRepresentativeOverride() ?: "",
            governorOverride = prefsManager.getGovernorOverride() ?: "",
            capitalOverride = prefsManager.getCapitalOverride() ?: "",
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )

        // Auto-fetch dynamic answers if we have a valid ZIP but missing data
        val currentZip = _state.value.settings.zipCode
        if (isValidZipCode(currentZip)) {
            fetchDynamicAnswersForZip(currentZip)
        }
    }

    fun navigate(screen: Screen) {
        // Mark first launch complete when navigating away from Settings
        if (_state.value.screen == Screen.Settings && screen == Screen.Home) {
            prefsManager.markFirstLaunchComplete()
            _state.value = _state.value.copy(isFirstLaunch = false)
        }

        _state.value = _state.value.copy(screen = screen)
        if (screen == Screen.Results) {
            refreshAttempts()
        }
    }

    fun updateZip(zip: String) {
        _state.value = _state.value.copy(settings = _state.value.settings.copy(zipCode = zip))
        // Auto-fetch dynamic answers when ZIP code is valid
        if (isValidZipCode(zip)) {
            fetchDynamicAnswersForZip(zip)
        }
    }

    private fun isValidZipCode(zip: String): Boolean {
        return zip.matches(Regex("^\\d{5}$"))
    }

    fun fetchDynamicAnswersForZip(zipCode: String) {
        if (!isValidZipCode(zipCode)) {
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            runCatching {
                repository.fetchDynamicAnswers(zipCode)
            }.onSuccess { officials ->
                // Update UI state with fetched values, but preserve any manual overrides

                // Federal Officials - only update if empty AND not generic fallback
                if (_state.value.presidentOverride.isBlank() && !officials.presidentName.contains("Current President")) {
                    prefsManager.setPresidentOverride(officials.presidentName)
                    _state.value = _state.value.copy(presidentOverride = officials.presidentName)
                }

                if (_state.value.vicePresidentOverride.isBlank() && !officials.vicePresidentName.contains("Current Vice President")) {
                    prefsManager.setVicePresidentOverride(officials.vicePresidentName)
                    _state.value = _state.value.copy(vicePresidentOverride = officials.vicePresidentName)
                }

                if (_state.value.speakerOverride.isBlank() && !officials.speakerName.contains("Current Speaker")) {
                    prefsManager.setSpeakerOverride(officials.speakerName)
                    _state.value = _state.value.copy(speakerOverride = officials.speakerName)
                }

                if (_state.value.chiefJusticeOverride.isBlank() && !officials.chiefJusticeName.contains("Current Chief Justice")) {
                    prefsManager.setChiefJusticeOverride(officials.chiefJusticeName)
                    _state.value = _state.value.copy(chiefJusticeOverride = officials.chiefJusticeName)
                }

                // State/Local Officials - only update if empty
                if (_state.value.senator1Override.isBlank() && _state.value.senator2Override.isBlank() && officials.senators.isNotEmpty()) {
                    val senators = officials.senators.filter { !it.startsWith("Visit") }
                    if (senators.isNotEmpty()) {
                        prefsManager.setSenatorsOverride(senators)
                        _state.value = _state.value.copy(
                            senator1Override = senators.getOrNull(0) ?: "",
                            senator2Override = senators.getOrNull(1) ?: ""
                        )
                    }
                }

                if (_state.value.representativeOverride.isBlank() && !officials.representativeName.startsWith("Visit")) {
                    prefsManager.setRepresentativeOverride(officials.representativeName)
                    _state.value = _state.value.copy(representativeOverride = officials.representativeName)
                }

                if (_state.value.governorOverride.isBlank() && !officials.governorName.contains("governor of")) {
                    prefsManager.setGovernorOverride(officials.governorName)
                    _state.value = _state.value.copy(governorOverride = officials.governorName)
                }

                if (_state.value.capitalOverride.isBlank() && !officials.capitalName.contains("State capital")) {
                    prefsManager.setCapitalOverride(officials.capitalName)
                    _state.value = _state.value.copy(capitalOverride = officials.capitalName)
                }

                // Update notification state
                _state.value = _state.value.copy(
                    loading = false,
                    showDefaultsNotification = !prefsManager.hasAnyOverrides()
                )
            }.onFailure { ex ->
                _state.value = _state.value.copy(loading = false)
                // Silent fail - user can still enter manually
            }
        }
    }

    fun updateMode(mode: QuizMode) {
        _state.value = _state.value.copy(settings = _state.value.settings.copy(mode = mode))
    }

    fun updatePresidentOverride(name: String) {
        prefsManager.setPresidentOverride(name)
        _state.value = _state.value.copy(
            presidentOverride = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun updateVicePresidentOverride(name: String) {
        prefsManager.setVicePresidentOverride(name)
        _state.value = _state.value.copy(
            vicePresidentOverride = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun updateSpeakerOverride(name: String) {
        prefsManager.setSpeakerOverride(name)
        _state.value = _state.value.copy(
            speakerOverride = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun updateChiefJusticeOverride(name: String) {
        prefsManager.setChiefJusticeOverride(name)
        _state.value = _state.value.copy(
            chiefJusticeOverride = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun updateSenator1Override(name: String) {
        val current = prefsManager.getSenatorsOverride() ?: emptyList()
        val updated = if (name.isBlank()) {
            current.drop(1)
        } else {
            listOf(name) + current.drop(1)
        }
        prefsManager.setSenatorsOverride(updated)
        _state.value = _state.value.copy(
            senator1Override = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun updateSenator2Override(name: String) {
        val current = prefsManager.getSenatorsOverride() ?: emptyList()
        val first = current.firstOrNull() ?: ""
        val updated = if (name.isBlank()) {
            if (first.isBlank()) emptyList() else listOf(first)
        } else {
            listOf(first, name)
        }
        prefsManager.setSenatorsOverride(updated)
        _state.value = _state.value.copy(
            senator2Override = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun updateRepresentativeOverride(name: String) {
        prefsManager.setRepresentativeOverride(name)
        _state.value = _state.value.copy(
            representativeOverride = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun updateGovernorOverride(name: String) {
        prefsManager.setGovernorOverride(name)
        _state.value = _state.value.copy(
            governorOverride = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun updateCapitalOverride(name: String) {
        prefsManager.setCapitalOverride(name)
        _state.value = _state.value.copy(
            capitalOverride = name,
            showDefaultsNotification = !prefsManager.hasAnyOverrides()
        )
    }

    fun dismissDefaultsNotification() {
        _state.value = _state.value.copy(showDefaultsNotification = false)
    }

    fun startQuiz() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching {
                repository.loadQuestions(_state.value.settings)
            }.onSuccess { questions ->
                val session = QuizEngine.createSession(questions, _state.value.settings.mode)
                _state.value = _state.value.copy(
                    loading = false,
                    screen = Screen.Quiz,
                    session = session,
                    currentQuestion = session.questions.firstOrNull(),
                    showAnswer = false,
                    lastCorrect = null,
                    freeTextAnswer = ""
                )
            }.onFailure { ex ->
                _state.value = _state.value.copy(loading = false, error = ex.message)
            }
        }
    }

    fun updateFreeText(text: String) {
        _state.value = _state.value.copy(freeTextAnswer = text)
    }

    fun submitAnswer(selectedOption: String? = null, selfMarkedCorrect: Boolean? = null) {
        val session = _state.value.session ?: return
        val question = _state.value.currentQuestion ?: return

        val isCorrect = when {
            selfMarkedCorrect != null -> selfMarkedCorrect
            _state.value.settings.mode == QuizMode.PRACTICE_MCQ || _state.value.settings.mode == QuizMode.PRACTICE_MCQ_ALL -> {
                val chosen = selectedOption.orEmpty()
                question.acceptedAnswers.any { it.equals(chosen, ignoreCase = true) }
            }
            else -> QuizEngine.evaluate(question, _state.value.freeTextAnswer)
        }

        val updatedSession = QuizEngine.submitAnswer(session, isCorrect)
        val progress = QuizEngine.progress(updatedSession)

        _state.value = _state.value.copy(
            session = updatedSession,
            showAnswer = true,
            lastCorrect = isCorrect,
            freeTextAnswer = ""
        )

        if (updatedSession.finished) {
            persistAttempt(updatedSession)
            _state.value = _state.value.copy(screen = Screen.Results)
            refreshAttempts()
        }

        if (progress.failed || progress.passed) {
            persistAttempt(updatedSession)
            _state.value = _state.value.copy(screen = Screen.Results)
            refreshAttempts()
        }
    }

    fun continueAfterReveal() {
        val session = _state.value.session ?: return
        val nextQuestion = if (session.finished) null else session.questions[session.currentIndex]
        _state.value = _state.value.copy(
            currentQuestion = nextQuestion,
            showAnswer = false,
            lastCorrect = null
        )
    }

    private fun persistAttempt(session: QuizSession) {
        viewModelScope.launch {
            val progress = QuizEngine.progress(session)
            val entity = AttemptEntity(
                mode = session.mode.name,
                asked = progress.asked,
                correct = progress.correct,
                passed = progress.correct >= progress.passThreshold,
                timestampEpochMs = System.currentTimeMillis()
            )
            withContext(Dispatchers.IO) {
                dao.insert(entity)
            }
        }
    }

    fun refreshAttempts() {
        viewModelScope.launch {
            val attempts = withContext(Dispatchers.IO) { dao.listAttempts() }
            _state.value = _state.value.copy(attempts = attempts)
        }
    }
}
