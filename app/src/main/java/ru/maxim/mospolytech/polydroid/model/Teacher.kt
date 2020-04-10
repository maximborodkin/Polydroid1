package ru.maxim.mospolytech.polydroid.model

class Teacher(
    id: Int,
    name: String
) : SearchObject(id, name) {
    override fun getType() = ScheduleType.Teacher
}
