package com.example.bankapp.di

import com.example.bankapp.data.datasource.ProfileApiClient
import com.example.bankapp.data.mapper.ProfileMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModuleProvides {

    @Provides
    @Singleton
    fun provideProfileApiClient(): ProfileApiClient = ProfileApiClient()

    @Provides
    @Singleton
    fun provideProfileMapper(): ProfileMapper = ProfileMapper()
}
