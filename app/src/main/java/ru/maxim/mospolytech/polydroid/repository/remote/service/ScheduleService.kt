package ru.maxim.mospolytech.polydroid.repository.remote.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.repository.remote.RetrofitClient

interface ScheduleService {

    @GET("schedule/group/{groupId}")
    fun getGroupSchedule(@Path("groupId") groupId: Int): Call<Schedule>

    @GET("schedule/teacher/{teacherId}")
    fun getTeacherSchedule(@Path("teacherId") teacherId: Int): Call<Schedule>

    @GET("schedule/group/{classroomId}")
    fun getClassroomSchedule(@Path("classroomId") classroomId: Int): Call<Schedule>

    @GET("schedule/search?q")
    fun searchSchedule(@Query("q") q: String) : Call<Schedule>

    companion object{
        operator fun invoke(): ScheduleService =
            RetrofitClient.instance.create(ScheduleService::class.java)
    }
}