package com.joshy.civictestuscis.ui

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.joshy.civictestuscis.data.QuizMode
import com.joshy.civictestuscis.domain.QuizEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivicApp(activity: Activity, context: Context) {
    val viewModel = remember { CivicViewModel(context) }
    val ui by viewModel.state.collectAsState()

    // Handle back button - always go to Home screen
    BackHandler(enabled = ui.screen != Screen.Home) {
        viewModel.navigate(Screen.Home)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Civic Test USCIS") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (ui.screen) {
                Screen.Home -> HomeScreen(ui, viewModel)
                Screen.Settings -> SettingsScreen(ui, viewModel, activity)
                Screen.Quiz -> QuizScreen(ui, viewModel, activity)
                Screen.Results -> ResultsScreen(ui, viewModel)
            }
        }
    }
}

@Composable
private fun HomeScreen(ui: UiState, viewModel: CivicViewModel) {
    Text("One question at a time", style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(8.dp))
    Text("ZIP for location-aware answers: ${ui.settings.zipCode}")
    Text("Mode: ${ui.settings.mode}")
    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = { viewModel.startQuiz() }, modifier = Modifier.fillMaxWidth()) {
        Text("Start Test")
    }
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedButton(onClick = { viewModel.navigate(Screen.Settings) }, modifier = Modifier.fillMaxWidth()) {
        Text("Settings")
    }
    Spacer(modifier = Modifier.height(8.dp))
    TextButton(onClick = { viewModel.navigate(Screen.Results) }, modifier = Modifier.fillMaxWidth()) {
        Text("Past Results")
    }

    if (ui.error != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(ui.error, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun SettingsScreen(ui: UiState, viewModel: CivicViewModel, activity: Activity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Welcome card for first-time users
        if (ui.isFirstLaunch) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "👋 Welcome to Civic Test USCIS!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Please configure your settings before starting the quiz:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Enter your ZIP code for location-based questions", style = MaterialTheme.typography.bodySmall)
                    Text("• Select your quiz mode", style = MaterialTheme.typography.bodySmall)
                    Text("• Fill in required official names (highlighted in red)", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Dynamic answers will auto-populate when you enter a valid ZIP code!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Notification card when using generic defaults
        if (ui.showDefaultsNotification) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "⚠️ Using Generic Defaults",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Some quiz questions require current official names. Please update the dynamic answers below for accurate quiz content.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.dismissDefaultsNotification() }) {
                        Text("Dismiss")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = ui.settings.zipCode,
            onValueChange = { viewModel.updateZip(it.filter(Char::isDigit).take(5)) },
            label = { Text("ZIP / Pincode") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            supportingText = {
                if (ui.loading) {
                    Text("Fetching dynamic answers...", color = MaterialTheme.colorScheme.primary)
                } else if (ui.settings.zipCode.length == 5) {
                    Text("✓ Valid ZIP - dynamic answers will auto-populate", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text("Mode")
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = ui.settings.mode == QuizMode.INTERVIEW_2025,
                    onClick = { viewModel.updateMode(QuizMode.INTERVIEW_2025) },
                    label = { Text("Interview 20/12") }
                )
                FilterChip(
                    selected = ui.settings.mode == QuizMode.SPECIAL_65_20,
                    onClick = { viewModel.updateMode(QuizMode.SPECIAL_65_20) },
                    label = { Text("65/20") }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = ui.settings.mode == QuizMode.PRACTICE_MCQ,
                    onClick = { viewModel.updateMode(QuizMode.PRACTICE_MCQ) },
                    label = { Text("Practice MCQ (20)") }
                )
                FilterChip(
                    selected = ui.settings.mode == QuizMode.PRACTICE_MCQ_ALL,
                    onClick = { viewModel.updateMode(QuizMode.PRACTICE_MCQ_ALL) },
                    label = { Text("Practice MCQ (All 128)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Dynamic Answers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Override answers for location-based or time-sensitive questions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Federal Officials Section
        Text("Federal Officials", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        ValidatedNameField(
            value = ui.presidentOverride,
            onValueChange = { viewModel.updatePresidentOverride(it) },
            label = "President",
            questionHint = "Q38"
        )
        Spacer(modifier = Modifier.height(8.dp))

        ValidatedNameField(
            value = ui.vicePresidentOverride,
            onValueChange = { viewModel.updateVicePresidentOverride(it) },
            label = "Vice President",
            questionHint = "Q39"
        )
        Spacer(modifier = Modifier.height(8.dp))

        ValidatedNameField(
            value = ui.speakerOverride,
            onValueChange = { viewModel.updateSpeakerOverride(it) },
            label = "Speaker of the House",
            questionHint = "Q30"
        )
        Spacer(modifier = Modifier.height(8.dp))

        ValidatedNameField(
            value = ui.chiefJusticeOverride,
            onValueChange = { viewModel.updateChiefJusticeOverride(it) },
            label = "Chief Justice",
            questionHint = "Q57"
        )
        Spacer(modifier = Modifier.height(16.dp))

        // State/Local Officials Section
        Text("Your Representatives", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        ValidatedNameField(
            value = ui.senator1Override,
            onValueChange = { viewModel.updateSenator1Override(it) },
            label = "U.S. Senator #1",
            questionHint = "Q23"
        )
        Spacer(modifier = Modifier.height(8.dp))

        ValidatedNameField(
            value = ui.senator2Override,
            onValueChange = { viewModel.updateSenator2Override(it) },
            label = "U.S. Senator #2",
            questionHint = "Q23"
        )
        Spacer(modifier = Modifier.height(8.dp))

        ValidatedNameField(
            value = ui.representativeOverride,
            onValueChange = { viewModel.updateRepresentativeOverride(it) },
            label = "U.S. Representative",
            questionHint = "Q29"
        )
        Spacer(modifier = Modifier.height(8.dp))

        ValidatedNameField(
            value = ui.governorOverride,
            onValueChange = { viewModel.updateGovernorOverride(it) },
            label = "State Governor",
            questionHint = "Q61"
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ui.capitalOverride,
            onValueChange = { viewModel.updateCapitalOverride(it) },
            label = { Text("State Capital") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            supportingText = { Text("Q62 - Can include city names with numbers/special chars") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.navigate(Screen.Home) }, 
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (ui.isFirstLaunch) "Continue to Home →" else "Back to Home")
        }
    }
}

@Composable
private fun ValidatedNameField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    questionHint: String
) {
    val isValid = value.isBlank() || com.joshy.civictestuscis.data.PreferencesManager.isValidName(value)
    val isEmpty = value.isBlank()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = !isValid || isEmpty,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            errorBorderColor = if (isEmpty) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.error,
            errorLabelColor = if (isEmpty) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.error
        ),
        supportingText = {
            when {
                !isValid -> Text("Only letters, spaces, periods, hyphens, and apostrophes allowed", color = MaterialTheme.colorScheme.error)
                isEmpty -> Text("⚠️ Required - Please fill this field", color = MaterialTheme.colorScheme.error)
                else -> Text("✓ $questionHint", color = MaterialTheme.colorScheme.primary)
            }
        },
        singleLine = true
    )
}

@Composable
private fun QuizScreen(ui: UiState, viewModel: CivicViewModel, activity: Activity) {
    val session = ui.session ?: return
    val question = ui.currentQuestion ?: return
    val progress = QuizEngine.progress(session)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Progress and Score Card at the top
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Question ${progress.asked + 1} of ${progress.maxQuestions}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Mode: ${ui.settings.mode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(
                        "Score: ${progress.correct}/${progress.asked}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (progress.correct >= progress.passThreshold && progress.asked >= progress.maxQuestions) 
                            MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.onSurface
                    )
                    if (progress.asked > 0) {
                        val percentage = (progress.correct.toFloat() / progress.asked * 100).toInt()
                        Text(
                            "$percentage%",
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                percentage >= 80 -> MaterialTheme.colorScheme.primary
                                percentage >= 60 -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(question.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(16.dp))

        if (ui.settings.mode == QuizMode.PRACTICE_MCQ || ui.settings.mode == QuizMode.PRACTICE_MCQ_ALL) {
            val options = remember(question.id) {
                // Helper to check if text is instructional (not a real answer)
                fun isInstructional(text: String): Boolean {
                    return text.startsWith("Visit uscis.gov", ignoreCase = true) ||
                           text.startsWith("Answers will vary", ignoreCase = true) ||
                           text.contains("[District of Columbia", ignoreCase = true)
                }

                // Get the correct answer for this question (filter out instructional text)
                val validAnswers = question.acceptedAnswers.filterNot { isInstructional(it) }
                val correctAnswer = validAnswers.firstOrNull() ?: ""

                // Use pre-defined distractor options from JSON if available
                val distractors = if (question.distractorOptions.isNotEmpty()) {
                    question.distractorOptions.take(3)
                } else {
                    // Fallback: Generate distractors from non-dynamic questions only
                    session.questions
                        .filterNot { it.requiresDynamicAnswer }
                        .flatMap { it.acceptedAnswers }
                        .filterNot { isInstructional(it) }
                        .filterNot { it == correctAnswer }
                        .distinct()
                        .shuffled()
                        .take(3)
                }

                // If we have a valid correct answer, include it; otherwise just show distractors
                if (correctAnswer.isNotEmpty()) {
                    (distractors + correctAnswer).shuffled()
                } else {
                    // No valid answer available (API failed), show only distractors
                    distractors.take(4).shuffled()
                }
            }
            options.forEach { option ->
                OutlinedButton(
                    onClick = { viewModel.submitAnswer(selectedOption = option) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(option) }
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            if (!ui.showAnswer) {
                OutlinedTextField(
                    value = ui.freeTextAnswer,
                    onValueChange = { viewModel.updateFreeText(it) },
                    label = { Text("Your answer") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.submitAnswer() }) { Text("Submit") }
                    OutlinedButton(onClick = { viewModel.submitAnswer(selfMarkedCorrect = true) }) {
                        Text("✓ Correct")
                    }
                    OutlinedButton(onClick = { viewModel.submitAnswer(selfMarkedCorrect = false) }) {
                        Text("✗ Incorrect")
                    }
                }
            }
        }

        if (ui.showAnswer) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val lastColor = if (ui.lastCorrect == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    Text(
                        if (ui.lastCorrect == true) "✓ Correct!" else "✗ Review this answer",
                        color = lastColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Accepted answers:", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    question.acceptedAnswers.take(6).forEach {
                        Text("• $it", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (session.finished.not()) {
                Button(
                    onClick = { viewModel.continueAfterReveal() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next Question →")
                }
            } else {
                Button(
                    onClick = { viewModel.navigate(Screen.Results) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Results")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.navigate(Screen.Home) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultsScreen(ui: UiState, viewModel: CivicViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Quiz Results", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Current Session Result (if available)
        val session = ui.session
        if (session != null) {
            val progress = QuizEngine.progress(session)
            val passed = progress.correct >= progress.passThreshold

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Latest Quiz",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (passed) "✓ PASSED" else "✗ FAILED",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Mode: ${session.mode}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Score: ${progress.correct} / ${progress.asked}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (progress.asked > 0) {
                        val percentage = (progress.correct.toFloat() / progress.asked * 100).toInt()
                        Text(
                            "Accuracy: $percentage%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Required to pass: ${progress.passThreshold} / ${progress.maxQuestions}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Past Attempts
        Text(
            "Past Attempts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (ui.attempts.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        "No past attempts yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Complete a quiz to see your results here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            ui.attempts.sortedByDescending { it.timestampEpochMs }.forEach { attempt ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                attempt.mode,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                if (attempt.passed) "✓ Passed" else "✗ Failed",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (attempt.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Score: ${attempt.correct} / ${attempt.asked}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (attempt.asked > 0) {
                            val percentage = (attempt.correct.toFloat() / attempt.asked * 100).toInt()
                            Text(
                                "Accuracy: $percentage%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val date = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(attempt.timestampEpochMs))
                        Text(
                            date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.navigate(Screen.Home) }, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Home")
        }
    }
}

