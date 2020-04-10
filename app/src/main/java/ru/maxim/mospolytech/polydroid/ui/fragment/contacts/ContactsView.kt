package ru.maxim.mospolytech.polydroid.ui.fragment.contacts

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import ru.maxim.mospolytech.polydroid.model.Contact

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface ContactsView : MvpView {

    fun onContactsLoaded(contacts: List<Contact>)
    fun onContactsLoadError()
}