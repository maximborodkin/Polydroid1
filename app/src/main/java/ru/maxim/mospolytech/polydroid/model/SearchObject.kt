package ru.maxim.mospolytech.polydroid.model

import java.util.*
import kotlin.collections.ArrayList

abstract class SearchObject(val id: Int, val name: String) {

    fun hasEntry(other: String) =
        name.toLowerCase(Locale.ROOT).trim().contains(other.toLowerCase(Locale.ROOT).trim())

    override operator fun equals(other: Any?) = (other as String) == name
    override fun hashCode() = 31 * id + name.hashCode()
    abstract fun getType(): String
}

data class SearchObjects(
    var date: Long,
    val groups: List<Group>,
    val teachers: List<Teacher>,
    val classrooms: List<Classroom>
){
    fun toArrayList() =
        ArrayList<SearchObject>().apply {
            addAll(groups)
            addAll(teachers)
            addAll(classrooms)
        }
}