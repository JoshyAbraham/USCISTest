package com.joshy.civictestuscis.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user-entered overrides for all dynamic answer fields.
 * Provides validation and defaults for current officials and location-based answers.
 */
class PreferencesManager(context: Context) {
	private val prefs: SharedPreferences = context.getSharedPreferences(
		"civic_test_prefs",
		Context.MODE_PRIVATE
	)

	companion object {
		// Keys for all dynamic fields
		private const val KEY_PRESIDENT = "president_override"
		private const val KEY_VICE_PRESIDENT = "vice_president_override"
		private const val KEY_SPEAKER = "speaker_override"
		private const val KEY_CHIEF_JUSTICE = "chief_justice_override"
		private const val KEY_SENATORS = "senators_override"  // JSON array string
		private const val KEY_REPRESENTATIVE = "representative_override"
		private const val KEY_GOVERNOR = "governor_override"
		private const val KEY_CAPITAL = "capital_override"
		private const val KEY_FIRST_LAUNCH = "is_first_launch"

		/**
		 * Validates a name input: allows letters, spaces, periods, hyphens, apostrophes
		 */
		fun isValidName(name: String): Boolean {
			if (name.isBlank()) return false
			return name.all { it.isLetter() || it.isWhitespace() || it == '.' || it == '-' || it == '\'' }
		}

		/**
		 * Validates a list of senator names (max 2)
		 */
		fun isValidSenatorList(senators: List<String>): Boolean {
			return senators.size <= 2 && senators.all { isValidName(it) }
		}
	}

	// First launch flag
	fun isFirstLaunch(): Boolean = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
	fun markFirstLaunchComplete() {
		prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
	}

	// President
	fun getPresidentOverride(): String? = prefs.getString(KEY_PRESIDENT, null)
	fun setPresidentOverride(name: String) {
		if (isValidName(name)) {
			prefs.edit().putString(KEY_PRESIDENT, name.trim()).apply()
		}
	}

	// Vice President
	fun getVicePresidentOverride(): String? = prefs.getString(KEY_VICE_PRESIDENT, null)
	fun setVicePresidentOverride(name: String) {
		if (isValidName(name)) {
			prefs.edit().putString(KEY_VICE_PRESIDENT, name.trim()).apply()
		}
	}

	// Speaker
	fun getSpeakerOverride(): String? = prefs.getString(KEY_SPEAKER, null)
	fun setSpeakerOverride(name: String) {
		if (isValidName(name)) {
			prefs.edit().putString(KEY_SPEAKER, name.trim()).apply()
		}
	}

	// Chief Justice
	fun getChiefJusticeOverride(): String? = prefs.getString(KEY_CHIEF_JUSTICE, null)
	fun setChiefJusticeOverride(name: String) {
		if (isValidName(name)) {
			prefs.edit().putString(KEY_CHIEF_JUSTICE, name.trim()).apply()
		}
	}

	// Senators (stored as comma-separated)
	fun getSenatorsOverride(): List<String>? {
		val stored = prefs.getString(KEY_SENATORS, null) ?: return null
		return stored.split("|||").filter { it.isNotBlank() }
	}
	fun setSenatorsOverride(senators: List<String>) {
		val cleaned = senators.map { it.trim() }.filter { it.isNotBlank() }
		if (isValidSenatorList(cleaned)) {
			prefs.edit().putString(KEY_SENATORS, cleaned.joinToString("|||")).apply()
		}
	}

	// Representative
	fun getRepresentativeOverride(): String? = prefs.getString(KEY_REPRESENTATIVE, null)
	fun setRepresentativeOverride(name: String) {
		if (isValidName(name)) {
			prefs.edit().putString(KEY_REPRESENTATIVE, name.trim()).apply()
		}
	}

	// Governor
	fun getGovernorOverride(): String? = prefs.getString(KEY_GOVERNOR, null)
	fun setGovernorOverride(name: String) {
		if (isValidName(name)) {
			prefs.edit().putString(KEY_GOVERNOR, name.trim()).apply()
		}
	}

	// Capital
	fun getCapitalOverride(): String? = prefs.getString(KEY_CAPITAL, null)
	fun setCapitalOverride(name: String) {
		// Capital validation is more lenient (can have numbers, etc.)
		if (name.isNotBlank()) {
			prefs.edit().putString(KEY_CAPITAL, name.trim()).apply()
		}
	}

	/**
	 * Clear all overrides
	 */
	fun clearAllOverrides() {
		prefs.edit().clear().apply()
	}

	/**
	 * Check if any overrides are currently set
	 */
	fun hasAnyOverrides(): Boolean {
		return getPresidentOverride() != null ||
				getVicePresidentOverride() != null ||
				getSpeakerOverride() != null ||
				getChiefJusticeOverride() != null ||
				getSenatorsOverride() != null ||
				getRepresentativeOverride() != null ||
				getGovernorOverride() != null ||
				getCapitalOverride() != null
	}
}
