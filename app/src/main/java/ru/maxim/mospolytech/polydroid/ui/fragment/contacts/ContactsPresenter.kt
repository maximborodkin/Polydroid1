package ru.maxim.mospolytech.polydroid.ui.fragment.contacts

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.maxim.mospolytech.polydroid.model.Contact

@InjectViewState
class ContactsPresenter : MvpPresenter<ContactsView>() {

    private val contacts = ArrayList<Contact>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadContacts()
    }

    fun loadContacts(){
        contacts.clear()
        contacts.add(Contact(1, "Московский Политехнический университет",
            arrayListOf(Contact.ContactFaculty(
                null,
                Pair("107023, г. Москва, ул. Большая Семеновская, 38", null),
                arrayListOf(Pair("+7 (495) 223-05-23", null)),
                arrayListOf(Pair("(499) 785-62-24", null)),
                arrayListOf(Pair("mospolytech@mospolytech.ru", null))))))

        contacts.add(Contact(2, "Отделение «На Большой Семёновской»",
            arrayListOf(Contact.ContactFaculty(
                "для обучающихся Транспортного факультета",
                Pair("107023, г. Москва, ул. Большая Семеновская, 38", "ауд В-107, В-108"),
                arrayListOf(
                    Pair("+7 (495) 223-05-23", "доб. 1120, 1122, 1123 (очная форма)"),
                    Pair("+7 (495) 223-05-23", "доб. 1215, 1304 (заочная форма)")),
                null,
                arrayListOf(Pair("crs-transport@mospolytech.ru", null))),

                Contact.ContactFaculty(
                    "для обучающихся Факультета информационных технологий",
                    Pair("107023, г. Москва, ул. Большая Семеновская, 38", "ауд В-101, В102"),
                    arrayListOf(Pair("+7 (495) 223-05-23", "доб. 1175, 1375, 1475, 1331")),
                    null,
                    arrayListOf(Pair("crs-informatika@mospolytech.ru", null)))
            )))
        contacts.add(Contact(3, "Приёмная комиссия",
            arrayListOf(
                Contact.ContactFaculty(
                null,
                null,
                arrayListOf(
                    Pair("+7 (495) 223-05-19", "ул. Б. Семеновская, 38"),
                    Pair("+7 (495) 276-33-61", "ул. Автозаводская, 16"),
                    Pair("+7 (495) 607-24-83", "ул. Садовая-Спасская, 6")
                ), null, null)
            )))
        viewState.onContactsLoaded(contacts)
    }
}