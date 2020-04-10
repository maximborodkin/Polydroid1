package ru.maxim.mospolytech.polydroid.model

data class Contact (
    val id: Long,
    val title: String,
    val detail: ArrayList<ContactFaculty>
){
    data class ContactFaculty(
        val name: String?,
        val address: Pair<String, String?>?,
        val telephones: ArrayList<Pair<String, String?>>?,
        val faxes: ArrayList<Pair<String, String?>>?,
        val emails: ArrayList<Pair<String, String?>>?
    )
}