package com.example.repoviewer.di

import android.content.Context
import android.content.SharedPreferences
import com.example.repoviewer.data.storage.KeyValueStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideKeyValueStorage(sharedPreferences: SharedPreferences): KeyValueStorage {
        return KeyValueStorage(sharedPreferences)
    }
}