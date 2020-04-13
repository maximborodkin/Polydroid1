package ru.maxim.mospolytech.polydroid.repository.remote.service

import retrofit2.http.GET
import retrofit2.http.Url
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient

interface ScheduleService {

    @GET
    suspend fun getSchedule(@Url path: String): Schedule

    companion object{
        operator fun invoke(): ScheduleService =
            RetrofitClient.instance.create(ScheduleService::class.java)
    }
}