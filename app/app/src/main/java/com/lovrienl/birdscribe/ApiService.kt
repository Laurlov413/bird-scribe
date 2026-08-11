package com.lovrienl.birdscribe

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("transcribe")
    suspend fun transcribe(
        @Part audio: MultipartBody.Part
    ): Response<TranscribeResponse>
}

data class TranscribeResponse(
    val transcript: String
)

