package com.joshy.civictestuscis.data

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val ZIP_BASE_URL = "https://api.zippopotam.us/"
private const val GOVTRACK_BASE_URL = "https://www.govtrack.us/"
private const val CENSUS_BASE_URL = "https://geocoding.geo.census.gov/"

interface ZipApi {
    @GET("us/{zip}")
    suspend fun getZipInfo(@retrofit2.http.Path("zip") zip: String): ZipInfoResponse
}

interface GovTrackApi {
    @GET("api/v2/role")
    suspend fun getRoles(
        @Query("current") current: Boolean = true,
        @Query("role_type") roleType: String,
        @Query("state") state: String? = null,
        @Query("district") district: Int? = null
    ): GovTrackResponse
}

interface CensusApi {
    @GET("geocoder/geographies/coordinates")
    suspend fun geographiesByCoordinates(
        @Query("x") lon: Double,
        @Query("y") lat: Double,
        @Query("benchmark") benchmark: String = "Public_AR_Current",
        @Query("vintage") vintage: String = "Current_Current",
        @Query("format") format: String = "json"
    ): CensusResponse
}

class OfficialsResolver {
    private val http = OkHttpClient.Builder().build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val zipApi = Retrofit.Builder()
        .baseUrl(ZIP_BASE_URL)
        .client(http)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(ZipApi::class.java)

    private val govTrackApi = Retrofit.Builder()
        .baseUrl(GOVTRACK_BASE_URL)
        .client(http)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(GovTrackApi::class.java)

    private val censusApi = Retrofit.Builder()
        .baseUrl(CENSUS_BASE_URL)
        .client(http)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(CensusApi::class.java)

    suspend fun resolve(zipCode: String, overrides: DynamicAnswerOverrides? = null): OfficialAnswers {
        val zipInfo = zipApi.getZipInfo(zipCode)
        val place = zipInfo.places.firstOrNull()
            ?: throw IllegalStateException("ZIP code has no place data")

        val stateCode = place.stateAbbreviation
        val stateName = place.state
        val lat = place.latitude.toDouble()
        val lon = place.longitude.toDouble()

        val district = runCatching {
            val census = censusApi.geographiesByCoordinates(lon = lon, lat = lat)
            census.result.geographies.congressional119.firstOrNull()?.districtNumber?.toInt()
        }.getOrNull()

        val senators = govTrackApi.getRoles(roleType = "senator", state = stateCode)
            .objects
            .take(2)
            .map { it.person.firstname + " " + it.person.lastname }

        val representative = if (district != null) {
            govTrackApi.getRoles(roleType = "representative", state = stateCode, district = district)
                .objects
                .firstOrNull()
                ?.person
                ?.let { it.firstname + " " + it.lastname }
        } else null

        val president = runCatching {
            govTrackApi.getRoles(roleType = "president")
                .objects.firstOrNull()?.person?.let { it.firstname + " " + it.lastname }
        }.getOrNull()

        val vicePresident = runCatching {
            govTrackApi.getRoles(roleType = "vicepresident")
                .objects.firstOrNull()?.person?.let { it.firstname + " " + it.lastname }
        }.getOrNull()

        return OfficialAnswers(
            stateCode = stateCode,
            stateName = stateName,
            representativeName = overrides?.representativeName ?: representative ?: "Visit house.gov to find your representative",
            senators = overrides?.senators ?: senators.ifEmpty { listOf("Visit senate.gov for current senators") },
            governorName = overrides?.governorName ?: governorsByState[stateCode] ?: "Current governor of $stateName",
            capitalName = overrides?.capitalName ?: capitalsByState[stateCode] ?: "State capital of $stateName",
            presidentName = overrides?.presidentName ?: president ?: "Current President of the United States",
            vicePresidentName = overrides?.vicePresidentName ?: vicePresident ?: "Current Vice President of the United States",
            speakerName = overrides?.speakerName ?: "Current Speaker of the House of Representatives",
            chiefJusticeName = overrides?.chiefJusticeName ?: "Current Chief Justice of the United States"
        )
    }
}

