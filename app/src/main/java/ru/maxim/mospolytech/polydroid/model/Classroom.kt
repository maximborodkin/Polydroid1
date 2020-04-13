package ru.maxim.mospolytech.polydroid.model

class Classroom(
    id: Int,
    name: String,
    val color: String
) : SearchObject(id, name) {
    override fun getType() = "classroom"
}
