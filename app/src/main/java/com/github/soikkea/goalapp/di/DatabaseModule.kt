package com.github.soikkea.goalapp.di

import android.content.Context
import com.github.soikkea.goalapp.data.GoalDao
import com.github.soikkea.goalapp.data.GoalDatabase
import com.github.soikkea.goalapp.data.GoalProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideGoalDatabase(@ApplicationContext context: Context): GoalDatabase {
        return GoalDatabase.getDatabase(context)
    }

    @Provides
    fun provideGoalDao(goalDatabase: GoalDatabase): GoalDao {
        return goalDatabase.goalDao()
    }

    @Provides
    fun provideProgressDao(goalDatabase: GoalDatabase): GoalProgressDao {
        return goalDatabase.progressDao()
    }
}