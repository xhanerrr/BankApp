package com.example.bankapp.di

import com.example.bankapp.data.repository.AuthRepositoryImpl
import com.example.bankapp.data.repository.ProfileRepositoryImpl
import com.example.bankapp.data.repository.TransactionRepositoryImpl
import com.example.bankapp.data.repository.UserRepositoryImpl
import com.example.bankapp.domain.repository.AuthRepository
import com.example.bankapp.domain.repository.ProfileRepository
import com.example.bankapp.domain.repository.TransactionRepository
import com.example.bankapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleBinds {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository
}
