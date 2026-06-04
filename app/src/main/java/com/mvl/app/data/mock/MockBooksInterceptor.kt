package com.mvl.app.data.mock

import com.google.gson.Gson
import com.mvl.app.data.api.BookResponse
import com.mvl.app.data.api.LocationDto
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.util.UUID

/**
 * Intercepts all requests to /books and returns deterministic mocked responses.
 * This layer is completely decoupled from business logic — swap it out without
 * touching any ViewModel or Repository code.
 */
class MockBooksInterceptor(private val gson: Gson) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url

        if (!url.encodedPath.contains("/books")) return chain.proceed(request)

        val json = when {
            request.method == "POST" -> handlePostBooks(request)
            request.method == "GET" -> handleGetBooks(url.queryParameter("year")?.toIntOrNull(),
                url.queryParameter("month")?.toIntOrNull())
            else -> chain.proceed(request).let { return it }
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(json.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun handlePostBooks(request: okhttp3.Request): String {
        val bodyString = request.body?.let {
            val buffer = okio.Buffer()
            it.writeTo(buffer)
            buffer.readUtf8()
        } ?: "{}"

        val requestJson = JSONObject(bodyString)
        val aJson = requestJson.optJSONObject("a")
        val bJson = requestJson.optJSONObject("b")

        val response = BookResponse(
            id = UUID.randomUUID().toString(),
            a = LocationDto(
                latitude = aJson?.optDouble("latitude") ?: 0.0,
                longitude = aJson?.optDouble("longitude") ?: 0.0,
                aqi = aJson?.optInt("aqi") ?: 0,
                name = aJson?.optString("name") ?: ""
            ),
            b = LocationDto(
                latitude = bJson?.optDouble("latitude") ?: 0.0,
                longitude = bJson?.optDouble("longitude") ?: 0.0,
                aqi = bJson?.optInt("aqi") ?: 0,
                name = bJson?.optString("name") ?: ""
            ),
            price = 10000.0
        )
        return gson.toJson(response)
    }

    private fun handleGetBooks(year: Int?, month: Int?): String {
        // Generate deterministic mock history entries based on year/month
        val seed = ((year ?: 2024) * 100 + (month ?: 1)).toLong()
        val count = (seed % 4 + 2).toInt() // 2-5 entries

        val records = (1..count).map { i ->
            BookResponse(
                id = "mock-$year-$month-$i",
                a = LocationDto(
                    latitude = 36.564 + i * 0.01,
                    longitude = 127.001 + i * 0.005,
                    aqi = 20 + i * 10,
                    name = "Location A-$i"
                ),
                b = LocationDto(
                    latitude = 36.567 + i * 0.01,
                    longitude = 127.000 + i * 0.005,
                    aqi = 30 + i * 10,
                    name = "Location B-$i"
                ),
                price = 10000.0 * i
            )
        }
        return gson.toJson(records)
    }
}
