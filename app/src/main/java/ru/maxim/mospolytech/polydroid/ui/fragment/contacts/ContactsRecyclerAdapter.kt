package ru.maxim.mospolytech.polydroid.ui.fragment.contacts

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contact.view.*
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.model.Contact
import ru.maxim.mospolytech.polydroid.ui.fragment.contacts.ContactsRecyclerAdapter.ContactViewHolder

class ContactsRecyclerAdapter(private val contacts: List<Contact>) : RecyclerView.Adapter<ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.contactTitle
    }

    fun contactsEquals(other: List<Contact>): Boolean {
        return contacts == other
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        val parent = holder.itemView.contactItemLayout
        val context = parent.context

        holder.title.apply {
            text = contact.title
        }

        val itemParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        itemParams.setMargins(12, 0, 0, 0)

        contact.detail.forEach {

            if (it.name != null){
                parent.addView(TextView(context).apply {
                    text = it.name
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(0, 12, 0, 0)
                    layoutParams = params
                    textSize = 16F
                })
            }

            it.address?.apply {
                parent.addView(AppCompatTextView(context).apply {
                    text = createLabel(first, second)
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_gray_24dp, 0, 0, 0)
                    layoutParams = itemParams
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${first}"))
                        startActivity(context, intent, null)
                    }
                })
            }

            it.telephones?.forEach { telephone ->
                parent.addView(AppCompatTextView(context).apply {
                    text = createLabel(telephone.first, telephone.second)
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_gray_24dp, 0, 0, 0)
                    layoutParams = itemParams
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel: ${telephone.first}"))
                        startActivity(context, intent, null)
                    }
                })
            }

            it.faxes?.forEach { fax ->
                parent.addView(AppCompatTextView(context).apply {
                    text = createLabel(fax.first, fax.second)
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fax_gray_24dp, 0, 0, 0)
                    layoutParams = itemParams
                })
            }

            it.emails?.forEach { email ->
                parent.addView(AppCompatTextView(context).apply {
                    text = createLabel(email.first, email.second)
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_gray_24dp, 0, 0, 0)
                    layoutParams = itemParams
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:${email.first}?subject=&body="))
                        startActivity(context, intent, null)
                    }
                })
            }
        }
    }

    private fun createLabel(first: String, second: String?) =
         "$first ${if (second.isNullOrEmpty()) "" else "($second)"}"
}