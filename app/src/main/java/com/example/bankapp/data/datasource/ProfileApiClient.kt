package com.example.bankapp.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.http.ContentType
import com.example.bankapp.data.model.ProfileResponseDto
import javax.inject.Inject

const val GITHUB_RAW_URL =
    "https://gist.githubusercontent.com/xhanerrr/581178bc7cd116081eb878b9650f23db/raw/578e9bdae28dbdcd924c2bcaabbf65e8d9731515/profile_data.json"

class ProfileApiClient @Inject constructor() {

    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {

            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )

            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                },
                contentType = ContentType.Text.Plain
            )
        }
    }

    suspend fun getProfileUrls(): ProfileResponseDto {
        return httpClient.get(GITHUB_RAW_URL).body()
    }
}
