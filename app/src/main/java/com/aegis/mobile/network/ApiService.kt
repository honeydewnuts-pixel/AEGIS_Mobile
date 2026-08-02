package com.aegis.mobile.network

import com.aegis.mobile.models.AnalysisResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("/aegis/analyze")
    suspend fun analyzeScreenshot(
        @Part image: MultipartBody.Part
    ): Response<AnalysisResponse>
}
