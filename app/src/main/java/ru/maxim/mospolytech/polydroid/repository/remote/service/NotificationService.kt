package ru.maxim.mospolytech.polydroid.repository.remote.service

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.maxim.mospolytech.polydroid.model.Notification
import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient

interface NotificationService {

    @GET("/notifications")
    suspend fun getNotifications(
        @Query("last_index") lastIndex: Int,
        @Query("target") target: String): List<Notification>

    companion object{
        operator fun invoke(): NotificationService =
            RetrofitClient.instance.create(NotificationService::class.java)
    }
}