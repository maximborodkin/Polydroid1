package ru.maxim.mospolytech.polydroid.repository.local

import android.content.Context
import com.google.gson.Gson
import ru.maxim.mospolytech.polydroid.model.Schedule
import ru.maxim.mospolytech.polydroid.model.SearchObjects
import ru.maxim.mospolytech.polydroid.repository.local.CacheManager.context
import java.io.File

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

    fun getCachedSearchObjects(): SearchObjects? =
        getObjectFromCache(searchObjectsFileName)

    fun saveSearchObjects(searchObjects: SearchObjects) =
        saveObjectToCache(searchObjects, searchObjectsFileName)

    fun getSchedule(query: String): Schedule? =
        getObjectFromCache("${scheduleFileName}_${query.replace("/", "_")}")

    fun saveSchedule(schedule: Schedule, query: String) =
        saveObjectToCache(schedule, "${scheduleFileName}_${query.replace("/", "_")}")

    fun getCacheSize(): Long {
        var totalSize = 0L
        cacheDir.listFiles()?.forEach {
            if (it.isFile && it.exists())
                totalSize += it.length()
        }
        return totalSize
    }

    private inline fun <reified T> getObjectFromCache(fileName: String): T? {
        try {
            context.openFileInput(fileName).use {
                val jsonString = it.bufferedReader().use { reader -> reader.readText() }
                return Gson().fromJson(jsonString, T::class.java)
            }
        } catch (e: Exception){
            return null
        }
    }

    private inline fun <reified T> saveObjectToCache(obj: T, fileName: String) {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                val jsonString = Gson().toJson(obj)
                it.write(jsonString.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}