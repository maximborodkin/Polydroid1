package ru.maxim.mospolytech.polydroid.model

import com.google.gson.annotations.SerializedName

data class Lesson(
    val id: Int,
    val name: String,
    val teachers: List<Teacher>,
    val group: Group,
    val classrooms: List<Classroom>,
    val type: String,
    @SerializedName("date_from")
    val dateFrom: Long,
    @SerializedName("date_to")
    val dateTo: Long,
    @SerializedName("day_of_week")
    var dayOfWeek: Int?,
    val number: Int,
    val week: String
)