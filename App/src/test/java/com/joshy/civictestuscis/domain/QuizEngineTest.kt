package com.joshy.civictestuscis.domain

import com.joshy.civictestuscis.data.CivicsQuestion
import com.joshy.civictestuscis.data.QuizMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizEngineTest {
    @Test
    fun specialModeUsesTenQuestionsAndSixPass() {
        val (maxQ, pass) = QuizEngine.thresholds(QuizMode.SPECIAL_65_20)
        assertEquals(10, maxQ)
        assertEquals(6, pass)
    }

    @Test
    fun interviewModeUsesTwentyQuestionsAndTwelvePass() {
        val (maxQ, pass) = QuizEngine.thresholds(QuizMode.INTERVIEW_2025)
        assertEquals(20, maxQ)
        assertEquals(12, pass)
    }

    @Test
    fun evaluateMatchesAcceptedAnswerIgnoringPunctuation() {
        val question = CivicsQuestion(
            id = 1,
            question = "test",
            acceptedAnswers = listOf("Constitution-based federal republic"),
            isStarred = false
        )
        assertTrue(QuizEngine.evaluate(question, "constitution based federal republic"))
    }
}
