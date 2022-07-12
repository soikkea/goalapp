package com.example.goalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// TODO: set exportSchema to true
@Database(entities = [Goal::class, GoalProgress::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GoalDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun progressDao(): GoalProgressDao

    companion object {
        @Volatile
        private var INSTANCE: GoalDatabase? = null

        fun getDatabase(context: Context): GoalDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): GoalDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                GoalDatabase::class.java,
                "goal_database"
            )
                // TODO: Remove this from final version
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}