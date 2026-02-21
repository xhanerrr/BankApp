package com.example.bankapp.data.repository

import com.example.bankapp.data.datasource.ProfileApiClient
import com.example.bankapp.data.mapper.ProfileMapper
import com.example.bankapp.domain.model.ProfileUrls
import com.example.bankapp.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiClient: ProfileApiClient,
    private val mapper: ProfileMapper
) : ProfileRepository {

    override suspend fun getProfileUrls(): ProfileUrls {
        val dto = apiClient.getProfileUrls()
        return mapper.mapToDomain(dto)
    }
}