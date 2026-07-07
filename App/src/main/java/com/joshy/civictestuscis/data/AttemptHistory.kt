package com.joshy.civictestuscis.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val asked: Int,
    val correct: Int,
    val passed: Boolean,
    val timestampEpochMs: Long
)

@Dao
interface AttemptDao {
    @Insert
    suspend fun insert(attempt: AttemptEntity)

    @Query("SELECT * FROM attempts ORDER BY timestampEpochMs DESC")
    suspend fun listAttempts(): List<AttemptEntity>
}

@Database(entities = [AttemptEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attemptDao(): AttemptDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "civic_test.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
