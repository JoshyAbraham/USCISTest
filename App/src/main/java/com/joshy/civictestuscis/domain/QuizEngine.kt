package com.joshy.civictestuscis.domain

import com.joshy.civictestuscis.data.CivicsQuestion
import com.joshy.civictestuscis.data.QuizMode
import kotlin.random.Random

data class QuizProgress(
    val asked: Int,
    val correct: Int,
    val maxQuestions: Int,
    val passThreshold: Int,
    val passed: Boolean,
    val failed: Boolean
)

data class QuizSession(
    val questions: List<CivicsQuestion>,
    val mode: QuizMode,
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val answeredCount: Int = 0,
    val finished: Boolean = false
)

object QuizEngine {
    fun createSession(allQuestions: List<CivicsQuestion>, mode: QuizMode): QuizSession {
        val pool = when (mode) {
            QuizMode.INTERVIEW_2025, QuizMode.PRACTICE_MCQ, QuizMode.PRACTICE_MCQ_ALL -> allQuestions
            QuizMode.SPECIAL_65_20 -> allQuestions.filter { it.isStarred }
        }
        val maxQuestions = when (mode) {
            QuizMode.SPECIAL_65_20 -> 10
            QuizMode.PRACTICE_MCQ_ALL -> pool.size  // All questions
            else -> 20
        }
        val shuffled = pool.shuffled(Random(System.currentTimeMillis())).take(maxQuestions)
        return QuizSession(questions = shuffled, mode = mode)
    }

    fun thresholds(mode: QuizMode): Pair<Int, Int> {
        return when (mode) {
            QuizMode.SPECIAL_65_20 -> 10 to 6
            QuizMode.PRACTICE_MCQ_ALL -> 128 to 90  // 70% pass rate for all 128
            else -> 20 to 12
        }
    }

    fun evaluate(question: CivicsQuestion, userAnswer: String): Boolean {
        val normalizedUser = normalize(userAnswer)
        return question.acceptedAnswers.any { accepted ->
            val normalizedAccepted = normalize(accepted)
            normalizedUser == normalizedAccepted || normalizedUser.contains(normalizedAccepted)
        }
    }

    fun submitAnswer(session: QuizSession, isCorrect: Boolean): QuizSession {
        val (maxQ, passThreshold) = thresholds(session.mode)
        val newAnswered = session.answeredCount + 1
        val newCorrect = session.correctCount + if (isCorrect) 1 else 0
        val remaining = maxQ - newAnswered
        val canStillPass = newCorrect + remaining >= passThreshold
        val passed = newCorrect >= passThreshold
        val failed = !canStillPass
        val finished = passed || failed || newAnswered >= maxQ

        return session.copy(
            currentIndex = (session.currentIndex + 1).coerceAtMost(session.questions.lastIndex),
            correctCount = newCorrect,
            answeredCount = newAnswered,
            finished = finished
        )
    }

    fun progress(session: QuizSession): QuizProgress {
        val (maxQ, passThreshold) = thresholds(session.mode)
        val remaining = maxQ - session.answeredCount
        val canStillPass = session.correctCount + remaining >= passThreshold
        val passed = session.correctCount >= passThreshold
        val failed = !canStillPass
        return QuizProgress(
            asked = session.answeredCount,
            correct = session.correctCount,
            maxQuestions = maxQ,
            passThreshold = passThreshold,
            passed = passed,
            failed = failed
        )
    }

    private fun normalize(value: String): String {
        return value
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
