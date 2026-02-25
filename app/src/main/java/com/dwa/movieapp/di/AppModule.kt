package com.dwa.movieapp.di

import com.dwa.movieapp.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("api_key")
    fun provideApiKey(): String = BuildConfig.TMDB_API_KEY
}
