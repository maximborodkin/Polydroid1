package ru.maxim.mospolytech.polydroid.model

abstract class SearchObject(val id: Int, val name: String){

    fun hasEntry(other: String) = name.toLowerCase().trim().contains(other.toLowerCase().trim())
    override operator fun equals(other: Any?) = (other as String) == name
    override fun hashCode() = 31 * id + name.hashCode()
    abstract fun getType(): ScheduleType

    class SearchObjects(
        val groups: List<Group>,
        val teachers: List<Teacher>,
        val classrooms: List<Classroom>
    )
}