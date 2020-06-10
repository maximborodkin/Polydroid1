package ru.maxim.mospolytech.polydroid.model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.lang.Exception

data class Notification(
    val id: Int,
    val time: Long,
    @SerializedName("old_lesson")
    private val oldLessonRaw: String,
    @SerializedName("new_lesson")
    private val newLessonRaw: String
) {
    fun getOldLesson(): Lesson? = try {
        val str = oldLessonRaw.substring(0, oldLessonRaw.length)
        Log.d("ND", "oldLessonStr $str")
        Gson().fromJson(str, Lesson::class.java)
    } catch (e: Exception) { null }

    fun getNewLesson(): Lesson? = try {
        val str = newLessonRaw.substring(0, newLessonRaw.length)
        Log.d("ND", "newLessonStr $str")
        Gson().fromJson(str, Lesson::class.java)
    } catch (e: Exception) { null }
}