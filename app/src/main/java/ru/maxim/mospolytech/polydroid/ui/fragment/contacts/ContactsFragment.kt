package ru.maxim.mospolytech.polydroid.ui.fragment.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_contacts.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.model.Contact

class ContactsFragment : MvpAppCompatFragment(), ContactsView {

    @InjectPresenter
    lateinit var contactsPresenter: ContactsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_contacts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.contacts)
        contactsRefreshLayout.setOnRefreshListener {
            contactsPresenter.loadContacts()
        }
    }

    override fun onContactsLoaded(contacts: List<Contact>) {
        contactsRefreshLayout.isRefreshing = false
        contactsRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ContactsRecyclerAdapter(contacts)
        }
    }

    override fun onContactsLoadError() {
        contactsRefreshLayout.isRefreshing = false
        contactsMessage.visibility = View.VISIBLE
        contactsMessage.text = getString(R.string.contacts_load_error)
    }
}