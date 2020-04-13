package ru.maxim.mospolytech.polydroid.model

class Group(
    id: Int,
    name: String,
    val isEvening: Boolean
) : SearchObject(id, name) {
    override fun getType() = "group"
}