private val capitalsByState = mapOf(
    "AL" to "Montgomery", "AK" to "Juneau", "AZ" to "Phoenix", "AR" to "Little Rock",
    "CA" to "Sacramento", "CO" to "Denver", "CT" to "Hartford", "DE" to "Dover",
    "FL" to "Tallahassee", "GA" to "Atlanta", "HI" to "Honolulu", "ID" to "Boise",
    "IL" to "Springfield", "IN" to "Indianapolis", "IA" to "Des Moines", "KS" to "Topeka",
    "KY" to "Frankfort", "LA" to "Baton Rouge", "ME" to "Augusta", "MD" to "Annapolis",
    "MA" to "Boston", "MI" to "Lansing", "MN" to "Saint Paul", "MS" to "Jackson",
    "MO" to "Jefferson City", "MT" to "Helena", "NE" to "Lincoln", "NV" to "Carson City",
    "NH" to "Concord", "NJ" to "Trenton", "NM" to "Santa Fe", "NY" to "Albany",
    "NC" to "Raleigh", "ND" to "Bismarck", "OH" to "Columbus", "OK" to "Oklahoma City",
    "OR" to "Salem", "PA" to "Harrisburg", "RI" to "Providence", "SC" to "Columbia",
    "SD" to "Pierre", "TN" to "Nashville", "TX" to "Austin", "UT" to "Salt Lake City",
    "VT" to "Montpelier", "VA" to "Richmond", "WA" to "Olympia", "WV" to "Charleston",
    "WI" to "Madison", "WY" to "Cheyenne"
)

private val governorsByState = mapOf(
    "AL" to "Kay Ivey", "AK" to "Mike Dunleavy", "AZ" to "Katie Hobbs", "AR" to "Sarah Huckabee Sanders",
    "CA" to "Gavin Newsom", "CO" to "Jared Polis", "CT" to "Ned Lamont", "DE" to "Matt Meyer",
    "FL" to "Ron DeSantis", "GA" to "Brian Kemp", "HI" to "Josh Green", "ID" to "Brad Little",
    "IL" to "J.B. Pritzker", "IN" to "Mike Braun", "IA" to "Kim Reynolds", "KS" to "Laura Kelly",
    "KY" to "Andy Beshear", "LA" to "Jeff Landry", "ME" to "Janet Mills", "MD" to "Wes Moore",
    "MA" to "Maura Healey", "MI" to "Gretchen Whitmer", "MN" to "Tim Walz", "MS" to "Tate Reeves",
    "MO" to "Mike Kehoe", "MT" to "Greg Gianforte", "NE" to "Jim Pillen", "NV" to "Joe Lombardo",
    "NH" to "Kelly Ayotte", "NJ" to "Phil Murphy", "NM" to "Michelle Lujan Grisham", "NY" to "Kathy Hochul",
    "NC" to "Josh Stein", "ND" to "Kelly Armstrong", "OH" to "Mike DeWine", "OK" to "Kevin Stitt",
    "OR" to "Tina Kotek", "PA" to "Josh Shapiro", "RI" to "Dan McKee", "SC" to "Henry McMaster",
    "SD" to "Larry Rhoden", "TN" to "Bill Lee", "TX" to "Greg Abbott", "UT" to "Spencer Cox",
    "VT" to "Phil Scott", "VA" to "Glenn Youngkin", "WA" to "Bob Ferguson", "WV" to "Patrick Morrisey",
    "WI" to "Tony Evers", "WY" to "Mark Gordon"
)

// API response models

data class ZipInfoResponse(
    @Json(name = "country") val country: String,
    @Json(name = "post code") val postCode: String,
    @Json(name = "places") val places: List<ZipPlace>
)

data class ZipPlace(
    @Json(name = "place name") val placeName: String,
    @Json(name = "state") val state: String,
    @Json(name = "state abbreviation") val stateAbbreviation: String,
    @Json(name = "latitude") val latitude: String,
    @Json(name = "longitude") val longitude: String
)

data class GovTrackResponse(val objects: List<GovTrackRole>)
data class GovTrackRole(val person: GovTrackPerson)
data class GovTrackPerson(val firstname: String, val lastname: String)

data class CensusResponse(@Json(name = "result") val result: CensusResult)
data class CensusResult(@Json(name = "geographies") val geographies: CensusGeographies)

data class CensusGeographies(
    @Json(name = "119th Congressional Districts")
    val congressional119: List<CongressionalDistrict> = emptyList()
)

data class CongressionalDistrict(@Json(name = "CD119") val districtNumber: String)
