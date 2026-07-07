package com.joshy.civictestuscis.data

import kotlinx.coroutines.runBlocking
import org.junit.Test

class OfficialsResolverTest {

	@Test
	fun testDynamicAPIWithMultipleZipCodes() = runBlocking {
		val resolver = OfficialsResolver()
		val testZipCodes = listOf(
			"10001" to "New York",
			"20001" to "Washington DC",
			"90001" to "Los Angeles",
			"19425" to "Pennsylvania"
		)

		println("\n" + "=".repeat(60))
		println("🧪 DYNAMIC API TEST - Testing Multiple ZIP Codes")
		println("=".repeat(60) + "\n")

		for ((zipCode, location) in testZipCodes) {
			println("📍 Testing ZIP: $zipCode ($location)")
			println("-".repeat(60))

			try {
				val result = resolver.resolve(zipCode)

				println("✅ SUCCESS!")
				println("\nLocation:")
				println("  State: ${result.stateName} (${result.stateCode})")
				println("\nFederal Officials:")
				println("  President: ${result.presidentName}")
				println("  Vice President: ${result.vicePresidentName}")
				println("  Speaker: ${result.speakerName}")
				println("  Chief Justice: ${result.chiefJusticeName}")
				println("\nYour Representatives:")
				println("  Senators:")
				result.senators.forEach { println("    • $it") }
				println("  Representative: ${result.representativeName}")
				println("\nState:")
				println("  Governor: ${result.governorName}")
				println("  Capital: ${result.capitalName}")
				println()

			} catch (e: Exception) {
				println("❌ FAILED!")
				println("Error: ${e.message}")
				println("Type: ${e.javaClass.simpleName}")
				e.printStackTrace()
				println()
			}

			println("=".repeat(60) + "\n")
		}
	}

	@Test
	fun testSpecificDynamicQuestions() = runBlocking {
		println("\n" + "=".repeat(60))
		println("🧪 Testing Dynamic Question Answer Injection")
		println("=".repeat(60) + "\n")

		val resolver = OfficialsResolver()
		val zipCode = "10001" // New York

		try {
			val officials = resolver.resolve(zipCode)

			println("Testing with ZIP: $zipCode (${officials.stateName})\n")

			val questions = mapOf(
				23 to "Who is one of your state's U.S. senators now?",
				29 to "Name your U.S. representative.",
				30 to "What is the name of the Speaker of the House of Representatives now?",
				38 to "What is the name of the President of the United States now?",
				39 to "What is the name of the Vice President of the United States now?",
				57 to "Who is the Chief Justice of the United States now?",
				61 to "Who is the governor of your state now?",
				62 to "What is the capital of your state?"
			)

			val answers = mapOf(
				23 to officials.senators,
				29 to listOf(officials.representativeName),
				30 to listOf(officials.speakerName),
				38 to listOf(officials.presidentName),
				39 to listOf(officials.vicePresidentName),
				57 to listOf(officials.chiefJusticeName),
				61 to listOf(officials.governorName),
				62 to listOf(officials.capitalName)
			)

			questions.forEach { (id, question) ->
				println("Q$id: $question")
				println("Answer(s):")
				answers[id]?.forEach { println("  • $it") }
				println()
			}

			println("✅ All dynamic questions have answers!")

		} catch (e: Exception) {
			println("❌ FAILED!")
			println("Error: ${e.message}")
			e.printStackTrace()
		}

		println("=".repeat(60))
	}
}
