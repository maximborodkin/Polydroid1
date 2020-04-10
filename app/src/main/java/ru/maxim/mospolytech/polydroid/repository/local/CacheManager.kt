package ru.maxim.mospolytech.polydroid.repository.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okio.IOException
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.model.ScheduleType
import ru.maxim.mospolytech.polydroid.model.SearchObjects
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

/**
 *  Singleton object for managing cache files
 *  @property context sets from [ru.maxim.mospolytech.polydroid.App] class
 *  Base directory for cache files is data/data/ru.maxim.mospolytech.polydroid/files
 */
object CacheManager {

    lateinit var context: Context
    private val cacheDir: File by lazy { File(context.filesDir.toURI()) }
    private const val searchObjectsFileName = "SearchObjects.json"
    private const val scheduleFileName = "Schedule"

    fun getCachedSearchObjects(): Pair<SearchObjects, Date>? =
        getObjectFromCache(searchObjectsFileName)

    fun saveSearchObjects(searchObjects: SearchObjects) =
        saveObjectToCache(searchObjects, searchObjectsFileName)

    fun getSchedule(type: ScheduleType, id: Int): Pair<Schedule, Date>? =
        getObjectFromCache("${scheduleFileName}_${type.name}_${id}")

    fun getSchedule(query: String): Pair<Schedule, Date>? =
        getObjectFromCache("${scheduleFileName}_search_${query}")

    fun saveSchedule(type: ScheduleType, schedule: Schedule, id: Int) =
        saveObjectToCache(schedule, "${scheduleFileName}_${type}_${id}")

    fun saveSchedule(schedule: Schedule, query: String) =
        saveObjectToCache(schedule, "${scheduleFileName}_search_${query}")

    fun getCacheSize(): Long {
        var totalSize = 0L
        cacheDir.listFiles()?.forEach {
            if (it.isFile && it.exists())
                totalSize += it.length()
        }
        return totalSize
    }

    private inline fun <reified T> getObjectFromCache(fileName: String): Pair<T, Date>? {
        val cacheFileDir = cacheDir.listFiles()
        val file = cacheFileDir?.find { it.name == fileName }
        return if (file != null && file.exists() && file.isFile) {
            val creationDate = Date(file.lastModified())
            try {
                val inputStream = file.inputStream()
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val schedule = Gson().fromJson(jsonString, T::class.java)
                Pair(schedule, creationDate)
            } catch(e: IOException) {
                e.printStackTrace()
                null
            } catch (e: JsonSyntaxException){
                e.printStackTrace()
                null
            }
        } else null
    }

    private inline fun <reified T> saveObjectToCache(obj: T, fileName: String) {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = context.openFileOutput(fileName, MODE_PRIVATE)
            val jsonString = Gson().toJson(obj)
            fileOutputStream.write(jsonString.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            fileOutputStream?.close()
        }
    }
}