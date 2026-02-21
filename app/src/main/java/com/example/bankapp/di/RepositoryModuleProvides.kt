package com.example.bankapp.di

import com.example.bankapp.data.datasource.ProfileApiClient
import com.example.bankapp.data.mapper.ProfileMapper
import com.example.bankapp.data.repository.ProfileRepositoryImpl
import com.example.bankapp.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModuleProvides {

    @Provides
    @Singleton
    fun provideProfileRepository(
        apiClient: ProfileApiClient,
        mapper: ProfileMapper
    ): ProfileRepository = ProfileRepositoryImpl(apiClient, mapper)
}
