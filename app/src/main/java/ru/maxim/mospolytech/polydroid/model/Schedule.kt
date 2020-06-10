package ru.maxim.mospolytech.polydroid.model

data class Schedule(
    val id: Int,
    val type: String,
    val date: Long,
    val title: String,
    val grid: List<List<List<Lesson>>>
)
