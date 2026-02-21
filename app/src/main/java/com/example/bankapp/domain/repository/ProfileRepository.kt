package com.example.bankapp.domain.repository

import com.example.bankapp.domain.model.ProfileUrls

interface ProfileRepository {
    suspend fun getProfileUrls(): ProfileUrls
}