package com.example.bankapp.data.mapper

import com.example.bankapp.data.model.ProfileResponseDto
import com.example.bankapp.domain.model.ProfileUrls
import javax.inject.Inject

class ProfileMapper @Inject constructor() {
    fun mapToDomain(dto: ProfileResponseDto): ProfileUrls {
        return ProfileUrls(urls = dto.urls)
    }
}