package com.example.bankapp.di

import com.example.bankapp.domain.datasource.AuthDataSource
import com.example.bankapp.data.datasource.AuthDataSourceImpl
import com.example.bankapp.domain.datasource.TransactionDataSource
import com.example.bankapp.data.datasource.TransactionDataSourceImpl
import com.example.bankapp.domain.datasource.UserDataSource
import com.example.bankapp.data.datasource.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModuleBinds {

    @Binds
    @Singleton
    abstract fun bindTransactionDataSource(
        impl: TransactionDataSourceImpl
    ): TransactionDataSource

    @Binds
    @Singleton
    abstract fun bindAuthDataSource(
        impl: AuthDataSourceImpl
    ): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindUserDataSource(
        impl: UserDataSourceImpl
    ): UserDataSource
}
